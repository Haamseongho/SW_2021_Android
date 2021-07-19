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

import net.teamcadi.angelbrowser.Activity_Back.data.UserInfo;
import net.teamcadi.angelbrowser.Activity_Back.service.Network;
import net.teamcadi.angelbrowser.Pattern.RegexUtil;
import net.teamcadi.angelbrowser.R;
import net.teamcadi.angelbrowser.SharedPref.SharedPrefStorage;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MemberJoinKakao extends Activity implements View.OnClickListener {
    private static final String TAG = "MemberJoinKakao";
    // 연동되어 오는 값 저장 관련.
    private String myKakaoProfileLink; // 프로필 사진 경로를 담고 있는 링크.
    private Bitmap myKakaoProfileBitmap; // 프로필 사진을 담을 Bitmap 객체.
    private Handler myKakaoProfileHandler = new Handler(); // 외부 Thread에서 화면을 그리기 위한 용도로 사용.

    private EditText edtKakaoEmail; // 카카오 계정 연동해서 이메일 가져온 것
    private EditText edtKakaoPw; // 카카오 로그인 부분 비밀번호
    private EditText edtKakaoPwCheck; // 카카오 로그인 부분 비밀번호 확인
    private TextView txtKakaoName; // 카카오 계정 연동해서 이름 가져온 것
    private RadioButton rdGeneralUser; // 일반 이용자
    private RadioButton rdWheelUser; // 휠체어 이용자
    private RadioButton rdManual; // 수동 휠체어
    private RadioButton rdElectronic; // 전동 휠체어
    private RadioButton rbNothing; // 휠체어 사용 안함
    private TextView txtMyKakaoID; // 카카오 아이디
    private int rUserIndex = 0; // 일반 or 휠체어 이용자 1,2
    private int rbWheelIndex = 0; // 수동 휠체어 , 전동 휠체어 1,2
    private Button btnEmailRegCheck;
    private ImageView btnKakaoJoinOK; // 카카오 계정 가입완료 로그인
    private boolean emailChekcOk; // 이메일 체크 TF값
    private TextView edtMyKakaoID; // 카카오 아이디 입력
    private SharedPrefStorage prefStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.member_join_kakao);

        // Intent를 통해서 값을 가져옴.
        Intent KakaoReceiveIntent = getIntent();
        prefStorage = new SharedPrefStorage(this); // 카톡에 대한 토큰 지우기 위함.

        myKakaoProfileLink = KakaoReceiveIntent.getExtras().getString("my_kakao_profile_picture");
        Log.i(TAG, "카카오 프로필 이미지 : " + myKakaoProfileLink);
        // Thread를 사용하여 이미지를 가져옴.
        Thread kakaoProfileThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final ImageView ivMyKakaoProfile = (ImageView) findViewById(R.id.ivMyKakaoProfile);
                    URL myKakaoProfileURL = new URL(myKakaoProfileLink);
                    InputStream myKakaoProfileIS = myKakaoProfileURL.openStream();
                    myKakaoProfileBitmap = BitmapFactory.decodeStream(myKakaoProfileIS);

                    // 핸들러를 사용하여 이미지뷰(비트맵) 객체와 연동.
                    myKakaoProfileHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            ivMyKakaoProfile.setImageBitmap(myKakaoProfileBitmap); // 사진 반영.

                            // 사진 둥글게 처리.
                            ivMyKakaoProfile.setBackground(new ShapeDrawable(new OvalShape()));
                            ivMyKakaoProfile.setClipToOutline(true);
                        }
                    });
                    ivMyKakaoProfile.setImageBitmap(myKakaoProfileBitmap);


                    // 사진 둥글게 처리.
                    ivMyKakaoProfile.setBackground(new ShapeDrawable(new OvalShape()));
                    ivMyKakaoProfile.setClipToOutline(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        kakaoProfileThread.start(); // Thread 시작.

        initViews();
    }


    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent KKToSelect = new Intent(MemberJoinKakao.this, SelectJoinType.class);
        KKToSelect.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        KKToSelect.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        prefStorage.removeUserTokenFromPref("kakao_token");
        startActivity(KKToSelect);
        finish();
    }

    private void initViews() {
        edtKakaoEmail = (EditText) findViewById(R.id.tvMyKakaoEmailID);
        txtKakaoName = (TextView) findViewById(R.id.tvMyKakaoName);
        edtKakaoPw = (EditText) findViewById(R.id.etMyInputPW);
        edtKakaoPwCheck = (EditText) findViewById(R.id.etMyInputPWCheck);
        edtMyKakaoID = (EditText) findViewById(R.id.edtMyKakaoID); // 카카오 아이디 넣는 공란

        // 일반 , 휠체어 이용자
        rdManual = (RadioButton) findViewById(R.id.rbTypeGeneral);
        rdGeneralUser = (RadioButton) findViewById(R.id.rbWheelManual);
        // 수동, 전동 휠체어
        rdElectronic = (RadioButton) findViewById(R.id.rbWheelElectric);
        rdWheelUser = (RadioButton) findViewById(R.id.rbTypeWheel);

        // 휠체어 사용 안함
        rbNothing = (RadioButton) findViewById(R.id.rbNothing);

        btnEmailRegCheck = (Button) findViewById(R.id.btnKakaoEmailCheck);
        btnKakaoJoinOK = (ImageView) findViewById(R.id.btnKakaoJoinOK);


        rdManual.setOnClickListener(new RadioButtonClickListener());
        rdGeneralUser.setOnClickListener(new RadioButtonClickListener());
        rdElectronic.setOnClickListener(new RadioButtonClickListener());
        rdWheelUser.setOnClickListener(new RadioButtonClickListener());
        rbNothing.setOnClickListener(new RadioButtonClickListener());

        btnEmailRegCheck.setOnClickListener(this);
        btnKakaoJoinOK.setOnClickListener(this);


        initNamings();
    }

    private void initNamings() {
        txtKakaoName.setText(getIntent().getExtras().getString("my_kakao_nickname", null));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnKakaoEmailCheck:
                checkRegexEmail();
                break;

            case R.id.btnKakaoJoinOK:
                checkElementsAll();
                break;
        }
    }

    // 가입 정보 모두 체크하기
    private void checkElementsAll() {

        if (!emailChekcOk) {
            Toast.makeText(MemberJoinKakao.this, "이메일 중복 확인을 해주시기 바랍니다.", Toast.LENGTH_LONG).show();
        }

        if (rUserIndex == 0 || rbWheelIndex == 0) {
            Toast.makeText(MemberJoinKakao.this, "휠체어 사용여부와 본인 상태를 선택하여 주시기 바랍니다.", Toast.LENGTH_LONG).show();
        }

        if (edtMyKakaoID.getText().toString().equals("")) {
            Toast.makeText(MemberJoinKakao.this, "카카오 계정을 적어주시기 바랍니다.", Toast.LENGTH_LONG).show();
        }

        if (!(edtKakaoPw.getText().toString().equals(edtKakaoPwCheck.getText().toString()))) {
            Toast.makeText(MemberJoinKakao.this, "비밀번호가 서로 일치하지 않습니다.", Toast.LENGTH_LONG).show();

        }


        if (!emailChekcOk || rUserIndex == 0 || rbWheelIndex == 0 || edtMyKakaoID.getText().toString().equals("")
                || !(edtKakaoPw.getText().toString().equals(edtKakaoPwCheck.getText().toString()))) {
            // 이메일 중복 조회 안했고 공란이고 라디오 버튼 체크 안하고 카톡 아이디 안적었을 때 + 비밀번호 일치 안할 때
        } else {
            sendKakaoUserInfoToServer(edtKakaoEmail.getText().toString(), edtKakaoPw.getText().toString(), rUserIndex, rbWheelIndex, edtMyKakaoID.getText().toString());
        }
    }


    // 서버에서 바꿀 내용은 비밀번호 , 휠체어 수동 전동
    // 로컬 DB에서 수정할 것은 이미지 . 토큰
    // 서버에 전송
    private void sendKakaoUserInfoToServer(String email, String password, int rUserIndex, int rbWheelIndex, final String kakaoID) {
        SharedPrefStorage prefStorage = new SharedPrefStorage(this);
        String kkRefeshToken = prefStorage.getUserTokenFromPref("kakao_token");

        Log.i(TAG, "토큰 확인해보자! : (kk)" + kkRefeshToken);
// GET >> /user/info 에 저장하기
        Network network = Network.getNetworkInstance();
        network.getUserProxy().sendUserProfileToServer(email, password, kkRefeshToken, kakaoID, String.valueOf(rUserIndex), String.valueOf(rbWheelIndex), myKakaoProfileLink, new Callback<UserInfo>() {
            @Override
            public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
                if (response.isSuccessful()) {
                    Log.i(TAG, "userDB 저장 완료__ 토큰:" + response.body().getToken());
                    startActivity(new Intent(MemberJoinKakao.this, HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            .setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                            .putExtra("login_type", "kakao")
                            .putExtra("kakaoId",kakaoID));
                    finish();
                }
            }

            @Override
            public void onFailure(Call<UserInfo> call, Throwable t) {
                Log.e(TAG, t.toString());
            }
        });
    }

    private void checkRegexEmail() {
        if (edtKakaoEmail.getText().toString().equals("") || (!RegexUtil.validateEmail(edtKakaoEmail.getText().toString()))) {
            // 공란 이거나 이메일 양식아 안맞을 경우
            Toast.makeText(MemberJoinKakao.this, "이메일 양식에 맞게 다시 작성해주세요.", Toast.LENGTH_LONG).show();
            emailChekcOk = false;
        } else {
            Toast.makeText(MemberJoinKakao.this, "사용 가능한 이메일입니다.", Toast.LENGTH_LONG).show();
            emailChekcOk = true;
        }
    }

    private class RadioButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                // 일반
                case R.id.rbTypeGeneral:
                    rUserIndex = 1;  // 0 >> 일반
                    break;
                // 휠체어
                case R.id.rbTypeWheel:
                    rUserIndex = 2;  // 1 >> 휠체어 사용자
                    break;
                // 수동
                case R.id.rbWheelManual:
                    rbWheelIndex = 1; // 수동
                    break;
                // 전동
                case R.id.rbWheelElectric:
                    rbWheelIndex = 2; // 전동
                    break;
                // 사용안함 = 3;
                case R.id.rbNothing:
                    rbWheelIndex = 3;
                    break;

            }
        }
    }
}