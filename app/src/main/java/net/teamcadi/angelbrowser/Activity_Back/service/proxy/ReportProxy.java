package net.teamcadi.angelbrowser.Activity_Back.service.proxy;

import net.teamcadi.angelbrowser.Activity_Back.data.ElevLocation;
import net.teamcadi.angelbrowser.Activity_Back.data.ReportData;
import net.teamcadi.angelbrowser.Activity_Back.data.ReportDetailInfo;
import net.teamcadi.angelbrowser.Activity_Back.service.Service;
import net.teamcadi.angelbrowser.Activity_Front.data.CommonSubData;

import java.util.Date;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by haams on 2017-12-18.
 */

public class ReportProxy {
    private Service service;

    public ReportProxy(Retrofit retrofit) {
        service = retrofit.create(Service.class);
    }

    // 제보 페이지에서 제보 보내기
    public void sendReportToServer(RequestBody title, RequestBody subname, RequestBody subline, RequestBody contents, RequestBody fbtoken, RequestBody name, MultipartBody.Part photo, Callback<ResponseBody> callback) {
        Call<ResponseBody> call = service.sendReportToServer(title, subname, subline, contents, fbtoken, name, photo);
        call.enqueue(callback);
    }


    public void sendReport2ToServer(RequestBody title, RequestBody subname, RequestBody subline, RequestBody contents, RequestBody fbtoken, RequestBody name, MultipartBody.Part photo, MultipartBody.Part photo2, Callback<ResponseBody> callback) {
        Call<ResponseBody> call = service.sendReport2ToServer(title, subname, subline, contents, fbtoken, name, photo, photo2);
        call.enqueue(callback);
    }

    public void sendReport3ToServer(RequestBody title, RequestBody subname, RequestBody subline, RequestBody contents, RequestBody fbtoken, RequestBody name, MultipartBody.Part photo, MultipartBody.Part photo2, MultipartBody.Part photo3, Callback<ResponseBody> callback) {
        Call<ResponseBody> call = service.sendReport3ToServer(title, subname, subline, contents, fbtoken, name, photo, photo2, photo3);
        call.enqueue(callback);
    }
// 역정보 페이지에서 실시간 제보 내용 가지고 오기

    public void getSubInfoReportDataFromServer(String subname, String subline, Callback<List<ReportData>> callback) {
        Call<List<ReportData>> call = service.getSubInfoReportFromServer(subname, subline);
        call.enqueue(callback);
    }

    public void getReportDetailInfoFromServer(String title, String name, String time, Callback<ReportDetailInfo> callback) {
        Call<ReportDetailInfo> call = service.getDetailReportInfoFromServer(title, name, time);
        call.enqueue(callback);
    }

    // subway 제보 관련 되어서 자주가는 역 부분만 가져오도록 진행 + size

    public void getCommonSubDataFromServer(String subname, Callback<List<CommonSubData>> callback) {
        Call<List<CommonSubData>> call = service.getCommonSubDataFromServer(subname);
        call.enqueue(callback);
    }

    // 엘레베이터 지상 위치 알려주기 위함 , 호선도 가져올 것
    public void getElevLocationFromServer(String station, Callback<List<ElevLocation>> callback) {
        Call<List<ElevLocation>> call = service.getElevLocationFromServer(station);
        call.enqueue(callback);
    }
}
