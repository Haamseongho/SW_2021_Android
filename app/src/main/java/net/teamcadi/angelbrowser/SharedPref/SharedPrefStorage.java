package net.teamcadi.angelbrowser.SharedPref;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.Surface;

import java.util.Set;

/**
 * Created by haams on 2017-12-11.
 */

public class SharedPrefStorage {
    private Context context;

    public SharedPrefStorage(Context context) {
        this.context = context;
    }

    public void saveUserTokenInPref(String key, String value) {
        SharedPreferences pref = context.getSharedPreferences("token1", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value); // fbValue >> firebase token
        editor.commit();
    }

    // FireBase Token saved >> SharedPreference
    public void saveFBTokenInPref(String fbKey, String fbValue) {
        SharedPreferences pref = context.getSharedPreferences("fbToken", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(fbKey, fbValue); // fbValue >> firebase token
        editor.commit();
    }
    // 이름 저장
    public void saveNameInPref(String key,String value){
        SharedPreferences pref = context.getSharedPreferences("name", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void saveImageData(String imgKey, Surface surface) {
        SharedPreferences pref = context.getSharedPreferences("imgKey", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putStringSet(imgKey, (Set<String>) surface);
        editor.commit();
    }

    public void saveClientAnswer(String key, String value) {
        SharedPreferences pref = context.getSharedPreferences("mResult", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String getFBTokenByPref(String fbKey) {
        SharedPreferences pref = context.getSharedPreferences("fbToken", Context.MODE_PRIVATE);
        return pref.getString(fbKey, ""); // key값이 다를 경우 default로 빈 값
    }

    public String getClientResult(String key) {
        SharedPreferences pref = context.getSharedPreferences("mResult", Context.MODE_PRIVATE);
        return pref.getString(key, ""); // key값이 다를 경우 default로 빈 값
    }

    public void saveStatNmForElevator(String key, String value) {
        SharedPreferences pref = context.getSharedPreferences("statNm", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String getStatNmForElevator(String key) {
        SharedPreferences pref = context.getSharedPreferences("statNm", Context.MODE_PRIVATE);
        return pref.getString(key, "");
    }

    public String getUserTokenFromPref(String key) {
        SharedPreferences pref = context.getSharedPreferences("token1", Context.MODE_PRIVATE);
        return pref.getString(key, "");
    }

    // remove Token
    public void removeUserTokenFromPref(String key){
        SharedPreferences removePref = context.getSharedPreferences("token1",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = removePref.edit();
        editor.remove(key);
        editor.commit();
    }

    public String getNameFromPref(String key){
        SharedPreferences pref = context.getSharedPreferences("name", Context.MODE_PRIVATE);
        return pref.getString(key, "");
    }
}
