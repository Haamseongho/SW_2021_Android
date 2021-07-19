package net.teamcadi.angelbrowser.Activity_Back.data;

/**
 * Created by haams on 2017-12-31.
 */
// 지하철 이용 > 경로 (출발경로 >> 도착경로)
public class SubwayRoute {
    private String shtStatnNm; // 출발역부터 도착역까지 거쳐가는 지하철 역 보여주기 (최단경로)
    private String shtTransferCnt; // 최단경로로 이동하였을 때 거쳐가는 지하철 역 횟수
    private String shtTransferMsg; // 최단경로로 이동하였을 때 메세지
    private String shtTravelTm; // 최단경로로 갔을 때 걸리는 시간
    private String minTransferCnt; // 환승 횟수
    private String shtStatnId; // 경유하는 역을 구분 짓기 위해서 만드는 역 고유의 ID 값

    public SubwayRoute(String shtStatnNm, String shtTransferCnt, String shtTransferMsg, String shtTravelTm) {
        this.shtStatnNm = shtStatnNm;
        this.shtTransferCnt = shtTransferCnt;
        this.shtTransferMsg = shtTransferMsg;
        this.shtTravelTm = shtTravelTm;
    }

    public SubwayRoute(String shtStatnNm, String shtTransferCnt, String shtTransferMsg, String shtTravelTm, String minTransferCnt, String shtStatnId) {
        this.shtStatnNm = shtStatnNm;
        this.shtTransferCnt = shtTransferCnt;
        this.shtTransferMsg = shtTransferMsg;
        this.shtTravelTm = shtTravelTm;
        this.minTransferCnt = minTransferCnt;
        this.shtStatnId = shtStatnId;
    }

    public String getShtStatnId() {
        return shtStatnId;
    }

    public String getShtStatnNm() {
        return shtStatnNm;
    }

    public String getShtTransferCnt() {
        return shtTransferCnt;
    }

    public String getShtTransferMsg() {
        return shtTransferMsg;
    }

    public String getShtTravelTm() {
        return shtTravelTm;
    }

    public String getMinTransferCnt() {
        return minTransferCnt;
    }
}
