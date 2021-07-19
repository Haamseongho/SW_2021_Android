package net.teamcadi.angelbrowser.Activity_Back.data;

/**
 * Created by haams on 2018-01-22.
 */

public class UserInfo {
    private String userID;
    private String userPW;
    private String token;
    private String profile;
    private int userIndex;
    private int wheelIndex;
    private String kakaoId;

    public UserInfo(String userID, String userPW, String token, String profile, int userIndex, int wheelIndex, String kakaoId) {
        this.userID = userID;
        this.userPW = userPW;
        this.token = token;
        this.profile = profile;
        this.userIndex = userIndex;
        this.wheelIndex = wheelIndex;
        this.kakaoId = kakaoId;
    }

    public String getUserID() {
        return userID;
    }

    public String getUserPW() {
        return userPW;
    }

    public String getToken() {
        return token;
    }

    public String getProfile() {
        return profile;
    }

    public int getUserIndex() {
        return userIndex;
    }

    public int getWheelIndex() {
        return wheelIndex;
    }

    public String getKakaoId() {
        return kakaoId;
    }
}
