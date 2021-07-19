package net.teamcadi.angelbrowser.Activity_Front;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import net.teamcadi.angelbrowser.Activity_Back.data.Elevator;
import net.teamcadi.angelbrowser.Activity_Back.data.ElevatorPic;
import net.teamcadi.angelbrowser.Activity_Back.service.Network;
import net.teamcadi.angelbrowser.Activity_Front.data.ElevPicData;
import net.teamcadi.angelbrowser.Activity_Front.data.ElevatorInfoData;
import net.teamcadi.angelbrowser.Adapters.ElevGridAdapter;
import net.teamcadi.angelbrowser.Adapters.ElevatorInfoAdapter;
import net.teamcadi.angelbrowser.R;
import net.teamcadi.angelbrowser.SharedPref.SharedPrefStorage;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ElevatorActivity extends AppCompatActivity implements View.OnClickListener {

    private String mapUrl;
    private static final String TAG = "ElevatorActivity";
    private ListView elevatorListView;
    private ElevatorInfoAdapter elvAdapter;
    private ArrayList<ElevatorInfoData> elvDataList;
    private ImageView btnElevRealPic;
    private Network network;
    private static final String serverUrl = "http://13.125.93.27:2721";
    private ArrayList<ElevPicData> elevPicList; // GridView에 들어갈 사진 데이터 리스트
    private static final int MAX = 100; // 배열 크기
    private ElevGridAdapter elvGridAdapter;
    private static String statNm = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elevator);

        initViews();
        initListView();
    }

    private void initListView() {
        elevatorListView = (ListView) findViewById(R.id.elevator_info_listView);
        elvDataList = new ArrayList<>();

        network = Network.getNetworkInstance();
        Log.e(TAG, getIntent().getStringExtra("statNm"));
// 엘레베이터 이름, 이용범위 , 위치 정보 불러오기
        network.getChatProxy().receiveElevatorInfoFromServer(getIntent().getStringExtra("statNm"), new Callback<List<Elevator>>() {
            @Override
            public void onResponse(Call<List<Elevator>> call, Response<List<Elevator>> response) {
                if (response.isSuccessful()) {
                    for (int i = 0; i < response.body().size(); i++) {
                        Log.i(TAG, response.body().get(i).getCoverage() + "// " + response.body().get(i).getLocation());
                        elvDataList.add(new ElevatorInfoData(String.valueOf((i + 1)), "[이름] \n" + response.body().get(i).getName(), "[이용 범위] \n" + response.body().get(i).getCoverage()
                                , "[엘레베이터 위치] \n " + response.body().get(i).getLocation()));
                    }


                    elvAdapter = new ElevatorInfoAdapter(elvDataList, ElevatorActivity.this); // 어댑터 생성
                    elevatorListView.setAdapter(elvAdapter);
                }
            }

            @Override
            public void onFailure(Call<List<Elevator>> call, Throwable t) {
                Log.e(TAG, t.toString());
            }
        });
    }

    private void initViews() {

        mapUrl = getIntent().getExtras().getString("mapUrl");
        Log.e(TAG, "Map" + mapUrl);
        findViewById(R.id.btn_back_elevator_info).setOnClickListener(this); // 뒤로 가기 버튼

        network = Network.getNetworkInstance();

        // 이미지가 있을 경우
        if (mapUrl != null) {
            // 지도가지고 역 이름 가지고오기 .. 채팅 봇 구조 때문에 디비를 한 번 더 거치는 번거로움이 발생..
            network.getSubwayProxy().getStatNmByElevImage(mapUrl, new Callback<List<Elevator>>() {
                @Override
                public void onResponse(Call<List<Elevator>> call, Response<List<Elevator>> response) {
                    if (response.isSuccessful()) {
                        statNm = response.body().get(0).getStation();
                        Log.i(TAG, "역 이름: " + statNm);
                    }
                }

                @Override
                public void onFailure(Call<List<Elevator>> call, Throwable t) {

                }
            });

            btnElevRealPic = (ImageView) findViewById(R.id.btn_Elevator_Pic);
            btnElevRealPic.setOnClickListener(this);

            Glide.with(this).asBitmap()
                    .load(mapUrl).into((ImageView) findViewById(R.id.ev_image));
        }
        // mapUrl이 없을 경우 >> null
        else {
            // 이미지 준비중 사진 넣어야함 
            Glide.with(this).asBitmap()
                    .load(R.drawable.ic_dialog_close_dark).into((ImageView) findViewById(R.id.ev_image));
        }

        findViewById(R.id.ev_image).setOnClickListener(this);

        elevPicList = new ArrayList<>();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back_elevator_info:
                finish();
                break;

            case R.id.btn_Elevator_Pic:
                showRealPicForElevator();
                break;

            case R.id.ev_image:
                sendPhotoViewer();
                break;
        }
    }

    private void sendPhotoViewer() {
        Intent pvIntent = new Intent(this, PhotoViewActivity.class);
        pvIntent.putExtra("elevPic", mapUrl);
        pvIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(pvIntent);
    }

    private void showRealPicForElevator() {
        final AlertDialog.Builder dlg = new AlertDialog.Builder(this);
        View eleView = LayoutInflater.from(this).inflate(R.layout.elevator_real_pic_items, null);

        dlg.setView(eleView);
        final GridView elevGridView = (GridView) eleView.findViewById(R.id.elev_GridView);

        // 엘레베이터 페이지로 들어오면 statNm을 키로한 역정보 내용을 SharedPreference에 넣어둔다.
        // 이 내용을 가지고와서 해당 내용들을 불러온다.

        final String[][] photos = {new String[MAX]}; // 최대 100개
        final String[] photoUrl = {""};
        network.getSubwayProxy().getElevPicsFromServer(statNm, new Callback<List<ElevatorPic>>() {
            @Override
            public void onResponse(Call<List<ElevatorPic>> call, Response<List<ElevatorPic>> response) {
                if (response.isSuccessful()) {
                    elevPicList.clear();
                    elevGridView.setAdapter(null);

                    for (int i = 0; i < response.body().size(); i++) {
                        Log.i(TAG, response.body().get(i).getPhoto() + " // " + response.body().get(i).getTitle());
                        photos[0] = response.body().get(i).getPhoto().split("/");
                        photoUrl[0] = serverUrl + "/" + photos[0][5] + "/" + photos[0][6];
                        elevPicList.add(i, new ElevPicData(photoUrl[0], response.body().get(i).getTitle()));
                        Log.i(TAG, elevPicList.get(i).getPhoto().toString());
                    }
                    if (response.body().size() != 0) {
                        elvGridAdapter = new ElevGridAdapter(elevPicList, ElevatorActivity.this);
                        elevGridView.setAdapter(elvGridAdapter);
                        dlg.show();
                    } else {
                        elevDataClearPopUp();
//                        Toast.makeText(ElevatorActivity.this, "아직 제보에 따른 지하철 실사 사진이 없습니다. 정보가 업로드 되는 대로 빠르게 수정하겠습니다.", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<ElevatorPic>> call, Throwable t) {
                Log.e(TAG, t.toString());
            }
        });
    }

    private void elevDataClearPopUp() {
        AlertDialog.Builder dlg2 = new AlertDialog.Builder(this);
        View dataClearView = LayoutInflater.from(this).inflate(R.layout.elev_data_clear_items, null);
        dlg2.setView(dataClearView);
        dlg2.show();
    }
}
