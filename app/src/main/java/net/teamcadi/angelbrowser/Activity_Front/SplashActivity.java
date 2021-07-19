package net.teamcadi.angelbrowser.Activity_Front;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Bundle;
import android.view.Window;

import net.teamcadi.angelbrowser.R;

// 앱 실행시 제일 처음 뜨는 화면.
// Publisher : Jaey, Date : 2017.09.27.
public class SplashActivity extends Activity
{
    // 네트워크 관리 변수 선언.
    private boolean isInternetWiMax = false; // 4G망 상태 값 저장.
    private boolean isInternetWiFi = false; // WiFi망 상태 값 저장.
    private boolean isInternetMobile = false; // 3G망 상태 값 저장.

    private Handler controlHandler; // 연결 확인 메시지를 띄워줄 창을 제어할 Handler 객체 선언.

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.splash_activity);

        controlHandler = new Handler(); // 연결 확인 메시지를 띄워줄 창을 제어 해줄 Handler 객체 생성.

        // 연결 상태를 확인하기 위한 ConnectivityManager 객체 생성 및 선언.
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        // 연결 상태망 확인.
        // 3G, 4G, WiFi 등 정상적인 연결이 확인이 되었을 때.
        if (cm.getActiveNetworkInfo() != null)
        {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo(); // 현재 접속된 네트워크의 정보를 가지고 옴.

            // 4G로 연결 될 경우에 처리하는 구간.
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIMAX)
            {
                isInternetWiMax = true; // 4G 상태값 변화.
                goLoginType(); // 로그인 화면으로 이동함.
            }

            // WiFi로 연결 될 경우에 처리하는 구간.
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
            {
                isInternetWiFi = true; // WiFi 상태값 변화.
                goLoginType(); // 로그인 화면으로 이동함.
            }

            // 3G로 연결 될 경우에 처리하는 구간.
            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
            {
                isInternetMobile = true; // 3G 상태값 변화.
                goLoginType(); // 로그인 화면으로 이동함.
            }
        }

        // 연결이 정상적으로 되지 않았을 때.
        else
        {
            // 다이얼로그 메시지를 띄워서 네트워크 연결 되도록 함!
            AlertDialog.Builder errMsgBuilder = new AlertDialog.Builder(this); // AlertDialog 사용을 위한 Builder 생성.

            // AlertDialog 속성 설정.
            errMsgBuilder.setTitle("네트워크 연결 오류") // 제목 설정
                    .setPositiveButton("확인", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i)
                        {
                            android.os.Process.killProcess(android.os.Process.myPid()); // 액티비티 죽임.
                        }
                    });
            AlertDialog errMsgDialog = errMsgBuilder.create(); // 알림창 객체 생성.
            errMsgDialog.show(); // 알림창 띄우기.
        }
    }

    // 네트워크 연결이 정상적으로 되었을 때, 화면으로 이동함.
    // 기본적으로는 3초 이후에 SelectLoginType으로 이동함.
    private void goLoginType()
    {
        // UI 자원 제어를 위한 사용.
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                // Handler에 연결하여, 동작 수행.
                controlHandler.postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        try
                        {
                            // 로그인 선택 화면으로 이동함.
                            Intent SplashToType = new Intent(SplashActivity.this, SelectJoinType.class);
                            startActivity(SplashToType);
                            finish();
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }, 3000); // 3초 뒤에 화면 전환.
            }
        });
    }
}