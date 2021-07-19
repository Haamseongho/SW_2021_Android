package net.teamcadi.angelbrowser.Activity_Back.data;

/**
 * Created by haams on 2018-01-09.
 */

public class photo {
    private String _id;
    private String size;
    private String filepath;
    private String filename;

    public photo(String _id, String size, String filepath, String filename) {
        this._id = _id;
        this.size = size;
        this.filepath = filepath;
        this.filename = filename;
    }

    public String get_id() {
        return _id;
    }

    public String getSize() {
        return size;
    }

    public String getFilepath() {
        return filepath;
    }

    public String getFilename() {
        return filename;
    }
}
