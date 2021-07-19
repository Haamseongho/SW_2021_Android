package net.teamcadi.angelbrowser.Activity_Front;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import net.teamcadi.angelbrowser.Activity_Back.service.Network;
import net.teamcadi.angelbrowser.Activity_Front.data.Report;
import net.teamcadi.angelbrowser.Adapters.SearchAdapter;
import net.teamcadi.angelbrowser.R;
import net.teamcadi.angelbrowser.SharedPref.SharedPrefStorage;
import net.teamcadi.angelbrowser.Utils.FileUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportStation extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_IMAGE_CROP = 1003;
    private EditText rptTitle, rptContents, subFilterEdtxt;  // 제보의 제목, 제보의 내용 , 역 검색
    private Button rptSubBtn; // 제보 시 역 선택 버튼
    private ImageView rptImage; // 제보 시 이미지
    private ImageView rptUploadBtn; // 제보시 업로드 버튼(이미지)
    private ListView subFilterLstView; // 역 검색 후 리스트 뷰
    private SearchAdapter searchAdapter; // 검색 어댑터
    private List<String> list; // 데이터를 넣은 리스트변수
    private ArrayList<String> arrayList;
    private static final String TAG = ReportStation.class.getSimpleName();
    private TextView rptSubwayNm, rptDate;
    private static final int GALLERY_CODE = 1002;
    private Uri photoURI, albumUri, albumUri2, albumUri3;
    private String mCurrentPhotoPath;
    private AlertDialog.Builder cameraDlgBox; // 카메라 & 사진첩 대화상자
    private Uri photoUir;
    private String currentPhotoPath;
    private String mImageCaptureName;
    private Uri reportImgUri; // 제보 이미지 uri
    FileOutputStream output; // 이미지 파일 저장될 곳(갤러리에서 가지고 온 이미지 파일)
    FileOutputStream output2; // 이미지 파일 저장될 곳(갤러리에서 가지고 온 이미지 파일)
    private File gFile; // Gallery에서 가져 온 파일
    String targetDir;  // Gallery에서 가져왔을 때 저장될 경로
    String fileName;  // Gallery에서 가져왔을 때 저장될 파일 이름
    private Network network;
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
    private ImageView rptImage2, rptImage3; // 이미지2 , 이미지3
    private int rptIndex = 0;
    private Button btnRptAddPic; // 제보 사진 추가하는 버튼


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        initTedPermission();
        initViews();


    }


    // 카메라 사용 퍼미션 , 외부 저장소 읽고 쓰기 퍼미션
    private void initTedPermission() {
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(ReportStation.this, "권한이 제공되었습니다.", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(ReportStation.this, "권한이 없습니다.", Toast.LENGTH_LONG).show();
            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setDeniedMessage("퍼미션 거절 시 앱 사용 불가")
                .setPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE
                        , Manifest.permission.READ_EXTERNAL_STORAGE).check();
    }

    private void initViews() {
        rptTitle = (EditText) findViewById(R.id.rptTitle); // 제보 제목
        rptContents = (EditText) findViewById(R.id.rptContents); // 제보 내용
        rptDate = (TextView) findViewById(R.id.rptDate); // 작성 일자
        rptSubBtn = (Button) findViewById(R.id.rptSubBtn); // 역 검색 버튼
        rptUploadBtn = (ImageView) findViewById(R.id.rptUploadBtn); // 제보 업로드 버튼


        rptImage = (ImageView) findViewById(R.id.rptImage); // 제보할 이미지 & 카메라 연동
        rptSubwayNm = (TextView) findViewById(R.id.rptSubNm); // 제보할 역 이름

        rptSubBtn.setOnClickListener(this);
        rptUploadBtn.setOnClickListener(this);

        rptImage.setOnClickListener(this);

        findViewById(R.id.btn_back_report_page).setOnClickListener(this); // header에서 뒤로가기 버튼 클릭 시

        // 시간 조정
        SimpleDateFormat sdf;
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date today = new Date();
        rptDate.setText(sdf.format(today));
        rptImage2 = (ImageView) findViewById(R.id.rptImage2);
        rptImage3 = (ImageView) findViewById(R.id.rptImage3);

        rptImage2.setOnClickListener(this);
        rptImage3.setOnClickListener(this);

    }

    // 버튼 별 리스너
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.rptSubBtn:
                // 역 필터링 진행 >> 검색
                findSubwayInfo();
                break;
            case R.id.rptUploadBtn:
                // 서버 전송 , 업로드 이 전 확인하기
                checkBeforeUpload();
                break;


            case R.id.rptImage:
                // 카메라 연동
                rptIndex = 1;
                connectCamera();
                break;

            case R.id.rptImage2:
                // 카메라 연동
                rptIndex = 2;
                connectCamera();
                break;

            case R.id.rptImage3:
                // 카메라 연동
                rptIndex = 3;
                connectCamera();
                break;

            case R.id.btn_back_report_page:
                finish();
                break;

        }
    }

    // 카메라 연동 & 사진첩 연동
    private void connectCamera() {
        cameraDlgBox = new AlertDialog.Builder(this);
        /*final View view = (LayoutInflater.from(this)).inflate(R.layout.camera_or_gallery_use, null);
        cameraDlgBox.setView(view);
        final AlertDialog cameraDlgRealBox = cameraDlgBox.create();
        final ImageView galleryBtn = (ImageView) view.findViewById(R.id.galleryBtn);

*/
        cameraDlgBox.setTitle("사진 가져오기");
        cameraDlgBox.setMessage("지하철 엘레베이터에 대한 올바른 정보만을 제보해주시기 바랍니다.");
        cameraDlgBox.setPositiveButton("등록 선택", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                useGalleryForPic();
                cameraDlgBox.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        Log.e(TAG, "대화 상자 지우기");
                        //cameraDlgRealBox.dismiss();
                    }
                });
            }
        });
        cameraDlgBox.setNegativeButton("등록 취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (rptIndex == 1) {
                    Glide.with(ReportStation.this)
                            .asBitmap().load((Uri) null).into(rptImage);

                    // 첫 번째 사진 지울 경우 >> 나머지 두 사진이 아무것도 없으면 rptIndex는 0으로 둬서 이미지 넣어달라고 말하기
                    if (rptImage2 != null && rptImage3 != null)
                        rptIndex = 2;
                    else if (rptImage2 != null && rptImage3 == null)
                        rptIndex = 1;
                    else if (rptImage2 == null && rptImage3 != null)
                        rptIndex = 1;
                    else
                        rptIndex = 0; // (rptImage2 == null , rptImage3 == null
                }
                if (rptIndex == 2) {
                    Glide.with(ReportStation.this)
                            .asBitmap().load((Uri) null).into(rptImage2);

                    if (rptImage != null && rptImage3 != null)
                        rptIndex = 2;
                    else if (rptImage != null && rptImage3 == null)
                        rptIndex = 1;
                    else if (rptImage == null && rptImage3 != null)
                        rptIndex = 1;
                    else
                        rptIndex = 0; // (rptImage == null , rptImage3 == null
                }
                if (rptIndex == 3) {
                    Glide.with(ReportStation.this)
                            .asBitmap().load((Uri) null).into(rptImage3);
                    // 세 번째꺼 지워도 1,2 사진이 있으면 rptIndex = 2 , 1번 사진 또는 2번 사진 하나씩만 있을 경우 rptIndex = 1
                    if (rptImage != null && rptImage2 != null)
                        rptIndex = 2;
                    else if (rptImage != null && rptImage2 == null)
                        rptIndex = 1;
                    else if (rptImage == null && rptImage2 != null)
                        rptIndex = 1;
                    else
                        rptIndex = 0; // (rptImage == null , rptImage2 == null
                }

            }
        });



      /*  galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 갤러리 연동

            }
        });*/
        cameraDlgBox.show();
        //cameraDlgRealBox.show();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e(TAG, "다시 시작하기");

    }

    private String getRealPathFromURI(Uri contentUri) {
        int column_index = 0;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        }

        return cursor.getString(column_index);
    }


    // GALLERY 에서 찾기 (사진)
    private void useGalleryForPic() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        galleryIntent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(galleryIntent, GALLERY_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {


                case GALLERY_CODE:
                    if (data.getData() != null) {


                        if (rptIndex == 1) {
                            albumUri = data.getData();
                            rptImage.setScaleType(ImageView.ScaleType.CENTER_CROP);

                            targetDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures/angel/";
                            fileName = String.valueOf(new SimpleDateFormat("yyyy-MM-dd_hh:mm:ss").format(new Date())) + ".jpg";
                            File dir = new File(targetDir);
                            if (!dir.exists()) {
                                dir.mkdirs();
                            }

                            // 파일 생성 후 복 붙 진행 (FileOutputStream으로 JPEG로 압축해서 내용 넣고 저장)
                            try {
                                output = new FileOutputStream(targetDir + fileName);
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), albumUri);
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
                                Log.e(TAG, "파일 OutputStream 경로 체크" + output.getFD() + "///" + output.getChannel());
                                output.close();

                                gFile = new File(targetDir + fileName);

                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            Log.i(TAG, "albumUri1 : " + albumUri);
                            Log.i(TAG, "albumUri1 :" + gFile.getName() + "/" + gFile.getAbsolutePath());

                            Glide.with(this).asBitmap()
                                    .load(albumUri).into(rptImage);
                        } else if (rptIndex == 2) {
                            albumUri2 = data.getData();
                            rptImage2.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            targetDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures/angel/";
                            fileName = String.valueOf(new SimpleDateFormat("yyyy-MM-dd_hh:mm:ss").format(new Date())) + ".jpg";
                            File dir = new File(targetDir);
                            if (!dir.exists()) {
                                dir.mkdirs();
                            }

                            // 파일 생성 후 복 붙 진행 (FileOutputStream으로 JPEG로 압축해서 내용 넣고 저장)
                            try {
                                output = new FileOutputStream(targetDir + fileName);
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), albumUri2);
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
                                Log.e(TAG, "파일 OutputStream 경로 체크" + output.getFD() + "///" + output.getChannel());
                                output.close();

                                gFile = new File(targetDir + fileName);

                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            Log.i(TAG, "albumUri2 : " + albumUri2);
                            Log.i(TAG, "albumUri1 :" + gFile.getName() + "/" + gFile.getAbsolutePath());

                            Glide.with(this).asBitmap()
                                    .load(albumUri2).into(rptImage2);
                        } else if (rptIndex == 3) {
                            albumUri3 = data.getData();
                            rptImage3.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            targetDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures/angel/";
                            fileName = String.valueOf(new SimpleDateFormat("yyyy-MM-dd_hh:mm:ss").format(new Date())) + ".jpg";
                            File dir = new File(targetDir);
                            if (!dir.exists()) {
                                dir.mkdirs();
                            }

                            // 파일 생성 후 복 붙 진행 (FileOutputStream으로 JPEG로 압축해서 내용 넣고 저장)
                            try {
                                output = new FileOutputStream(targetDir + fileName);
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), albumUri3);
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
                                Log.e(TAG, "파일 OutputStream 경로 체크" + output.getFD() + "///" + output.getChannel());
                                output.close();

                                gFile = new File(targetDir + fileName);

                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            Glide.with(this).asBitmap()
                                    .load(albumUri3).into(rptImage3);
                        }

                    }

                    break;


            }
        }
    }

    private void storeCropImage(Bitmap profilePhoto, String filePath) {
        File copyFile = new File(filePath);
        BufferedOutputStream out = null;
        try {
            copyFile.createNewFile();
            output = new FileOutputStream(copyFile);
            out = new BufferedOutputStream(output);
            profilePhoto.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkBeforeUpload() {
        final AlertDialog.Builder dlg = new AlertDialog.Builder(this);
        dlg.setTitle("제보 사항 확인");
        dlg.setMessage("작성된 내용이 정확한 내용인가요? \n" + "허위 사실을 기재한 것이 적발될 경우 \n" + "추후 이용에 제재가 될 수 있으니 \n" + "이 점 참고 후, 다시 확인해주세요!");
        dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (rptTitle.getText().toString().equals("") || rptContents.getText().toString().equals("")
                        || rptIndex == 0 || rptSubwayNm.getText().toString().equals("역이름")
                        ) {
                    Toast.makeText(ReportStation.this, "빠진 항목이 있으니 확인해주시기 바랍니다.", Toast.LENGTH_LONG).show();
                } else {
                    sendReportToServer();    // 서버에 데이터 전송
                }

            }
        });
        dlg.setNegativeButton("취소", null);

        dlg.show();
    }

    // Report내용 서버에 전송
    private void sendReportToServer() {
        Log.i(TAG, "서버 전송");

        String[] subNmLn = rptSubwayNm.getText().toString().split("/"); // 지하철 역 이랑 호선 구분 하기 위함 >> '/'
        Log.i(TAG, rptTitle.getText().toString() + " // " + subNmLn[0].toString() + "//" + subNmLn[1].toString() + " // " + rptContents.getText().toString());
        network = Network.getNetworkInstance();

        final File imgFile = new File(targetDir + fileName);
        Log.i(TAG, "file name : " + imgFile.getName() + " 경로 : " + imgFile.getAbsolutePath());


        SharedPrefStorage pref = new SharedPrefStorage(this); // fbToken 받아오기 위함
        RequestBody name = null;

        if (rptIndex == 1) {

            // 파일 이미지 하나만 서버에 보낼 경우
            RequestBody title = RequestBody.create(MultipartBody.FORM, rptTitle.getText().toString()); // 제목 보내기
            RequestBody subNm = RequestBody.create(MultipartBody.FORM, subNmLn[0].toString()); // 역 이름 보내기
            RequestBody subLn = RequestBody.create(MultipartBody.FORM, subNmLn[1].toString()); // 역 호선 보내기
            RequestBody contents = RequestBody.create(MultipartBody.FORM, rptContents.getText().toString()); // 내용 보내기
            RequestBody fbToken = RequestBody.create(MultipartBody.FORM, pref.getFBTokenByPref("fbToken")); // 파베 토큰 보내기
            name = RequestBody.create(MultipartBody.FORM, pref.getNameFromPref("user_email"));


            File originalFile = FileUtils.getFile(this, albumUri);


            try {
                Log.i(TAG, "파일 경로 체크 .. " + albumUri.getPath());
                Log.i(TAG, "파일 체크 : " + originalFile.getName());
                Log.i(TAG, "getContentResolver() : " + MediaType.parse(getContentResolver().getType(albumUri)).toString());
                RequestBody filePart = RequestBody.create(MediaType.parse(getContentResolver().getType(albumUri)),
                        originalFile);
                Log.i(TAG, "파일 경로 체크23 .. " + filePart.toString());

                MultipartBody.Part imageFile = MultipartBody.Part.createFormData("photo", originalFile.getName(), filePart);


                // 서버에 전송 (Report 내용)
                network.getReportProxy().sendReportToServer(title, subNm, subLn, contents, fbToken, name, imageFile, new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Log.i(TAG, "파일 전송 성공적");
                            Toast.makeText(ReportStation.this, "성공적으로 제보가 완료되었습니다. 검토 이 후에 올바르게 반영될 수 있도록 노력하겠습니다. ", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e(TAG, t.toString()); // 파일 전송 실패
                        Toast.makeText(ReportStation.this, "성공적으로 제보가 실패하였습니다. 무엇이 문제인지 확인해보겠습니다. 불편을 드려서 죄송합니다.", Toast.LENGTH_LONG).show();

                    }
                });
            } catch (NullPointerException e) {
                Toast.makeText(ReportStation.this, "이미지가 업로드 되지 않았습니다. 확인해주시기 바랍니다.", Toast.LENGTH_LONG).show();
            }


        } // 이미지 파일 하나 보내기

        if (rptIndex == 2) {

            // 두 개 파일 서버에 전송
            RequestBody title = RequestBody.create(MultipartBody.FORM, rptTitle.getText().toString()); // 제목 보내기
            RequestBody subNm = RequestBody.create(MultipartBody.FORM, subNmLn[0].toString()); // 역 이름 보내기
            RequestBody subLn = RequestBody.create(MultipartBody.FORM, subNmLn[1].toString()); // 역 호선 보내기
            RequestBody contents = RequestBody.create(MultipartBody.FORM, rptContents.getText().toString()); // 내용 보내기
            RequestBody fbToken = RequestBody.create(MultipartBody.FORM, pref.getFBTokenByPref("fbToken")); // 파베 토큰 보내기
            name = RequestBody.create(MultipartBody.FORM, pref.getNameFromPref("user_email"));
            /*if (!pref.getNameFromPref("kakao_name").equals(""))
                name = RequestBody.create(MultipartBody.FORM, pref.getNameFromPref("kakao_name")); // 작성자 이름 보내기 >> 수정해야함
            else if (!pref.getNameFromPref("facebook_name").equals("")) {
                name = RequestBody.create(MultipartBody.FORM, pref.getNameFromPref("facebook_name"));
            } else {
                );
            }*/

            try {
                File originalFile = FileUtils.getFile(this, albumUri);
                RequestBody filePart = RequestBody.create(MediaType.parse(getContentResolver().getType(albumUri)),
                        originalFile);

                File originalFile2 = FileUtils.getFile(this, albumUri2);
                RequestBody filePart2 = RequestBody.create(MediaType.parse(getContentResolver().getType(albumUri2)),
                        originalFile2);

                MultipartBody.Part imageFile = MultipartBody.Part.createFormData("photo", originalFile.getName(), filePart);
                MultipartBody.Part imageFile2 = MultipartBody.Part.createFormData("photo2", originalFile2.getName(), filePart2);


                Log.i(TAG, "imageFile1 :" + imageFile.body());
                Log.i(TAG, "imageFile2 : " + imageFile2.body());
                // 서버에 전송 (Report 내용)
                network.getReportProxy().sendReport2ToServer(title, subNm, subLn, contents, fbToken, name, imageFile, imageFile2, new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Log.i(TAG, "파일 전송 성공적");
                            Toast.makeText(ReportStation.this, "성공적으로 제보가 완료되었습니다. 검토 이 후에 올바르게 반영될 수 있도록 노력하겠습니다. ", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e(TAG, t.toString()); // 파일 전송 실패
                        Toast.makeText(ReportStation.this, "성공적으로 제보가 실패하였습니다. 무엇이 문제인지 확인해보겠습니다. 불편을 드려서 죄송합니다.", Toast.LENGTH_LONG).show();

                    }
                });
            } catch (NullPointerException e) {
                Toast.makeText(ReportStation.this, "두 번째 사진이 제대로 업로드 되지 않았습니다. 확인해주시고 다시 이용해주시길 바랍니다.", Toast.LENGTH_LONG).show();
            }

        }
        if (rptIndex == 3) {

            // 두 개 파일 서버에 전송
            RequestBody title = RequestBody.create(MultipartBody.FORM, rptTitle.getText().toString()); // 제목 보내기
            RequestBody subNm = RequestBody.create(MultipartBody.FORM, subNmLn[0].toString()); // 역 이름 보내기
            RequestBody subLn = RequestBody.create(MultipartBody.FORM, subNmLn[1].toString()); // 역 호선 보내기
            RequestBody contents = RequestBody.create(MultipartBody.FORM, rptContents.getText().toString()); // 내용 보내기
            RequestBody fbToken = RequestBody.create(MultipartBody.FORM, pref.getFBTokenByPref("fbToken")); // 파베 토큰 보내기
            name = RequestBody.create(MultipartBody.FORM, pref.getNameFromPref("user_email"));

            // 첫 번째 파일
            File originalFile = FileUtils.getFile(this, albumUri);
            RequestBody filePart = RequestBody.create(MediaType.parse(getContentResolver().getType(albumUri)),
                    originalFile);

            // 두 번째 파일
            File originalFile2 = FileUtils.getFile(this, albumUri2);
            RequestBody filePart2 = RequestBody.create(MediaType.parse(getContentResolver().getType(albumUri2)),
                    originalFile2);

            // 세 번째 파일
            File originalFile3 = FileUtils.getFile(this, albumUri3);
            RequestBody filePart3 = RequestBody.create(MediaType.parse(getContentResolver().getType(albumUri3)),
                    originalFile3);
            try {
                MultipartBody.Part imageFile = MultipartBody.Part.createFormData("photo", originalFile.getName(), filePart);
                MultipartBody.Part imageFile2 = MultipartBody.Part.createFormData("photo2", originalFile2.getName(), filePart2);
                MultipartBody.Part imageFile3 = MultipartBody.Part.createFormData("photo3", originalFile3.getName(), filePart3);

                Log.i(TAG, "imageFile1 :" + imageFile.body());
                Log.i(TAG, "imageFile2 : " + imageFile2.body());
                Log.i(TAG, "imageFile3 : " + imageFile3.body());
                // 서버에 전송 (Report 내용)
                network.getReportProxy().sendReport3ToServer(title, subNm, subLn, contents, fbToken, name, imageFile, imageFile2, imageFile3, new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Log.i(TAG, "파일 전송 성공적");
                            Toast.makeText(ReportStation.this, "성공적으로 제보가 완료되었습니다. 검토 이 후에 올바르게 반영될 수 있도록 노력하겠습니다. ", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e(TAG, t.toString()); // 파일 전송 실패
                        Toast.makeText(ReportStation.this, "성공적으로 제보가 실패하였습니다. 무엇이 문제인지 확인해보겠습니다. 불편을 드려서 죄송합니다.", Toast.LENGTH_LONG).show();

                    }
                });
            } catch (NullPointerException e) {
                Toast.makeText(ReportStation.this, "세 번째 사진이 제대로 업로드 되지 않았습니다. 확인해주시고 다시 이용해주시길 바랍니다.", Toast.LENGTH_LONG).show();
            }
        }

        finish(); // 제보 페이지 아웃
    }


    private void findSubwayInfo() {
        final AlertDialog.Builder dlg = new AlertDialog.Builder(this);
        final View view = LayoutInflater.from(this).inflate(R.layout.station_alertbox, null);
        dlg.setView(view);

        final AlertDialog dialogBox = dlg.create();
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
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, String.valueOf(parent.getAdapter().getItem(position)));
                rptSubwayNm.setText(String.valueOf(parent.getAdapter().getItem(position)));
                dialogBox.cancel();
            }
        });
        dialogBox.show();
    }

    private void search(String findTxt) {
        // 문자 입력이 없을 경우 리스트 지우고 새로 뿌려주기
        list.clear(); // 지하철 역이름 (데이터)

        // 문자 입력이 없을때는 모든 데이터를 보여준다.
        if (findTxt.length() == 0) {
            list.addAll(arrayList);
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
        list.add("총신대입구(이수)/4호선");
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
        list.add("총신대입구(이수)/7호선");
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