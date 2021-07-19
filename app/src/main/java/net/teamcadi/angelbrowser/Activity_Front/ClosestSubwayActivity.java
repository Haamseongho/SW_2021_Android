package net.teamcadi.angelbrowser.Activity_Front;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import net.teamcadi.angelbrowser.Activity_Back.service.Network;
import net.teamcadi.angelbrowser.R;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import kr.hyosang.coordinate.CoordPoint;
import kr.hyosang.coordinate.TransCoord;

public class ClosestSubwayActivity extends AppCompatActivity {

    private static final String TAG = ClosestSubwayActivity.class.getSimpleName();
    private double myLat; // 현재 나의 위치 > 위도
    private double myLng; // 현재 나의 위치 > 경도
    private boolean isGPSEnabled = false; // GPS 사용 여부
    private boolean isNetworkEnabled = false; // 와이파이 (네트워크)
    private boolean isGetLocation = false; // GPS 상태값
    private LocationManager locationManager; // LocationManager로 위치 찾을 거임 (사용할 방법도 선택)
    private Location location;
    private StringBuilder urlBuilder; // Url 담아서 get방식으로 호출하기 위함.
    private URL url;  // url로 연결 짓기 위함

    private BufferedReader rd = null; // 받은 내용을 저장할 버퍼
    private StringBuilder sb = new StringBuilder(); // 버퍼 내의 내용을 담을 문자열 공간
    private String line; // 버퍼를 통해 받아온 정보를 임시 문자열 변수에 넣어두기
    private String abc;
    private String change[], tx[], ty[];
    private String change1, tempString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_closest_subway);

        initTedPermission();
        initGPS();
    }

    // 위치 찾기 퍼미션
    private void initTedPermission() {
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(ClosestSubwayActivity.this, "권한이 제공되었습니다.", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(ClosestSubwayActivity.this, "권한이 없습니다.", Toast.LENGTH_LONG).show();
            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setDeniedMessage("퍼미션 거절 시 앱 사용 불가")
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
                ).check();

    }

    // GPS 찾기 (현재 위치)
    private void initGPS() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // 와이파이로 위치 찾기

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                1000, 1, mLocationListener
        );

        if (locationManager != null) {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null) {
                myLat = location.getLatitude();
                myLng = location.getLongitude();
            }
        }

        // GPS로 현재 위치 찾기

        if (location == null) {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    1000, 1, mLocationListener
            );
            if (locationManager != null) {
                location = locationManager
                        .getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if (location != null) {
                    myLat = location.getLatitude();
                    myLng = location.getLongitude();
                    Log.i(TAG, "X좌표1 : " + myLat + "  Y좌표1 : " + myLng);
                }
            }

        }

        // Thread에서 웹 연결 // 메인스레드에선 불가능하므로
        new Thread() {
            @Override
            public void run() {
                super.run();
                String[] statNm = setLatLng();
                    Bundle bundle = new Bundle();
                    bundle.putString("statNm", "closest");
                    Message msg = handler.obtainMessage();
                    msg.setData(bundle);
                    handler.sendMessage(msg);
            }
        }.start();

        //new SubThread().setLatLngData(myLat, myLng);
    }


    // LocationListener에서 설정한 측정 방식을 체크한다.
    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
           /* if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
                isGPSEnabled = true;
            }
            if (location.getProvider().equals(LocationManager.NETWORK_PROVIDER)) {
                isNetworkEnabled = true;
            }*/
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    public String[] setLatLng() {

        Log.i(TAG, "현재위치 X : " + myLat + "  현재위치 Y : " + myLng);
        String nowx = String.valueOf(myLng);
        String nowy = String.valueOf(myLat);
        double x, y;
        CoordPoint pt = new CoordPoint(Double.valueOf(nowx), Double.valueOf(nowy)); //현재 wtm 좌표계
        CoordPoint ktmPt;
        ktmPt = TransCoord.getTransCoord(pt, TransCoord.COORD_TYPE_WGS84, TransCoord.COORD_TYPE_WTM);//wtm을 wgs로 wgs를 wtm으로

        nowx = String.format("%.5f", ktmPt.x);
        nowy = String.format("%.5f", ktmPt.y);
        // 변환한 값은 본래 위도,경도에서 지하철 검색 위도,경도로 변환됨


        urlBuilder = new StringBuilder("http://swopenAPI.seoul.go.kr/api/subway/78634d49496861613130375248757257/json/nearBy/0/5/");

        /*
        X축 Y축 변환
         */
        try {
            urlBuilder.append(URLEncoder.encode(nowx, "UTF-8"));

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            urlBuilder.append("/" + URLEncoder.encode(nowy, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        try {
            url = new URL(urlBuilder.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpURLConnection conn = null; // url연결 (http통신)

        try {
            conn = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            conn.setRequestMethod("GET");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }

        conn.setRequestProperty("Content-type", "application/json"); // 요청 보내고 받는 프로퍼티는 json 방식으로

        // response code 로 체크하기 200~304 good
        try {
            if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                // 잘 받았을 경우
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // BufferedReader 할 역할 다하면 닫아주기
        try {
            rd.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 공공데이터 포털에 get방식으로 요청한 connection 닫기
        conn.disconnect();

        String temp = sb.toString();

        abc = "";
        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(temp);
            org.json.simple.JSONArray jsonArray = (org.json.simple.JSONArray) jsonObject.get("stationList");

            change = new String[jsonArray.size()];
            tx = new String[jsonArray.size()];
            ty = new String[jsonArray.size()];

            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject object = (JSONObject) jsonArray.get(i);
                change[i] = object.get("statnNm").toString();
                tx[i] = object.get("subwayXcnts").toString();
                ty[i] = object.get("subwayYcnts").toString();
            }


            Log.i(TAG, "X좌표 : " + myLat + "Y좌표 : " + myLng);
            Log.i(TAG, "X좌표2 : " + nowx + "Y좌표2 : " + nowy);
            Log.i(TAG, "역 이름: " + change + "/" + "지하철 X좌표 : " + tx + "/" + "지하철 Y좌표: " + ty);

            sendChangeStatNmToServer(change, tx, ty);
        } catch (org.json.simple.parser.ParseException e) {
            e.printStackTrace();
        }
        return change;
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            String subname = bundle.getString("statNm");
            Log.i(TAG, "역이름 : " + subname);
        }
    };


    // 역 정보 보내기 >> 서버로 >> 데이터 받아서 '역정보' 페이지로 보낼 예정
    private void sendChangeStatNmToServer(final String[] subname, String[] tx, String[] ty) {
        Network network = Network.getNetworkInstance();
        // subname[0] >> 가장 가까운 역
        Log.i(TAG, "가장 가까운 역 : " + subname[0]);
// SplashActivity
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        final Intent intent = new Intent(ClosestSubwayActivity.this, ClosestSubwayMapActivity.class);
                        intent.putExtra("subwayNames", subname); // 배열을 보내는 거임
                        intent.putExtra("lat",myLat);
                        intent.putExtra("lng",myLng);
                        startActivity(intent);
                        finish();
                    }
                }, 3000);
            }
        });
    }
}
