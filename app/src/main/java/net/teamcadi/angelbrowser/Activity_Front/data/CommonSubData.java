package net.teamcadi.angelbrowser.Activity_Front.data;

/**
 * Created by haams on 2018-01-19.
 */

public class CommonSubData {
    private String subname;
    private String subline;
    private String location;
    private String title;
    private int count;


    public CommonSubData(String subname, String subline, String location) {
        this.subname = subname;
        this.subline = subline;
        this.location = location;
    }

    public CommonSubData(String subname, String subline, String location, int count) {
        this.subname = subname;
        this.subline = subline;
        this.location = location;
        this.count = count;
    }

    public String getLocation() {
        return location;
    }

    public String getSubname() {
        return subname;
    }

    public String getSubline() {
        return subline;
    }

    public String getTitle() {
        return title;
    }

    public int getCount() {
        return count;
    }
}
