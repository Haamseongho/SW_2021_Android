package net.teamcadi.angelbrowser.Activity_Front;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import net.teamcadi.angelbrowser.Activity_Back.data.ElevLocation;
import net.teamcadi.angelbrowser.Activity_Back.service.Network;
import net.teamcadi.angelbrowser.Activity_Front.data.CommonSubData;
import net.teamcadi.angelbrowser.Adapters.CommonStationAdapter;
import net.teamcadi.angelbrowser.R;
import net.teamcadi.angelbrowser.SharedPref.SharedPrefStorage;
import net.teamcadi.angelbrowser.Sqlite.DBhelper;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommonStationActivity extends AppCompatActivity {

    private static final String TAG = CommonStationActivity.class.getSimpleName();
    private DBhelper dBhelper; // Sqlite 연결 >> 자주 가는 역 체크
    private SQLiteDatabase db;
    private RecyclerView comRecyclerView;
    private EditText edtCommonStatNm;
    private Button btnCommonStatNm;
    private Network network;
    private ArrayList<String> commonStatNmList;
    private ArrayList<String> commonSubLineList;
    private ArrayList<String> commonElevLocationList; // 엘레베이터 위치 리스트
    private ArrayList<CommonSubData> commonSubDataArrayList; // 자주 가는 역 데이터 넣을 리스트
    private static String stationName = null;
    private CommonStationAdapter commonStationAdapter; // 리사이클러뷰에 꼽을 어댑터
    private static int reportSize; // 제보 갯수
    private SharedPrefStorage sharedPrefStorage;

    String subName;
    String subLine;
    String subLocation;
    String fbToken; // 토큰 값으로 처음 설정한 거 가져오게 진행할 것 (자주가는 곳);
    int index = 0; // 자주가는 역 데이터 넣은 갯수


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_common_station);



        // DB 생성
        dBhelper = new DBhelper(this);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "자주가는 역을 추가합니다.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                insertSubwayName();
            }
        });

        findViewById(R.id.btn_common_station_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void initDB() {
        commonStatNmList = new ArrayList<>();
        commonSubLineList = new ArrayList<>();
        commonSubDataArrayList = new ArrayList<>();
        commonElevLocationList = new ArrayList<>();
        comRecyclerView = (RecyclerView) findViewById(R.id.common_subRecyclerView);
    }

    private void insertSubwayName() {
        final AlertDialog.Builder dlg = new AlertDialog.Builder(this);
        final View itemView = LayoutInflater.from(this).inflate(R.layout.common_subway_info, null);
        edtCommonStatNm = (EditText) itemView.findViewById(R.id.edt_common_statNm);
        btnCommonStatNm = (Button) itemView.findViewById(R.id.btn_common_statNm);
        Toast.makeText(getApplicationContext(), "역 이름을 공백없이 모두 적어주세요 (ex: 동대문역사문화공원역)", Toast.LENGTH_LONG).show();

        btnCommonStatNm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 역이 마지막에 들어가야 하는뎁.. 어떻게 하지
                Log.i(TAG, "눌림!");
                if ((Pattern.matches(".*[역]$", edtCommonStatNm.getText().toString()))) {

                    Snackbar.make(v, "자주가는 역이 등록되었습니다.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                    saveStationName(edtCommonStatNm.getText().toString());
                } else {
                    Snackbar.make(v, "역 이름 전체를 띄어쓰기 없이 적어주세요 예:) 동대문역사문화공원역", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                dlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        dialog.dismiss();
                    }
                });
            }
        });
        dlg.setView(itemView);
        dlg.show();
    }

    private void saveStationName(final String statNm) {

        Log.i(TAG, "역이름:" + statNm);
        int splitIndex = statNm.lastIndexOf("역");
        stationName = statNm.substring(0, splitIndex);
        Log.i(TAG, "역이름2:" + stationName);
        network = Network.getNetworkInstance();

        // 자주가는 역 입력 시 >> 제보 데이터 먼저 체크하면서 역이름이랑 역호선 가지고 오기
        // 위치는 이 후에 가져올 것
        network.getReportProxy().getCommonSubDataFromServer(stationName, new Callback<List<CommonSubData>>() {
            @Override
            public void onResponse(Call<List<CommonSubData>> call, Response<List<CommonSubData>> response) {
                if (response.isSuccessful()) {
                    for (int i = 0; i < response.body().size(); i++) {
                        Log.i(TAG, response.body().get(i).getSubline() + " // " + response.body().get(i).getLocation());
                        Log.i(TAG, response.body().get(i).getSubline());
                        commonSubLineList.add(response.body().get(i).getSubline());
                    }
                    reportSize = response.body().size();
                    getElevDataFromServer(commonSubLineList);
                }
            }

            @Override
            public void onFailure(Call<List<CommonSubData>> call, Throwable t) {
                Log.e(TAG, t.toString());
                Toast.makeText(CommonStationActivity.this, "역 호선이 없어요", Toast.LENGTH_LONG).show();
            }
        });

        // 역 이름으로 호선 가지고 오기 (최근꺼 -- 사실 별 의미 없음 같은 내용일테니)
        // EV_TOTAL_TAB 접근


    }

    private void getElevDataFromServer(final ArrayList<String> commonSubLineList) {

        final String[] subline = {null};
        final String[] location = {null};

        if (stationName == "총신대입구역") {
            stationName = "총신대입구(이수)역";
        } else if (stationName == "이수역") {
            stationName = "총신대입구(이수)역";
        }

        network.getReportProxy().getElevLocationFromServer(stationName, new Callback<List<ElevLocation>>() {
            @Override
            public void onResponse(Call<List<ElevLocation>> call, Response<List<ElevLocation>> response) {
                if (response.isSuccessful()) {
                    if (response.isSuccessful()) {
                        for (int i = 0; i < response.body().size(); i++) {
                            // ~번 출구 이런거만 뽑아오기 (출구가 나올 경우 다 뽑아오기)

                            if (Pattern.matches(".*(~지상)$", response.body().get(i).getCoverage())) {
                                // 여기서의 호선을 가지고 오기
                                commonElevLocationList.add(response.body().get(i).getLocation());
                                Log.i(TAG, "성공 : " + response.body().get(i).getCoverage() + "// " + response.body().get(i).getLocation() + "// " + response.body().get(i).getSubline());
                                subline[0] = response.body().get(i).getSubline();
                                location[0] = response.body().get(i).getLocation();
                            }
                        }
                        saveLocalSqlite(stationName, subline[0], location[0], commonElevLocationList);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<ElevLocation>> call, Throwable t) {
                Log.e(TAG, "엘레베이터 위치 뽑아오기 >> 역정보로");
                Log.e(TAG, t.toString());
            }
        });

        Log.i(TAG, "DB saved well");
    }

    private void saveLocalSqlite(String stationName, String subline, String location, ArrayList<String> commonElevLocationList) {

        db = dBhelper.getReadableDatabase();
        String sql = "SELECT * FROM reportTB WHERE subname = " + "'" + stationName + "'";

        Cursor chkCursor = db.rawQuery(sql, null);


        Log.i(TAG, "count" + String.valueOf(chkCursor.getCount()));
        Log.i(TAG, "column1 : " + chkCursor.getColumnName(0));
        Log.i(TAG, "column2 : " + chkCursor.getColumnName(1));
        Log.i(TAG, "column3 : " + chkCursor.getColumnName(2));
        Log.i(TAG, "column4 : " + chkCursor.getColumnName(3));
        Log.i(TAG, "column5 : " + chkCursor.getColumnName(4));


        if (chkCursor.getCount() != 0) {
            // db에 있다는 증거
            String updtSql = "UPDATE reportTB SET subname = " + "'" + stationName + "'," + "subline = " + "'" + subline + "'," +
                    "location = " + "'" + location + "'," + "count = " + reportSize + " WHERE subname = " + "'" + stationName + "'";

            Log.i(TAG, "업데이트 구간 : " + "UPDATE reportTB SET subname = " + "'" + stationName + "'," + "subline = " + "'" + subline + "'," +
                    "location = " + "'" + location + "'," + "count = " + reportSize + " WHERE subname = " + "'" + stationName + "'");
            try {
                db = dBhelper.getWritableDatabase();
                db.execSQL(updtSql);
            } catch (SQLiteException ex) {
                ex.printStackTrace();
            }
        }
        // DB에 없으면 insert
        else {
            try {
                Log.i(TAG, "insert gogo");
                if (subline == null && location == null) {
                    subline = " ";
                    location = " ";
                }
                String insSql = "INSERT INTO reportTB VALUES (null ,'" + fbToken + "','" + stationName + "','" + subline + "','" + location + "'," + reportSize + " );";
                db = dBhelper.getWritableDatabase();
                db.execSQL(insSql);
                Log.i(TAG, "Insert done");
            } catch (SQLException e) {
                Log.e(TAG, "error in inserting in DB :" + e.getMessage());
            }
        }

        setRecyclerViewData(subline, commonElevLocationList);

    }


    // 본격적으로 RecyclerView에 넣는 과정
    private void setRecyclerViewData(String subline, ArrayList<String> commonElevLocationList) {
        comRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        String sql = "SELECT * FROM reportTB WHERE subname = " + "'" + stationName + "'";
        db = dBhelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);


        while (cursor.moveToNext()) {
            Log.i(TAG, "subname4" + cursor.getString(2) + " / subline 4: " + cursor.getString(3) + " / location4 : " + cursor.getString(4));
            subName = cursor.getString(2);
            subLine = cursor.getString(3);
            subLocation = cursor.getString(4);
        }

        commonSubDataArrayList.add(new CommonSubData(stationName, subLine, subLocation, reportSize));
        commonStationAdapter = new CommonStationAdapter(commonSubDataArrayList, this);
        comRecyclerView.setAdapter(commonStationAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                commonStationAdapter.move(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                commonStationAdapter.removeAtPostion(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(comRecyclerView);


    }

    @Override
    protected void onStart() {

        // 두 차례 방문할 경우
        initDB();

        sharedPrefStorage = new SharedPrefStorage(this);
        fbToken = sharedPrefStorage.getFBTokenByPref("fbToken"); // firebase 토큰 값으로 데이터 가져올 건데 onCreate() 호출 순서가 onStart() 보다 빠르기 때문에 먼저 선언
        Log.i(TAG, "onStart() 호출" + fbToken);
        comRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        String selectSql = "SELECT * FROM reportTB WHERE name = '" + fbToken + "'";

        comRecyclerView.setAdapter(null);
        commonSubDataArrayList.clear();

        db = dBhelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectSql, null);

        if (cursor.getCount() == 0) {
            // Nothing to show

        }
        // fbToken 값으로 데이터를 가지고 있을 경우
        else {
            while (cursor.moveToNext()) {
                Log.i(TAG, "cursor234 : " + cursor.getString(1) + "/" + cursor.getString(2));
                Log.i(TAG, "cursor234 : " + cursor.getString(3) + "/" + cursor.getString(4));
                Log.i(TAG, cursor.getString(1) + "의 카운트 : " + reportSize);

                commonSubDataArrayList.add(new CommonSubData(cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getInt(5)));
                index++;
            }

            Log.i(TAG, "index : " + index);
            commonStationAdapter = new CommonStationAdapter(commonSubDataArrayList, this);
            comRecyclerView.setAdapter(commonStationAdapter);
        }
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    // SQLiteDB 먼저 찾기
    public class WorkerThread extends Thread {
        @Override
        public void run() {
            super.run();
        }
    }
}
