package net.teamcadi.angelbrowser.Activity_Back.data;

/**
 * Created by haams on 2018-01-19.
 */

public class ElevLocation {
    private String station;
    private String subline;
    private String location;
    private String coverage;

    public ElevLocation(String station, String subline) {
        this.station = station;
        this.subline = subline;
    }

    public String getCoverage() {
        return coverage;
    }

    public String getStation() {
        return station;
    }

    public String getSubline() {
        return subline;
    }

    public String getLocation() {
        return location;
    }
}
