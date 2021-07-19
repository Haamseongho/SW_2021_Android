package net.teamcadi.angelbrowser.Activity_Back.service.proxy;

import net.teamcadi.angelbrowser.Activity_Back.data.User;
import net.teamcadi.angelbrowser.Activity_Back.data.UserInfo;
import net.teamcadi.angelbrowser.Activity_Back.service.Service;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by haams on 2017-12-11.
 */

public class UserProxy {
    private Service service;

    public UserProxy(Retrofit retrofit) {
        service = retrofit.create(Service.class);
    }

    public void sendUserInfoToServer(String token, String name, String sex, String disabled, String id, String pw, String kakaoID, String wheelTAG, String gpsYN, Callback<User> callback) {
        Call<User> call = service.sendUserInfoToServer(token, name, sex, disabled, id, pw, kakaoID, wheelTAG, gpsYN);
        call.enqueue(callback);
    }

    // 사용자 계정 서버에 전송
    public void sendUserProfileToServer(String userID, String userPW, String token, String kakaoId, String userIndex, String wheelIndex, String photo, Callback<UserInfo> callback) {
        Call<UserInfo> call = service.sendUserProfileToServer(userID, userPW, token, kakaoId, userIndex, wheelIndex, photo);
        call.enqueue(callback);
    }

    // token으로 사용자 프로필 가지고오기
    public void getUserProfileFromServer(String token, Callback<UserInfo> callback) {
        Call<UserInfo> call = service.getUserProfileFromServer(token);
        call.enqueue(callback);
    }

    // 일반 유저 회원가입
    public void sendGeneralUserToServer(RequestBody userID, RequestBody userPW, RequestBody token, RequestBody kakaoId, RequestBody userIndex, RequestBody wheelIndex, MultipartBody.Part profile, Callback<ResponseBody> callback) {
        Call<ResponseBody> call = service.sendGeneralUserToServer(userID, userPW, token, kakaoId, userIndex, wheelIndex, profile);
        call.enqueue(callback);
    }

    // 일반 유저 토큰 생기게하기
    public void getGenTokenFromServer(String userID, String kakaoId, Callback<UserInfo> callback) {
        Call<UserInfo> call = service.getGenTokenFromServer(userID, kakaoId);
        call.enqueue(callback);
    }

    public void getFacebookTokenFromServer(String fbtoken, Callback<UserInfo> callback) {
        Call<UserInfo> call = service.getFacebookTokenFromServer(fbtoken);
        call.enqueue(callback);
    }
}
