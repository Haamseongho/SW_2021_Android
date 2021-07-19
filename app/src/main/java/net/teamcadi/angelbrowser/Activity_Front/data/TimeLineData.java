package net.teamcadi.angelbrowser.Activity_Front.data;

/**
 * Created by haams on 2018-01-17.
 */

public class TimeLineData {
    private String arrivalTime; // 도착할 예정 시간 (몇 초 후)
    private String arrivalMsg; // 현재 위치 (메세지로 알려줌)
    private String arrivalStatNm; // 종착역 이름
    private String stationType; // 급행 , ITX 타입 정하기 : 기본은 null

    public TimeLineData(String arrivalTime, String arrivalMsg, String arrivalStatNm, String stationType) {
        this.arrivalTime = arrivalTime;
        this.arrivalMsg = arrivalMsg;
        this.arrivalStatNm = arrivalStatNm;
        this.stationType = stationType;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public String getArrivalMsg() {
        return arrivalMsg;
    }

    public String getArrivalStatNm() {
        return arrivalStatNm;
    }

    public String getStationType() {
        return stationType;
    }
}
