package net.teamcadi.angelbrowser.Activity_Back.data;

/**
 * Created by haams on 2017-12-22.
 */
// 환승 정보 메세지
public class TransferMessage {
    private String statNm; // 지하철 역 이름
    private String subwayNm; // 지하철 노선 이름
    private String sDirection; // 환승 전 지하철 방향
    private String eDirection; // 환승 후 지하철 방향
    private String arriveTime; // 도착 시간 (지하철)
    private String leftTime; // 출발 시간 (지하철)
    private String subwayeNm; // 환승역에서 지하철이 가려는 방향 (예: 사당 >> 남태령)

    public TransferMessage(String statNm, String subwayNm, String sDirection, String eDirection, String arriveTime, String leftTime, String subwayeNm) {
        this.statNm = statNm;
        this.subwayNm = subwayNm;
        this.sDirection = sDirection;
        this.eDirection = eDirection;
        this.arriveTime = arriveTime;
        this.leftTime = leftTime;
        this.subwayeNm = subwayeNm;
    }

    public TransferMessage(String statNm, String subwayNm, String sDirection, String eDirection) {
        this.statNm = statNm;
        this.subwayNm = subwayNm;
        this.sDirection = sDirection;
        this.eDirection = eDirection;
    }

    public TransferMessage(String statNm, String subwayNm, String arriveTime, String leftTime, String subwayeNm) {
        this.statNm = statNm;
        this.subwayNm = subwayNm;
        this.arriveTime = arriveTime;
        this.leftTime = leftTime;
        this.subwayeNm = subwayeNm;
    }

    public String getStatNm() {
        return statNm;
    }

    public String getSubwayNm() {
        return subwayNm;
    }

    public String getsDirection() {
        return sDirection;
    }

    public String geteDirection() {
        return eDirection;
    }

    public String getArriveTime() {
        return arriveTime;
    }

    public String getLeftTime() {
        return leftTime;
    }

    public String getSubwayeNm() {
        return subwayeNm;
    }
}
