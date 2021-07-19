package net.teamcadi.angelbrowser.Activity_Front.data;

import android.widget.ImageView;

import java.util.Date;

/**
 * Created by haams on 2018-01-06.
 */

public class Report {
    private String rptName;
    private String rptTitle;
    private String rptContents;
    private ImageView rptImage;
    private String rptDate;


    public Report(String rptName, String rptTitle) {
        this.rptName = rptName;
        this.rptTitle = rptTitle;
    }

    public Report(String rptName, String rptTitle, String rptContents, ImageView rptImage) {
        this.rptName = rptName;
        this.rptTitle = rptTitle;
        this.rptContents = rptContents;
        this.rptImage = rptImage;
    }

    public Report(String rptName, String rptTitle, String rptDate) {
        this.rptName = rptName;
        this.rptTitle = rptTitle;
        this.rptDate = rptDate;
    }

    public String getRptDate() {
        return rptDate;
    }

    public String getRptName() {
        return rptName;
    }

    public String getRptTitle() {
        return rptTitle;
    }

    public String getRptContents() {
        return rptContents;
    }

    public ImageView getRptImage() {
        return rptImage;
    }
}
