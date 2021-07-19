package net.teamcadi.angelbrowser.Firebase;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by haams on 2017-07-27.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = "firebaseID";

    // FireBase 토큰 값 refresh 된 것
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG,  " refreshToken = " + refreshToken);

    }
}
