package net.teamcadi.angelbrowser.Activity_Front;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.iid.FirebaseInstanceId;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.naver.speech.clientapi.SpeechRecognitionResult;

import net.teamcadi.angelbrowser.Activity_Back.data.Elevator;
import net.teamcadi.angelbrowser.Activity_Back.data.Push;
import net.teamcadi.angelbrowser.Activity_Back.data.UserInfo;
import net.teamcadi.angelbrowser.Activity_Back.service.Network;
import net.teamcadi.angelbrowser.Activity_Front.data.ChatArray;
import net.teamcadi.angelbrowser.Adapters.ChatListAdapter;
import net.teamcadi.angelbrowser.Firebase.MyFirebaseInstanceIDService;
import net.teamcadi.angelbrowser.Handler.BackPressCloseHandler;
import net.teamcadi.angelbrowser.R;
import net.teamcadi.angelbrowser.SharedPref.SharedPrefStorage;
import net.teamcadi.angelbrowser.Utils.AudioWriterPCM;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import co.intentservice.chatui.ChatView;
import co.intentservice.chatui.models.ChatMessage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends Activity implements View.OnClickListener {
    // 기능 관련 객체 생성.
    private BackPressCloseHandler backPressCloseHandler; // 뒤로 가기 버튼 사용을 위한 Handler 객체.

    // Drawer 메뉴 관련 객체 생성.
    private DrawerLayout homeDrawerLayout; // DrawerLayout 객체.
    private View homeDrawerView; // 메뉴 뷰 객체들을 감싸는 레이아웃.
    private ImageView btnOpenMenu; // Drawer 메뉴를 여는 버튼.

    private static final String TAG = HomeActivity.class.getSimpleName();
    private static final String CLIENT_ID = "gHRuCgzl3BX0Hncwj2fY"; // Clova.ai Client ID
    private RecognitionHandler handler;
    private NaverRecognizer naverRecognizer;

    private ImageButton btnStart;
    private String mResult;

    private AudioWriterPCM writer; // 오디오 사용 >> 로컬에 storage용 파일 만들기
    private ChatView chatView; // ChatView가 채팅을 구성할 때 메시지로 보여주는 역할을 하기 때문에 지속적으로 호출 예정이라 전역으로 설정
    private Network network;  // Server 연결 Network
    ChatMessage chatMessage; // chatMessage를 생성하면서 채팅을 구성하기 때문에 전역으로 설정 후 지속적으로 호출
    private ArrayList<ChatMessage> chatMsgList; // 채팅 메시지를 ArrayList에 넣어서 출력할 예정
    private MyFirebaseInstanceIDService myFirebaseInstanceIDService; // FireBase Token 뽑아내기 위함.
    private SharedPrefStorage sharedPrefStorage; // SharedPreference 저장.

    private int fbIndex; // fbIndex >> 값 0 또는 1로 설정하기 위함 (푸시알림 > 0 은 안받고 / 1은 받고)
    private Intent fstIntent; // 첫 번째 질문 관련 답변 담는 인텐트
    private Intent scndIntent; // 두 번째 질문 관련 답변 담는 인텐트
    private Intent thrdIntent; // 세 번째 질문 관련 답변 담는 인텐트
    private int sizeIndex = 0; // 답변 액티비티에 보낼 사이즈 인덱스

    private ImageView subClosest, subCommon, subReport, subPush; // 가까운 역 , 자주가는 역 , 제보 , 푸시
    private ImageView profile_image; // 프로필 이미지
    private TextView userEmail; // 계정 연동 이메일
    private SharedPrefStorage prefStorage;

    // 채팅
    private ChatListAdapter chatListAdapter;
    private ArrayList<ChatArray> chatArrayList;
    private RecyclerView chatListView;
    private EditText edtChat;
    private ImageView btnChatSend;

    // 생성자.
    public HomeActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.home_activity);

        getFcmToken();
        initTedPermissions();
        initViews();
        initChat();

        backPressCloseHandler = new BackPressCloseHandler(this); // 뒤로 가기 버튼 사용을 위한 Handler 객체.

        // Drawer 메뉴 관련 객체 선언.
        homeDrawerLayout = (DrawerLayout) findViewById(R.id.home_drawer_layout); // DrawerLayout 객체.
        homeDrawerView = (View) findViewById(R.id.home_drawer); // 메뉴 뷰 객체들을 감싸는 레이아웃.

        // Drawer 메뉴 열기 버튼을 누르면.
        btnOpenMenu = (ImageView) findViewById(R.id.btnOpenMenu);
        btnOpenMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 메뉴 창 오픈.
                homeDrawerLayout.openDrawer(homeDrawerView);
            }
        });

        homeDrawerLayout.addDrawerListener(homeDrawerListener); // DrawerLayout을 사용하기 위한 DrawerListener 생성 및 선언.
        homeDrawerView.setOnTouchListener(new View.OnTouchListener() // Drawer 메뉴 사용을 위한 TouchListener 생성 및 선언.
        {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    private void getFcmToken() {
        myFirebaseInstanceIDService = new MyFirebaseInstanceIDService();
        myFirebaseInstanceIDService.onTokenRefresh();
        //Toast.makeText(this, "FB TOken : " + FirebaseInstanceId.getInstance().getToken(), Toast.LENGTH_LONG).show();
        sharedPrefStorage = new SharedPrefStorage(HomeActivity.this);
        sharedPrefStorage.saveFBTokenInPref("fbToken", FirebaseInstanceId.getInstance().getToken());

    }

    // DrawerLayout을 사용하기 위한 DrawerListener 생성.
    DrawerLayout.DrawerListener homeDrawerListener = new DrawerLayout.DrawerListener() {
        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {

        }

        @Override
        public void onDrawerOpened(View drawerView) {
            subReport.setOnClickListener(HomeActivity.this);
            subPush.setOnClickListener(HomeActivity.this);
            subCommon.setOnClickListener(HomeActivity.this);
            subClosest.setOnClickListener(HomeActivity.this);
        }

        @Override
        public void onDrawerClosed(View drawerView) {

        }

        @Override
        public void onDrawerStateChanged(int newState) {

        }
    };


    // 파이어베이스 인덱스 (푸시알림 설정 부분) 조정하는 메소드
    private void checkFbIndex() {
        final AlertDialog.Builder dlg = new AlertDialog.Builder(this);
        dlg.setTitle("푸시 알림 설정");
        dlg.setMessage("엘레베이터 관련 푸시메시지를 받겠습니까?");
        dlg.setPositiveButton("네 받겠습니다.", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                fbIndex = 1; // 푸시 메시지 받겠습니다! >> fbIndex = 1
                sendFbTokenWithIndex(fbIndex, sharedPrefStorage.getFBTokenByPref("fbToken")); // 토큰과 인덱스 서버로 보내기
            }
        });
        dlg.setNegativeButton("아니오 받지 않겠습니다.", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                fbIndex = 0; // 푸시 메시지를 받지 않겠습니다 >> fbIndex = 0
                sendFbTokenWithIndex(fbIndex, sharedPrefStorage.getFBTokenByPref("fbToken")); // 토큰과 인덱스 서버로 보내기
            }
        });
        dlg.show();
    }

    // 푸시 메시지 설정 메소드 (Token과 index를 같이 보낸다.)
    private void sendFbTokenWithIndex(final int fbIndex, final String fbToken) {
        network = Network.getNetworkInstance();
        network.getFbProxy().sendFBTokenToServer(fbToken, fbIndex, new Callback<Push>() {
            @Override
            public void onResponse(Call<Push> call, Response<Push> response) {
                if (response.isSuccessful()) {
                    // firebaseToken과 fbIndex값을 같이 잘 보낼 경우 (서버로)
                    Toast.makeText(HomeActivity.this, "푸시 설정이 완료되었습니다. " +
                                    "엘레베이터 관련 변경 사항들은 푸시 알림을 통해 받아보실 수 있습니다.",
                            Toast.LENGTH_LONG).show();

                    homeDrawerLayout.closeDrawer(homeDrawerView); // 전송 성공 이 후 drawer 닫기

                }
            }

            @Override
            public void onFailure(Call<Push> call, Throwable t) {
                Log.e(TAG, t.toString());
                homeDrawerLayout.closeDrawer(homeDrawerView);
            }
        });
    }

    // 뒤로 가기 버튼 클릭 시.
    @Override
    public void onBackPressed() {
        backPressCloseHandler.onBackPressed();
        //super.onBackPressed();
    }


    /*
    handle message queue
     */

    private void handleMessage(Message msg) {
        switch (msg.what) {
            case R.id.clientReady: // 음성 인식 준비
                Glide.with(HomeActivity.this)
                        .load(R.drawable.mike_on).into(btnStart);

                writer = new AudioWriterPCM(Environment.getExternalStorageDirectory().getAbsolutePath() + "/NaverSpeechTest");
                writer.open("Test");
                break;
            case R.id.partialResult:
                mResult = (String) (msg.obj);

                Glide.with(HomeActivity.this)
                        .load(R.drawable.mike_on).into(btnStart);
                break;
            case R.id.finalResult: // 최종 인식 결과
                SpeechRecognitionResult speechRecognitionResult = (SpeechRecognitionResult) msg.obj;
                List<String> results = speechRecognitionResult.getResults();
                StringBuilder strBuf = new StringBuilder();
             /*
             for (String result : results) {
                    strBuf.append(result);
                    strBuf.append("\n");
                }
             */
                //mResult = strBuf.toString();
                mResult = results.get(0).toString();
                //chatMessage = new ChatMessage(mResult, 30, ChatMessage.Type.SENT);
                //chatView.addMessage(chatMessage);
                Log.i(TAG, "메세지 음성 인식 : " + mResult);
                // 메세지를 서버로 보내는 것 (마이크를 통해서 인식한 내용)
                Log.i(TAG, " chatArrayList Size : " + chatArrayList.size());
                chatListAdapter.addAtPosition(chatArrayList.size(), mResult, true);
                sendMsgToServer(mResult);

                Glide.with(HomeActivity.this)
                        .load(R.drawable.mike_on).into(btnStart);
                break;
            case R.id.recognitionError:
                if (writer != null) {
                    writer.close();
                }
                mResult = "Error code : " + msg.obj.toString();
                btnStart.setEnabled(true);
                Glide.with(HomeActivity.this)
                        .load(R.drawable.mike_off).into(btnStart);
                break;
            case R.id.clientInactive:
                if (writer != null) {
                    writer.close();
                }
                btnStart.setEnabled(true);
                Glide.with(HomeActivity.this)
                        .load(R.drawable.mike_off).into(btnStart);
                break;
        }
    }
    /*
    first intro message >> 처음 입장할 때 나오는 메시지
     */

    // 처음 입장할 때 서버로 부터 메세지 받는 거부터 진행하게 됩니다.
    private void getIntroMsgFromServer() {

        chatListView = (RecyclerView) findViewById(R.id.chatListView);
        btnChatSend = (ImageView) findViewById(R.id.btnChatSend);
        edtChat = (EditText) findViewById(R.id.edtChat);
        chatArrayList = new ArrayList<>();
        chatListView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));


        network = Network.getNetworkInstance();
        final String[] introMsg = {""};
        // message 부분에 토큰에 대한 사용자 정보 (이름) 가지고 오기
        // 초기 가입자
        if (getIntent().getExtras().getString("kakaoId", null) == null) {
            network.getChatProxy().getIntroMessage("처음 가입자", new Callback<String>() {
                @SuppressLint("ResourceType")
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    introMsg[0] = response.body();
/*
                    chatMessage = new ChatMessage(introMsg, 30, ChatMessage.Type.RECEIVED);

                    chatView.addMessage(chatMessage);
                    chatView.setBackground(getDrawable(R.drawable.scrollbar_bg1));

           */
                    setReceiveMsgFromServer(introMsg[0]);
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.e(TAG, t.toString());
                }
            });

            // 기존 가입자
        } else {

            final String[] introMsg2 = {""};
            network.getChatProxy().getIntroMessage(getIntent().getExtras().getString("kakaoId", ""), new Callback<String>() {
                @SuppressLint("ResourceType")
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    introMsg2[0] = response.body();
                /*    chatMessage = new ChatMessage(introMsg, 30, ChatMessage.Type.RECEIVED);

                    chatView.addMessage(chatMessage);
                    chatView.setBackground(getDrawable(R.drawable.scrollbar_bg1));
*/
                    setReceiveMsgFromServer(introMsg2[0]);
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.e(TAG, t.toString());
                }
            });
        }


    }

    // 처음에 나오는 인사 메세지 (서버로부터 받음)
    private void setReceiveMsgFromServer(String rcvMessage) {
        // 서버로 부터 받은 메세지 (초반)
        // 데이터 넣어주기 (우선 입력한 걸 여기다 넣어야지)! send
        chatArrayList.add(new ChatArray(rcvMessage));
        chatListAdapter = new ChatListAdapter(chatArrayList, this);

        chatListView.setAdapter(chatListAdapter);

        setSendMsgToServer();
    }

    private void setSendMsgToServer() {

        final String[] strChat = {""};
        // 채팅 Send
        btnChatSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strChat[0] += edtChat.getText().toString();
                chatListAdapter.addAtPosition(chatArrayList.size(), strChat[0], true);

                // 데이터 서버로 보내기 (입력한 내용)
                sendMsgToServer(edtChat.getText().toString());
                edtChat.setText("");
                strChat[0] = "";
            }
        });
    }

    /*
    처음 메세지 진행하는 거
     */
    private void sendMsgToServer(final String mResult) {
        // mResult => message (음성 인식 or 채팅으로 친 메시지)
        network = Network.getNetworkInstance();
        thrdIntent = new Intent(HomeActivity.this, ElevatorActivity.class); // 세 번째 질문에 따른 답변으로 액티비티 보냄 >> 엘레베이터 정보 보여주는 액티비티
        thrdIntent.putExtra("statNm", mResult);
        /*
        message >> ChatMessage >> result로 들어 온 String 메시지를 ChatMessage로 만들어서 보내기
        mResult가 사용자가 입력한 내용이고 이걸 서버에 보내는 부분
        chatView에 add 함으로써 메시지가 채팅창에 등록

        서버에서 받은 메세지 (첫 번째 질문에 따른 내용)
        >> 답변을 가지고 와서 채팅장 ui에 등록하기 (질문이 역정보 또는 역 정보) 이 경우만 첫 번째 질문의 답변에 대한 메시지 등록하도록 정리
         */

        if (Pattern.matches("역정보", mResult) || Pattern.matches("역 정보", mResult)) {
            // final String[] message1 = {""}; // 메세지1은 질문 1에 대한 답변으로 지속적인 합을 통해 응답을 구성

            fstIntent = new Intent(HomeActivity.this, StationInfoActivity.class);
            network.getChatProxy().receiveMessageFromServer(mResult, new Callback<List<net.teamcadi.angelbrowser.Activity_Back.data.ChatMessage>>() {
                @Override
                public void onResponse(Call<List<net.teamcadi.angelbrowser.Activity_Back.data.ChatMessage>> call, Response<List<net.teamcadi.angelbrowser.Activity_Back.data.ChatMessage>> response) {
                    Log.i(TAG, "Size : " + response.body().size());
                    for (int i = 0; i < response.body().size(); i++) {
                        Log.i(TAG, response.body().get(i).getStatNm() + " // " + response.body().get(i).getSubwayNm()
                                + " // " + response.body().get(i).getEntrcNm() + " // " + response.body().get(i).getInfraNm());

                        sizeIndex++; // 같이 보낼 예정 (사이즈로)

                        fstIntent.putExtra("statNm", response.body().get(0).getStatNm() + "역");
                    }

                    fstIntent.putExtra("size", sizeIndex);
                    SharedPrefStorage pref = new SharedPrefStorage(HomeActivity.this); // 여기다가 mResult 값을 넣을 예정입니다. 이걸로 어댑터에서 정보 가져올 예정입니다.
                    pref.saveClientAnswer("mResult", mResult);
                    Log.i(TAG, "mResult1 : " + mResult);
/*
                    chatMessage = new ChatMessage("선택해주신 " + response.body().get(0).getStatNm() + "역" + " 에 대한 정보를 가지고 오고 있습니다. 잠시만 기다려주세요.", 50, ChatMessage.Type.RECEIVED);

                    chatMessage.setMessage("선택해주신 " + response.body().get(0).getStatNm() + "역" + " 에 대한 정보를 가지고 오고 있습니다. 잠시만 기다려주세요.");
                    //chatView.setBackgroundColor(Color.parseColor("#49a7eb"));

                    chatView.addMessage(chatMessage);
                    Log.i(TAG, " HomeActivity_ChatView : " + chatMessage.getFormattedTime() + "//" + chatMessage.getTimestamp());*/

                    chatListAdapter.addAtPosition(chatArrayList.size(),
                            "선택해주신 " + response.body().get(0).getStatNm() + "역" + " 에 대한 정보를 가지고 오고 있습니다. 잠시만 기다려주세요."
                            , false);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(fstIntent);
                        }
                    }, 2000);

                }

                @Override
                public void onFailure(Call<List<net.teamcadi.angelbrowser.Activity_Back.data.ChatMessage>> call, Throwable t) {
                    Log.e(TAG, t.toString());
                    Toast.makeText(HomeActivity.this, "죄송합니다 정보를 가져 오는데 문제가 생겼습니다. 역정보를 다시 입력해주세요.", Toast.LENGTH_LONG).show();
                }
            });
        }
        // 두 번째 질문에 대한 답변을 여기서 정리해주면 됨 (서버 전송)  받아 올 정보는 두 가지다.
        //  1번 . 역에 대한 정보 { 환승역 지하철 이름 : statNm, 환승역 지하철 호선명 : subwayNm , 환승 전 지하철 방면 : sDirection , 환승 후 지하철 방면 : eDirection
        //  2번 . 환승역에 대한 정보 { 환승역 지하철 이름 : statNm , 환승역 지하철 호선명 : subwayNm [위와 동일] , arriveTime : 지하철이 환승역에 도착하는 시간
        //  leftTime : 지하철이 환승역에서 출발하는 시간 , subwayeNm: 환승역에서 지하철이 어느 방향으로 가는지 체크하는 것 (예: 사당 >> 당고개)
        // 두 번째 질문에 대한 답변을 여기서 정리해주면 됨 (서버 전송)

        else if (Pattern.matches("엘레베이터 정보", mResult) || Pattern.matches("엘리베이터 정보", mResult)) {
            final String[] message3 = {""}; // 메세지1은 질문 3에 대한 답변으로 지속적인 합을 통해 응답을 구성
            final String[] mapUrl = new String[1];
            Log.i(TAG, "응답 : " + mResult);
            network.getChatProxy().receiveElevatorInfoFromServer(mResult, new Callback<List<Elevator>>() {
                @Override
                public void onResponse(Call<List<Elevator>> call, Response<List<Elevator>> response) {
                    if (response.isSuccessful()) {
                        for (int i = 0; i < response.body().size(); i++) {
                            Log.i(TAG, "[범위] : " + response.body().get(i).getCoverage() + "\n [위치] : " + response.body().get(i).getLocation());
                            message3[0] += "[범위]: " + response.body().get(i).getCoverage() + "\n" + "[위치]: " + response.body().get(i).getLocation() + "\n";
                        }
                    }
                    if (response.body().get(0).getMap() != null)
                        mapUrl[0] = response.body().get(0).getMap();
                    else
                        mapUrl[0] = null;


                    Log.i(TAG, "mapUrl : " + mapUrl[0]);

                    /*chatMessage = new ChatMessage("선택하신 " + response.body().get(0).getStation() + "역에 대한 엘레베이터 정보를 수집 중입니다. 잠시만 기다려주세요.", 50, ChatMessage.Type.RECEIVED);
                    if (response.body().get(0).getStation().toString().equals("서울역"))
                        chatMessage.setMessage("선택하신 " + response.body().get(0).getStation() + " 에 대한 엘레베이터 정보를 수집 중입니다. 잠시만 기다려주세요.");
                    chatView.addMessage(chatMessage);
                    message3[0] = "";*/

                    chatListAdapter.addAtPosition(chatArrayList.size(),
                            "선택하신 " + response.body().get(0).getStation() + " 에 대한 엘레베이터 정보를 수집 중입니다. 잠시만 기다려주세요."
                            , false);

                    thrdIntent.putExtra("mapUrl", mapUrl[0]);


                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(thrdIntent);
                        }
                    }, 2000);

                }

                @Override
                public void onFailure(Call<List<Elevator>> call, Throwable t) {
                    Log.e(TAG, t.toString());
                }
            });
        }
        // 그 외 질문들은 여기서 정리 [ DialogFlow 연동 ] >> 채팅 봇의 답변을 들을 수 있음
        else {
            network.getChatProxy().sendMessageToServer(mResult, new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.isSuccessful()) {
                        String rcvMsg = response.body().toString();
                       /* chatMessage = new ChatMessage(rcvMsg, 50, ChatMessage.Type.RECEIVED);
                        chatView.addMessage(chatMessage);*/
                        chatListAdapter.addAtPosition(chatArrayList.size(), rcvMsg, false);
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.e(TAG, t.toString());
                }
            });
        }

    }


    /*
        채팅 그냥 수기로 입력하면 채팅창에 등록되는 거
         */
    private void initChat() {
        /*chatView = (ChatView) findViewById(R.id.chat_view);
        *//*
        msg send
         *//*

        chatView.setOnSentMessageListener(new ChatView.OnSentMessageListener() {
            @Override
            public boolean sendMessage(ChatMessage chatMessage) {
                sendMsgToServer(chatMessage.getMessage());
                if (chatMessage.getType() == ChatMessage.Type.SENT) {
                    chatView.addMessage(chatMessage);
                } else {
                    chatView.addMessage(chatMessage);
                }
                return false;
            }
        });*/
    }

    /*
    Audio Permission >> api24 이상
     */

    private void initTedPermissions() {
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(HomeActivity.this, "권한이 제공되었습니다.", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(HomeActivity.this, "권한이 없습니다.", Toast.LENGTH_LONG).show();
            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setDeniedMessage("퍼미션 거절 시 앱 사용 불가")
                .setPermissions(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE
                        , Manifest.permission.READ_EXTERNAL_STORAGE).check();
    }

    /*
    마이크 클릭 시 Clova.ai는 시작된다.
     */

    private void initViews() {

        btnStart = (ImageButton) findViewById(R.id.btn_start);
        handler = new RecognitionHandler(this);
        naverRecognizer = new NaverRecognizer(this, handler, CLIENT_ID);
        chatMsgList = new ArrayList<>();

        subClosest = (ImageView) findViewById(R.id.img_subway_closest);
        subCommon = (ImageView) findViewById(R.id.img_subway_common);
        subPush = (ImageView) findViewById(R.id.img_push_elevator);
        subReport = (ImageView) findViewById(R.id.img_report_elevator);
        profile_image = (ImageView) findViewById(R.id.profile_image);
        userEmail = (TextView) findViewById(R.id.user_Email);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!naverRecognizer.getSpeechRecognizer().isRunning()) {
                    //mResult = "";
                    // 연결 중
                    Glide.with(HomeActivity.this)
                            .load(R.drawable.mike_on).into(btnStart);
                    naverRecognizer.recognize();
                } else {
                    Log.d(TAG, "stop and wait Final Result");
                    btnStart.setEnabled(false);
                    naverRecognizer.getSpeechRecognizer().stop();
                }
            }
        });
    }


    // 서버에서 사용자 프로필 가지고 오기
    private void getUserProfileFromServer() {
        network = Network.getNetworkInstance();
        String kkToken = prefStorage.getUserTokenFromPref("kakao_token"); // 카카오 토큰
        String fbToken = prefStorage.getUserTokenFromPref("facebook_token"); // 페이스북 토큰'


        Log.i(TAG, "kkToken : " + kkToken);
        Log.i(TAG, "FBToken " + fbToken);

        Log.e(TAG, prefStorage.getUserTokenFromPref("kakao_token") + "kakao");
        Log.e(TAG, prefStorage.getUserTokenFromPref("facebook_token") + "facebook");

        switch (getIntent().getExtras().getString("login_type")) {
            case "kakao":
                if (!kkToken.equals("")) {
                    network.getUserProxy().getUserProfileFromServer(kkToken, new Callback<UserInfo>() {
                        @Override
                        public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
                            if (response.isSuccessful()) {
                                // 토큰이 저장되어 있을 경우
                                Log.i(TAG, response.body().getUserID() + "//" + response.body().getKakaoId());

                                setUserProfileFilled(response.body().getProfile(), response.body().getUserID());
                            }
                        }

                        @Override
                        public void onFailure(Call<UserInfo> call, Throwable t) {
                            Log.e(TAG, t.toString() + "   유저 정보 가지고오기 부분 ");
                        }
                    });
                }
                break;

            case "facebook":
                if (!fbToken.equals("")) {
                    network.getUserProxy().getUserProfileFromServer(fbToken, new Callback<UserInfo>() {
                        @Override
                        public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
                            if (response.isSuccessful()) {
                                // 토큰이 저장되어 있을 경우
                                Log.i(TAG, response.body().getUserID() + "//" + response.body().getKakaoId());
                                setUserProfileFilled(response.body().getProfile(), response.body().getUserID());
                            }
                        }

                        @Override
                        public void onFailure(Call<UserInfo> call, Throwable t) {
                            Log.e(TAG, t.toString() + "   유저 정보 가지고오기 부분 ");
                        }
                    });
                }
        }
    }


    private void setUserProfileFilled(String profile, String userID) {
        profile_image.setScaleType(ImageView.ScaleType.FIT_XY);
        Glide.with(this).asBitmap().load(profile)
                .into(profile_image);

        userEmail.setTextSize(15);
        userEmail.setText(userID);
        prefStorage.saveNameInPref("user_email",userEmail.getText().toString());
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        chatListAdapter.resetAll();
    }

    @Override
    protected void onStart() {
        super.onStart(); // 음성인식 서버 초기화는 여기서
        naverRecognizer.getSpeechRecognizer().initialize();
        Log.i(TAG, "시작 되었씁니다. OnStarT()");
        prefStorage = new SharedPrefStorage(HomeActivity.this);
        getUserProfileFromServer();
        getIntroMsgFromServer();
    }


    @Override
    protected void onResume() {
        super.onResume();
        btnStart.setEnabled(true);
    }

    @Override
    protected void onStop() {
        super.onStop(); // 음성인식 서버 종료
        naverRecognizer.getSpeechRecognizer().release();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_push_elevator:
                // 푸시
                checkFbIndex(); // 인덱스 값 변동 (0 >> 푸시알림 x , 1 >> 푸시알림 o
                break;
            case R.id.img_report_elevator:
                // 제보
                final Intent rIntent = new Intent(HomeActivity.this, ReportStation.class);
                startActivity(rIntent);
                break;
            case R.id.img_subway_closest:
                // 현 위치에서 가장 가까운 역 정보 알려주기
                setClosestSubway();
                break;
            case R.id.img_subway_common:
                // 자주 가는 역
                setSubwayToGoCommon();
                break;

        }
    }

    // 현 위치에서 가장 가까운 역 정보 알려주기
    private void setClosestSubway() {
        startActivity(new Intent(this, ClosestSubwayActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

    // 자주 가는 역 설정
    private void setSubwayToGoCommon() {
        startActivity(new Intent(this, CommonStationActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

    static class RecognitionHandler extends Handler {
        private final WeakReference<HomeActivity> mActivity;

        RecognitionHandler(HomeActivity activity) {
            mActivity = new WeakReference<HomeActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            HomeActivity activity = mActivity.get();
            if (activity != null) {
                activity.handleMessage(msg);
            }
        }
    }
}
