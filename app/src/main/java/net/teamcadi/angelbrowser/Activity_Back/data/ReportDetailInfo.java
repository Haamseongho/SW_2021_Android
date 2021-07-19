package net.teamcadi.angelbrowser.Activity_Back.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.MultipartBody;

/**
 * Created by haams on 2018-01-07.
 */

public class ReportDetailInfo {
    private String title;
    private String name;
    private Date time;
    private String contents;
    private ArrayList<photo> photo;
    private ArrayList<photo> photo2;
    private ArrayList<photo> photo3;

    public ReportDetailInfo(String title, String name, Date time, ArrayList<net.teamcadi.angelbrowser.Activity_Back.data.photo> photo, ArrayList<net.teamcadi.angelbrowser.Activity_Back.data.photo> photo2, ArrayList<net.teamcadi.angelbrowser.Activity_Back.data.photo> photo3) {
        this.title = title;
        this.name = name;
        this.time = time;
        this.photo = photo;
        this.photo2 = photo2;
        this.photo3 = photo3;
    }

    public ReportDetailInfo(String title, String name, Date time, String contents, ArrayList<net.teamcadi.angelbrowser.Activity_Back.data.photo> photo, ArrayList<net.teamcadi.angelbrowser.Activity_Back.data.photo> photo2, ArrayList<net.teamcadi.angelbrowser.Activity_Back.data.photo> photo3) {
        this.title = title;
        this.name = name;
        this.time = time;
        this.contents = contents;
        this.photo = photo;
        this.photo2 = photo2;
        this.photo3 = photo3;
    }

    public String getContents() {
        return contents;
    }

    public ArrayList<net.teamcadi.angelbrowser.Activity_Back.data.photo> getPhoto() {
        return photo;
    }

    public ArrayList<net.teamcadi.angelbrowser.Activity_Back.data.photo> getPhoto2() {
        return photo2;
    }

    public ArrayList<net.teamcadi.angelbrowser.Activity_Back.data.photo> getPhoto3() {
        return photo3;
    }

    public String getTitle() {
        return title;
    }

    public String getName() {
        return name;
    }

    public Date getTime() {
        return time;
    }

}
