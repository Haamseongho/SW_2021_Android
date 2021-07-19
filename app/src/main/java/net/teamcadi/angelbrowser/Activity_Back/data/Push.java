package net.teamcadi.angelbrowser.Activity_Back.data;

/**
 * Created by haams on 2017-12-11.
 */

public class Push {
    private String fbToken;
    private int fbIndex; // 푸시 메시지를 보낼지 말지 정하는 인덱스 (0으로 가면 푸시 x / 1로가면 푸시o)

    public Push(String fbToken, int fbIndex) {
        this.fbToken = fbToken;
        this.fbIndex = fbIndex;
    }

    public String getFbToken() {
        return fbToken;
    }

    public int getFbIndex() {
        return fbIndex;
    }
}
