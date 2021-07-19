package net.teamcadi.angelbrowser.Activity_Back;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.kakao.auth.ErrorCode;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;

import net.teamcadi.angelbrowser.Activity_Front.PersonInfoAgree;
import net.teamcadi.angelbrowser.Activity_Front.SelectJoinType;

public class KakaoSignUp extends Activity
{
    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState); // 기존 Session 정보가 저장된 객체.
        requestMe();
    }

    // 유저의 정보를 받아오는 함수.
    protected void requestMe()
    {
        UserManagement.requestMe(new MeResponseCallback()
        {
            @Override
            public void onSessionClosed(ErrorResult errorResult)
            {
                redirectLoginActivity();
            }

            @Override
            public void onNotSignedUp()
            {
                showSignUp(); // 카카오톡 회원이 아닐 경우, 호출해서 처리하는 부분.
            }

            @Override
            // 성공시 userProfile 형태로 변환함.
            public void onSuccess(UserProfile result)
            {
                redirectKakaoSignAgreeActivity(); // 로그인 성공시 KakaoSignAgreeActivity로 이동함.
            }

            @Override
            public void onFailure(ErrorResult errorResult)
            {
                String errMsg = "유저 정보를 가져오는데 실패했습니다. 에러 내용 : " + errorResult;
                System.out.println(errMsg);

                ErrorCode result = ErrorCode.valueOf(errorResult.getErrorCode()); // 에러코드를 가져와서 저장.
                if(result == ErrorCode.CLIENT_ERROR_CODE)
                {
                    finish();
                }
                else
                {
                    redirectLoginActivity();
                }
            }
        });
    }

    // 카카오톡 회원이 아닐 경우, 호출해서 처리하는 부분.
    protected void showSignUp()
    {
        redirectKakaoSignAgreeActivity(); // 로그인 성공시 KakaoSignAgreeActivity로 이동함.
    }

    // 로그인 성공시 카카오용 정책 동의 화면으로 이동함.
    private void redirectKakaoSignAgreeActivity()
    {
        // 카카오에서 연동된 정보 값들을 가져옴.
        Intent profileIntent = getIntent();
        String profileLink = profileIntent.getExtras().getString("kakao_profile");

        // 정보를 실어서 정책 동의 화면으로 이동함.
        Intent SignUpToMain = new Intent(KakaoSignUp.this, PersonInfoAgree.class);
        SignUpToMain.putExtra("type", "kakao");
        SignUpToMain.putExtra("kakao_profile_picture", profileLink);
        startActivity(SignUpToMain);
        finish();
    }

    // 다시 로그인 화면으로 던지는 메소드.
    protected void redirectLoginActivity()
    {
        final Intent i = new Intent(KakaoSignUp.this, SelectJoinType.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(i);
        finish();
    }
}
