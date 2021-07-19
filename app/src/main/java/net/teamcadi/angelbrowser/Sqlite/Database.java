package net.teamcadi.angelbrowser.Sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by haams on 2018-01-19.
 */

public class Database {
    public static final String TAG = "Database";
    public static int DATABASE_VERSION = 1;
    public int USER_NUM = 0;
    private static Database database = null;
    private SQLiteDatabase db;
    private DBhelper dbHelper;
    private Context context;
    private String DB_NAME = "report.db";

    private Database(Context context) {
        this.context = context;
    }

    private Database(Context context, int USER_NUM) {
        this.context = context;
        this.USER_NUM = USER_NUM;
    }

    // 데이터베이스 사용을 위해 외부에서 사용하는 함수(유저 식별번호도 넘겨받아야함)
    public static Database getInstance(Context context, int USER_NUM) {
        if (database == null) {
            database = new Database(context, USER_NUM);
        }
        return database;
    }
    // 데이터베이스를 열어주는 함수


    // 데이터베이스를 닫는 함수
    public void close() {
        db.close();
        database = null;
    }

    // 커서를 리턴하는 함수
    public Cursor rawQuery(String SQL) {
        Cursor c1 = null;
        try {
            c1 = db.rawQuery(SQL, null);
        } catch (Exception ex) {
            Log.e(TAG, "Exception in excute-query", ex);
        }

        return c1;
    }

    // SQL문을 실행하는 함수
    public boolean execSQL(String SQL) {
        try {
            Log.d(TAG, "SQL : " + SQL);
            db.execSQL(SQL);
        } catch (Exception ex) {
            Log.e(TAG, "Exception in execute-query", ex);
            return false;
        }

        return true;
    }
}
