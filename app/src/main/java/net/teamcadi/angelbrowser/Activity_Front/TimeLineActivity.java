package net.teamcadi.angelbrowser.Activity_Front;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import net.teamcadi.angelbrowser.Activity_Back.data.ChatMessage;
import net.teamcadi.angelbrowser.Activity_Back.data.Timeline;
import net.teamcadi.angelbrowser.Activity_Back.service.Network;
import net.teamcadi.angelbrowser.Activity_Front.data.TimeLineData;
import net.teamcadi.angelbrowser.Adapters.TimeLineAdapter;
import net.teamcadi.angelbrowser.R;
import net.teamcadi.angelbrowser.SharedPref.SharedPrefStorage;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TimeLineActivity extends AppCompatActivity {

    private static final String TAG = TimeLineActivity.class.getSimpleName();
    private static String statNm;
    private RecyclerView upTimeLineView; // 상행 리사이클러뷰
    private RecyclerView dnTimeLineView; // 하행 리사이클러뷰
    private TextView timeLineStatnNm; // 타임라인 현 위치
    private Button btnTimeLine1, btnTimeLine2, btnTimeLine3, btnTimeLine4; // 역 내 환승역에 따른 호선 나눌 번호 버튼
    private Network network;
    private ArrayList<TimeLineData> timeLineUpList;  // 상행 부분의 리스트
    private ArrayList<TimeLineData> timeLineDnList;  // 하행 부분의 리스트
    private TimeLineAdapter timeLineAdapter1, timeLineAdapter2; // 사이클러뷰에 담을 내용의 어댑터 1 > 상행 , 2 > 하행
    private SharedPrefStorage pref;
    private ArrayList<String> subwayLnList; // 호선 리스트
    private ArrayList<String> subLineList; // 라인 넣을 리스트
    private String subline1; // 버튼 1에 따른 호선 subwayId
    private String subline2; // 버튼 2에 따른 호선 subwayId
    private String subline3; // 버튼 3에 따른 호선 subwayId
    private String subline4; // 버튼 4에 따른 호선
    private boolean timeLineUsed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_line);

        statNm = getIntent().getExtras().getString("statnNm", null); // 역 이름 가져온 것

        initViews();
        initButtonActivated();
    }

    private void initViews() {
        timeLineStatnNm = (TextView) findViewById(R.id.timeline_station_name);
        timeLineStatnNm.setText(statNm);
        btnTimeLine1 = (Button) findViewById(R.id.btn_timeline1);
        btnTimeLine2 = (Button) findViewById(R.id.btn_timeline2);
        btnTimeLine3 = (Button) findViewById(R.id.btn_timeline3);
        btnTimeLine4 = (Button) findViewById(R.id.btn_timeline4);
        upTimeLineView = (RecyclerView) findViewById(R.id.timeLine_Up_RecyclerView);
        dnTimeLineView = (RecyclerView) findViewById(R.id.timeLine_Dn_RecyclerView);
        network = Network.getNetworkInstance(); // 서버 연결
        timeLineUpList = new ArrayList<>();
        timeLineDnList = new ArrayList<>();
    }

    private void initButtonActivated() {
        // 버튼 활성화
        // 역 이름으로 호선 가지고 오기 -> 호선 가지고 온 걸로 역이름이랑 같이 키값으로 쓸 것
        pref = new SharedPrefStorage(this);
        final List<ChatMessage> chatList = new ArrayList<>();
        subwayLnList = new ArrayList<>();
        subLineList = new ArrayList<>();

        String mResult = pref.getClientResult("mResult"); // 첫 번째 질문에 대한 답변으로 보내는 부분 (Client >> 역정보)
        Log.i(TAG, mResult);


        network.getChatProxy().receiveMessageFromServer(mResult, new Callback<List<ChatMessage>>() {
            @Override
            public void onResponse(Call<List<ChatMessage>> call, Response<List<ChatMessage>> response) {
                if (response.isSuccessful()) {
                    for (int i = 0; i < response.body().size(); i++) {
                        // subwayLnList >> 호선 정보 리스트로 넣어 준 것
                        subwayLnList.add(response.body().get(i).getSubwayNm());
                    }
                    setLineByBtnClick(subwayLnList);

                }
            }

            @Override
            public void onFailure(Call<List<ChatMessage>> call, Throwable t) {
                Log.e(TAG, t.toString());
            }
        });
    }

    private void setLineByBtnClick(ArrayList<String> subwayLnList) {
        subLineList.add(subwayLnList.get(0)); // 초기 값은 넣어주어야 비교 분석이 가능합니다.
        btnTimeLine1.setText(subLineList.get(0)); // 버튼 1에 호선 적어주기
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
            btnTimeLine2.setText(subLineList.get(1));
            btnTimeLine3.setText(subLineList.get(2));
            btnTimeLine4.setText(subLineList.get(3));
            // 버튼을 만들어야함 ..
        } else if (subLineList.size() == 4) {
            btnTimeLine2.setText(subLineList.get(1));
            btnTimeLine3.setText(subLineList.get(2));
            btnTimeLine4.setText(subLineList.get(3));
            btnTimeLine2.setVisibility(View.VISIBLE);
            btnTimeLine3.setVisibility(View.VISIBLE);
            btnTimeLine4.setVisibility(View.VISIBLE);
            // 정확한 숫자대로 맞춰짐
        } else if (subLineList.size() == 3) {
            btnTimeLine2.setText(subLineList.get(1));
            btnTimeLine3.setText(subLineList.get(2));
            btnTimeLine2.setVisibility(View.VISIBLE);
            btnTimeLine3.setVisibility(View.VISIBLE);
            // 말이 안됨 (역의 호선이 없다는 것임 ㅡㅡ..)
        } else if (subLineList.size() == 2) {
            btnTimeLine2.setText(subLineList.get(1));
            btnTimeLine2.setVisibility(View.VISIBLE);
        } else {
            // Nothing special
        }

        final String mResult = pref.getClientResult("mResult"); // 첫 번째 질문에 대한 답변으로 보내는 부분 (Client >> 역정보)
        // 첫 번째 호선 버튼 눌렀을 경우


        switch (btnTimeLine1.getText().toString()) {
            case "1호선":
                subline1 = "1001";
                break;
            case "2호선":
                subline1 = "1002";
                break;
            case "3호선":
                subline1 = "1003";
                break;
            case "4호선":
                subline1 = "1004";
                break;
            case "5호선":
                subline1 = "1005";
                break;
            case "6호선":
                subline1 = "1006";
                break;
            case "7호선":
                subline1 = "1007";
                break;
            case "8호선":
                subline1 = "1008";
                break;
            case "경의중앙선":
                subline1 = "1063";
                break;
            case "공항철도":
                subline1 = "1065";
                break;
            case "신분당선":
                subline1 = "1077";
                break;
            case "분당선":
                subline1 = "1075";
                break;
            case "9호선":
                subline1 = "1009";
                break;


        }

        switch (btnTimeLine2.getText().toString()) {
            case "1호선":
                subline2 = "1001";
                break;
            case "2호선":
                subline2 = "1002";
                break;
            case "3호선":
                subline2 = "1003";
                break;
            case "4호선":
                subline2 = "1004";
                break;
            case "5호선":
                subline2 = "1005";
                break;
            case "6호선":
                subline2 = "1006";
                break;
            case "7호선":
                subline2 = "1007";
                break;
            case "8호선":
                subline2 = "1008";
                break;
            case "경의중앙선":
                subline2 = "1063";
                break;
            case "공항철도":
                subline2 = "1065";
                break;
            case "신분당선":
                subline2 = "1077";
                break;
            case "분당선":
                subline2 = "1075";
                break;
            case "9호선":
                subline2 = "1009";
                break;


        }

        switch (btnTimeLine3.getText().toString()) {
            case "1호선":
                subline3 = "1001";
                break;
            case "2호선":
                subline3 = "1002";
                break;
            case "3호선":
                subline3 = "1003";
                break;
            case "4호선":
                subline3 = "1004";
                break;
            case "5호선":
                subline3 = "1005";
                break;
            case "6호선":
                subline3 = "1006";
                break;
            case "7호선":
                subline3 = "1007";
                break;
            case "8호선":
                subline3 = "1008";
                break;
            case "경의중앙선":
                subline3 = "1063";
                break;
            case "공항철도":
                subline3 = "1065";
                break;
            case "신분당선":
                subline3 = "1077";
                break;
            case "분당선":
                subline3 = "1075";
                break;
            case "9호선":
                subline3 = "1009";
                break;


        }
        switch (btnTimeLine4.getText().toString()) {
            case "1호선":
                subline4 = "1001";
                break;
            case "2호선":
                subline4 = "1002";
                break;
            case "3호선":
                subline4 = "1003";
                break;
            case "4호선":
                subline4 = "1004";
                break;
            case "5호선":
                subline4 = "1005";
                break;
            case "6호선":
                subline4 = "1006";
                break;
            case "7호선":
                subline4 = "1007";
                break;
            case "8호선":
                subline4 = "1008";
                break;
            case "경의중앙선":
                subline4 = "1063";
                break;
            case "공항철도":
                subline4 = "1065";
                break;
            case "신분당선":
                subline4 = "1077";
                break;
            case "분당선":
                subline4 = "1075";
                break;
            case "9호선":
                subline4 = "1009";
                break;


        }


        // 1번 버튼 클릭 시 >> 역 정보 + 호선 (subwayId로 넘어가서 정보 가지고 오기)
        btnTimeLine1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (timeLineUsed) {
                    timeLineUpList.clear();
                    upTimeLineView.setAdapter(null);
                    timeLineDnList.clear();
                    dnTimeLineView.setAdapter(null);
                    timeLineUsed = false;
                }


                network.getSubwayProxy().getTimeLineFromServer(statNm, subline1, new Callback<List<Timeline>>() {
                    @Override
                    public void onResponse(Call<List<Timeline>> call, Response<List<Timeline>> response) {
                        // 역 이름이랑 호선으로 출력 , 호선으로 넘겨줄 경우 서버에서 변환을 해줘야 한다. 2호선 >> 1002
                        if (response.isSuccessful()) {

                            if (response.body().size() == 0) {
                                Toast.makeText(TimeLineActivity.this, "서비스 준비중입니다. 죄송합니다.", Toast.LENGTH_LONG).show();
                            }
                            for (int i = 0; i < response.body().size(); i++) {
                                // 역, 호선으로 가지고 온 정보가 상행 일 경우
                                if (response.body().get(i).getUpdnLine().equals("상행")) {
                                    timeLineUpList.add(new TimeLineData(Integer.parseInt(response.body().get(i).getBarvlDt()) / 60 + "분 " + Integer.parseInt(response.body().get(i).getBarvlDt()) % 60 + "초", response.body().get(i).getArvlMsg2(),
                                            response.body().get(i).getTrainLineNm(), response.body().get(i).getBtrainStts()));
                                } else if (response.body().get(i).getUpdnLine().equals("하행")) {
                                    timeLineDnList.add(new TimeLineData(Integer.parseInt(response.body().get(i).getBarvlDt()) / 60 + "분 " + Integer.parseInt(response.body().get(i).getBarvlDt()) % 60 + "초", response.body().get(i).getArvlMsg2(),
                                            response.body().get(i).getTrainLineNm(), response.body().get(i).getBtrainStts()));
                                }
                            }
                            // 상행
                            timeLineAdapter1 = new TimeLineAdapter(timeLineUpList, TimeLineActivity.this);
                            upTimeLineView.setLayoutManager(new LinearLayoutManager(TimeLineActivity.this, LinearLayoutManager.VERTICAL, false));
                            upTimeLineView.setAdapter(timeLineAdapter1);
                            // 하행
                            timeLineAdapter2 = new TimeLineAdapter(timeLineDnList, TimeLineActivity.this);
                            dnTimeLineView.setLayoutManager(new LinearLayoutManager(TimeLineActivity.this, LinearLayoutManager.VERTICAL, false));
                            dnTimeLineView.setAdapter(timeLineAdapter2);
                            timeLineUsed = true;
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Timeline>> call, Throwable t) {
                        Toast.makeText(TimeLineActivity.this, "서비스 준비중입니다. 이용에 불편함을 드려서 죄송합니다.", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        // 2번 버튼 클릭 시 >> 역 정보 + 호선 (subwayId로 넘어가서 정보 가지고 오기)
        btnTimeLine2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (timeLineUsed) {
                    timeLineUpList.clear();
                    upTimeLineView.setAdapter(null);
                    timeLineDnList.clear();
                    dnTimeLineView.setAdapter(null);
                    timeLineUsed = false;
                }

                network.getSubwayProxy().getTimeLineFromServer(statNm, subline2, new Callback<List<Timeline>>() {
                    @Override
                    public void onResponse(Call<List<Timeline>> call, Response<List<Timeline>> response) {
                        // 역 이름이랑 호선으로 출력 , 호선으로 넘겨줄 경우 서버에서 변환을 해줘야 한다. 2호선 >> 1002
                        if (response.isSuccessful()) {
                            if (response.body().size() == 0) {
                                Toast.makeText(TimeLineActivity.this, "서비스 준비중입니다. 죄송합니다.", Toast.LENGTH_LONG).show();
                            }
                            for (int i = 0; i < response.body().size(); i++) {
                                // 역, 호선으로 가지고 온 정보가 상행 일 경우
                                if (response.body().get(i).getUpdnLine().equals("상행")) {
                                    timeLineUpList.add(new TimeLineData(Integer.parseInt(response.body().get(i).getBarvlDt()) / 60 + "분 " + Integer.parseInt(response.body().get(i).getBarvlDt()) % 60 + "초", response.body().get(i).getArvlMsg2(),
                                            response.body().get(i).getTrainLineNm(), response.body().get(i).getBtrainStts()));
                                } else if (response.body().get(i).getUpdnLine().equals("하행")) {
                                    timeLineDnList.add(new TimeLineData(Integer.parseInt(response.body().get(i).getBarvlDt()) / 60 + "분 " + Integer.parseInt(response.body().get(i).getBarvlDt()) % 60 + "초", response.body().get(i).getArvlMsg2(),
                                            response.body().get(i).getTrainLineNm(), response.body().get(i).getBtrainStts()));
                                }
                            }
                            // 상행
                            timeLineAdapter1 = new TimeLineAdapter(timeLineUpList, TimeLineActivity.this);
                            upTimeLineView.setLayoutManager(new LinearLayoutManager(TimeLineActivity.this, LinearLayoutManager.VERTICAL, false));
                            upTimeLineView.setAdapter(timeLineAdapter1);
                            // 하행
                            timeLineAdapter2 = new TimeLineAdapter(timeLineDnList, TimeLineActivity.this);
                            dnTimeLineView.setLayoutManager(new LinearLayoutManager(TimeLineActivity.this, LinearLayoutManager.VERTICAL, false));
                            dnTimeLineView.setAdapter(timeLineAdapter2);
                            timeLineUsed = true;
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Timeline>> call, Throwable t) {
                        Toast.makeText(TimeLineActivity.this, "서비스 준비중입니다. 이용에 불편함을 드려서 죄송합니다.", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        // 3번 버튼 클릭 시 >> 역 정보 + 호선 (subwayId로 넘어가서 정보 가지고 오기)
        btnTimeLine3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (timeLineUsed) {
                    timeLineUpList.clear();
                    upTimeLineView.setAdapter(null);
                    timeLineDnList.clear();
                    dnTimeLineView.setAdapter(null);
                    timeLineUsed = false;
                }

                network.getSubwayProxy().getTimeLineFromServer(statNm, subline3, new Callback<List<Timeline>>() {
                    @Override
                    public void onResponse(Call<List<Timeline>> call, Response<List<Timeline>> response) {
                        // 역 이름이랑 호선으로 출력 , 호선으로 넘겨줄 경우 서버에서 변환을 해줘야 한다. 2호선 >> 1002
                        if (response.isSuccessful()) {
                            if (response.body().size() == 0) {
                                Toast.makeText(TimeLineActivity.this, "서비스 준비중입니다. 죄송합니다.", Toast.LENGTH_LONG).show();
                            }
                            for (int i = 0; i < response.body().size(); i++) {
                                // 역, 호선으로 가지고 온 정보가 상행 일 경우
                                if (response.body().get(i).getUpdnLine().equals("상행")) {
                                    timeLineUpList.add(new TimeLineData(Integer.parseInt(response.body().get(i).getBarvlDt()) / 60 + "분 " + Integer.parseInt(response.body().get(i).getBarvlDt()) % 60 + "초", response.body().get(i).getArvlMsg2(),
                                            response.body().get(i).getTrainLineNm(), response.body().get(i).getBtrainStts()));
                                } else if (response.body().get(i).getUpdnLine().equals("하행")) {
                                    timeLineDnList.add(new TimeLineData(Integer.parseInt(response.body().get(i).getBarvlDt()) / 60 + "분 " + Integer.parseInt(response.body().get(i).getBarvlDt()) % 60 + "초", response.body().get(i).getArvlMsg2(),
                                            response.body().get(i).getTrainLineNm(), response.body().get(i).getBtrainStts()));
                                }
                            }
                            timeLineAdapter1 = new TimeLineAdapter(timeLineUpList, TimeLineActivity.this);
                            upTimeLineView.setLayoutManager(new LinearLayoutManager(TimeLineActivity.this, LinearLayoutManager.VERTICAL, false));
                            upTimeLineView.setAdapter(timeLineAdapter1);
                            // 하행
                            timeLineAdapter2 = new TimeLineAdapter(timeLineDnList, TimeLineActivity.this);
                            dnTimeLineView.setLayoutManager(new LinearLayoutManager(TimeLineActivity.this, LinearLayoutManager.VERTICAL, false));
                            dnTimeLineView.setAdapter(timeLineAdapter2);
                            timeLineUsed = true;
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Timeline>> call, Throwable t) {
                        Toast.makeText(TimeLineActivity.this, "서비스 준비중입니다. 이용에 불편함을 드려서 죄송합니다.", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        // 4번 버튼 클릭 시 >> 역 정보 + 호선 (subwayId로 넘어가서 정보 가지고 오기)
        btnTimeLine4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (timeLineUsed) {
                    timeLineUpList.clear();
                    upTimeLineView.setAdapter(null);
                    timeLineDnList.clear();
                    dnTimeLineView.setAdapter(null);
                    timeLineUsed = false;
                }

                network.getSubwayProxy().getTimeLineFromServer(statNm, subline4, new Callback<List<Timeline>>() {
                    @Override
                    public void onResponse(Call<List<Timeline>> call, Response<List<Timeline>> response) {
                        // 역 이름이랑 호선으로 출력 , 호선으로 넘겨줄 경우 서버에서 변환을 해줘야 한다. 2호선 >> 1002
                        if (response.isSuccessful()) {
                            if (response.body().size() == 0) {
                                Toast.makeText(TimeLineActivity.this, "서비스 준비중입니다. 죄송합니다.", Toast.LENGTH_LONG).show();
                            }
                            for (int i = 0; i < response.body().size(); i++) {
                                // 역, 호선으로 가지고 온 정보가 상행 일 경우
                                if (response.body().get(i).getUpdnLine().equals("상행")) {
                                    timeLineUpList.add(new TimeLineData(Integer.parseInt(response.body().get(i).getBarvlDt()) / 60 + "분 " + Integer.parseInt(response.body().get(i).getBarvlDt()) % 60 + "초", response.body().get(i).getArvlMsg2(),
                                            response.body().get(i).getTrainLineNm(), response.body().get(i).getBtrainStts()));
                                } else if (response.body().get(i).getUpdnLine().equals("하행")) {
                                    timeLineDnList.add(new TimeLineData(Integer.parseInt(response.body().get(i).getBarvlDt()) / 60 + "분 " + Integer.parseInt(response.body().get(i).getBarvlDt()) % 60 + "초", response.body().get(i).getArvlMsg2(),
                                            response.body().get(i).getTrainLineNm(), response.body().get(i).getBtrainStts()));
                                }
                            }
                            // 상행
                            timeLineAdapter1 = new TimeLineAdapter(timeLineUpList, TimeLineActivity.this);
                            upTimeLineView.setLayoutManager(new LinearLayoutManager(TimeLineActivity.this, LinearLayoutManager.VERTICAL, false));
                            upTimeLineView.setAdapter(timeLineAdapter1);
                            // 하행
                            timeLineAdapter2 = new TimeLineAdapter(timeLineDnList, TimeLineActivity.this);
                            dnTimeLineView.setLayoutManager(new LinearLayoutManager(TimeLineActivity.this, LinearLayoutManager.VERTICAL, false));
                            dnTimeLineView.setAdapter(timeLineAdapter2);
                            timeLineUsed = true;
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Timeline>> call, Throwable t) {
                        Toast.makeText(TimeLineActivity.this, "서비스 준비중입니다. 이용에 불편함을 드려서 죄송합니다.", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}
