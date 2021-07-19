package net.teamcadi.angelbrowser.Activity_Back.database;

// 서버에 있는 MySQL을 제어하는 Thread 상속 클래스.
// Publisher : Jaey, Date : 2017.12.20.

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;

import net.teamcadi.angelbrowser.Activity_Front.MemberJoinKakao;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ControlMySQL extends Thread
{
    public static boolean active = false; // 활성화 상태를 담는 변수.

    String searchURL = null; // 아이디 검색을 위한 URL.
    String registerURL = null; // 회원 가입을 위한 URL.
    int getType = 0; // 처리 되는 키 값을 위한 변수.

    Handler mHandler = new Handler();

    // 아이디 중복확인 체크를 수행하는 메소드.
    public ControlMySQL(String id, int type)
    {
        String idCheckURL = "http://211.249.61.14/back/data/join_member.php?email_id=";
        String userInputID = id;

        // 입력 받은 type 값에 따라 동작 수행.
        switch (type)
        {
            case 2:
                searchURL = idCheckURL + userInputID;
                getType = 8;
                break;
        }
    }

    // 회원가입 동작을 수행하는 메소드.
    public ControlMySQL(String id) // 인자값으로 매개 변수를 던져주면 됨.
    {
        String userRegisterURL = "http://211.249.61.14/front/php/join_approval.php?email_id=";
        String userID = id;

        registerURL = userRegisterURL;
        getType = 3; // 회원가입 처리 세션으로 이동.
    }

    // Thread를 동작하여 서버 연결.
    @Override
    public void run()
    {
        super.run();

        // 동작이 활성화 된 경우.
        if(active)
        {
            StringBuilder jsonHtml = new StringBuilder();
            try
            {
                URL phpUrl = new URL(searchURL);

                HttpURLConnection conn = (HttpURLConnection)phpUrl.openConnection();

                if ( conn != null )
                {
                    conn.setConnectTimeout(10000);
                    conn.setUseCaches(false);

                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK)
                    {
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                        while ( true )
                        {
                            String line = br.readLine();
                            if ( line == null )
                                break;
                            jsonHtml.append(line + "\n");
                        }
                        br.close();
                    }
                    conn.disconnect();
                }
            }

            catch ( Exception e )
            {
                e.printStackTrace();
            }
            showResult(jsonHtml.toString());
        }
    }

    private void showResult(final String result)
    {
        mHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                switch(getType)
                {
                    case 3: // 회원가입.
                        //MemberJoinKakao.userRegisterKakao(result);
                        break;
                    case 8: // 아이디 중복 확인.
                        //MemberJoinKakao.checkInputIDResult(result);
                        break;
                    default:
                        break;
                }
            }
        });
    }
}
