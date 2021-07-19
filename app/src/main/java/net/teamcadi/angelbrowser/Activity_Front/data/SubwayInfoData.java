package net.teamcadi.angelbrowser.Activity_Front.data;

/**
 * Created by haams on 2017-12-26.
 */

public class SubwayInfoData {
    private String entrcNm;
    private String infraInfo;

    public SubwayInfoData(String entrcNm, String infraInfo) {
        this.entrcNm = entrcNm;
        this.infraInfo = infraInfo;
    }

    public SubwayInfoData(String entrcNm) {
        this.entrcNm = entrcNm;
    }

    public String getEntrcNm() {
        return entrcNm;
    }

    public String getInfraInfo() {
        return infraInfo;
    }

    public void setEntrcNm(String entrcNm) {
        this.entrcNm = entrcNm;
    }

    public void setInfraInfo(String infraInfo) {
        this.infraInfo = infraInfo;
    }
}
