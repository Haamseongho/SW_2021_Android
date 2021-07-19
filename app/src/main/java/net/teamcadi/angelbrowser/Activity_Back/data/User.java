package net.teamcadi.angelbrowser.Activity_Back.data;

/**
 * Created by haams on 2017-12-11.
 */

public class User {
    private String token;
    private String name;
    private String sex;
    private String disabled;
    private String id;
    private String pw;
    private String kakaoID;
    private String wheelTAG;
    private String gpsYN;

    public User(String token, String name, String sex, String disabled, String id, String pw, String kakaoID, String wheelTAG, String gpsYN) {
        this.token = token;
        this.name = name;
        this.sex = sex;
        this.disabled = disabled;
        this.id = id;
        this.pw = pw;
        this.kakaoID = kakaoID;
        this.wheelTAG = wheelTAG;
        this.gpsYN = gpsYN;
    }

    public String getToken() {
        return token;
    }

    public String getName() {
        return name;
    }

    public String getSex() {
        return sex;
    }

    public String getDisabled() {
        return disabled;
    }

    public String getId() {
        return id;
    }

    public String getPw() {
        return pw;
    }

    public String getKakaoID() {
        return kakaoID;
    }

    public String getWheelTAG() {
        return wheelTAG;
    }

    public String getGpsYN() {
        return gpsYN;
    }
}
