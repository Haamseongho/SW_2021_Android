package net.teamcadi.angelbrowser.Activity_Back.data;

/**
 * Created by haams on 2017-12-28.
 */

public class Elevator {
    private String name;
    private String coverage;
    private String location;
    private String map;
    private String station;


    public Elevator(String name, String coverage, String location, String map) {
        this.name = name;
        this.coverage = coverage;
        this.location = location;
        this.map = map;
    }

    public Elevator(String coverage, String location) {
        this.coverage = coverage;
        this.location = location;
    }

    public Elevator(String coverage, String location, String map) {
        this.coverage = coverage;
        this.location = location;
        this.map = map;
    }

    public String getStation() {
        return station;
    }

    public String getMap() {
        return map;
    }

    public String getCoverage() {
        return coverage;
    }

    public String getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }
}
