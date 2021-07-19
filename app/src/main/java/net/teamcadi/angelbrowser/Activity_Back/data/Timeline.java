package net.teamcadi.angelbrowser.Activity_Back.data;

/**
 * Created by haams on 2018-01-17.
 */

public class Timeline {
    private String statnNm; // 역 이름
    private String subwayId; // 역 호선 구분하기 위한 Id
    private String updnLine; // 상 하행 구분
    private String arvlMsg2; // 도착 메세지
    private String barvlDt; // 도착 전 몇 초 걸리는 지 체크
    private String btrainStts; // 열차 종류
    private String trainLineNm; // 종착역 >> ~ 행
    private String subwayHead; // 왼쪽 오른쪽 내리는 방향

    public Timeline(String statnNm, String subwayId, String updnLine, String arvlMsg2, String barvlDt, String btrainStts, String trainLineNm, String subwayHead) {
        this.statnNm = statnNm;
        this.subwayId = subwayId;
        this.updnLine = updnLine;
        this.arvlMsg2 = arvlMsg2;
        this.barvlDt = barvlDt;
        this.btrainStts = btrainStts;
        this.trainLineNm = trainLineNm;
        this.subwayHead = subwayHead;
    }

    public Timeline(String statnNm) {
        this.statnNm = statnNm;
    }

    public String getStatnNm() {
        return statnNm;
    }

    public String getSubwayId() {
        return subwayId;
    }

    public String getUpdnLine() {
        return updnLine;
    }

    public String getArvlMsg2() {
        return arvlMsg2;
    }

    public String getBarvlDt() {
        return barvlDt;
    }

    public String getBtrainStts() {
        return btrainStts;
    }

    public String getTrainLineNm() {
        return trainLineNm;
    }

    public String getSubwayHead() {
        return subwayHead;
    }
}
