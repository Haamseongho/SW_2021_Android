package net.teamcadi.angelbrowser.Activity_Back.data;

/**
 * Created by haams on 2018-01-14.
 */

public class ElevatorPic {
    private String subname;
    private String title;
    private String photo;
    private String time;

    public ElevatorPic(String subname, String title, String photo, String time) {
        this.subname = subname;
        this.title = title;
        this.photo = photo;
        this.time = time;
    }

    public String getSubname() {
        return subname;
    }

    public String getTitle() {
        return title;
    }

    public String getPhoto() {
        return photo;
    }

    public String getTime() {
        return time;
    }
}
