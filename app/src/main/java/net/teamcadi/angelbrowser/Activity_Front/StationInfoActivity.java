package net.teamcadi.angelbrowser.Activity_Front;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import net.teamcadi.angelbrowser.Activity_Back.data.ChatMessage;
import net.teamcadi.angelbrowser.Activity_Back.data.InfraStructure;
import net.teamcadi.angelbrowser.Activity_Back.data.ReportData;
import net.teamcadi.angelbrowser.Activity_Back.data.SubwayRoute;
import net.teamcadi.angelbrowser.Activity_Back.service.Network;
import net.teamcadi.angelbrowser.Activity_Front.data.Report;
import net.teamcadi.angelbrowser.Activity_Front.data.SubwayInfoData;
import net.teamcadi.angelbrowser.Activity_Front.data.TimeLineData;
import net.teamcadi.angelbrowser.Adapters.ReportListAdapter;
import net.teamcadi.angelbrowser.Adapters.SearchAdapter;
import net.teamcadi.angelbrowser.Adapters.TimeLineAdapter;
import net.teamcadi.angelbrowser.R;
import net.teamcadi.angelbrowser.SharedPref.SharedPrefStorage;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StationInfoActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener {

    private Intent gIntent; // 보낸 내용들 받는 인텐트
    // 호선 별 버튼
    private Button btnSubLine1, btnSubLine2, btnSubLine3, btnSubLine4;
    // 출발역 설정 , 도착역 설정 , 역 타임라인 확인
    private ImageView img_sub_departure, img_sub_arrival, img_sub_timeline;
    // 시설 관련된 텍스트뷰 정보들 (영어 그대로 읽으시면 됩니다.)
    private TextView txt_subway_wheel_lift, txt_disabled_toilet_usage, txt_subway_safety_footrest, txt_subway_auto_wheel_recharge, txt_station_name;
    // 역 별 안전센터 호출 번호
    private TextView txt_subway_help_call;
    // 서버 연결 네트워크
    private Network network;
    private SharedPrefStorage pref;
    private static final String TAG = StationInfoActivity.class.getSimpleName();

    private TextView txt_subway_help_name;  // 지하철 관계자 이름
    private TextView txt_subway_help_address;  // 지하철 안전센터 주소
    private ImageView subway_help_dial; // 관계자에게 바로 전화 걸기

    private ReportListAdapter reportListAdapter; // 실시간 제보 데이터 담을 어댑터
    private RecyclerView rptReCyclerView; // 실시간 제보 데이터 담을 뷰
    private ArrayList<Report> reportArrayList; // 제보 데이터 내용 들어갈 리스트


    private SearchAdapter searchAdapter; // 검색 어댑터 ( 팝업상자에서 보이는 리스트뷰의 어댑터 )
    private ArrayList<String> subwayLnList; // 지하철 노선 다 적어서 넣기
    private ArrayList<String> subLineList; // 지하철 필요한 노선(중복된 노선 없이) >> Spinner 쪽 ArrayAdapter에 넣을 예정
    ArrayList<SubwayInfoData> subwayInfoDataList; // 역 별 출구 정보 리스트
    private LinearLayout subTimeLineFrame1; // 上行 타임라인
    private LinearLayout subTimeLineFrame2; // 下行 타임라인

    private boolean checkDataInList = false; // 버튼 다른 것 눌렀을 때 리스트 비우기 위함
    private Geocoder geocoder;  // 주소 값 >> 위도 & 경도로 변환
    private GoogleMap mMap; // 지도 보여줄 예정
    private double lat, lng; // 위도 , 경도
    private static String statNm; // 지하철 역 이름
    private ArrayList<String> list; // 역 정보 검색 리스트
    private ArrayList<String> arrayList; // 역 정보 검색 리스트 복사용 리스트
    private EditText subFilterEdtxt; // 검색 필터링 적용 시 EditText
    private ListView subFilterLstView; // 검색 필터링 적용 시 ListView
    private HashMap<String, String> subwayNmMap; // 시작역 & 출발역 넣을 hashmap
    private boolean hashMapReset = false; // hashMap reset boolean
    private Intent transIntent;
    private Boolean reportReset = false; // 처음 시작은 리셋 안하기 (리사이클러뷰에 꼽을 때)
    private LinearLayout RecyclerViewFrame; // 현재 까지의 제보 보여주는 페이지


    private RecyclerView timeLineCycleView; // 타임라인 보여주는 리사이클러뷰
    private TextView timeLineArrivalSubName; // 타임라인 도착역(종착역 방향)
    private Spinner timeLineSubLineSpinner; // 타임라인 호선 스피너
    private Spinner timeLineUpDnSpinner; // 타임라인 상 하행 스피너
    private TextView timeLineStatnNm; // 타임라인에서 현재 역 이름
    private TimeLineAdapter timeLineAdapter; // 타임라인 어댑터 >> RecyclerView에 꼽을 어댑터
    private ArrayList<TimeLineData> timeLineDataList; // 타임라인 리사이클러뷰에 들어갈 데이터들
    private static final int MAX = 20;  // 라인 수 & 상하 배열에 넣고자 .. (기본적으로 얼마 많지 않기 때문에 20으로 그냥 둔 것)
    int idx = 0; // timeLineSubLineArray 인덱스 값
    private String mapLocation;
    String mResult;
    private String subname, subline; // 현재 위치에서 가까운 역 이름 이랑 호선 받기 위함.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_info);
        // 구글맵 동기화 & 비동기 함수 설정
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this); // 메인쓰레드에서 진행되어야 함 (워커쓰레드 X)

        initViews();

        initListView();
        setLocationOnTheMap(); // 지도 보여주기 >> lat,lng 체크
    }


    private void initViews() {
        // 최대 환승 구역이 4개 초과로 겹치는 일이 없다는 전제
        btnSubLine1 = (Button) findViewById(R.id.btn_subline1); // 호선 별 버튼1
        btnSubLine2 = (Button) findViewById(R.id.btn_subline2); // 호선 별 버튼2
        btnSubLine3 = (Button) findViewById(R.id.btn_subline3); // 호선 별 버튼3
        btnSubLine4 = (Button) findViewById(R.id.btn_subline4); // 호선 별 버튼3

        network = Network.getNetworkInstance(); // 서버 연동

        gIntent = getIntent();

        if (getIntent().getExtras().getString("statNm", null) != null)
            statNm = gIntent.getStringExtra("statNm");

        else if (getIntent().getExtras().getString("subname", null) != null)
            statNm = gIntent.getStringExtra("subname") + "역";

        pref = new SharedPrefStorage(this);
        mResult = pref.getClientResult("mResult"); // 첫 번째 질문에 대한 답변으로 보내는 부분 (Client >> 역정보)
        Log.i(TAG, "mResult : " + mResult);

        txt_station_name = (TextView) findViewById(R.id.station_here_name); // 현재 지하철 역 이름

        img_sub_departure = (ImageView) findViewById(R.id.subway_departure_info);  // 출발역 설정
        img_sub_arrival = (ImageView) findViewById(R.id.subway_arrival_info);  // 도착역 설정
        img_sub_timeline = (ImageView) findViewById(R.id.subway_timeline); // 시간표 확인

        img_sub_departure.setOnClickListener(this);
        img_sub_arrival.setOnClickListener(this);
        subwayNmMap = new HashMap<String, String>();
        img_sub_timeline.setOnClickListener(this); // 역 지나는 지하철 시간 조회

        txt_disabled_toilet_usage = (TextView) findViewById(R.id.txt_disabled_toilet_usage); // 장애인 화장실 사용 가능
        txt_subway_auto_wheel_recharge = (TextView) findViewById(R.id.txt_subway_auto_wheel_recharge); // 전동 휠체어 급속 충전 가능
        txt_subway_safety_footrest = (TextView) findViewById(R.id.txt_subway_safety_footrest); // 지하철 안전 발판 사용 가능
        txt_subway_wheel_lift = (TextView) findViewById(R.id.txt_subway_wheel_lift); // 휠체어 리프트 사용 가능
        txt_subway_help_call = (TextView) findViewById(R.id.txt_subway_help_call); // 안전센터 연락

        txt_subway_help_name = (TextView) findViewById(R.id.txt_subway_help_name); // 관계자 이름
        txt_subway_help_address = (TextView) findViewById(R.id.txt_subway_help_address); // 관계자 주소
        subway_help_dial = (ImageView) findViewById(R.id.subway_help_dial); // 관계자에게 바로 전화걸기

        /*subTimeLineFrame1 = (LinearLayout) findViewById(R.id.sub_timeline_frame1); // 상행 프레임
        subTimeLineFrame2 = (LinearLayout) findViewById(R.id.sub_timeline_frame2); // 하행 프레임
*/
        RecyclerViewFrame = (LinearLayout) findViewById(R.id.RecyclerView_Frame); // 현재 까지의 제보 보여주는 프레임

        rptReCyclerView = (RecyclerView) findViewById(R.id.rpt_RecyclerView);
        reportArrayList = new ArrayList<>(); // 제보 리스트
        subway_help_dial.setOnClickListener(this); // 전화걸기

        transIntent = new Intent(StationInfoActivity.this, TransferActivity.class);
        findViewById(R.id.btn_back_station_info).setOnClickListener(this); // 뒤로 가기


    }


    private void initListView() {
        //subwayInfoListView = (ListView) findViewById(R.id.subway_info_listView);

        final List<ChatMessage> chatList = new ArrayList<>();
        subwayLnList = new ArrayList<>();
        subLineList = new ArrayList<>();
        subwayInfoDataList = new ArrayList<>();

        Log.i(TAG, mResult + " 입니다..!!");

        network.getChatProxy().receiveMessageFromServer(mResult, new Callback<List<ChatMessage>>() {
            @Override
            public void onResponse(Call<List<ChatMessage>> call, Response<List<ChatMessage>> response) {
                if (response.isSuccessful()) {
                    for (int i = 0; i < response.body().size(); i++) {
                        // subwayLnList >> 호선 정보 리스트로 넣어 준 것
                        subwayLnList.add(response.body().get(i).getSubwayNm());
                    }
                    if (statNm != null) {
                        txt_station_name.setText(statNm); // 역 이름 등록
                    } else
                        txt_station_name.setText(response.body().get(0).getStatNm());

                    Log.i(TAG, "subwayLnList : " + subwayLnList.get(0) + "/" + subwayLnList.get(1));
                    setLineByBtnClick(subwayLnList);

                }
            }

            @Override
            public void onFailure(Call<List<ChatMessage>> call, Throwable t) {
                Log.e(TAG, t.toString());
            }
        });
    }

    private void setLineByBtnClick(final ArrayList<String> subwayLnList) {
        subLineList.add(subwayLnList.get(0)); // 초기 값은 넣어주어야 비교 분석이 가능합니다.
        btnSubLine1.setText(subLineList.get(0)); // 버튼 1에 호선 적어주기
        for (int i = 0; i < subwayLnList.size() - 1; i++) {
            if (subwayLnList.get(i).equals(subwayLnList.get(i + 1))) {
                // 앞 뒤로 계속 비교중.. 같을 경우 비교를 계속 할 것
                continue;
            }
            // 앞 뒤로 비교했을 때 다를 경우
            else {
                if (subLineList.contains(subwayLnList.get(i))) {
                    // 이미 값을 포함하고 있을 경우 나가리
                    // 별도로 마지막 값만 비교
                    // 포함하고 있지 않다면 넣어 둬!
                }
                // 값을 포함하고 있지 않을 경우 리스트에 넣어주기
                else {
                    subLineList.add(subwayLnList.get(i));
                }
            }
        }

        if (subLineList.contains(subwayLnList.get(subwayLnList.size() - 1))) {
            // nothing
        } else {
            subLineList.add(subwayLnList.get(subwayLnList.size() - 1));
        }

        // 버튼에 호선이 늘어가는 만큼 값 넣어주면서 보이게 하기
        if (subLineList.size() > 5) {
            btnSubLine2.setText(subLineList.get(1));
            btnSubLine3.setText(subLineList.get(2));
            btnSubLine4.setText(subLineList.get(3));
            // 버튼을 만들어야함 ..
        } else if (subLineList.size() == 4) {
            btnSubLine2.setText(subLineList.get(1));
            btnSubLine3.setText(subLineList.get(2));
            btnSubLine4.setText(subLineList.get(3));
            btnSubLine2.setVisibility(View.VISIBLE);
            btnSubLine3.setVisibility(View.VISIBLE);
            btnSubLine4.setVisibility(View.VISIBLE);

            switch (btnSubLine2.getText().toString()) {
                case "1호선":
                    btnSubLine2.setBackgroundResource(R.drawable.round_circle_line1);
                    break;
                case "2호선":
                    btnSubLine2.setBackgroundResource(R.drawable.round_circle_line2);
                    break;
                case "3호선":
                    btnSubLine2.setBackgroundResource(R.drawable.round_circle_line3);
                    break;
                case "4호선":
                    btnSubLine2.setBackgroundResource(R.drawable.round_circle_line4);
                    break;
                case "5호선":
                    btnSubLine2.setBackgroundResource(R.drawable.round_circle_line5);
                    break;
                case "6호선":
                    btnSubLine2.setBackgroundResource(R.drawable.round_circle_line6);
                    break;
                case "7호선":
                    btnSubLine2.setBackgroundResource(R.drawable.round_circle_line7);
                    break;
                case "8호선":
                    btnSubLine2.setBackgroundResource(R.drawable.round_circle_line8);
                    break;
                case "9호선":
                    btnSubLine2.setBackgroundResource(R.drawable.round_circle_line9);
                    break;
                case "경의중앙선":
                    btnSubLine2.setBackgroundResource(R.drawable.round_circle_line10);
                    break;
                case "신분당선":
                    btnSubLine2.setBackgroundResource(R.drawable.round_circle_line13);
                    break;
                case "공항철도":
                    btnSubLine2.setBackgroundResource(R.drawable.round_circle_line12);
                    break;
                case "분당선":
                    btnSubLine2.setBackgroundResource(R.drawable.round_circle_line11);
                    break;


            }

            switch (btnSubLine3.getText().toString()) {
                case "1호선":
                    btnSubLine3.setBackgroundResource(R.drawable.round_circle_line1);
                    break;
                case "2호선":
                    btnSubLine3.setBackgroundResource(R.drawable.round_circle_line2);
                    break;
                case "3호선":
                    btnSubLine3.setBackgroundResource(R.drawable.round_circle_line3);
                    break;
                case "4호선":
                    btnSubLine3.setBackgroundResource(R.drawable.round_circle_line4);
                    break;
                case "5호선":
                    btnSubLine3.setBackgroundResource(R.drawable.round_circle_line5);
                    break;
                case "6호선":
                    btnSubLine3.setBackgroundResource(R.drawable.round_circle_line6);
                    break;
                case "7호선":
                    btnSubLine3.setBackgroundResource(R.drawable.round_circle_line7);
                    break;
                case "8호선":
                    btnSubLine3.setBackgroundResource(R.drawable.round_circle_line8);
                    break;
                case "9호선":
                    btnSubLine3.setBackgroundResource(R.drawable.round_circle_line9);
                    break;
                case "경의중앙선":
                    btnSubLine3.setBackgroundResource(R.drawable.round_circle_line10);
                    break;
                case "신분당선":
                    btnSubLine3.setBackgroundResource(R.drawable.round_circle_line13);
                    break;
                case "공항철도":
                    btnSubLine3.setBackgroundResource(R.drawable.round_circle_line12);
                    break;
                case "분당선":
                    btnSubLine3.setBackgroundResource(R.drawable.round_circle_line11);
                    break;


            }

            switch (btnSubLine4.getText().toString()) {
                case "1호선":
                    btnSubLine4.setBackgroundResource(R.drawable.round_circle_line1);
                    break;
                case "2호선":
                    btnSubLine4.setBackgroundResource(R.drawable.round_circle_line2);
                    break;
                case "3호선":
                    btnSubLine4.setBackgroundResource(R.drawable.round_circle_line3);
                    break;
                case "4호선":
                    btnSubLine4.setBackgroundResource(R.drawable.round_circle_line4);
                    break;
                case "5호선":
                    btnSubLine4.setBackgroundResource(R.drawable.round_circle_line5);
                    break;
                case "6호선":
                    btnSubLine4.setBackgroundResource(R.drawable.round_circle_line6);
                    break;
                case "7호선":
                    btnSubLine4.setBackgroundResource(R.drawable.round_circle_line7);
                    break;
                case "8호선":
                    btnSubLine4.setBackgroundResource(R.drawable.round_circle_line8);
                    break;
                case "9호선":
                    btnSubLine4.setBackgroundResource(R.drawable.round_circle_line9);
                    break;
                case "경의중앙선":
                    btnSubLine4.setBackgroundResource(R.drawable.round_circle_line10);
                    break;
                case "신분당선":
                    btnSubLine4.setBackgroundResource(R.drawable.round_circle_line13);
                    break;
                case "공항철도":
                    btnSubLine4.setBackgroundResource(R.drawable.round_circle_line12);
                    break;
                case "분당선":
                    btnSubLine4.setBackgroundResource(R.drawable.round_circle_line11);
                    break;


            }
            // 정확한 숫자대로 맞춰짐
        } else if (subLineList.size() == 3) {
            btnSubLine2.setText(subLineList.get(1));
            btnSubLine3.setText(subLineList.get(2));
            btnSubLine2.setVisibility(View.VISIBLE);
            btnSubLine3.setVisibility(View.VISIBLE);

            switch (btnSubLine2.getText().toString()) {
                case "1호선":
                    btnSubLine2.setBackgroundResource(R.drawable.round_circle_line1);
                    break;
                case "2호선":
                    btnSubLine2.setBackgroundResource(R.drawable.round_circle_line2);
                    break;
                case "3호선":
                    btnSubLine2.setBackgroundResource(R.drawable.round_circle_line3);
                    break;
                case "4호선":
                    btnSubLine2.setBackgroundResource(R.drawable.round_circle_line4);
                    break;
                case "5호선":
                    btnSubLine2.setBackgroundResource(R.drawable.round_circle_line5);
                    break;
                case "6호선":
                    btnSubLine2.setBackgroundResource(R.drawable.round_circle_line6);
                    break;
                case "7호선":
                    btnSubLine2.setBackgroundResource(R.drawable.round_circle_line7);
                    break;
                case "8호선":
                    btnSubLine2.setBackgroundResource(R.drawable.round_circle_line8);
                    break;
                case "9호선":
                    btnSubLine2.setBackgroundResource(R.drawable.round_circle_line9);
                    break;
                case "경의중앙선":
                    btnSubLine2.setBackgroundResource(R.drawable.round_circle_line10);
                    break;
                case "신분당선":
                    btnSubLine2.setBackgroundResource(R.drawable.round_circle_line13);
                    break;
                case "공항철도":
                    btnSubLine2.setBackgroundResource(R.drawable.round_circle_line12);
                    break;
                case "분당선":
                    btnSubLine2.setBackgroundResource(R.drawable.round_circle_line11);
                    break;


            }

            switch (btnSubLine3.getText().toString()) {
                case "1호선":
                    btnSubLine3.setBackgroundResource(R.drawable.round_circle_line1);
                    break;
                case "2호선":
                    btnSubLine3.setBackgroundResource(R.drawable.round_circle_line2);
                    break;
                case "3호선":
                    btnSubLine3.setBackgroundResource(R.drawable.round_circle_line3);
                    break;
                case "4호선":
                    btnSubLine3.setBackgroundResource(R.drawable.round_circle_line4);
                    break;
                case "5호선":
                    btnSubLine3.setBackgroundResource(R.drawable.round_circle_line5);
                    break;
                case "6호선":
                    btnSubLine3.setBackgroundResource(R.drawable.round_circle_line6);
                    break;
                case "7호선":
                    btnSubLine3.setBackgroundResource(R.drawable.round_circle_line7);
                    break;
                case "8호선":
                    btnSubLine3.setBackgroundResource(R.drawable.round_circle_line8);
                    break;
                case "9호선":
                    btnSubLine3.setBackgroundResource(R.drawable.round_circle_line9);
                    break;
                case "경의중앙선":
                    btnSubLine3.setBackgroundResource(R.drawable.round_circle_line10);
                    break;
                case "신분당선":
                    btnSubLine3.setBackgroundResource(R.drawable.round_circle_line13);
                    break;
                case "공항철도":
                    btnSubLine3.setBackgroundResource(R.drawable.round_circle_line12);
                    break;
                case "분당선":
                    btnSubLine3.setBackgroundResource(R.drawable.round_circle_line11);
                    break;


            }
            // 말이 안됨 (역의 호선이 없다는 것임 ㅡㅡ..)
        } else if (subLineList.size() == 2) {
            btnSubLine2.setText(subLineList.get(1));
            btnSubLine2.setVisibility(View.VISIBLE);

            switch (btnSubLine2.getText().toString()) {
                case "1호선":
                    btnSubLine2.setBackgroundResource(R.drawable.round_circle_line1);
                    break;
                case "2호선":
                    btnSubLine2.setBackgroundResource(R.drawable.round_circle_line2);
                    break;
                case "3호선":
                    btnSubLine2.setBackgroundResource(R.drawable.round_circle_line3);
                    break;
                case "4호선":
                    btnSubLine2.setBackgroundResource(R.drawable.round_circle_line4);
                    break;
                case "5호선":
                    btnSubLine2.setBackgroundResource(R.drawable.round_circle_line5);
                    break;
                case "6호선":
                    btnSubLine2.setBackgroundResource(R.drawable.round_circle_line6);
                    break;
                case "7호선":
                    btnSubLine2.setBackgroundResource(R.drawable.round_circle_line7);
                    break;
                case "8호선":
                    btnSubLine2.setBackgroundResource(R.drawable.round_circle_line8);
                    break;
                case "9호선":
                    btnSubLine2.setBackgroundResource(R.drawable.round_circle_line9);
                    break;
                case "경의중앙선":
                    btnSubLine2.setBackgroundResource(R.drawable.round_circle_line10);
                    break;
                case "신분당선":
                    btnSubLine2.setBackgroundResource(R.drawable.round_circle_line13);
                    break;
                case "공항철도":
                    btnSubLine2.setBackgroundResource(R.drawable.round_circle_line12);
                    break;
                case "분당선":
                    btnSubLine2.setBackgroundResource(R.drawable.round_circle_line11);
                    break;


            }
        } else {
            // Nothing special
        }

        switch (btnSubLine1.getText().toString()) {
            case "1호선":
                btnSubLine1.setBackgroundResource(R.drawable.round_circle_line1);
                break;
            case "2호선":
                btnSubLine1.setBackgroundResource(R.drawable.round_circle_line2);
                break;
            case "3호선":
                btnSubLine1.setBackgroundResource(R.drawable.round_circle_line3);
                break;
            case "4호선":
                btnSubLine1.setBackgroundResource(R.drawable.round_circle_line4);
                break;
            case "5호선":
                btnSubLine1.setBackgroundResource(R.drawable.round_circle_line5);
                break;
            case "6호선":
                btnSubLine1.setBackgroundResource(R.drawable.round_circle_line6);
                break;
            case "7호선":
                btnSubLine1.setBackgroundResource(R.drawable.round_circle_line7);
                break;
            case "8호선":
                btnSubLine1.setBackgroundResource(R.drawable.round_circle_line8);
                break;
            case "9호선":
                btnSubLine1.setBackgroundResource(R.drawable.round_circle_line9);
                break;
            case "경의중앙선":
                btnSubLine1.setBackgroundResource(R.drawable.round_circle_line10);
                break;
            case "신분당선":
                btnSubLine1.setBackgroundResource(R.drawable.round_circle_line13);
                break;
            case "공항철도":
                btnSubLine1.setBackgroundResource(R.drawable.round_circle_line12);
                break;
            case "분당선":
                btnSubLine1.setBackgroundResource(R.drawable.round_circle_line11);
                break;


        }

        final String mResult = pref.getClientResult("mResult"); // 첫 번째 질문에 대한 답변으로 보내는 부분 (Client >> 역정보)
        // 첫 번째 호선 버튼 눌렀을 경우
        btnSubLine1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RecyclerViewFrame.setVisibility(View.VISIBLE);
// 초기에는 다 안보이게 시작
                txt_disabled_toilet_usage.setVisibility(View.INVISIBLE);
                txt_subway_wheel_lift.setVisibility(View.INVISIBLE);
                txt_subway_auto_wheel_recharge.setVisibility(View.INVISIBLE);
                txt_subway_safety_footrest.setVisibility(View.INVISIBLE);
                txt_subway_help_call.setText("");
                txt_subway_help_name.setText("");
                txt_subway_help_address.setText("");

                if (reportReset) {
                    reportArrayList.clear();
                    rptReCyclerView.setAdapter(null); // 리셋하기
                    reportReset = false;
                }

                Log.i(TAG, "statNm : " + txt_station_name.getText().toString() + "//" + "subline : " + btnSubLine1.getText().toString());
                // 시설 정보도 보내기 위함 (station 이름 + subline 호선)
                network.getSubwayProxy().getInfraInfoBySubwayInfo(txt_station_name.getText().toString(), btnSubLine1.getText().toString(), new Callback<List<InfraStructure>>() {
                    @Override
                    public void onResponse(Call<List<InfraStructure>> call, Response<List<InfraStructure>> response) {
                        if (response.isSuccessful()) {


                            if (response.body().size() > 0) {
                                // 시설물 관련되어서 DB거쳐서 가지고 왔을 때 한 개라도 있으면 사용 가능으로 표시함
                                if (response.body().get(0).getDisabled_toilet() > 0) {
                                    txt_disabled_toilet_usage.setVisibility(View.VISIBLE);
                                } else {
                                    txt_disabled_toilet_usage.setVisibility(View.INVISIBLE);
                                }


                                if (response.body().get(0).getWheel_lift() > 0) {
                                    txt_subway_wheel_lift.setVisibility(View.VISIBLE);
                                } else {
                                    txt_subway_wheel_lift.setVisibility(View.INVISIBLE);
                                }


                                if (response.body().get(0).getAuto_wheel_recharge() > 0) {
                                    txt_subway_auto_wheel_recharge.setVisibility(View.VISIBLE);
                                } else {
                                    txt_subway_auto_wheel_recharge.setVisibility(View.INVISIBLE);
                                }


                                if (response.body().get(0).getSafety_footrest() > 0) {
                                    txt_subway_safety_footrest.setVisibility(View.VISIBLE);
                                } else {
                                    txt_subway_safety_footrest.setVisibility(View.INVISIBLE);
                                }


                                if ((response.body().get(0).getAdmin_name() != null) && (response.body().get(0).getAddress() != null) && (response.body().get(0).getPhone_number() != null)) {
                                    txt_subway_help_name.setText("이름 : " + response.body().get(0).getAdmin_name());
                                    txt_subway_help_address.setText("주소 : " + response.body().get(0).getAddress());
                                    txt_subway_help_call.setText("연락처 : " + response.body().get(0).getPhone_number());
                                } else {
                                    Toast.makeText(StationInfoActivity.this, "해당 역의 안전 센터에 대한 정보는 아직 정의 되어 있지 않습니다. 빠른 시일내에 해결하도록 하겠습니다. " +
                                            "이용에 불편함을 드려서 죄송합니다.", Toast.LENGTH_LONG).show();
                                }
                                if ((txt_disabled_toilet_usage.getVisibility() == View.INVISIBLE) && (txt_subway_wheel_lift.getVisibility() == View.INVISIBLE) &&
                                        (txt_subway_auto_wheel_recharge.getVisibility() == View.INVISIBLE) && (txt_subway_safety_footrest.getVisibility() == View.INVISIBLE)) {
                                    Toast.makeText(StationInfoActivity.this, "선택하신 역과 호선에서의 안전 시설 정보가 아직 없습니다. 이용에 불편함을 드려서 죄송합니다. 안전 센터가 생기게 되면 바로 추가하도록 하겠습니다.", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                if ((txt_disabled_toilet_usage.getVisibility() == View.INVISIBLE) && (txt_subway_wheel_lift.getVisibility() == View.INVISIBLE) &&
                                        (txt_subway_auto_wheel_recharge.getVisibility() == View.INVISIBLE) && (txt_subway_safety_footrest.getVisibility() == View.INVISIBLE)) {
                                    Toast.makeText(StationInfoActivity.this, "선택하신 역과 호선에서의 안전 시설 정보가 아직 없습니다. \n 이용에 불편함을 드려서 죄송합니다.", Toast.LENGTH_LONG).show();
                                }
                                Toast.makeText(StationInfoActivity.this, "해당 역의 안전 센터에 대한 정보는 아직 정의 되어 있지 않습니다. \n 빠른 시일내에 해결하도록 하겠습니다. " +
                                        "이용에 불편함을 드려서 죄송합니다.", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(StationInfoActivity.this, "시설 관련 정보가 아직 서비스 되지 않았습니다. 이용에 불편을 드려서 죄송합니다. 빠르게 준비하겠습니다.", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<InfraStructure>> call, Throwable t) {
                        Log.e(TAG, t.toString());
                    }
                });

                network.getReportProxy().getSubInfoReportDataFromServer(txt_station_name.getText().toString(), btnSubLine1.getText().toString(), new Callback<List<ReportData>>() {
                    @Override
                    public void onResponse(Call<List<ReportData>> call, Response<List<ReportData>> response) {
                        if (response.isSuccessful()) {
                            for (int i = 0; i < response.body().size(); i++) {
                                Log.i(TAG, "이름 : " + response.body().get(i).getName() + " // " + "제목 : " + response.body().get(i).getTitle());
                                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                                String rptDay = df.format(response.body().get(i).getTime());
                                reportArrayList.add(new Report(response.body().get(i).getName(), response.body().get(i).getTitle(), rptDay));
                                if (i == 4)
                                    break; // 최대 5개 최근 제보 데이터만 보여주기
                                // 내용 리스트에 넣어주기
                            }

                            reportListAdapter = new ReportListAdapter(reportArrayList, StationInfoActivity.this);

                            rptReCyclerView.setLayoutManager(new LinearLayoutManager(StationInfoActivity.this, LinearLayoutManager.VERTICAL, false));
                            rptReCyclerView.setHasFixedSize(true);
                            rptReCyclerView.setAdapter(reportListAdapter); // RecyclerView에 어댑터 꼽기
                            reportReset = true;
                        }
                    }

                    @Override
                    public void onFailure(Call<List<ReportData>> call, Throwable t) {
                        Log.e(TAG, t.toString());
                    }
                });
            }
        });

        btnSubLine2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RecyclerViewFrame.setVisibility(View.VISIBLE);

                txt_disabled_toilet_usage.setVisibility(View.INVISIBLE);
                txt_subway_wheel_lift.setVisibility(View.INVISIBLE);
                txt_subway_auto_wheel_recharge.setVisibility(View.INVISIBLE);
                txt_subway_safety_footrest.setVisibility(View.INVISIBLE);
                txt_subway_help_call.setText("");
                txt_subway_help_name.setText("");
                txt_subway_help_address.setText("");

                if (reportReset) {
                    reportArrayList.clear();
                    rptReCyclerView.setAdapter(null); // 리셋하기
                    reportReset = false;
                }

                // 시설 정보도 보내기 위함 (station 이름 + subline 호선)
                network.getSubwayProxy().getInfraInfoBySubwayInfo(txt_station_name.getText().toString(), btnSubLine2.getText().toString(), new Callback<List<InfraStructure>>() {
                    @Override
                    public void onResponse(Call<List<InfraStructure>> call, Response<List<InfraStructure>> response) {
                        if (response.isSuccessful()) {
                            if (response.body().size() > 0) {
                                // 시설물 관련되어서 DB거쳐서 가지고 왔을 때 한 개라도 있으면 사용 가능으로 표시함
                                if (response.body().get(0).getDisabled_toilet() > 0) {
                                    txt_disabled_toilet_usage.setVisibility(View.VISIBLE);
                                } else {
                                    txt_disabled_toilet_usage.setVisibility(View.INVISIBLE);
                                }


                                if (response.body().get(0).getAuto_wheel_recharge() > 0) {
                                    txt_subway_wheel_lift.setVisibility(View.VISIBLE);
                                } else {
                                    txt_subway_wheel_lift.setVisibility(View.INVISIBLE);
                                }


                                if (response.body().get(0).getAuto_wheel_recharge() > 0) {
                                    txt_subway_auto_wheel_recharge.setVisibility(View.VISIBLE);
                                } else {
                                    txt_subway_auto_wheel_recharge.setVisibility(View.INVISIBLE);
                                }


                                if (response.body().get(0).getSafety_footrest() > 0) {
                                    txt_subway_safety_footrest.setVisibility(View.VISIBLE);
                                } else {
                                    txt_subway_safety_footrest.setVisibility(View.INVISIBLE);
                                }
                                if ((response.body().get(0).getAdmin_name() != null) && (response.body().get(0).getAddress() != null) && (response.body().get(0).getPhone_number() != null)) {
                                    txt_subway_help_name.setText("이름 : " + response.body().get(0).getAdmin_name());
                                    txt_subway_help_address.setText("주소 : " + response.body().get(0).getAddress());
                                    txt_subway_help_call.setText("연락처 : " + response.body().get(0).getPhone_number());
                                } else {
                                    Toast.makeText(StationInfoActivity.this, "해당 역의 안전 센터에 대한 정보는 아직 정의 되어 있지 않습니다. 빠른 시일내에 해결하도록 하겠습니다. " +
                                            "이용에 불편함을 드려서 죄송합니다.", Toast.LENGTH_LONG).show();
                                }
                                if ((txt_disabled_toilet_usage.getVisibility() == View.INVISIBLE) && (txt_subway_wheel_lift.getVisibility() == View.INVISIBLE) &&
                                        (txt_subway_auto_wheel_recharge.getVisibility() == View.INVISIBLE) && (txt_subway_safety_footrest.getVisibility() == View.INVISIBLE)) {
                                    Toast.makeText(StationInfoActivity.this, "선택하신 역과 호선에서의 안전 시설 정보가 아직 없습니다. 이용에 불편함을 드려서 죄송합니다. 안전 센터가 생기게 되면 바로 추가하도록 하겠습니다.", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                if ((txt_disabled_toilet_usage.getVisibility() == View.INVISIBLE) && (txt_subway_wheel_lift.getVisibility() == View.INVISIBLE) &&
                                        (txt_subway_auto_wheel_recharge.getVisibility() == View.INVISIBLE) && (txt_subway_safety_footrest.getVisibility() == View.INVISIBLE)) {
                                    Toast.makeText(StationInfoActivity.this, "선택하신 역과 호선에서의 안전 시설 정보가 아직 없습니다. \n 이용에 불편함을 드려서 죄송합니다.", Toast.LENGTH_LONG).show();
                                }
                                Toast.makeText(StationInfoActivity.this, "해당 역의 안전 센터에 대한 정보는 아직 정의 되어 있지 않습니다. \n 빠른 시일내에 해결하도록 하겠습니다. " +
                                        "이용에 불편함을 드려서 죄송합니다.", Toast.LENGTH_LONG).show();
                            }
                        }
                        // 시설 정보가 없을 경우
                        else {
                            Toast.makeText(StationInfoActivity.this, "시설 관련 정보가 아직 서비스 되지 않았습니다. 이용에 불편을 드려서 죄송합니다. 빠르게 준비하겠습니다.", Toast.LENGTH_LONG).show();
                        }
                    }


                    @Override
                    public void onFailure(Call<List<InfraStructure>> call, Throwable t) {

                    }
                });

                network.getReportProxy().getSubInfoReportDataFromServer(txt_station_name.getText().toString(), btnSubLine2.getText().toString(), new Callback<List<ReportData>>() {
                    @Override
                    public void onResponse(Call<List<ReportData>> call, Response<List<ReportData>> response) {
                        if (response.isSuccessful()) {
                            for (int i = 0; i < response.body().size(); i++) {
                                Log.i(TAG, "이름 : " + response.body().get(i).getName() + " // " + "제목 : " + response.body().get(i).getTitle());
                                SimpleDateFormat df = new SimpleDateFormat("YYYY-MM-dd hh:mm:ss");
                                String rptDay = df.format(response.body().get(i).getTime());
                                reportArrayList.add(new Report(response.body().get(i).getName(), response.body().get(i).getTitle(), rptDay));
                                // 내용 리스트에 넣어주기
                                if (i == 4)
                                    break; // 최대 5개 최근 제보 데이터만 보여주기
                            }

                            reportListAdapter = new ReportListAdapter(reportArrayList, StationInfoActivity.this);

                            rptReCyclerView.setLayoutManager(new LinearLayoutManager(StationInfoActivity.this, LinearLayoutManager.VERTICAL, false));
                            rptReCyclerView.setHasFixedSize(true);
                            rptReCyclerView.setAdapter(reportListAdapter); // RecyclerView에 어댑터 꼽기
                            reportReset = true;
                        }
                    }

                    @Override
                    public void onFailure(Call<List<ReportData>> call, Throwable t) {
                        Log.e(TAG, t.toString());
                    }
                });
            }
        });

        btnSubLine3.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                RecyclerViewFrame.setVisibility(View.VISIBLE);

                txt_disabled_toilet_usage.setVisibility(View.INVISIBLE);
                txt_subway_wheel_lift.setVisibility(View.INVISIBLE);
                txt_subway_auto_wheel_recharge.setVisibility(View.INVISIBLE);
                txt_subway_safety_footrest.setVisibility(View.INVISIBLE);
                txt_subway_help_call.setText("");
                txt_subway_help_name.setText("");
                txt_subway_help_address.setText("");


                if (reportReset) {
                    reportArrayList.clear();
                    rptReCyclerView.setAdapter(null); // 리셋하기
                    reportReset = false;
                }
                // 시설 정보도 보내기 위함 (station 이름 + subline 호선)
                network.getSubwayProxy().getInfraInfoBySubwayInfo(txt_station_name.getText().toString(), btnSubLine3.getText().toString(), new Callback<List<InfraStructure>>() {
                    @Override
                    public void onResponse(Call<List<InfraStructure>> call, Response<List<InfraStructure>> response) {
                        if (response.isSuccessful()) {
                            if (response.body().size() > 0) {
                                // 시설물 관련되어서 DB거쳐서 가지고 왔을 때 한 개라도 있으면 사용 가능으로 표시함
                                if (response.body().get(0).getDisabled_toilet() > 0) {
                                    txt_disabled_toilet_usage.setVisibility(View.VISIBLE);
                                } else {
                                    txt_disabled_toilet_usage.setVisibility(View.INVISIBLE);
                                }


                                if (response.body().get(0).getAuto_wheel_recharge() > 0) {
                                    txt_subway_wheel_lift.setVisibility(View.VISIBLE);
                                } else {
                                    txt_subway_wheel_lift.setVisibility(View.INVISIBLE);
                                }


                                if (response.body().get(0).getAuto_wheel_recharge() > 0) {
                                    txt_subway_auto_wheel_recharge.setVisibility(View.VISIBLE);
                                } else {
                                    txt_subway_auto_wheel_recharge.setVisibility(View.INVISIBLE);
                                }


                                if (response.body().get(0).getSafety_footrest() > 0) {
                                    txt_subway_safety_footrest.setVisibility(View.VISIBLE);
                                } else {
                                    txt_subway_safety_footrest.setVisibility(View.INVISIBLE);
                                }
                                if ((response.body().get(0).getAdmin_name() != null) && (response.body().get(0).getAddress() != null) && (response.body().get(0).getPhone_number() != null)) {
                                    txt_subway_help_name.setText("이름 : " + response.body().get(0).getAdmin_name());
                                    txt_subway_help_address.setText("주소 : " + response.body().get(0).getAddress());
                                    txt_subway_help_call.setText("연락처 : " + response.body().get(0).getPhone_number());
                                } else {
                                    Toast.makeText(StationInfoActivity.this, "해당 역의 안전 센터에 대한 정보는 아직 정의 되어 있지 않습니다. 빠른 시일내에 해결하도록 하겠습니다. " +
                                            "이용에 불편함을 드려서 죄송합니다.", Toast.LENGTH_LONG).show();
                                }
                                if ((txt_disabled_toilet_usage.getVisibility() == View.INVISIBLE) && (txt_subway_wheel_lift.getVisibility() == View.INVISIBLE) &&
                                        (txt_subway_auto_wheel_recharge.getVisibility() == View.INVISIBLE) && (txt_subway_safety_footrest.getVisibility() == View.INVISIBLE)) {
                                    Toast.makeText(StationInfoActivity.this, "선택하신 역과 호선에서의 안전 시설 정보가 아직 없습니다. 이용에 불편함을 드려서 죄송합니다. 안전 센터가 생기게 되면 바로 추가하도록 하겠습니다.", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                if ((txt_disabled_toilet_usage.getVisibility() == View.INVISIBLE) && (txt_subway_wheel_lift.getVisibility() == View.INVISIBLE) &&
                                        (txt_subway_auto_wheel_recharge.getVisibility() == View.INVISIBLE) && (txt_subway_safety_footrest.getVisibility() == View.INVISIBLE)) {
                                    Toast.makeText(StationInfoActivity.this, "선택하신 역과 호선에서의 안전 시설 정보가 아직 없습니다. \n 이용에 불편함을 드려서 죄송합니다.", Toast.LENGTH_LONG).show();
                                }
                                Toast.makeText(StationInfoActivity.this, "해당 역의 안전 센터에 대한 정보는 아직 정의 되어 있지 않습니다. \n 빠른 시일내에 해결하도록 하겠습니다. " +
                                        "이용에 불편함을 드려서 죄송합니다.", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(StationInfoActivity.this, "시설 관련 정보가 아직 서비스 되지 않았습니다. 이용에 불편을 드려서 죄송합니다. 빠르게 준비하겠습니다.", Toast.LENGTH_LONG).show();
                        }
                    }


                    @Override
                    public void onFailure(Call<List<InfraStructure>> call, Throwable t) {

                    }
                });

                network.getReportProxy().getSubInfoReportDataFromServer(txt_station_name.getText().toString(), btnSubLine3.getText().toString(), new Callback<List<ReportData>>() {
                    @Override
                    public void onResponse(Call<List<ReportData>> call, Response<List<ReportData>> response) {
                        if (response.isSuccessful()) {
                            for (int i = 0; i < response.body().size(); i++) {
                                Log.i(TAG, "이름 : " + response.body().get(i).getName() + " // " + "제목 : " + response.body().get(i).getTitle());
                                SimpleDateFormat df = new SimpleDateFormat("YYYY-MM-dd hh:mm:ss");
                                String rptDay = df.format(response.body().get(i).getTime());
                                reportArrayList.add(new Report(response.body().get(i).getName(), response.body().get(i).getTitle(), rptDay));
                                // 내용 리스트에 넣어주기
                                if (i == 4)
                                    break; // 최대 5개 최근 제보 데이터만 보여주기
                            }

                            reportListAdapter = new ReportListAdapter(reportArrayList, StationInfoActivity.this);

                            rptReCyclerView.setLayoutManager(new LinearLayoutManager(StationInfoActivity.this, LinearLayoutManager.VERTICAL, false));
                            rptReCyclerView.setHasFixedSize(true);
                            rptReCyclerView.setAdapter(reportListAdapter); // RecyclerView에 어댑터 꼽기
                            reportReset = true;
                        }
                    }

                    @Override
                    public void onFailure(Call<List<ReportData>> call, Throwable t) {
                        Log.e(TAG, t.toString());
                    }
                });
            }
        });
        btnSubLine4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecyclerViewFrame.setVisibility(View.VISIBLE);

                txt_disabled_toilet_usage.setVisibility(View.INVISIBLE);
                txt_subway_wheel_lift.setVisibility(View.INVISIBLE);
                txt_subway_auto_wheel_recharge.setVisibility(View.INVISIBLE);
                txt_subway_safety_footrest.setVisibility(View.INVISIBLE);
                txt_subway_help_call.setText("");
                txt_subway_help_name.setText("");
                txt_subway_help_address.setText("");

                if (reportReset) {
                    reportArrayList.clear();
                    rptReCyclerView.setAdapter(null); // 리셋하기
                    reportReset = false;
                }
                // 시설 정보도 보내기 위함 (station 이름 + subline 호선)
                network.getSubwayProxy().getInfraInfoBySubwayInfo(txt_station_name.getText().toString(), btnSubLine4.getText().toString(), new Callback<List<InfraStructure>>() {
                    @Override
                    public void onResponse(Call<List<InfraStructure>> call, Response<List<InfraStructure>> response) {
                        if (response.isSuccessful()) {
                            // 시설물 관련되어서 DB거쳐서 가지고 왔을 때 한 개라도 있으면 사용 가능으로 표시함
                            if (response.body().size() > 0) {
                                if (response.body().get(0).getDisabled_toilet() > 0) {
                                    txt_disabled_toilet_usage.setVisibility(View.VISIBLE);
                                } else {
                                    txt_disabled_toilet_usage.setVisibility(View.INVISIBLE);
                                }


                                if (response.body().get(0).getAuto_wheel_recharge() > 0) {
                                    txt_subway_wheel_lift.setVisibility(View.VISIBLE);
                                } else {
                                    txt_subway_wheel_lift.setVisibility(View.INVISIBLE);
                                }


                                if (response.body().get(0).getAuto_wheel_recharge() > 0) {
                                    txt_subway_auto_wheel_recharge.setVisibility(View.VISIBLE);
                                } else {
                                    txt_subway_auto_wheel_recharge.setVisibility(View.INVISIBLE);
                                }


                                if (response.body().get(0).getSafety_footrest() > 0) {
                                    txt_subway_safety_footrest.setVisibility(View.VISIBLE);
                                } else {
                                    txt_subway_safety_footrest.setVisibility(View.INVISIBLE);
                                }

                                if ((response.body().get(0).getAdmin_name() != null) && (response.body().get(0).getAddress() != null) && (response.body().get(0).getPhone_number() != null)) {
                                    txt_subway_help_name.setText("이름 : " + response.body().get(0).getAdmin_name());
                                    txt_subway_help_address.setText("주소 : " + response.body().get(0).getAddress());
                                    txt_subway_help_call.setText("연락처 : " + response.body().get(0).getPhone_number());
                                } else {
                                    Toast.makeText(StationInfoActivity.this, "해당 역의 안전 센터에 대한 정보는 아직 정의 되어 있지 않습니다. 빠른 시일내에 해결하도록 하겠습니다. " +
                                            "이용에 불편함을 드려서 죄송합니다.", Toast.LENGTH_LONG).show();
                                }

                                if ((txt_disabled_toilet_usage.getVisibility() == View.INVISIBLE) && (txt_subway_wheel_lift.getVisibility() == View.INVISIBLE) &&
                                        (txt_subway_auto_wheel_recharge.getVisibility() == View.INVISIBLE) && (txt_subway_safety_footrest.getVisibility() == View.INVISIBLE)) {
                                    Toast.makeText(StationInfoActivity.this, "선택하신 역과 호선에서의 안전 시설 정보가 아직 없습니다. 이용에 불편함을 드려서 죄송합니다. 안전 센터가 생기게 되면 바로 추가하도록 하겠습니다.", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                if ((txt_disabled_toilet_usage.getVisibility() == View.INVISIBLE) && (txt_subway_wheel_lift.getVisibility() == View.INVISIBLE) &&
                                        (txt_subway_auto_wheel_recharge.getVisibility() == View.INVISIBLE) && (txt_subway_safety_footrest.getVisibility() == View.INVISIBLE)) {
                                    Toast.makeText(StationInfoActivity.this, "선택하신 역과 호선에서의 안전 시설 정보가 아직 없습니다. \n 이용에 불편함을 드려서 죄송합니다.", Toast.LENGTH_LONG).show();
                                }
                                Toast.makeText(StationInfoActivity.this, "해당 역의 안전 센터에 대한 정보는 아직 정의 되어 있지 않습니다. \n 빠른 시일내에 해결하도록 하겠습니다. " +
                                        "이용에 불편함을 드려서 죄송합니다.", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(StationInfoActivity.this, "시설 관련 정보가 아직 서비스 되지 않았습니다. 이용에 불편을 드려서 죄송합니다. 빠르게 준비하겠습니다.", Toast.LENGTH_LONG).show();
                        }
                    }


                    @Override
                    public void onFailure(Call<List<InfraStructure>> call, Throwable t) {
                        Log.e(TAG, t.toString());
                    }
                });

                network.getReportProxy().getSubInfoReportDataFromServer(txt_station_name.getText().toString(), btnSubLine4.getText().toString(), new Callback<List<ReportData>>() {
                    @Override
                    public void onResponse(Call<List<ReportData>> call, Response<List<ReportData>> response) {
                        if (response.isSuccessful()) {
                            for (int i = 0; i < response.body().size(); i++) {
                                Log.i(TAG, "이름 : " + response.body().get(i).getName() + " // " + "제목 : " + response.body().get(i).getTitle());
                                SimpleDateFormat df = new SimpleDateFormat("YYYY-MM-dd hh:mm:ss");
                                String rptDay = df.format(response.body().get(i).getTime());
                                reportArrayList.add(new Report(response.body().get(i).getName(), response.body().get(i).getTitle(), rptDay));
                                // 내용 리스트에 넣어주기
                                if (i == 4)
                                    break; // 최대 5개 최근 제보 데이터만 보여주기
                            }

                            reportListAdapter = new ReportListAdapter(reportArrayList, StationInfoActivity.this);

                            rptReCyclerView.setLayoutManager(new LinearLayoutManager(StationInfoActivity.this, LinearLayoutManager.VERTICAL, false));
                            rptReCyclerView.setHasFixedSize(true);
                            rptReCyclerView.setAdapter(reportListAdapter); // RecyclerView에 어댑터 꼽기
                            reportReset = true;
                        }
                    }

                    @Override
                    public void onFailure(Call<List<ReportData>> call, Throwable t) {
                        Log.e(TAG, t.toString());
                    }
                });
            }
        });

    }

    // 지도 보이기 (위도,경도 체크 >> 주소에 따라서 변경하기)
    private void setLocationOnTheMap() {
        geocoder = new Geocoder(this, Locale.KOREA);

        if (getIntent().getExtras().getString("subname", null) != null) {
            statNm = getIntent().getStringExtra("subname") + "역";
        }

        Log.e(TAG, statNm + "이름");

        try {
            List<Address> addresses = geocoder.getFromLocationName(statNm, 5);
            lat = addresses.get(0).getLatitude();
            lng = addresses.get(0).getLongitude();
            Log.i(TAG, "위도: " + addresses.get(0).getLatitude());
            Log.i(TAG, "경도: " + addresses.get(0).getLongitude());


            /*if (addresses != null) {
                if (addresses.size() == 0) {
                    Toast.makeText(StationInfoActivity.this, "해당되는 주소 정보는 존재하지 않습니다.", Toast.LENGTH_LONG).show();
                } else {


                }
            }*/
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "서버에서 주소 변환 에러 발생 ");
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(lat, lng)).title(statNm + "입니다.");
        mMap.addMarker(markerOptions);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setTrafficEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        Log.i(TAG, "위도 경도 확인 : " + lat + "/" + lng);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lat, lng)));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back_station_info:
                finish();
                break;

            case R.id.subway_departure_info:
                setDepartureStatNm();
                break;

            case R.id.subway_arrival_info:
                setArrivalStatNm();
                break;

            case R.id.subway_help_dial:
                callToSafetyCenter();
                break;

            case R.id.subway_timeline:
                initTimeLines();
                break;

        }
    }

    // 지하철 역 지나는 타임 라인 확인하기
    private void initTimeLines() {
        startActivity(new Intent(this, TimeLineActivity.class).putExtra("statnNm", statNm));
    }

    // 안전 센터에 전화걸기
    private void callToSafetyCenter() {
        Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
        phoneIntent.setData(Uri.parse("tel:02" + txt_subway_help_call.getText().toString()));
        startActivity(phoneIntent);
    }

    // 도착역 설정
    private void setArrivalStatNm() {
        final AlertDialog.Builder dlg = new AlertDialog.Builder(this);
        final View view = LayoutInflater.from(this).inflate(R.layout.station_alertbox, null);
        dlg.setView(view);
        final AlertDialog dialogBoxMain = dlg.create();
        list = new ArrayList<>();

        settingSubwayList(); // subway List 미리 설정

        arrayList = new ArrayList<>(); // 리스트의 모든 데이터를 arrayList에 복사 // subDataList 복사본
        arrayList.addAll(list);

        searchAdapter = new SearchAdapter(this, list);

        subFilterEdtxt = (EditText) view.findViewById(R.id.sub_filter_editText);
        subFilterLstView = (ListView) view.findViewById(R.id.sub_filter_list_view);


        subFilterLstView.setAdapter(searchAdapter);

        subFilterEdtxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String findTxt = subFilterEdtxt.getText().toString();
                search(findTxt);
            }
        });

        subFilterLstView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
                Log.i(TAG, String.valueOf(parent.getAdapter().getItem(position)));
                final AlertDialog.Builder dlg2 = new AlertDialog.Builder(StationInfoActivity.this);
                final View dlgView = LayoutInflater.from(StationInfoActivity.this).inflate(R.layout.dialogbox_sample, null);
                final TextView departSubMsg = (TextView) dlgView.findViewById(R.id.txt_subway_question);
                final Button btnDepartYes = (Button) dlgView.findViewById(R.id.txt_subway_question_yes);
                final Button btnDepartNo = (Button) dlgView.findViewById(R.id.txt_subway_question_no);

                dlg2.setTitle("도착역 설정");
                dlg2.setView(dlgView);

                // dialog 설정
                final AlertDialog dialogbox = dlg2.create();

                departSubMsg.setText("도착역으로 설정하시겠습니까?");

                btnDepartYes.setText("네");
                btnDepartYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(StationInfoActivity.this, (parent.getAdapter().getItem(position) + "을 도착역으로 설정하였습니다."), Toast.LENGTH_LONG).show();
                        setSubNmForArrival(parent.getAdapter().getItem(position)); // 도착역 보내기 / 시작역도 같이 보낼 것임
                        dialogbox.cancel();
                        dialogBoxMain.cancel();
                    }
                });

                btnDepartNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogbox.dismiss();
                        dialogBoxMain.dismiss();
                    }
                });

                /*dlg2.setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(StationInfoActivity.this, (parent.getAdapter().getItem(position) + "을 도착역으로 설정하였습니다."), Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                        dlg2.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                dialog.dismiss();
                            }
                        });
                        setSubNmForArrival(parent.getAdapter().getItem(position)); // 도착역 보내기 / 시작역도 같이 보낼 것임
                    }
                });*/
                dialogbox.show();
            }
        });
        dialogBoxMain.show();
    }

    //  도착역 정의하는 부분
    private void setSubNmForArrival(Object item) {
        if (hashMapReset) {
            subwayNmMap.clear(); // HashMap 비우기
            hashMapReset = false;
        }
        subwayNmMap.put("arrival", item.toString()); // 도착역 hashMap에 넣기
        if (subwayNmMap.get("departure") != null) { // 출발역 도착역 모두 정의 되어야 네트워크 연동
            getTheShortestRoute(subwayNmMap.get("departure"), subwayNmMap.get("arrival"));
        } else {
            Toast.makeText(StationInfoActivity.this, "출발역을 설정해주시기 바랍니다!", Toast.LENGTH_LONG).show();
        }
    }

    // 서버 연동할 부분 (최단 경로 보여주기) 매우 중요!!!!


    private void getTheShortestRoute(final String departure, final String arrival) {
        Log.i(TAG, "연동 작업 되나요?");
        Log.i(TAG, subwayNmMap.get("arrival") + " // " + subwayNmMap.get("departure"));
        String departStatNm[] = new String[2];
        departStatNm = departure.split("/"); // 역/호선
        String arrivalStatNm[] = new String[2];
        arrivalStatNm = arrival.split("/"); // 역/호선

        final String[] finalDepartStatNm = departStatNm;
        final String[] finalArrivalStatNm = arrivalStatNm;

        Log.i(TAG, finalDepartStatNm[0] + " // " + finalArrivalStatNm[0]);
        network.getSubwayProxy().setSubwayRouter(finalDepartStatNm[0], finalArrivalStatNm[0], new Callback<List<SubwayRoute>>() {
            @Override
            public void onResponse(Call<List<SubwayRoute>> call, Response<List<SubwayRoute>> response) {
                if (response.isSuccessful()) {

/*
출발역 , 도착역 , 환승정보 관련 메세지, 최소 환승 횟수, 최소 환승 역 구간 , 최소 환승 시간
 */

                    try {
                        transIntent.putExtra("depart_statNm", finalDepartStatNm[0]); // 출발역 넘기기
                        transIntent.putExtra("arrive_statNm", finalArrivalStatNm[0]); // 도착역 넘기기
                        transIntent.putExtra("depart_subline", finalDepartStatNm[1]); // 출발역 지하철 호선
                        transIntent.putExtra("arrive_subline", finalArrivalStatNm[1]);  // 도착역 지하철 호선
                        transIntent.putExtra("sht_transfer_Msg", response.body().get(response.body().size() - 1).getShtTransferMsg());
                        transIntent.putExtra("sht_transfer_minTime", response.body().get(response.body().size() - 1).getShtTravelTm());
                        transIntent.putExtra("sht_transfer_minCount", response.body().get(response.body().size() - 1).getShtTransferCnt());
                        transIntent.putExtra("sht_transfer_statnNm", response.body().get(response.body().size() - 1).getShtStatnNm());
                        transIntent.putExtra("min_transfer_count", response.body().get(response.body().size() - 1).getMinTransferCnt());
                        transIntent.putExtra("sht_transfer_statnId", response.body().get(response.body().size() - 1).getShtStatnId());
                        hashMapReset = true; //  HashMap reset하기 위함
                        startActivity(transIntent); // 환승 정보 페이지로 이동
                    } catch (ArrayIndexOutOfBoundsException e) {
                        e.printStackTrace();
                        Toast.makeText(StationInfoActivity.this, "데이터를 불러오는데 문제가 발생하였습니다. 다시 이용해주시기 바랍니다.", Toast.LENGTH_LONG).show();
                    }

                }
            }

            @Override
            public void onFailure(Call<List<SubwayRoute>> call, Throwable t) {
                Log.e(TAG, t.toString());
            }
        });

    }


    // 시작역 설정
    private void setDepartureStatNm() {
        final AlertDialog.Builder dlg = new AlertDialog.Builder(this);
        final View view = LayoutInflater.from(this).inflate(R.layout.station_alertbox, null);
        list = new ArrayList<>();
        dlg.setView(view);
        final AlertDialog dialogBoxMain = dlg.create();
        settingSubwayList(); // subway List 미리 설정

        arrayList = new ArrayList<>(); // 리스트의 모든 데이터를 arrayList에 복사 // subDataList 복사본
        arrayList.addAll(list);

        searchAdapter = new SearchAdapter(this, list);


        subFilterEdtxt = (EditText) view.findViewById(R.id.sub_filter_editText);
        subFilterLstView = (ListView) view.findViewById(R.id.sub_filter_list_view);

        subFilterLstView.setAdapter(searchAdapter);
        subFilterEdtxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String findTxt = subFilterEdtxt.getText().toString();
                search(findTxt);
            }
        });


        subFilterLstView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {


                Log.i(TAG, String.valueOf(parent.getAdapter().getItem(position)));
                final AlertDialog.Builder dlg2 = new AlertDialog.Builder(StationInfoActivity.this);
                final View dlgView = LayoutInflater.from(StationInfoActivity.this).inflate(R.layout.dialogbox_sample, null);
                final TextView departSubMsg = (TextView) dlgView.findViewById(R.id.txt_subway_question);
                final Button btnDepartYes = (Button) dlgView.findViewById(R.id.txt_subway_question_yes);
                final Button btnDepartNo = (Button) dlgView.findViewById(R.id.txt_subway_question_no);

                dlg2.setTitle("출발역 설정");
                dlg2.setView(dlgView);

                // dialog 설정
                final AlertDialog dialogbox = dlg2.create();

                departSubMsg.setText("출발역으로 설정하시겠습니까?");

                btnDepartYes.setText("네");
                btnDepartYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(StationInfoActivity.this, (parent.getAdapter().getItem(position) + "을 시작역으로 설정하였습니다."), Toast.LENGTH_LONG).show();
                        setSubNmForDeparture(parent.getAdapter().getItem(position)); // 시작역 보내기 / 도착역도 같이 보낼 것임
                        dialogbox.cancel();
                        dialogBoxMain.cancel();
                    }
                });

                btnDepartNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogbox.dismiss();
                        dialogBoxMain.dismiss();
                    }
                });

              /*  dlg2.setNegativeButton("아니오", null);
                dlg2.setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(StationInfoActivity.this, (parent.getAdapter().getItem(position) + "을 시작역으로 설정하였습니다."), Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                        dlg2.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                dialog.dismiss();
                            }
                        });
                        setSubNmForDeparture(parent.getAdapter().getItem(position)); // 시작역 보내기 / 도착역도 같이 보낼 것임
                    }
                });*/
                dialogbox.show();
            }
        });

        dialogBoxMain.show();
    }

    // 출발역 정의하는 부분
    private void setSubNmForDeparture(Object item) {
        if (hashMapReset) { // 한 바퀴 돌았을 땐 clear 해줘야함
            subwayNmMap.clear();
            hashMapReset = false;
        }
        subwayNmMap.put("departure", item.toString()); // 도착역 hashMap에 넣기
        if (subwayNmMap.get("arrival") != null) { // 출발역 도착역 모두 정의 되어야 네트워크 연동
            getTheShortestRoute(subwayNmMap.get("departure"), subwayNmMap.get("arrival"));
        } else {
            Toast.makeText(StationInfoActivity.this, "도착역을 설정해주시기 바랍니다.", Toast.LENGTH_LONG).show();
        }
    }


    /*
    검색 알고리즘 진행
     */
    private void search(String findTxt) {
        list.clear(); //문자 입력이 없을 경우 다 클리어
        if (findTxt.length() == 0) {
            list.addAll(arrayList); // 처음엔 리스트에 다 띄우기
        } else {
            //  리스트의 모든 데이터를 검색해서 모든 데이터에 입력 받은 단어가 포함되어 있으면 true를 반환하여 검색된 데이터를 리스트에 추가한다..
            for (int i = 0; i < arrayList.size(); i++) {
                if (arrayList.get(i).contains(findTxt)) {
                    list.add(arrayList.get(i));
                }
            }
        }
        // 리스트 데이터가 변경되엇기에 어댑터를 갱신하여 검색된 데이터를 화면에 보여주도록 한다.
        searchAdapter.notifyDataSetChanged();
    }


    private void settingSubwayList() {

        // 1호선
        list.add("소요산/1호선");
        list.add("동두천/1호선");
        list.add("보산/1호선");
        list.add("동두천중앙/1호선");
        list.add("지행/1호선");
        list.add("덕정/1호선");
        list.add("덕계/1호선");
        list.add("양주/1호선");
        list.add("녹양/1호선");
        list.add("가능/1호선");
        list.add("의정부/1호선");
        list.add("회룡/1호선");
        list.add("망월사/1호선");
        list.add("도봉산/1호선");
        list.add("도봉/1호선");
        list.add("방학/1호선");
        list.add("창동/1호선");
        list.add("녹천/1호선");
        list.add("월계/1호선");
        list.add("광운대/1호선");
        list.add("석계/1호선");
        list.add("신이문/1호선");
        list.add("외대앞/1호선");
        list.add("회기/1호선");
        list.add("청량리/1호선");
        list.add("제기동/1호선");
        list.add("신설동/1호선");
        list.add("동묘앞/1호선");
        list.add("동대문/1호선");
        list.add("종로5가/1호선");
        list.add("종로3가/1호선");
        list.add("종각/1호선");
        list.add("시청/1호선");
        list.add("서울역/1호선");
        list.add("남영/1호선");
        list.add("용산/1호선");
        list.add("노량진/1호선");
        list.add("대방/1호선");
        list.add("신길/1호선");
        list.add("영등포/1호선");
        list.add("신도림/1호선");
        list.add("구로/1호선");
        list.add("구일/1호선");
        list.add("개봉/1호선");
        list.add("오류동/1호선");
        list.add("온수/1호선");
        list.add("역곡/1호선");
        list.add("소사/1호선");
        list.add("부천/1호선");
        list.add("중동/1호선");
        list.add("송내/1호선");
        list.add("부개/1호선");
        list.add("부평/1호선");
        list.add("백운/1호선");
        list.add("동암/1호선");
        list.add("간석/1호선");
        list.add("주안/1호선");
        list.add("도화/1호선");
        list.add("제물포/1호선");
        list.add("도원/1호선");
        list.add("동인천/1호선");
        list.add("인천/1호선");
        list.add("가산디지털단지/1호선");
        list.add("독산/1호선");
        list.add("금천구청/1호선");
        list.add("광명/1호선");
        list.add("석수/1호선");
        list.add("관악/1호선");
        list.add("안양/1호선");
        list.add("명학/1호선");
        list.add("금정/1호선");
        list.add("군포/1호선");
        list.add("당정/1호선");
        list.add("의왕/1호선");
        list.add("성균관대/1호선");
        list.add("화서/1호선");
        list.add("수원/1호선");
        list.add("세류/1호선");
        list.add("병점/1호선");
        list.add("서동탄/1호선");
        list.add("세마/1호선");
        list.add("오산대/1호선");
        list.add("오산/1호선");
        list.add("진위/1호선");
        list.add("평택/1호선");
        list.add("성환/1호선");
        list.add("직산/1호선");
        list.add("두정/1호선");
        list.add("천안/1호선");
        list.add("봉명/1호선");
        list.add("쌍용/1호선");
        list.add("아산/1호선");
        list.add("배방/1호선");
        list.add("온양온천/1호선");
        list.add("신창/1호선");

        // 2호선
        list.add("신설동/2호선");
        list.add("용두/2호선");
        list.add("신답/2호선");
        list.add("용답/2호선");
        list.add("성수/2호선");
        list.add("뚝섬/2호선");
        list.add("한양대/2호선");
        list.add("왕십리/2호선");
        list.add("상왕십리/2호선");
        list.add("신당/2호선");
        list.add("동대문역사문화공원/2호선");
        list.add("을지로4가/2호선");
        list.add("을지로3가/2호선");
        list.add("을지로입구/2호선");
        list.add("시청/2호선");
        list.add("충정로/2호선");
        list.add("아현/2호선");
        list.add("이대/2호선");
        list.add("신촌/2호선");
        list.add("홍대입구/2호선");
        list.add("합정/2호선");
        list.add("당산/2호선");
        list.add("영등포구청/2호선");
        list.add("문래/2호선");
        list.add("신도림/2호선");
        list.add("도림천/2호선");
        list.add("양천구청/2호선");
        list.add("신정네거리/2호선");
        list.add("까치산/2호선");
        list.add("대림/2호선");
        list.add("구로디지털단지/2호선");
        list.add("신대방/2호선");
        list.add("신림/2호선");
        list.add("봉천/2호선");
        list.add("서울대입구/2호선");
        list.add("낙성대/2호선");
        list.add("사당/2호선");
        list.add("방배/2호선");
        list.add("서초/2호선");
        list.add("교대/2호선");
        list.add("강남/2호선");
        list.add("역삼/2호선");
        list.add("선릉/2호선");
        list.add("삼성/2호선");
        list.add("종합운동장/2호선");
        list.add("잠실새내/2호선");
        list.add("잠실/2호선");
        list.add("잠실나루/2호선");
        list.add("강변/2호선");
        list.add("구의/2호선");
        list.add("건대입구/2호선");

        // 3호선
        list.add("대화/3호선");
        list.add("주엽/3호선");
        list.add("정발산/3호선");
        list.add("마두/3호선");
        list.add("백석/3호선");
        list.add("대곡/3호선");
        list.add("화정/3호선");
        list.add("원당/3호선");
        list.add("원흥/3호선");
        list.add("삼송/3호선");
        list.add("지축/3호선");
        list.add("구파발/3호선");
        list.add("연신내/3호선");
        list.add("불광/3호선");
        list.add("녹번/3호선");
        list.add("홍제/3호선");
        list.add("무악재/3호선");
        list.add("독립문/3호선");
        list.add("경복궁/3호선");
        list.add("안국/3호선");
        list.add("종로3가/3호선");
        list.add("충무로/3호선");
        list.add("동대입구/3호선");
        list.add("약수/3호선");
        list.add("금호/3호선");
        list.add("옥수/3호선");
        list.add("압구정/3호선");
        list.add("신사/3호선");
        list.add("잠원/3호선");
        list.add("고속터미널/3호선");
        list.add("남부터미널/3호선");
        list.add("양재/3호선");
        list.add("매봉/3호선");
        list.add("도곡/3호선");
        list.add("대치/3호선");
        list.add("학여울/3호선");
        list.add("대청/3호선");
        list.add("일원/3호선");
        list.add("수서/3호선");
        list.add("가락시장/3호선");
        list.add("경찰병원/3호선");
        list.add("오금/3호선");

        // 4호선
        list.add("당고개/4호선");
        list.add("상계/4호선");
        list.add("노원/4호선");
        list.add("창동/4호선");
        list.add("쌍문/4호선");
        list.add("수유/4호선");
        list.add("미아/4호선");
        list.add("미아사거리/4호선");
        list.add("길음/4호선");
        list.add("성신여대입구/4호선");
        list.add("한성대입구/4호선");
        list.add("혜화/4호선");
        list.add("동대문/4호선");
        list.add("동대문역사문화공원/4호선");
        list.add("충무로/4호선");
        list.add("명동/4호선");
        list.add("회현/4호선");
        list.add("서울역/4호선");
        list.add("숙대입구/4호선");
        list.add("삼각지/4호선");
        list.add("신용산/4호선");
        list.add("이촌/4호선");
        list.add("동작/4호선");
        list.add("총신대입구/4호선");
        list.add("사당/4호선");
        list.add("남태령/4호선");
        list.add("선바위/4호선");
        list.add("경마공원/4호선");
        list.add("대공원/4호선");
        list.add("과천/4호선");
        list.add("정부과천청사/4호선");
        list.add("인덕원/4호선");
        list.add("평촌/4호선");
        list.add("범계/4호선");
        list.add("금정/4호선");
        list.add("산본/4호선");
        list.add("수라산/4호선");
        list.add("대야미/4호선");
        list.add("반월/4호선");
        list.add("상록수/4호선");
        list.add("한대앞/4호선");
        list.add("중앙/4호선");
        list.add("고잔/4호선");
        list.add("초지/4호선");
        list.add("안산/4호선");
        list.add("신길온천/4호선");
        list.add("정왕/4호선");
        list.add("오이도/4호선");

        // 5호선
        list.add("방화/5호선");
        list.add("개화산/5호선");
        list.add("김포공항/5호선");
        list.add("송정/5호선");
        list.add("마곡/5호선");
        list.add("발산/5호선");
        list.add("우장산/5호선");
        list.add("화곡/5호선");
        list.add("까치산/5호선");
        list.add("신정/5호선");
        list.add("목동/5호선");
        list.add("오목교/5호선");
        list.add("양평/5호선");
        list.add("영등포구청/5호선");
        list.add("영등포시장/5호선");
        list.add("신길/5호선");
        list.add("여의도/5호선");
        list.add("여의나루/5호선");
        list.add("마포/5호선");
        list.add("공덕/5호선");
        list.add("애오개/5호선");
        list.add("충정로/5호선");
        list.add("서대문/5호선");
        list.add("광화문/5호선");
        list.add("종로3가/5호선");
        list.add("을지로4가/5호선");
        list.add("동대문역사문화공원/5호선");
        list.add("청구/5호선");
        list.add("신금호/5호선");
        list.add("행당/5호선");
        list.add("왕십리/5호선");
        list.add("마장/5호선");
        list.add("답십리/5호선");
        list.add("장한평/5호선");
        list.add("군자/5호선");
        list.add("아차산/5호선");
        list.add("광나루/5호선");
        list.add("천호/5호선");
        list.add("강동/5호선");
        list.add("길동/5호선");
        list.add("굽은다리/5호선");
        list.add("명일/5호선");
        list.add("고덕/5호선");
        list.add("상일동/5호선");
        list.add("둔춘동/5호선");
        list.add("올림픽공원/5호선");
        list.add("방이/5호선");
        list.add("오금/5호선");
        list.add("개롱/5호선");
        list.add("거여/5호선");
        list.add("마천/5호선");

        // 6호선

        list.add("봉화산/6호선");
        list.add("화랑대/6호선");
        list.add("태릉입구/6호선");
        list.add("석계/6호선");
        list.add("돌곶이/6호선");
        list.add("상월곡/6호선");
        list.add("월곡/6호선");
        list.add("고려대/6호선");
        list.add("안암/6호선");
        list.add("보문/6호선");
        list.add("창신/6호선");
        list.add("동묘앞/6호선");
        list.add("신당/6호선");
        list.add("청구/6호선");
        list.add("약수/6호선");
        list.add("버티고개/6호선");
        list.add("한강진/6호선");
        list.add("이태원/6호선");
        list.add("녹사평/6호선");
        list.add("삼각지/6호선");
        list.add("효창공원앞/6호선");
        list.add("공덕/6호선");
        list.add("대흥/6호선");
        list.add("광흥창/6호선");
        list.add("상수/6호선");
        list.add("합정/6호선");
        list.add("망원/6호선");
        list.add("마포구청/6호선");
        list.add("월드컵경기장/6호선");
        list.add("디지털미디어시티/6호선");
        list.add("증산/6호선");
        list.add("새절/6호선");
        list.add("응암/6호선");
        list.add("역촌/6호선");
        list.add("불광/6호선");
        list.add("독바위/6호선");
        list.add("연신내/6호선");
        list.add("구산/6호선");

        // 7호선

        list.add("부평구청/7호선");
        list.add("굴포천/7호선");
        list.add("삼산체육관/7호선");
        list.add("상동/7호선");
        list.add("부천시청/7호선");
        list.add("신중동/7호선");
        list.add("춘의/7호선");
        list.add("부천종합운동장/7호선");
        list.add("까치울/7호선");
        list.add("온수/7호선");
        list.add("천왕/7호선");
        list.add("광명사거리/7호선");
        list.add("철산/7호선");
        list.add("가산디지털단지/7호선");
        list.add("남구로/7호선");
        list.add("대림/7호선");
        list.add("신풍/7호선");
        list.add("보라매/7호선");
        list.add("신대방삼거리/7호선");
        list.add("장승배기/7호선");
        list.add("상도/7호선");
        list.add("숭실대입구/7호선");
        list.add("남성/7호선");
        list.add("총신대입구/7호선");
        list.add("내방/7호선");
        list.add("고속터미널/7호선");
        list.add("반포/7호선");
        list.add("논현/7호선");
        list.add("학동/7호선");
        list.add("강남구청/7호선");
        list.add("청담/7호선");
        list.add("뚝섬유원지/7호선");
        list.add("건대입구/7호선");
        list.add("어린이대공원/7호선");
        list.add("군자/7호선");
        list.add("중곡/7호선");
        list.add("용마산/7호선");
        list.add("사가정/7호선");
        list.add("면목/7호선");
        list.add("상봉/7호선");
        list.add("중화/7호선");
        list.add("먹골/7호선");
        list.add("태릉입구/7호선");
        list.add("공릉/7호선");
        list.add("하계/7호선");
        list.add("중계/7호선");
        list.add("노원/7호선");
        list.add("마들/7호선");
        list.add("수락산/7호선");
        list.add("도봉산/7호선");
        list.add("장암/7호선");

        // 8호선

        list.add("모란/8호선");
        list.add("수진/8호선");
        list.add("신흥/8호선");
        list.add("단대오거리/8호선");
        list.add("남한산성입구/8호선");
        list.add("산성/8호선");
        list.add("복정/8호선");
        list.add("장지/8호선");
        list.add("문정/8호선");
        list.add("가락시장/8호선");
        list.add("송파/8호선");
        list.add("석촌/8호선");
        list.add("잠실/8호선");
        list.add("몽촌토성/8호선");
        list.add("강동구청/8호선");
        list.add("천호/8호선");
        list.add("암사/8호선");

        // 9호선
        list.add("종합운동장/9호선");
        list.add("봉은사/9호선");
        list.add("삼성중앙/9호선");
        list.add("선정릉/9호선");
        list.add("언주/9호선");
        list.add("신논현/9호선");
        list.add("사평/9호선");
        list.add("고속터미널/9호선");
        list.add("신반포/9호선");
        list.add("구반포/9호선");
        list.add("동작/9호선");
        list.add("흑석/9호선");
        list.add("노들/9호선");
        list.add("노량진/9호선");
        list.add("샛강/9호선");
        list.add("여의도/9호선");
        list.add("국회의사당/9호선");
        list.add("당산/9호선");
        list.add("선유도/9호선");
        list.add("신목동/9호선");
        list.add("염창/9호선");
        list.add("등촌/9호선");
        list.add("증미/9호선");
        list.add("가양/9호선");
        list.add("양천향교/9호선");
        list.add("마곡나루/9호선");
        list.add("신방화/9호선");
        list.add("공항시장/9호선");
        list.add("김포공항/9호선");
        list.add("개화/9호선");

        // 경의중앙선

        list.add("문산/경의중앙선");
        list.add("파주/경의중앙선");
        list.add("월롱/경의중앙선");
        list.add("금촌/경의중앙선");
        list.add("금릉/경의중앙선");
        list.add("운정/경의중앙선");
        list.add("야당/경의중앙선");
        list.add("탄현/경의중앙선");
        list.add("일산/경의중앙선");
        list.add("풍산/경의중앙선");
        list.add("백마/경의중앙선");
        list.add("곡산/경의중앙선");
        list.add("대곡/경의중앙선");
        list.add("능곡/경의중앙선");
        list.add("행신/경의중앙선");
        list.add("강매/경의중앙선");
        list.add("화전/경의중앙선");
        list.add("수색/경의중앙선");
        list.add("디지털미디어시티/경의중앙선");
        list.add("가좌/경의중앙선");
        list.add("신촌/경의중앙선");
        list.add("서울역/경의중앙선");
        list.add("홍대입구/경의중앙선");
        list.add("서강대/경의중앙선");
        list.add("공덕/경의중앙선");
        list.add("효창공원앞/경의중앙선");
        list.add("용산/경의중앙선");
        list.add("이촌/경의중앙선");
        list.add("서빙고/경의중앙선");
        list.add("한남/경의중앙선");
        list.add("옥수/경의중앙선");
        list.add("응봉/경의중앙선");
        list.add("왕십리/경의중앙선");
        list.add("청량리/경의중앙선");
        list.add("회기/경의중앙선");
        list.add("중랑/경의중앙선");
        list.add("상봉/경의중앙선");
        list.add("망우/경의중앙선");
        list.add("양원/경의중앙선");
        list.add("구리/경의중앙선");
        list.add("도농/경의중앙선");
        list.add("양정/경의중앙선");
        list.add("덕소/경의중앙선");
        list.add("도심/경의중앙선");
        list.add("팔당/경의중앙선");
        list.add("운길산/경의중앙선");
        list.add("양수/경의중앙선");
        list.add("신원/경의중앙선");
        list.add("국수/경의중앙선");
        list.add("아신/경의중앙선");
        list.add("오빈/경의중앙선");
        list.add("양평/경의중앙선");
        list.add("원덕/경의중앙선");
        list.add("용문/경의중앙선");
        list.add("지평/경의중앙선");

        // 공항철도
        list.add("서울역/공항철도");
        list.add("공덕/공항철도");
        list.add("홍대입구/공항철도");
        list.add("디지털미디어시티/공항철도");
        list.add("김포공항/공항철도");
        list.add("계양/공항철도");
        list.add("검암/공항철도");
        list.add("청라국제도시/공항철도");
        list.add("영증/공항철도");
        list.add("운서/공항철도");
        list.add("공항화물청사/공항철도");
        list.add("인천국제공항/공항철도");


    }
}
/*

    @Override
    protected void onStart() {

        new Thread(){
            @Override
            public void run() {
                super.run();
                String serviceDone = closestSubService();
                Bundle bundle = new Bundle();
                bundle.putString("serviceDone",serviceDone);
                Message msg = handler.obtainMessage();
                msg.setData(bundle);
                handler.sendMessage(msg);
            }
        }.start();

        super.onStart();
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            String subname = bundle.getString("serviceDone");
            Log.i(TAG, "serviceDone : " + "service done");
        }
    };

    private String closestSubService() {
        subname = getIntent().getExtras().getString("subname", null);
        subline = getIntent().getExtras().getString("subline", null);
        // 버튼 부분
        network.getChatProxy().receiveMessageFromServer(mResult, new Callback<List<ChatMessage>>() {
            @Override
            public void onResponse(Call<List<ChatMessage>> call, Response<List<ChatMessage>> response) {
                if (response.isSuccessful()) {
                    for (int i = 0; i < response.body().size(); i++) {
                        // subwayLnList >> 호선 정보 리스트로 넣어 준 것
                        subwayLnList.add(response.body().get(i).getSubwayNm());
                    }
                    if (statNm != null){
                        txt_station_name.setText(statNm); // 역 이름 등록
                    }

                    else
                        txt_station_name.setText(response.body().get(0).getStatNm());

                    Log.i(TAG, "subwayLnList : " + subwayLnList.get(0) + "/" + subwayLnList.get(1));
                    setLineByBtnClick(subwayLnList);

                }
            }

            @Override
            public void onFailure(Call<List<ChatMessage>> call, Throwable t) {
                Log.e(TAG, t.toString());
            }
        });
        // 현재 위치에서 가까운 역으로 넘어올 경우시설 정보 및 주소 및 관계자 내용
        if ((subname != null) && (subline != null)) {
            network = Network.getNetworkInstance();
            network.getSubwayProxy().getInfraInfoBySubwayInfo(subname, subline, new Callback<List<InfraStructure>>() {
                @Override
                public void onResponse(Call<List<InfraStructure>> call, Response<List<InfraStructure>> response) {
                    if (response.isSuccessful()) {

                        if (response.body().size() > 0) {
                            // 시설물 관련되어서 DB거쳐서 가지고 왔을 때 한 개라도 있으면 사용 가능으로 표시함
                            if (response.body().get(0).getDisabled_toilet() > 0) {
                                txt_disabled_toilet_usage.setVisibility(View.VISIBLE);
                            } else {
                                txt_disabled_toilet_usage.setVisibility(View.INVISIBLE);
                            }


                            if (response.body().get(0).getWheel_lift() > 0) {
                                txt_subway_wheel_lift.setVisibility(View.VISIBLE);
                            } else {
                                txt_subway_wheel_lift.setVisibility(View.INVISIBLE);
                            }


                            if (response.body().get(0).getAuto_wheel_recharge() > 0) {
                                txt_subway_auto_wheel_recharge.setVisibility(View.VISIBLE);
                            } else {
                                txt_subway_auto_wheel_recharge.setVisibility(View.INVISIBLE);
                            }


                            if (response.body().get(0).getSafety_footrest() > 0) {
                                txt_subway_safety_footrest.setVisibility(View.VISIBLE);
                            } else {
                                txt_subway_safety_footrest.setVisibility(View.INVISIBLE);
                            }


                            if ((response.body().get(0).getAdmin_name() != null) && (response.body().get(0).getAddress() != null) && (response.body().get(0).getPhone_number() != null)) {
                                txt_subway_help_name.setText("이름 : " + response.body().get(0).getAdmin_name());
                                txt_subway_help_address.setText("주소 : " + response.body().get(0).getAddress());
                                txt_subway_help_call.setText("연락처 : " + response.body().get(0).getPhone_number());
                            } else {
                                Toast.makeText(StationInfoActivity.this, "해당 역의 안전 센터에 대한 정보는 아직 정의 되어 있지 않습니다. 빠른 시일내에 해결하도록 하겠습니다. " +
                                        "이용에 불편함을 드려서 죄송합니다.", Toast.LENGTH_LONG).show();
                            }
                            if ((txt_disabled_toilet_usage.getVisibility() == View.INVISIBLE) && (txt_subway_wheel_lift.getVisibility() == View.INVISIBLE) &&
                                    (txt_subway_auto_wheel_recharge.getVisibility() == View.INVISIBLE) && (txt_subway_safety_footrest.getVisibility() == View.INVISIBLE)) {
                                Toast.makeText(StationInfoActivity.this, "선택하신 역과 호선에서의 안전 시설 정보가 아직 없습니다. 이용에 불편함을 드려서 죄송합니다. 안전 센터가 생기게 되면 바로 추가하도록 하겠습니다.", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            if ((txt_disabled_toilet_usage.getVisibility() == View.INVISIBLE) && (txt_subway_wheel_lift.getVisibility() == View.INVISIBLE) &&
                                    (txt_subway_auto_wheel_recharge.getVisibility() == View.INVISIBLE) && (txt_subway_safety_footrest.getVisibility() == View.INVISIBLE)) {
                                Toast.makeText(StationInfoActivity.this, "선택하신 역과 호선에서의 안전 시설 정보가 아직 없습니다. \n 이용에 불편함을 드려서 죄송합니다.", Toast.LENGTH_LONG).show();
                            }
                            Toast.makeText(StationInfoActivity.this, "해당 역의 안전 센터에 대한 정보는 아직 정의 되어 있지 않습니다. \n 빠른 시일내에 해결하도록 하겠습니다. " +
                                    "이용에 불편함을 드려서 죄송합니다.", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(StationInfoActivity.this, "시설 관련 정보가 아직 서비스 되지 않았습니다. 이용에 불편을 드려서 죄송합니다. 빠르게 준비하겠습니다.", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<List<InfraStructure>> call, Throwable t) {
                    Log.e(TAG, "onStart() >> getSubwayProxy() 부분 에러");
                    Log.e(TAG, t.toString());
                }
            });

            // 가까운 위치에서 역 제보 내용 가져오기
            network.getReportProxy().getSubInfoReportDataFromServer(subname, subline, new Callback<List<ReportData>>() {
                @Override
                public void onResponse(Call<List<ReportData>> call, Response<List<ReportData>> response) {
                    if (response.isSuccessful()) {
                        for (int i = 0; i < response.body().size(); i++) {
                            Log.i(TAG, "이름 : " + response.body().get(i).getName() + " // " + "제목 : " + response.body().get(i).getTitle());
                            SimpleDateFormat df = new SimpleDateFormat("YYYY-MM-dd hh:mm:ss");
                            String rptDay = df.format(response.body().get(i).getTime());
                            reportArrayList.add(new Report(response.body().get(i).getName(), response.body().get(i).getTitle(), rptDay));
                            if (i == 4)
                                break; // 최대 5개 최근 제보 데이터만 보여주기
                            // 내용 리스트에 넣어주기
                        }

                        reportListAdapter = new ReportListAdapter(reportArrayList, StationInfoActivity.this);

                        rptReCyclerView.setLayoutManager(new LinearLayoutManager(StationInfoActivity.this, LinearLayoutManager.VERTICAL, false));
                        rptReCyclerView.setHasFixedSize(true);
                        rptReCyclerView.setAdapter(reportListAdapter); // RecyclerView에 어댑터 꼽기
                        reportReset = true;
                    }
                }

                @Override
                public void onFailure(Call<List<ReportData>> call, Throwable t) {
                    Log.e(TAG, "onStart() >> getReportProxy() 부분 에러");
                    Log.e(TAG, t.toString());
                }
            });
        }
        // 아무것도 아님 >> HomeActivity에서 넘어올 때로 진행하면 된다. (자연스럽게)
        else {
            Log.e(TAG, "역과 호선이 제대로 넘어오지 않았습니다.");
        }
        return "service well done";
    }
}


*/
