package net.teamcadi.angelbrowser.Activity_Front;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.maps.model.MarkerOptions;
import com.ssomai.android.scalablelayout.ScalableLayout;

import net.teamcadi.angelbrowser.Activity_Front.data.TransferSubName;
import net.teamcadi.angelbrowser.Adapters.ReportListAdapter;
import net.teamcadi.angelbrowser.Adapters.TransferAdapter;
import net.teamcadi.angelbrowser.R;

import java.util.ArrayList;

public class TransferActivity extends AppCompatActivity {

    private static final String TAG = TransferActivity.class.getSimpleName();
    private TextView txt_department_subwayNm; // 출발역
    private TextView txt_arrival_subwayNm; // 도착역
    private TextView txt_transfer_minTime; // 최소 환승 시간
    private TextView txt_transfer_minCount; // 최소 환승 횟수
    private TextView txt_transfer_minMsg; // 최소 환승 시 관련 정보 메세지
    //    private TextView txt_transfer_subwayNm; // 최소 환승 시 지나는 역들

    private String[] minStatnNm; // 최소 환승 시 지나는 역들을 담을 배열
    private String[] minStatnId; // 최소 환승 시 지나는 역들에 대한 ID값 담는 배열

    static final int MAX = 100;
    private TextView[] txt_transfer_subwayNmArray; // 지나는 역들 이름 담는 배열
    private LinearLayout transSubInfoFrame; // 지나는 역 담는 프레임
    private TextView txt_movement_times; // 이동 횟수

    private String departSubline, arriveSubline;
    private View subLineView; // 노선 그리기
    private String transCnt = ""; // 환승 관련 메세지에서 환승 횟수만 뽑을 것
    private ScrollView subRouteScrollView;
    private LinearLayout subRoute_Frame; // 지하철 지나는 노선 넣기 위함

    private ArrayList<TransferSubName> transferSubNames; // 이거 가지고 역 이름이랑 역 호선 색 변경시킬것
    private RecyclerView transCyclerView; // 환승역 정보 나오게 하는 cyclerView
    private TransferAdapter transferAdapter; // 어댑터 (환승 관련 내용)
    /*
    환승역에 대한 정보
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);
        initTransCnt();
        initViews();
    }


    private void initTransCnt() {
        String transMsg = getIntent().getExtras().getString("sht_transfer_Msg");
        txt_transfer_minCount = (TextView) findViewById(R.id.txt_transfer_minCount);
        Log.i(TAG, transMsg.length() + "길이 체크");
        for (int i = 0; i < transMsg.length(); i++) {
            if (transMsg.charAt(i) == '번') {
                transCnt = "" + transMsg.charAt(i - 1) + transMsg.charAt(i);
                break;
            }
        }
        txt_transfer_minCount.setText(transCnt);

    }

    private void initViews() {
        txt_department_subwayNm = (TextView) findViewById(R.id.txt_departure_statNm);
        txt_arrival_subwayNm = (TextView) findViewById(R.id.txt_arrival_statNm);
        txt_transfer_minTime = (TextView) findViewById(R.id.txt_transfer_minTime);

        txt_movement_times = (TextView) findViewById(R.id.txt_move_cnt);
        //txt_transfer_subwayNm = (TextView) findViewById(R.id.txt_transfer_subwayNm);

        transferSubNames = new ArrayList<>();
        transCyclerView = (RecyclerView) findViewById(R.id.transfer_cyclerView);
        transCyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        // 내용 넣기


        txt_department_subwayNm.setText(getIntent().getExtras().getString("depart_statNm"));
        txt_arrival_subwayNm.setText(getIntent().getExtras().getString("arrive_statNm"));
        txt_transfer_minTime.setText(getIntent().getExtras().getString("sht_transfer_minTime") + "분");
        // txt_transfer_minCount.setText(getIntent().getExtras().getString("min_transfer_count") + "번");

        minStatnNm = new String[MAX]; // 최대 100 >> 지하철 역 넣기
        minStatnId = new String[MAX]; // 최대 100 >> 지하철 역 ID값 넣기

        // 배열로 설정하게 되면 어차피 split으로 끊은 내용들은 각각 배열로 들어가기 때문에 다음과 같이 작업을 해도 된다.
        minStatnNm = getIntent().getExtras().getString("sht_transfer_statnNm").split(",");
        // ',' 로 구분 짓기 --> 지나는 역 표시
        minStatnId = getIntent().getExtras().getString("sht_transfer_statnId").split(",");
        // ',' 로 구분 짓기 --> 지나는 역의 ID 값 표시


        System.out.println(getIntent().getExtras().getString("sht_transfer_Msg"));

        txt_movement_times.setText((minStatnNm.length - 1) + "개"); // 배열의 수 == 지나는 횟

        departSubline = getIntent().getExtras().getString("depart_subline"); // 출발역 노선
        arriveSubline = getIntent().getExtras().getString("arrive_subline"); // 도착역 노선

        // 뒤로가기 누를 경우 (이미지)
        findViewById(R.id.btn_back_transfer_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // 뒤로가기 버튼과 동일
            }
        });

        for (int i = 0; i < minStatnNm.length; i++) {
            transferSubNames.add(new TransferSubName(minStatnNm[i], Integer.parseInt(minStatnId[i])));
            Log.i(TAG, "Subway ID :  " + minStatnId[i]);
            Log.i(TAG, "Subway Name : " + minStatnNm[i]);
        }

        transCyclerView.setHasFixedSize(true);
        transferAdapter = new TransferAdapter(this,transferSubNames);
        transCyclerView.setAdapter(transferAdapter);

/*
        txt_transfer_subwayNmArray = new TextView[MAX];

        // 경유하는 역 이름 적을 LayoutParams
        RelativeLayout.LayoutParams textParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        // View 그려주는 LayoutParams
        RelativeLayout.LayoutParams viewParams = new RelativeLayout.LayoutParams(3, 12);

        int height = 0;*/
/*        for (int i = 0; i < minStatnNm.length; i++) {
            txt_transfer_subwayNmArray[i] = new TextView(TransferActivity.this); // 숫자 만큼 생성을 한다.
            subLineView = new View(TransferActivity.this);
            // params는 margin을 잘 줘서 정리하도록 구현해야함 >> 우선 얼마나 갈 지 모르니 5의 배수로 해서 탑마진 두어 내려가도록 구현

            height += 5 * (i + 1);
            textParams.setMargins(20, height, 0, 5);
            viewParams.setMargins(10, height, 0, 5);

            Log.i(TAG, "Mesured Height: " + transSubInfoFrame.getHeight());
            txt_transfer_subwayNmArray[i].setText(minStatnNm[i]); // 배열에 하나 씩 역 이름을 넣는다.
            // txt_transfer_subwayNmArray[i].setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
//            txt_transfer_subwayNmArray[i].setBackground(ContextCompat.getDrawable(this, R.drawable.boxed_area_background_white));

            // 지하철 이용 경로에 따라 보여주는 레이아웃에 해당 내용 넣기 (6개 단위로 넣을 예정)
         *//*   if (i < 6) {
                subRouteFrame1.addView(txt_transfer_subwayNmArray[i], textParams);
                subRouteFrame1.addView(subLineView, viewParams);
            } else if (6 <= i && i < 12) {
                subRouteFrame2.addView(txt_transfer_subwayNmArray[i], textParams);
                subRouteFrame2.addView(subLineView, viewParams);
            } else if (12 <= i && i < 18) {
                subRouteFrame3.addView(txt_transfer_subwayNmArray[i], textParams);
                subRouteFrame3.addView(subLineView, viewParams);
            } else if (18 <= i && i < 24) {
                subRouteFrame4.addView(txt_transfer_subwayNmArray[i], textParams);
                subRouteFrame4.addView(subLineView, viewParams);
            } else if (24 <= i && i < 30) {
                subRouteFrame5.addView(txt_transfer_subwayNmArray[i], textParams);
                subRouteFrame5.addView(subLineView, viewParams);
            } else {
                subRouteFrame6.addView(txt_transfer_subwayNmArray[i], textParams);
                subRouteFrame6.addView(subLineView, viewParams);
            }*//*

            subRoute_Frame.addView(txt_transfer_subwayNmArray[i], textParams);
        }*/
    }
}
