package net.teamcadi.angelbrowser.Activity_Front.data;

/**
 * Created by haams on 2018-03-05.
 */

public class TransferSubName {
    private String transSubName;
    private int transSubId;

    public TransferSubName(String transSubName, int transSubId) {
        this.transSubName = transSubName;
        this.transSubId = transSubId;
    }

    public TransferSubName(String transSubName) {
        this.transSubName = transSubName;
    }

    public String getTransSubName() {
        return transSubName;
    }

    public int getTransSubId() {
        return transSubId;
    }
}
