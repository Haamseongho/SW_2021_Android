package net.teamcadi.angelbrowser.Activity_Front;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.teamcadi.angelbrowser.R;

public class ReportInfoDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private String filePath1, filePath2, filePath3; // 서버에 ImageFile 경로들
    private String filePathArray1[]; // 이미지 파일 1 배열
    private String filePathArray2[]; // 이미지 파일 2 배열
    private String filePathArray3[]; // 이미지 파일 3 배열
    private static final String serverUrl = "http://13.125.93.27:2721";
    private static final String TAG = ReportInfoDetailActivity.class.getSimpleName();
    private String rFilePath1, rFilePath2, rFilePath3; // 이미지로 꼽을 파일 경로들
    private ImageView rptDtImage1, rptDtImage2, rptDtImage3; // 이미지1,이미지2,이미지3
    private TextView rptDtContents; // 작성 내용
    private TextView rptDtTitle; // 작성 제목


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_info_detail);
        initViews();
        initFilePath();
        initDetails();
    }


    private void initViews() {
        rptDtImage1 = (ImageView) findViewById(R.id.report_detail_img1);
        rptDtImage2 = (ImageView) findViewById(R.id.report_detail_img2);
        rptDtImage3 = (ImageView) findViewById(R.id.report_detail_img3);

        rptDtImage1.setOnClickListener(this);
        rptDtImage2.setOnClickListener(this);
        rptDtImage3.setOnClickListener(this);

        rptDtTitle = (TextView) findViewById(R.id.report_detail_title);
        rptDtContents = (TextView) findViewById(R.id.report_detail_contents);

        findViewById(R.id.btn_back_report_detail_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initFilePath() {
        filePath1 = getIntent().getExtras().getString("photo1", null);
        filePath2 = getIntent().getExtras().getString("photo2", null);
        filePath3 = getIntent().getExtras().getString("photo3", null);

        Log.i(TAG, "filePath1: " + filePath1);
        Log.i(TAG, "filePath2: " + filePath2);
        Log.i(TAG, "filePath3: " + filePath3);

        // 파일1 경로가 유효할 때
        if (filePath1 != null) {
            filePathArray1 = filePath1.split("/");
            rFilePath1 = serverUrl + "/" + filePathArray1[5] + "/" + filePathArray1[6];
            Log.i(TAG, "경로: " + rFilePath1);
            Glide.with(this).asBitmap()
                    .load(rFilePath1).into(rptDtImage1);

        } else {
            // 이미지가 없을 경우
            Glide.with(this).asBitmap()
                    .load((Uri) null).into(rptDtImage1);
        }

        // 파일2 경로가 유효할 때
        if (filePath2 != null) {
            filePathArray2 = filePath2.split("/");
            rFilePath2 = serverUrl + "/" + filePathArray2[5] + "/" + filePathArray2[6];
            Log.i(TAG, "경로: " + rFilePath2);
            Glide.with(this).asBitmap()
                    .load(rFilePath2).into(rptDtImage2);
        } else {
            // 이미지가 없을 경우
            Glide.with(this).asBitmap()
                    .load((Uri) null).into(rptDtImage2);
        }

        // 파일3 경로가 유효할 때
        if (filePath3 != null) {
            filePathArray3 = filePath3.split("/");
            rFilePath3 = serverUrl + "/" + filePathArray3[5] + "/" + filePathArray3[6];
            Log.i(TAG, "경로: " + rFilePath3);
            Glide.with(this).asBitmap()
                    .load(rFilePath3).into(rptDtImage3);
        } else {
            // 이미지가 없을 경우
            Glide.with(this).asBitmap()
                    .load((Uri) null).into(rptDtImage3);
        }

    }

    private void initDetails() {
        rptDtTitle.setText(getIntent().getExtras().getString("title"));
        rptDtContents.setText(getIntent().getExtras().getString("contents"));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.report_detail_img1:
                startActivity(new Intent(ReportInfoDetailActivity.this,PhotoViewActivity.class)
                        .putExtra("elevPic",rFilePath1)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        .setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                break;

            case R.id.report_detail_img2:
                startActivity(new Intent(ReportInfoDetailActivity.this,PhotoViewActivity.class)
                        .putExtra("elevPic",rFilePath2)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        .setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                break;

            case R.id.report_detail_img3:
                startActivity(new Intent(ReportInfoDetailActivity.this,PhotoViewActivity.class)
                        .putExtra("elevPic",rFilePath3)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        .setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                break;
        }
    }
}
