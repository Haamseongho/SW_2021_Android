package net.teamcadi.angelbrowser.Activity_Front;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileManager;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.kakao.auth.AuthType;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.kakaotalk.KakaoTalkService;
import com.kakao.kakaotalk.callback.TalkResponseCallback;
import com.kakao.kakaotalk.response.KakaoTalkProfile;
import com.kakao.network.ErrorResult;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

import net.teamcadi.angelbrowser.Activity_Back.service.Network;
import net.teamcadi.angelbrowser.Firebase.MyFirebaseInstanceIDService;
import net.teamcadi.angelbrowser.R;
import net.teamcadi.angelbrowser.SharedPref.SharedPrefStorage;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class SelectJoinType extends Activity {
    private static final String TAG = SelectJoinType.class.getSimpleName();
    // 버튼 객체 생성.
    private Button btnFBJoin; // 페이스북으로 시작하기 버튼.

    // 카카오 연동 처리를 위한 부분.
    private SessionCallBack sessionCallBack; // 카카오 연동을 위한 콜백 선언
    private String myKakaoProfileLink; // 카카오톡 프로필 사진 URL 정보를 담을 String 객체.

    // 페이스북 연동 처리를 위한 부분.
    private Profile myFBProfile; // 프로필 정보를 담고 있는 객체.
    private String myFBProfileLink; // 프로필 사진 URL 정보를 담을 String 객체.
    private String myFBEmailID; // 프로필 이메일 정보를 담을 String 객체.
    private String myFBName; // 프로필 이름 정보를 담을 String 객체.
    private SharedPrefStorage prefStorage;
    private AccessToken fbAccessToken;  //Facebook AccessToken (트래킹)
    private URL facebookImgUrl; // 페이스북 프로필 이미지
    private MyFirebaseInstanceIDService myFirebaseInstanceIDService; // FireBase Token 뽑아내기 위함.
    private Network network; // 페이스북 토큰이 계속 바뀌기 때문에 먼저 서버 저장소에서 확인한 다음 서버 저장소에 없을 경우!! 새로 만들고 서버 저장소에 있을 경우 바로 로그인 !

    /*
    Session 관리
     */

    private CallbackManager callbackManager; // Facebook Session 관리 매니저

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //FacebookSdk.sdkInitialize(this);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(getApplication());
        setContentView(R.layout.select_join_type);

        initSessinCheck();

        network = Network.getNetworkInstance(); // 네트워크 연결 (페이스북 토큰 때문임)


        // 일반 회원가입 -> (일반 회원가입 Version) 개인정보 수집 동의 화면으로 이동.

        prefStorage = new SharedPrefStorage(this); // 토큰 저장용 (카카오,페이스북,일반계정)


        // 카카오톡, 페이스북 해시키 추출을 위한 구문.
        // 여기서 나오는 구문을 개발자 센터에다가 입력해주면 됨.
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);

            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                System.out.println("KEY : " + Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

        // 페이스북으로 시작하기 실행.
        btnFBJoin = (Button) this.findViewById(R.id.btnFBJoin);
        btnFBJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fbLoginClick(v);
            }
        });
    }

    private void initSessinCheck() {
        callbackManager = CallbackManager.Factory.create(); // 콜백 매니저 생성.

    }


    // 카카오톡 시작하기 버튼을 눌렀을 때 처리되는 부분.
    public void kakaoLoginClick(View v) {
        Log.e(TAG, "error check (KK) : " + prefStorage.getUserTokenFromPref("kakao_token"));
        if (!prefStorage.getUserTokenFromPref("kakao_token").equals("")) {
            Log.i(TAG, "kakao token1 " + prefStorage.getUserTokenFromPref("kakao_token"));
            startActivity(new Intent(this, HomeActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    .setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    .putExtra("login_type", "kakao"));
            finish();
        }

        // 토큰이 없을 땐 만들어주기
        else {
            try {
                PackageInfo info = getPackageManager().getPackageInfo(
                        this.getPackageName(),
                        PackageManager.GET_SIGNATURES
                );

                for (Signature signature : info.signatures) {
                    // 인증 후 서명 --> 암호화
                    MessageDigest md = null;
                    try {
                        md = MessageDigest.getInstance("SHA");
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                    md.update(signature.toByteArray());

                    // SHA 알고리즘으로 암호화 (Hashing 하기 전 Digest 메시지로 변환 //

                    Log.i("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
                }
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(TAG, "??");
                e.printStackTrace();
            }
            // 카카오 세션을 오픈함.
            sessionCallBack = new SessionCallBack();
            com.kakao.auth.Session.getCurrentSession().addCallback(sessionCallBack);
            com.kakao.auth.Session.getCurrentSession().checkAndImplicitOpen();

            Log.i(TAG, "정보확인 :" + com.kakao.auth.Session.getCurrentSession().checkAndImplicitOpen());
            com.kakao.auth.Session.getCurrentSession().open(AuthType.KAKAO_ACCOUNT, SelectJoinType.this);
            Log.i(TAG, "kakao Session : " + Session.getCurrentSession().getAccessToken());

        }
    }

     /*
    카카오톡 로그인
     */


    // 카카오 세션 연결 성공시 넘겨주는 부분.
    protected void redirectSignupActivity() {
        // 연동된 유저 정보를 던지는 부분.
        KakaoTalkService.requestProfile(new KakaoTalkResposeCallBack<KakaoTalkProfile>() {
            @Override
            public void onSuccess(KakaoTalkProfile myProfile) {
                // 카카오 세션 처리 후, 회원가입 화면으로 이동함. KakaoSignUp
                Intent i = new Intent(SelectJoinType.this, PersonInfoAgree.class);
                myKakaoProfileLink = myProfile.getThumbnailUrl(); // 현재 연동된 내 프로필 사진을 URL 형태로 저장.
                prefStorage.saveUserTokenInPref("kakao_token", Session.getCurrentSession().getRefreshToken());
                i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION); // 액티비티가 스크린에 등장할 때 사용하는 불필요한 애니메이션 효과 삭제.
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 액티비티 스택 구조에 쌓여버린거 다 종료.
                i.putExtra("type", "kakao");
                i.putExtra("kakao_profile", myKakaoProfileLink);
                i.putExtra("kakao_nickname", myProfile.getNickName());

                startActivity(i);
                finish();
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                // 에러 코드 구분.
                int errorCode = errorResult.getErrorCode();
                int clientErrorCode = -777;

                // 에러 메시지 출력.
                if (errorCode == clientErrorCode) {
                    Toast.makeText(getApplicationContext(), "카카오톡 서버의 네트워크가 불안정합니다. 관리자에게 문의해주세요!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "알 수 없는 오류로 카카오톡 연동이 불가능합니다. 관리자에게 문의해주세요!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(ErrorResult errorResult) {
                // 에러 코드 구분.
                int errorCode = errorResult.getErrorCode();
                int clientErrorCode = -777;

                // 에러 메시지 출력.
                if (errorCode == clientErrorCode) {
                    Toast.makeText(getApplicationContext(), "카카오톡 서버의 네트워크가 불안정합니다. 관리자에게 문의해주세요!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "알 수 없는 오류로 카카오톡 연동이 불가능합니다. 관리자에게 문의해주세요!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNotSignedUp() {
                super.onNotSignedUp();
            }
        });
    }

    // 페이스북 시작하기 버튼을 눌렀을 때 처리되는 부분.
    public void fbLoginClick(View v) {

        //AccessToken.getCurrentAccessToken().getPermissions();

        LoginManager.getInstance().logInWithReadPermissions(SelectJoinType.this, Arrays.asList("public_profile", "email"));
        myFBProfile = Profile.getCurrentProfile(); // 현재 로그인 된 유저의 프로필을 가져옴.

        Log.i(TAG,"facebook_token " + prefStorage.getUserTokenFromPref("facebook_token"));
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                GraphRequest request2;
                request2 = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(final JSONObject user, final GraphResponse response) {
                        if (response.getError() != null) {

                        } else {
                            try {
                                Log.i(TAG,"userId : " + user.getString("id"));
                                try {
                                    facebookImgUrl = new URL("https://graph.facebook.com/" + user.getString("id") + "/picture?height=300&width=300");
                                    Log.i(TAG,"facebookImageUrl : " + facebookImgUrl);
                                } catch (MalformedURLException e) {
                                    e.printStackTrace();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            // 토큰 값이 존재할 경우
                            if (!prefStorage.getUserTokenFromPref("facebook_token").equals("")) {

                                Log.i(TAG, "페이스북 유저 아이디 값: " + prefStorage.getUserTokenFromPref("facebook_token"));
                                startActivity(new Intent(SelectJoinType.this, HomeActivity.class)
                                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                        .setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                                        .putExtra("name", myFBName)
                                        .putExtra("login_type", "facebook"));
                                finish();

                            }

                            // 토큰 값이 존재 안할 경우
                            else {
                                Log.i(TAG,"facebook_user : " + user.toString());
                                Log.i(TAG, "user id : " + loginResult.getAccessToken().getUserId());
                                try {
                                    Log.e(TAG,"email : " + user.getString("email"));
                                    Log.e(TAG,"name : " + user.getString("name"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                setResult(RESULT_OK);
                                // 프로필 연동 값 저장 부분.
                                try {
                                    myFBEmailID = user.getString("email"); // 이메일 아이디 값 저장.
                                    myFBName = user.getString("name"); // 이름 값 저장.
                                    //myFBProfileLink = myFBProfile.getProfilePictureUri(300, 300).toString(); // 프로필 사진 링크를 따서, 사진 크기에 맞게 표시.
                                   // myFBProfileLink = facebookImgUrl;
                                    // 개인정보 동의서 화면으로 시작 경로 값을 보냄.
                                    Log.i(TAG,"facebookImageUrl22 : " + facebookImgUrl);
                                    Intent agreeIntent = new Intent(SelectJoinType.this, PersonInfoAgree.class);
                                    agreeIntent.putExtra("type", "facebook");
                                    agreeIntent.putExtra("profile", facebookImgUrl.toString());
                                    agreeIntent.putExtra("email", myFBEmailID);
                                    agreeIntent.putExtra("name", myFBName);
                                    prefStorage.saveUserTokenInPref("facebook_token", loginResult.getAccessToken().getUserId());

                                    agreeIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION); // 액티비티가 스크린에 등장할 때 사용하는 불필요한 애니메이션 효과 삭제.
                                    agreeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 액티비티 스택 구조에 쌓여버린거 다 종료.
                                    startActivity(agreeIntent);
                                    finish();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, name, email, gender, birthday");
                request2.setParameters(parameters);
                request2.executeAsync();
            }


            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), "Canceled!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException error) {
                if (error instanceof FacebookAuthorizationException) {
                    if (AccessToken.getCurrentAccessToken() != null) {
                        LoginManager.getInstance().logOut();
                    }
                   /* System.out.println("로그인 에러 코드 : " + error.toString());*/
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 페이스북 처리 부분.
        callbackManager.onActivityResult(requestCode, resultCode, data);
        // 카카오
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            Log.i(TAG + "111", Session.getCurrentSession() + "/" + requestCode + "/" + data + "입니다.");
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(sessionCallBack);
    }

    // 뒤로 가기 버튼 클릭 시.
    @Override
    public void onBackPressed() {
        // super.onBackPressed(); -> 일단은 주석 처리.
        // 종료 확인 메시지 실행.
        AlertDialog.Builder exitBuilder = new AlertDialog.Builder(this); // this = Activity의 this
        exitBuilder.setTitle("엔젤 브라우저 종료")
                .setMessage("엔젤 브라우저를 종료하시겠습니까?")
                .setPositiveButton("종료", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        android.os.Process.killProcess(android.os.Process.myPid()); // 완전히 종료.
                        finish();
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getApplicationContext(), "기존 회원가입된 내용은 삭제됩니다.", Toast.LENGTH_SHORT).show();
                    }
                });
        AlertDialog exitDialog = exitBuilder.create(); // 알림창 객체 생성.
        exitDialog.show();
    }

    // 카카오 최신 프로필 연동 처리를 위한 콜백 클래스.
    private abstract class KakaoTalkResposeCallBack<KakaoTalkProfile> extends TalkResponseCallback<KakaoTalkProfile> {
        @Override
        public void onNotKakaoTalkUser() {
            Toast.makeText(getApplicationContext(), "카카오톡 유저가 아닌 것으로 확인됩니다. 관리자에게 문의하세요!", Toast.LENGTH_SHORT).show(); // 에러 메시지 출력.
        }

        @Override
        public void onFailure(ErrorResult errorResult) {
            Toast.makeText(getApplicationContext(), "에러가 발생하였습니다. 관리자에게 문의하세요!" + errorResult, Toast.LENGTH_SHORT).show(); // 에러 메시지 출력.
        }

        @Override
        public void onSessionClosed(ErrorResult errorResult) {
        }

        @Override
        public void onNotSignedUp() {
        }
    }

    // 카카오 연동 세션을 처리하기 위한 콜백 클래스.
    private class SessionCallBack implements ISessionCallback {
        // 세션 연결 성공시 redirectSignupActivity() 호출.
        @Override
        public void onSessionOpened() {
            redirectSignupActivity();
        }

        // 세션 연결이 실패할 경우.
        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            if (exception != null) {
                Toast.makeText(getApplicationContext(), "error code : " + exception, Toast.LENGTH_LONG).show();
                System.out.println("error code : " + exception);
            }

            // 로그인 화면을 다시 불러옴.
            setContentView(R.layout.select_join_type);
        }
    }


    @Override
    protected void onStart() {

        super.onStart();
    }
}