package net.teamcadi.angelbrowser.Activity_Front.data;

/**
 * Created by haams on 2017-12-30.
 */

public class ElevatorInfoData {
    private String num;
    private String elvName;
    private String elvCoverage;
    private String elvLocation;

    public ElevatorInfoData(String elvName, String elvCoverage, String elvLocation) {
        this.elvName = elvName;
        this.elvCoverage = elvCoverage;
        this.elvLocation = elvLocation;
    }

    public ElevatorInfoData(String num, String elvName, String elvCoverage, String elvLocation) {
        this.num = num;
        this.elvName = elvName;
        this.elvCoverage = elvCoverage;
        this.elvLocation = elvLocation;
    }

    public String getNum() {
        return num;
    }

    public String getElvName() {
        return elvName;
    }

    public String getElvCoverage() {
        return elvCoverage;
    }

    public String getElvLocation() {
        return elvLocation;
    }
}
