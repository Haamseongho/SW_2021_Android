package net.teamcadi.angelbrowser.Activity_Back.data;

import java.util.Date;

/**
 * Created by haams on 2018-01-06.
 */
// 백앤드에서 가지고 오는 것 (역정보 >> 제보 데이터)
public class ReportData {
    private String title;
    private String subname;
    private String subline;
    private String fbtoken;
    private String name;
    private Date time;

    public ReportData(String title, String name) {
        this.title = title;
        this.name = name;
    }

    public ReportData(String title, String subname, String subline, String fbtoken, String name) {
        this.title = title;
        this.subname = subname;
        this.subline = subline;
        this.fbtoken = fbtoken;
        this.name = name;
    }

    public Date getTime() {
        return time;
    }

    public String getTitle() {
        return title;
    }

    public String getSubname() {
        return subname;
    }

    public String getSubline() {
        return subline;
    }

    public String getFbtoken() {
        return fbtoken;
    }

    public String getName() {
        return name;
    }
}
