package net.teamcadi.angelbrowser.Sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by haams on 2018-01-19.
 */

public class DBhelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "subway_reports.db";
    private static final int DB_VERSION = 1;

    public DBhelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public DBhelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE reportTB (" +
                "_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "subname TEXT, " +
                "subline TEXT, " +
                "location TEXT, " +
                "count INTEGER );");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS reportTB");
        onCreate(db);
    }
}
