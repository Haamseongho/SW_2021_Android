package net.teamcadi.angelbrowser.Activity_Front;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import net.teamcadi.angelbrowser.Activity_Back.service.Network;
import net.teamcadi.angelbrowser.Activity_Front.data.ClosestSubData;
import net.teamcadi.angelbrowser.Activity_Front.data.CommonSubData;
import net.teamcadi.angelbrowser.Adapters.ClosestSubwayAdapter;
import net.teamcadi.angelbrowser.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClosestSubwayMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = ClosestSubwayMapActivity.class.getSimpleName();
    private RecyclerView closestSubRecView; // 가까운 역 정보 리스트로 보여주기
    private ClosestSubwayAdapter closestSubwayAdapter; // 어댑터
    private ArrayList<ClosestSubData> closestSubDataArrayList; // 리스트에 들어갈 데이터 설정
    private Network network;
    private String[] subnames;
    private LinkedHashSet<String> subNameHashSet; // 겹치는 subwayName 안가져올 것임.
    private LinkedHashSet<String> subLineHashSet; // 겹치는 subwayLine 안가져올 것임.
    private Iterator<String> iterator1;
    private ArrayList<String> subnameList; // 리스트뷰에 담을 역 이름
    private ArrayList<String> sublineList; // 리스트뷰에 담을 라인 이름
    private Geocoder geocoder;  // 주소 값 >> 위도 & 경도로 변환
    private GoogleMap mMap; // 지도 보여줄 예정
    private double lat, lng; // 위도 , 경도
    private static String statNm; // 지하철 역 이름
    String subname;

    /*
    StationInfoActivity에 이름이랑 역을 한 번에 보내서 정보를 뽑아와야함...
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_closest_subway_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.cMap);
        mapFragment.getMapAsync(this); // 메인쓰레드에서 진행되어야 함 (워커쓰레드 X)


        findViewById(R.id.btn_closest_sub_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initViews();
    }

    private void initViews() {
        closestSubRecView = (RecyclerView) findViewById(R.id.closest_subway_RecView);
        network = Network.getNetworkInstance();
        closestSubDataArrayList = new ArrayList<>();
        subnames = getIntent().getStringArrayExtra("subwayNames"); // 보낸 배열
        subNameHashSet = new LinkedHashSet<>();
        subLineHashSet = new LinkedHashSet<>();
        sublineList = new ArrayList<>();
        subnameList = new ArrayList<>();
        statNm = subnames[0]; // 가장 가까운 역을 넣어주기

        setLocationOnTheMap();
        initListView();
    }

    private void initListView() {
        for (int i = 0; i < subnames.length; i++) {
            Log.i(TAG, "Array : " + subnames[i]);

            if (subNameHashSet.contains(subnames[i])) {
                // 포함될 경우 지우기
                subNameHashSet.remove(subnames[i]);
            }
            subNameHashSet.add(subnames[i]);
        }
        // subHashSet에는 이제 겹친 역이름이 없을 것임..!

        iterator1 = subNameHashSet.iterator();


        // iterator 패턴으로 Set 안에 들어간 내용들 (역이름) 가지고 와서 서버에 전송 >> subline만 뽑아오기 위함
        while (iterator1.hasNext()) {
            final String name = iterator1.next();
            Log.i(TAG, "Iterator name: " + name);
            network.getSubwayProxy().getSubLineBySubName(name, new Callback<List<net.teamcadi.angelbrowser.Activity_Back.data.ClosestSubData>>() {
                @Override
                public void onResponse(Call<List<net.teamcadi.angelbrowser.Activity_Back.data.ClosestSubData>> call, Response<List<net.teamcadi.angelbrowser.Activity_Back.data.ClosestSubData>> response) {
                    if (response.isSuccessful()) {
                        for (int i = 0; i < response.body().size(); i++) {
                            Log.i(TAG, "size: " + response.body().size());
                            Log.i(TAG, "역 라인:" + response.body().get(i).getSubline());
                            //closestSubDataArrayList.add(new ClosestSubData(name,response.body().get(i).getSubline(),response.body().get(i).getSubline2(),response.body().get(i).getSubline3(),response.body().get(i).getSubline4()));
                            // 단일역
                            if (response.body().size() == 1) {
                                closestSubDataArrayList.add(new ClosestSubData(name, response.body().get(0).getSubline(), "", "", ""));
                                break;
                            }
                            // 환승역 1개
                            else if (response.body().size() == 2) {
                                Log.i(TAG, "subline1 : " + response.body().get(0).getSubline() + " / subline2: " + response.body().get(1).getSubline());
                                closestSubDataArrayList.add(new ClosestSubData(name, response.body().get(0).getSubline(), response.body().get(1).getSubline(), "", ""));
                                break;
                            }
                            // 환승역 2개
                            else if (response.body().size() == 3) {
                                closestSubDataArrayList.add(new ClosestSubData(name, response.body().get(0).getSubline(), response.body().get(1).getSubline(), response.body().get(2).getSubline(), ""));
                                break;
                            }
                            // 환승역 3개
                            else if (response.body().size() == 4) {
                                closestSubDataArrayList.add(new ClosestSubData(name, response.body().get(0).getSubline(), response.body().get(1).getSubline(), response.body().get(2).getSubline(), response.body().get(3).getSubline()));
                                break;
                            }
                            // 아무것도 아님
                            else {
                                Log.e(TAG, "환승역 정보 넣어주세요");
                            }
                            closestSubDataArrayList.add(new ClosestSubData(name, response.body().get(i).getSubline()));
                        }
                        // 리스트뷰 정리
                        closestSubwayAdapter = new ClosestSubwayAdapter(closestSubDataArrayList, ClosestSubwayMapActivity.this);
                        closestSubRecView.setLayoutManager(new LinearLayoutManager(ClosestSubwayMapActivity.this, LinearLayoutManager.VERTICAL, false));
                        closestSubRecView.setAdapter(closestSubwayAdapter);
                    }
                }

                @Override
                public void onFailure(Call<List<net.teamcadi.angelbrowser.Activity_Back.data.ClosestSubData>> call, Throwable t) {
                    Log.e(TAG, t.toString());
                }
            });
        }
    }

    // 지도 보이기 (위도,경도 체크 >> 주소에 따라서 변경하기)
    private void setLocationOnTheMap() {
        geocoder = new Geocoder(this);
        List<Address> list = null;
        if (statNm == null)
            statNm = "시청역";

        Log.e(TAG, statNm + "이름"); // 역이 안붙어져 있을 거임

        // 서울역만 별도로 진행
        if (statNm == "서울역")
            statNm = "서울역";
        else
            statNm += "역";

        try {
            list = geocoder.getFromLocationName(
                    statNm, 10
            );
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "서버에서 주소 변환 에러 발생 ");
        }
        if (list != null) {
            if (list.size() == 0) {
                Toast.makeText(ClosestSubwayMapActivity.this, "해당되는 주소 정보는 존재하지 않습니다.", Toast.LENGTH_LONG).show();
            } else {
                Log.i(TAG, "위도: " + list.get(0).getLatitude());
                Log.i(TAG, "경도: " + list.get(0).getLongitude());
                lat = list.get(0).getLatitude();
                lng = list.get(0).getLongitude();
            }
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        MarkerOptions markerOptions = new MarkerOptions();
        Double myLat = getIntent().getExtras().getDouble("lat");  // GPS or Network로 위도 가지고 온 것
        Double myLng = getIntent().getExtras().getDouble("lng");  // GPS or Network로 경도 가지고 온 것

        markerOptions.position(new LatLng(myLat, myLng)).title("가장 가까운 역은 " + statNm + "입니다.");
        mMap.addMarker(markerOptions);
        // 가장 가까운 역

        TextView nearestSpot = new TextView(this);
        nearestSpot.setBackgroundResource(R.drawable.boxed_area_background);
        nearestSpot.setTextColor(Color.WHITE);
        nearestSpot.setPadding(5, 5, 5, 5);
        nearestSpot.setText("가장 가까운 역");
        nearestSpot.setTextSize(10);

        mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(this, nearestSpot))));
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setTrafficEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        Log.i(TAG, "위도 경도 확인 : " + lat + "/" + lng);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(myLat, myLng)));
    }

    private Bitmap createDrawableFromView(Context context, View view) {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }

}
