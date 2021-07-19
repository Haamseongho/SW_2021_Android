package net.teamcadi.angelbrowser.Firebase;

import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import net.teamcadi.angelbrowser.Activity_Front.HomeActivity;
import net.teamcadi.angelbrowser.R;

/**
 * Created by haams on 2017-07-27.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    // 푸시 메시지 전송 시 어느 액티비티에 뿌려줄 지 정리하는 부분

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
                //scheduleJob();
            } else {
                // Handle message within 10 seconds
                //handleNow();
            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
        showNotification(remoteMessage);
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    private void showNotification(RemoteMessage remoteMessage) {
        // 푸시 알림을 선택할 경우 HomeActivity.class로 이동
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        // requestCode를 1001로 보내어 HomeActivity에서 확인 가능함
        // 원격으로 보내진 메시지를 통해서 제목과 아이콘을 설정하여 푸시 알림이 보이도록 구현함
        // 우선순위를 최대로 준 다음 1초간 진동이 울리도록 구현
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1001, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"elevator_notification")
                .setSmallIcon(R.drawable.angel_logo)
                .setContentTitle(remoteMessage.getFrom())
                .setContentIntent(pendingIntent);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);
        builder.setVibrate(new long[]{0, 1000, 250, 1000});
        NotificationManagerCompat.from(this).notify(1001, builder.build());
    }
}
