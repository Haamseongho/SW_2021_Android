package net.teamcadi.angelbrowser.Activity_Back.data;

/**
 * Created by haams on 2018-01-21.
 */

public class ClosestSubData {
    private String subname;
    private String subline;
    private String subline2;
    private String subline3;
    private String subline4;

    public ClosestSubData(String subname, String subline, String subline2) {
        this.subname = subname;
        this.subline = subline;
        this.subline2 = subline2;
    }

    public ClosestSubData(String subname, String subline, String subline2, String subline3) {
        this.subname = subname;
        this.subline = subline;
        this.subline2 = subline2;
        this.subline3 = subline3;
    }

    public ClosestSubData(String subname, String subline, String subline2, String subline3, String subline4) {
        this.subname = subname;
        this.subline = subline;
        this.subline2 = subline2;
        this.subline3 = subline3;
        this.subline4 = subline4;
    }

    public ClosestSubData(String subname, String subline) {
        this.subname = subname;
        this.subline = subline;
    }

    public String getSubline2() {
        return subline2;
    }

    public String getSubline3() {
        return subline3;
    }

    public String getSubline4() {
        return subline4;
    }

    public ClosestSubData(String subname) {
        this.subname = subname;
    }

    public String getSubname() {
        return subname;
    }

    public String getSubline() {
        return subline;
    }
}
