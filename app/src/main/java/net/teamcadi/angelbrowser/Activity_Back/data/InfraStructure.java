package net.teamcadi.angelbrowser.Activity_Back.data;

/**
 * Created by haams on 2017-12-31.
 */

public class InfraStructure {
    private String subline;
    private String station;
    private String address;
    private String admin_name; // 관계자 이름
    private String phone_number; // 안전센터 번호
    private int safety_footrest; // 안전발판
    private int auto_wheel_recharge; // 전동휠체어 급속충전
    private int disabled_toilet; // 장애인 화장실
    private int wheel_lift; // 휠체어 사용 여부

    public InfraStructure(String subline, String station) {
        this.subline = subline;
        this.station = station;
    }

    public InfraStructure(int safety_footrest, int auto_wheel_recharge, int disabled_toilet, int wheel_lift) {
        this.safety_footrest = safety_footrest;
        this.auto_wheel_recharge = auto_wheel_recharge;
        this.disabled_toilet = disabled_toilet;
        this.wheel_lift = wheel_lift;
    }

    public InfraStructure(String subline, String station, String address, String phone_number) {
        this.subline = subline;
        this.station = station;
        this.address = address;
        this.phone_number = phone_number;
    }

    public InfraStructure(String subline, String station, String address, String admin_name, String phone_number, int safety_footrest, int auto_wheel_recharge, int disabled_toilet, int wheel_lift) {
        this.subline = subline;
        this.station = station;
        this.address = address;
        this.admin_name = admin_name;
        this.phone_number = phone_number;
        this.safety_footrest = safety_footrest;
        this.auto_wheel_recharge = auto_wheel_recharge;
        this.disabled_toilet = disabled_toilet;
        this.wheel_lift = wheel_lift;
    }

    public String getAdmin_name() {
        return admin_name;
    }

    public String getSubline() {
        return subline;
    }

    public String getStation() {
        return station;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public int getSafety_footrest() {
        return safety_footrest;
    }

    public int getAuto_wheel_recharge() {
        return auto_wheel_recharge;
    }

    public int getDisabled_toilet() {
        return disabled_toilet;
    }

    public int getWheel_lift() {
        return wheel_lift;
    }
}
