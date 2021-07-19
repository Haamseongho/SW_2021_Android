package net.teamcadi.angelbrowser.Activity_Front;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import net.teamcadi.angelbrowser.Activity_Front.data.PiaListData;
import net.teamcadi.angelbrowser.R;
import net.teamcadi.angelbrowser.SharedPref.SharedPrefStorage;
import net.teamcadi.angelbrowser.Utils.BackgroundPopupWindow.BackgroundBlurPopupWindow;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class PersonInfoAgree extends Activity {
    private static final String TAG = PersonInfoAgree.class.getSimpleName();
    // 항목 리스트뷰 관련.
    private ListView piaCheckList = null;
    private ListViewAdapter piaListViewAdapter = null;

    // 항목별 상세보기 처리 - '서비스 약관' 관련.
    private Button btnServiceTerms; // '서비스 약관' 상세보기 버튼.
    private CheckBox cbService; // '서비스 약관' 동의하기 체크 버튼.
    private ImageView btnTermsAgree; // '서비스 약관' 상세보기 닫기 버튼.

    // 항목별 상세보기 처리 - '개인정보 수집 항목' 관련.
    private Button btnInfoCollect; // '개인정보 수집 항목' 상세보기 버튼.
    private CheckBox cbInfoCollect; // '개인정보 수집 항목' 체크 버튼.
    private ImageView btnCollectAgree; // '개인정보 수집 항목' 닫기 버튼.

    // 항목별 상세보기 처리 - '개인정보 수집 및 이용 목적' 관련.
    private Button btnPurpose; // '개인정보 수집 및 이용 목적' 상세보기 버튼.
    private CheckBox cbPurpose; // '개인정보 수집 및 이용 목적' 체크 버튼.
    private ImageView btnPurposeAgree; // '개인정보 수집 항목' 닫기 버튼.

    // 항목별 상세보기 처리 - '개인정보 보유 및 이용 기간' 관련.
    private Button btnDate; // '개인정보 보유 및 이용기간' 상세보기 버튼.
    private CheckBox cbDate; // '개인정보 보유 및 이용기간' 체크 버튼.
    private ImageView btnDateAgree; // '개인정보 보유 및 이용기간' 닫기 버튼.

    // 항목별 상세보기 처리 - '모두 동의하기' 항목
    private CheckBox cbAllCheck; // '모두 동의하기' 체크 버튼.

    // 팝업 윈도우 처리를 위한 부분.
    private BackgroundBlurPopupWindow popupWindow; // 팝업 윈도우 객체.
    private int mWidthPixels, mHeightPixels; // 가로, 세로 픽셀.

    // '동의서 제출하기' 버튼을 누를 시, 날짜와 시간을 기록하기 위한 부분.
    private GregorianCalendar today; // 캘린더 호출.
    int year, month, day; // 연도, 월, 일.
    int hour, minute; // 시, 분.
    String agreeSubmitDate; // '동의서 제출하기' 버튼 클릭 시간 저장.

    // '동의서 제출하기' 버튼.
    private Button btnSubmitAgree;
    private SharedPrefStorage prefStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.person_info_agree);

        // SharedPreference ;
        prefStorage = new SharedPrefStorage(this);

        // CustomListView 선언 및 장착.
        piaCheckList = (ListView) findViewById(R.id.piaCheckList);
        piaListViewAdapter = new ListViewAdapter(this);
        piaCheckList.setAdapter(piaListViewAdapter);

        // 항목 추가 및 입력.
        piaListViewAdapter.addItem("서비스 약관");
        piaListViewAdapter.addItem("개인정보 수집 항목");
        piaListViewAdapter.addItem("개인정보 수집 및 이용 목적");
        piaListViewAdapter.addItem("개인정보 보유 및 이용 기간");
        piaListViewAdapter.addItem("개인정보 수집 및 이용에 모두 동의합니다.");

        // 팝업 윈도우 생성을 위한 설정.
        WindowManager w = getWindowManager();
        Display d = w.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        d.getMetrics(metrics);

        // 가로, 세로 픽셀 연동.
        mWidthPixels = metrics.widthPixels;
        mHeightPixels = metrics.heightPixels;

        // '서비스 약관' 상세보기 버튼 제어.
        btnServiceTerms = (Button) findViewById(R.id.btnServiceTerms);
        btnServiceTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initiateServicePopup(); // '서비스 약관' 상세보기 창 띄우기.
            }
        });

        // '개인정보 수집 항목' 상세보기 버튼 제어.
        btnInfoCollect = (Button) findViewById(R.id.btnInfoCollect);
        btnInfoCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiateCollectPopup(); // '개인정보 수집 항목' 상세보기 창 띄우기.
            }
        });

        // '개인정보 수집 및 이용목적' 상세보기 버튼 제어.
        btnPurpose = (Button) findViewById(R.id.btnPurpose);
        btnPurpose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiatePurposePopup(); // '개인정보 수집 및 이용목적' 상세보기 창 띄우기.
            }
        });

        // '개인정보 보유 및 이용기간' 상세보기 버튼 제어.
        btnDate = (Button) findViewById(R.id.btnDate);
        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiateDatePopup(); // '개인정보 보유 및 이용기간' 상세보기 창 띄우기.
            }
        });

        // 항목별 동의하기 체크 버튼 정의.
        cbService = (CheckBox) findViewById(R.id.cbService); // '서비스 약관' 체크 버튼.
        cbInfoCollect = (CheckBox) findViewById(R.id.cbInfoCollect); // '개인정보 수집' 체크 버튼.
        cbPurpose = (CheckBox) findViewById(R.id.cbPurpose); // '개인정보 수집 및 이용 목적' 체크 버튼.
        cbDate = (CheckBox) findViewById(R.id.cbDate); // '개인정보 보유 및 이용 기간' 체크 버튼.

        // '모두 동의하기' 체크 버튼 제어.
        cbAllCheck = (CheckBox) findViewById(R.id.cbAllCheck);
        cbAllCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // '모두 동의하기'에 체크를 하면,
                if (cbAllCheck.isChecked()) {
                    // 나머지 항목들을 전부 체크함.
                    cbService.setChecked(true); // '서비스 약관'
                    cbInfoCollect.setChecked(true); // '개인정보 수집'
                    cbPurpose.setChecked(true); // '개인정보 수집 및 이용 목적'
                    cbDate.setChecked(true); // '개인정보 보유 및 이용 기간'
                }

                // '모두 동의하기' 체크 해제를 하면,
                else {
                    // 나머지 항목들을 전부 체크 해제함.
                    cbService.setChecked(false); // '서비스 약관'
                    cbInfoCollect.setChecked(false); // '개인정보 수집'
                    cbPurpose.setChecked(false); // '개인정보 수집 및 이용 목적'
                    cbDate.setChecked(false); // '개인정보 보유 및 이용 기간'
                }
            }
        });

        // '동의서 제출하기' 버튼을 누를 시, 날짜와 시간을 기록하기 위한 부분.
        today = new GregorianCalendar(); // 캘린더 호출.
        year = today.get(today.YEAR); // 연도값 저장.
        month = today.get(today.MONTH) + 1; // 월 값 저장.
        day = today.get(today.DAY_OF_MONTH); // 일 값 저장.
        hour = today.get(Calendar.HOUR_OF_DAY); // 시 값 저장.
        minute = today.get(Calendar.MINUTE); // 분 값 저장.

        // 빠진 항목이 있을 때 띄워주는 에러 다이얼로그 메시지.
        final AlertDialog.Builder piaErrorBuilder = new AlertDialog.Builder(this)
                .setMessage("빠진 항목이 있으니 다시 확인해주세요!")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

        // '동의서 제출하기' 버튼 제어.
        btnSubmitAgree = (Button) findViewById(R.id.btnSubmitAgree);
        btnSubmitAgree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // '모두 동의하기'에 체크가 되어있거나, 제시된 항목에 모두 동의 체크 표시가 되어있다면,
                if (cbAllCheck.isChecked() || (cbDate.isChecked() && cbInfoCollect.isChecked() && cbPurpose.isChecked() && cbDate.isChecked())) {
                    agreeSubmitDate = year + "년 " + month + "월 " + day + "일  " + hour + "시 " + minute + "분"; // '동의서 제출하기' 버튼을 누를 시, 날짜와 시간 값 저장.

                    // 회원가입 경로를 알기 위한 값 수집.
                    Intent typeIntent = getIntent();
                    String joinType = typeIntent.getStringExtra("type");

                    // 시작 경로가 카카오일 경우.
                    if (joinType.equals("kakao")) {
                        // 연동된 값을 가지고 옴.
                        Intent myProfileIntent = getIntent();
                        String myKakaoPictureLink = myProfileIntent.getExtras().getString("kakao_profile");
                        String myKakaoNickName = myProfileIntent.getExtras().getString("kakao_nickname");
                        // 카카오 추가 입력창으로 이동함.
                        Intent kakaoIntent = new Intent(PersonInfoAgree.this, MemberJoinKakao.class);
                        kakaoIntent.putExtra("my_kakao_profile_picture", myKakaoPictureLink);
                        kakaoIntent.putExtra("my_kakao_nickname", myKakaoNickName);
                        startActivity(kakaoIntent);
                        finish();
                    }

                    // 시작 경로가 페이스북일 경우.
                    else if (joinType.equals("facebook")) {
                        // 페이스북 추가 입력창으로 이동함.

                        String myFBProfileLink = getIntent().getExtras().getString("profile");
                        String myFBEmailID = getIntent().getExtras().getString("email");
                        String myFBName = getIntent().getExtras().getString("name");
                        Log.i(TAG,"profile link " + myFBProfileLink);
                        // 페이스북 회원가입 양식 작성 부분으로 로그인 유저 정보들을 보냄.
                        Intent FBSendIntent = new Intent(PersonInfoAgree.this, MemberJoinFB.class);
                        FBSendIntent.putExtra("profile2", myFBProfileLink);
                        FBSendIntent.putExtra("email2", myFBEmailID);
                        FBSendIntent.putExtra("name2", myFBName);

                        startActivity(FBSendIntent);
                        finish();
                    }

                }

                // 하나라도 빠져있다면,
                else {
                    // 에러 다이얼로그 출력.
                    AlertDialog piaErrorDialog = piaErrorBuilder.create();
                    piaErrorDialog.show();
                }
            }
        });
    }

    // View를 잡아 줄 ViewHolder 클래스.
    private class ViewHolder {
        public TextView piaListTitle; // 각 선택 항목.
    }

    // 표시될 정보 설정을 위한 ListViewAdapter.
    private class ListViewAdapter extends BaseAdapter {
        private Context mContext = null;
        private ArrayList<PiaListData> mListData = new ArrayList<PiaListData>();

        public ListViewAdapter(Context mContext) {
            super();
            this.mContext = mContext;
        }

        @Override
        public int getCount() {
            return mListData.size();
        }

        @Override
        public Object getItem(int position) {
            return mListData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder piaHolder;

            if (convertView == null) {
                piaHolder = new ViewHolder();

                LayoutInflater piaInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = piaInflater.inflate(R.layout.pia_listview_item, null);

                piaHolder.piaListTitle = (TextView) convertView.findViewById(R.id.piaListTitle);

                convertView.setTag(piaHolder);
            } else {
                piaHolder = (ViewHolder) convertView.getTag();
            }

            PiaListData mData = mListData.get(position);

            piaHolder.piaListTitle.setText(mData.piaListTitle);

            return convertView;
        }

        // Custom ListView에 새로운 아이템을 더해주는 메소드.
        public void addItem(String piaListTitle) {
            PiaListData addInfo = null;
            addInfo = new PiaListData();

            addInfo.piaListTitle = piaListTitle;

            mListData.add(addInfo);
        }
    }

    // '서비스 약관' 상세보기 팝업 윈도우 정의.
    private void initiateServicePopup() {
        try {
            LayoutInflater serviceInflater = (LayoutInflater) PersonInfoAgree.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE); // LayoutInflater 객체화 시킴.

            View serviceLayout = serviceInflater.inflate(R.layout.detail_service_term, (ViewGroup) findViewById(R.id.service_popup_element)); // 팝업 윈도우에 올라가는 요소들을 전체로 묶어서 가져옴.


            popupWindow = new BackgroundBlurPopupWindow(serviceLayout, mWidthPixels - 100, mHeightPixels - 500, this, true);
            popupWindow.showAtLocation(serviceLayout, Gravity.CENTER, 0, 0);
            popupWindow.setFocusable(true);
            popupWindow.setBackgroundDrawable(new BitmapDrawable(getApplicationContext().getResources(), Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)));
            popupWindow.setAnimationStyle(android.R.style.Animation_Dialog);


            TextView googleMapServiceView = (TextView) serviceLayout.findViewById(R.id.googleMap_service_term);
            TextView subwayServiceView = (TextView) serviceLayout.findViewById(R.id.subway_service_term);

            googleMapServiceView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(PersonInfoAgree.this, WebViewActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                            .putExtra("webLoad", "https://developers.google.com/maps/terms?hl=ko"));
                }
            });

            subwayServiceView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(PersonInfoAgree.this, WebViewActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                            .putExtra("webLoad", "http://www.seoulmetro.co.kr/kr/page.do?menuIdx=742"));
                }
            });
            // 팝업 윈도우 닫기 버튼 설정.
            btnTermsAgree = (ImageView) serviceLayout.findViewById(R.id.btnTermsAgree);
            btnTermsAgree.setOnClickListener(ServiceCloseClickListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // '서비스 약관' 상세보기 팝업창 닫기 버튼.
    private View.OnClickListener ServiceCloseClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            popupWindow.dismiss(); // 팝업 윈도우를 닫고,
            cbService.setChecked(true); // '서비스 약관' 동의하기 부분 체크함.
        }
    };

    // '개인정보 수집 항목' 상세보기 창 띄우기.
    private void initiateCollectPopup() {
        try {
            LayoutInflater collectInflater = (LayoutInflater) PersonInfoAgree.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE); // LayoutInflater 객체화 시킴.

            View collectLayout = collectInflater.inflate(R.layout.detail_collect_info, (ViewGroup) findViewById(R.id.collect_popup_element)); // 팝업 윈도우에 올라가는 요소들을 전체로 묶어서 가져옴.

            // 팝업 윈도우 사이즈 설정 및 배치.
            popupWindow = new BackgroundBlurPopupWindow(collectLayout, mWidthPixels - 100, mHeightPixels - 500, this, true);
            popupWindow.showAtLocation(collectLayout, Gravity.CENTER, 0, 0);
            popupWindow.setFocusable(true);
            popupWindow.setBackgroundDrawable(new BitmapDrawable(getApplicationContext().getResources(), Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)));
            popupWindow.setAnimationStyle(android.R.style.Animation_Dialog);

            // 팝업 윈도우 닫기 버튼 설정.
            btnCollectAgree = (ImageView) collectLayout.findViewById(R.id.btnCollectAgree);
            btnCollectAgree.setOnClickListener(CollectCloseClickListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // '개인정보 수집 항목' 상세보기 팝업창 닫기 버튼.
    private View.OnClickListener CollectCloseClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            popupWindow.dismiss(); // 팝업 윈도우를 닫고,
            cbInfoCollect.setChecked(true); // '개인정보 수집 항목' 동의하기 부분 체크함.
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    // '개인정보 수집 및 이용목적' 상세보기 창 띄우기.
    private void initiatePurposePopup() {
        try {
            LayoutInflater purposeInflater = (LayoutInflater) PersonInfoAgree.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE); // LayoutInflater 객체화 시킴.

            View purposeLayout = purposeInflater.inflate(R.layout.detail_use_purpose, (ViewGroup) findViewById(R.id.purpose_popup_element)); // 팝업 윈도우에 올라가는 요소들을 전체로 묶어서 가져옴.

            // 팝업 윈도우 사이즈 설정 및 배치.
            popupWindow = new BackgroundBlurPopupWindow(purposeLayout, mWidthPixels - 100, mHeightPixels - 500, this, true);
            popupWindow.showAtLocation(purposeLayout, Gravity.CENTER, 0, 0);
            popupWindow.setFocusable(true);
            popupWindow.setBackgroundDrawable(new BitmapDrawable(getApplicationContext().getResources(), Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)));
            popupWindow.setAnimationStyle(android.R.style.Animation_Dialog);

            // 팝업 윈도우 닫기 버튼 설정.
            btnPurposeAgree = (ImageView) purposeLayout.findViewById(R.id.btnPurposeAgree);
            btnPurposeAgree.setOnClickListener(PurposeCloseClickListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // '개인정보 수집 및 이용목적' 상세보기 팝업창 닫기 버튼.
    private View.OnClickListener PurposeCloseClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            popupWindow.dismiss(); // 팝업 윈도우를 닫고,
            cbPurpose.setChecked(true); // '개인정보 수집 및 이용 목적' 동의하기 부분 체크함.
        }
    };

    // '개인정보 보유 및 이용기간' 상세보기 창 띄우기.
    private void initiateDatePopup() {
        try {
            LayoutInflater dateInflater = (LayoutInflater) PersonInfoAgree.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE); // LayoutInflater 객체화 시킴.

            View dateLayout = dateInflater.inflate(R.layout.detail_use_date, (ViewGroup) findViewById(R.id.date_popup_element)); // 팝업 윈도우에 올라가는 요소들을 전체로 묶어서 가져옴.

            // 팝업 윈도우 사이즈 설정 및 배치.
            popupWindow = new BackgroundBlurPopupWindow(dateLayout, mWidthPixels - 100, mHeightPixels - 500, this, true);
            popupWindow.showAtLocation(dateLayout, Gravity.CENTER, 0, 0);
            popupWindow.setFocusable(true);
            popupWindow.setBackgroundDrawable(new BitmapDrawable(getApplicationContext().getResources(), Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)));
            popupWindow.setAnimationStyle(android.R.style.Animation_Dialog);

            // 팝업 윈도우 닫기 버튼 설정.
            btnDateAgree = (ImageView) dateLayout.findViewById(R.id.btnDateAgree);
            btnDateAgree.setOnClickListener(DateCloseClickListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // '개인정보 보유 및 이용기간' 상세보기 팝업창 닫기 버튼.
    private View.OnClickListener DateCloseClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            popupWindow.dismiss(); // 팝업 윈도우를 닫고,
            cbDate.setChecked(true); // '개인정보 보유 및 이용기간' 동의하기 부분 체크함.
        }
    };

    // 뒤로 가기 버튼 클릭 시.
    @Override
    public void onBackPressed() {
        // super.onBackPressed(); -> 일단은 주석 처리.
        // 메시지 출력
        Toast.makeText(getApplicationContext(), "현재 진행 중인 회원가입 내용은 삭제됩니다.", Toast.LENGTH_LONG).show();
        //prefStorage.removeUserTokenFromPref("token2"); // 뒤로가기 할 땐 지워주기
        // 시작 유형 선택 화면으로 돌아감.

        if(!prefStorage.getUserTokenFromPref("facebook_token").equals("")){
            // 페이스북 토큰 값이 있을 경우
            prefStorage.removeUserTokenFromPref("facebook_token");
        }

        if(!prefStorage.getUserTokenFromPref("kakao_token").equals("")){
            // 페이스북 토큰 값이 있을 경우
            prefStorage.removeUserTokenFromPref("kakao_token");
        }

        // 관련된 내용이 있으면 모두 지우기 (토큰)
        Intent PersonToSelect = new Intent(PersonInfoAgree.this, SelectJoinType.class);
        PersonToSelect.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PersonToSelect.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(PersonToSelect);
        finish();
    }
}