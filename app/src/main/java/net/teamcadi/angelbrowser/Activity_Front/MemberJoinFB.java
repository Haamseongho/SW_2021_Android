package net.teamcadi.angelbrowser.Activity_Front;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import net.teamcadi.angelbrowser.Activity_Back.data.UserInfo;
import net.teamcadi.angelbrowser.Activity_Back.service.Network;
import net.teamcadi.angelbrowser.Pattern.RegexUtil;
import net.teamcadi.angelbrowser.R;
import net.teamcadi.angelbrowser.SharedPref.SharedPrefStorage;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import retrofit2.Callback;
import retrofit2.Response;

public class MemberJoinFB extends Activity implements View.OnClickListener {
    private static final String TAG = MemberJoinFB.class.getSimpleName();
    // 연동되어져 오는 값 저장 관련.
    private String myProfileLink; // 프로필 사진 경로를 담고 있는 링크.
    private Bitmap myProfileBitmap; // 프로필 사진을 담을 Bitmap 객체.
    private Handler myProfileHandler = new Handler(); // 외부 Thread에서 화면을 그리기 위한 용도로 사용.
    private String myEmailID; // 프로필 이메일 주소 정보를 담을 String 객체.
    private String myName; // 프로필 이름을 담을 String 객체.

    // 연동되어져 오는 값을 뿌려줄 View 객체.
    private TextView tvMyFBEmailID; // 이메일 아이디.
    private TextView tvMyFBName; // 이름.

    // 입력되는 양식 관련 View 객체.

    private EditText edtFBPw; // 페이스북 로그인 부분 비밀번호
    private EditText edtFBPwCheck; // 페이스북 로그인 부분 비밀번호 확인
    private TextView txtFBName; // 페이스북 계정 연동해서 이름 가져온 것
    private RadioButton rdGeneralUser; // 일반 이용자
    private RadioButton rdWheelUser; // 휠체어 이용자
    private RadioButton rdManual; // 수동 휠체어
    private RadioButton rdElectronic; // 전동 휠체어
    private RadioButton rbNothing; // 휠체어 사용 안함
    private TextView txtMyFaceBookID; // 페이스북 아이디
    private Button btnEmailRegCheck;
    private ImageView btnFBJoinOK; // 페이스북 계정 가입완료 로그인
    private boolean emailChekcOk; // 이메일 체크 TF값

    private int userIndex = 0;  // 사용자 상태
    private int wheelIndex = 0;  // 휠체어 상태 여부
    private boolean fbEmailCheck = false; // 페이스북 연동 이메일 체크
    private EditText etMyKakaoID2; // 카카오 계정 입력
    private SharedPrefStorage prefStorage; // 뒤로가기 할 때 페이스북 토큰 지우기 위함.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.member_join_fb);

        // Intent를 통하여 값들을 가져옴.
        Intent FBReceiveIntent = getIntent();
        myProfileLink = FBReceiveIntent.getExtras().getString("profile2"); // 프로필 사진 링크 값을 가져와 String 형태로 저장.
        myEmailID = FBReceiveIntent.getExtras().getString("email2"); // 프로필 이메일 주소 값을 가져와 String 형태로 저장.
        myName = FBReceiveIntent.getExtras().getString("name2"); // 프로필 이름 값을 가져와 String 형태로 저장.

        Log.i(TAG, "페이스북 프로필: " + myProfileLink);
        // Thread를 사용해 이미지 비트맵을 가져옴.
        Thread profileThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final ImageView ivMyFBProfile = (ImageView) findViewById(R.id.ivMyFBProfile); // 프로필 사진을 담을 이미지뷰.
                    URL myProfileURL = new URL(myProfileLink);
                    InputStream myProfileIS = myProfileURL.openStream();
                    myProfileBitmap = BitmapFactory.decodeStream(myProfileIS);

                    // 핸들러를 사용하여 이미지뷰(비트맵) 객체와 연동.
                    myProfileHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            ivMyFBProfile.setImageBitmap(myProfileBitmap); // 사진 반영.

                            // 사진 둥글게 처리.
                            ivMyFBProfile.setBackground(new ShapeDrawable(new OvalShape()));
                            ivMyFBProfile.setClipToOutline(true);
                        }
                    });
                    // 이미지 넣기

                    Glide.with(MemberJoinFB.this).asBitmap()
                            .load(myProfileLink).into(ivMyFBProfile);

                    //ivMyFBProfile.setImageBitmap(myProfileBitmap); // 사진 반영.

                    // 사진 둥글게 처리.
                    ivMyFBProfile.setBackground(new ShapeDrawable(new OvalShape()));
                    ivMyFBProfile.setClipToOutline(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        profileThread.start(); // Thread 시작.

        // 이메일 아이디 출력.
        tvMyFBEmailID = (TextView) findViewById(R.id.tvMyFBEmailID);
        tvMyFBEmailID.setText("" + myEmailID);

        // 이름 출력.
        tvMyFBName = (TextView) findViewById(R.id.tvMyFBName);
        tvMyFBName.setText("" + myName);

        initViews();
    }

    private void initViews() {
        edtFBPw = (EditText) findViewById(R.id.etMyPassword);
        edtFBPwCheck = (EditText) findViewById(R.id.etMyPWCheck);
        rdGeneralUser = (RadioButton) findViewById(R.id.rbTypeGeneral);
        rdWheelUser = (RadioButton) findViewById(R.id.rbTypeWheel); // 휠체어 이용자
        rdManual = (RadioButton) findViewById(R.id.rbWheelManual); // 수동 휠체어
        rdElectronic = (RadioButton) findViewById(R.id.rbWheelElectric); // 전동 휠체어
        rbNothing = (RadioButton) findViewById(R.id.rbWheelNothing); // 휠체어 사용 안함
        btnFBJoinOK = (ImageView) findViewById(R.id.btnFBJoinOK); // 가입 완료 버튼
        btnEmailRegCheck = (Button) findViewById(R.id.btnFBEmailCheck); // 이메일 양식 확인
        etMyKakaoID2 = (EditText) findViewById(R.id.etMyKakaoID2);

        btnEmailRegCheck.setOnClickListener(this);
        rdGeneralUser.setOnClickListener(this);
        rdElectronic.setOnClickListener(this);
        rdManual.setOnClickListener(this);
        rdWheelUser.setOnClickListener(this);
        rbNothing.setOnClickListener(this);
        btnFBJoinOK.setOnClickListener(this);
    }

    // 뒤로 가기 버튼 클릭 시.
    @Override
    public void onBackPressed() {
        // super.onBackPressed(); -> 일단은 주석 처리.
        // 메시지 출력
        Toast.makeText(getApplicationContext(), "현재 진행 중인 회원가입 내용은 삭제됩니다.", Toast.LENGTH_LONG).show();

        // 시작 유형 선택 화면으로 돌아감.
        Intent FBToSelect = new Intent(MemberJoinFB.this, SelectJoinType.class);
        FBToSelect.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        FBToSelect.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        prefStorage = new SharedPrefStorage(this);
        prefStorage.removeUserTokenFromPref("facebook_token"); // 페북 토큰 지우기 (뒤로갈 경우);
        startActivity(FBToSelect);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rbTypeGeneral:
                userIndex = 1;
                break;

            case R.id.rbTypeWheel:
                userIndex = 2;
                break;

            case R.id.rbWheelManual:
                wheelIndex = 1; // 수동
                break;

            case R.id.rbWheelElectric:
                wheelIndex = 2; // 전동
                break;

            case R.id.rbWheelNothing:
                wheelIndex = 3; // 사용 안함
                break;

            case R.id.btnFBJoinOK:
                checkElementsAll();
                break;

            case R.id.btnFBEmailCheck:
                checkRegexEmail();
                break;

        }
    }

    private void checkElementsAll() {

        if (!fbEmailCheck) {
            Toast.makeText(MemberJoinFB.this, "이메일 중복 확인을 해주시기 바랍니다.", Toast.LENGTH_LONG).show();
        }

        if (userIndex == 0 || wheelIndex == 0) {
            Toast.makeText(MemberJoinFB.this, "휠체어 사용여부와 본인 상태를 선택하여 주시기 바랍니다.", Toast.LENGTH_LONG).show();
        }


        if (!(edtFBPw.getText().toString().equals(edtFBPwCheck.getText().toString()))) {
            Toast.makeText(MemberJoinFB.this, "비밀번호가 서로 일치하지 않습니다. 확인해주시기 바랍니다.", Toast.LENGTH_LONG).show();
        }

        if (etMyKakaoID2.getText().toString().equals("")) {
            Toast.makeText(MemberJoinFB.this, "카카오 계정을 적어주시기 바랍니다.", Toast.LENGTH_LONG).show();

        }

        if (!fbEmailCheck || userIndex == 0 || wheelIndex == 0 || etMyKakaoID2.getText().toString().equals("")
                || !(edtFBPw.getText().toString().equals(edtFBPwCheck.getText().toString()))) {
            // 이메일 중복 조회 안했고 공란이고 라디오 버튼 체크 안하고 카톡 아이디 안적었을 때 + 비밀번호 일치 안할 때
        } else {
            sendKakaoUserInfoToServer(tvMyFBEmailID.getText().toString(), edtFBPw.getText().toString(), userIndex, wheelIndex, etMyKakaoID2.getText().toString());
        }
    }

    private void sendKakaoUserInfoToServer(String email, String password, int userIndex, int wheelIndex, final String kakaoId) {
        Network network = Network.getNetworkInstance();
        SharedPrefStorage prefStorage = new SharedPrefStorage(this);
        String facebookToken = prefStorage.getUserTokenFromPref("facebook_token");

        Log.i(TAG,"토큰 확인해보자! : (FB)" + facebookToken);
        network.getUserProxy().sendUserProfileToServer(email, password, facebookToken, kakaoId, String.valueOf(userIndex), String.valueOf(wheelIndex), myProfileLink, new Callback<UserInfo>() {
            @Override
            public void onResponse(retrofit2.Call<UserInfo> call, Response<UserInfo> response) {
                if (response.isSuccessful()) {
                    Log.i(TAG, "facebook userInfo DB 저장 완료 : token : " + response.body().getToken());
                    startActivity(new Intent(MemberJoinFB.this, HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                            .putExtra("login_type","facebook")
                            .putExtra("kakaoId",kakaoId));
                    finish();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<UserInfo> call, Throwable t) {
                Log.e(TAG, t.toString());
            }
        });
    }

    private void checkRegexEmail() {

        if (tvMyFBEmailID.getText().toString().equals("") || !(RegexUtil.validateEmail(tvMyFBEmailID.getText().toString()))) {
            Toast.makeText(MemberJoinFB.this, "이메일 양식에 맞지 않습니다.", Toast.LENGTH_LONG).show();
            fbEmailCheck = false;
        } else {
            Toast.makeText(MemberJoinFB.this, "사용 가능한 이메일입니다.", Toast.LENGTH_LONG).show();
            fbEmailCheck = true;
        }
    }
}