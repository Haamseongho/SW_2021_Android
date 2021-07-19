package net.teamcadi.angelbrowser.Activity_Back.data;

/**
 * Created by haams on 2017-12-04.
 */

public class ChatMessage {
    private String statNm; // 역이름
    private String subwayNm; // 역 호선 이름
    private String entrcNm; // 출구 번호
    private String infraNm; // 주요 시설물

    public ChatMessage(String statNm, String subwayNm, String entrcNm, String infraNm) {
        this.statNm = statNm;
        this.subwayNm = subwayNm;
        this.entrcNm = entrcNm;
        this.infraNm = infraNm;
    }

    public String getStatNm() {
        return statNm;
    }

    public String getSubwayNm() {
        return subwayNm;
    }

    public String getEntrcNm() {
        return entrcNm;
    }

    public String getInfraNm() {
        return infraNm;
    }
}
