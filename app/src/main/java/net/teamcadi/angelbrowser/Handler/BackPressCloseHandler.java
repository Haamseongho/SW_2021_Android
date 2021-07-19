package net.teamcadi.angelbrowser.Handler;

import android.app.Activity;
import android.widget.Toast;

// 뒤로 가기 종료 버튼 사용을 위한 Handler.
// Publisher : Jaey, Date : 2017.09.27.
public class BackPressCloseHandler
{
    private long backKeyPressedTime = 0;
    private Toast toast;

    private Activity activity;

    // 생성자.
    public BackPressCloseHandler(Activity context)
    {
        this.activity = context;
    }

    // 뒤로가기 버튼 클릭 시간에 따른 처리.
    public void onBackPressed()
    {
        if(System.currentTimeMillis() > backKeyPressedTime + 2000)
        {
            backKeyPressedTime = System.currentTimeMillis();
            showGuide();

            return;
        }

        if(System.currentTimeMillis() <= backKeyPressedTime + 2000)
        {
            activity.finish();
            toast.cancel();
        }
    }

    // 가이드 메시지 출력.
    public void showGuide()
    {
        toast = Toast.makeText(activity, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
        toast.show();
    }
}
