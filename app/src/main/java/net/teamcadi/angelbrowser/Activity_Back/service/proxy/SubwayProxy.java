package net.teamcadi.angelbrowser.Activity_Back.service.proxy;

import net.teamcadi.angelbrowser.Activity_Back.data.ClosestSubData;
import net.teamcadi.angelbrowser.Activity_Back.data.Elevator;
import net.teamcadi.angelbrowser.Activity_Back.data.ElevatorPic;
import net.teamcadi.angelbrowser.Activity_Back.data.InfraStructure;
import net.teamcadi.angelbrowser.Activity_Back.data.SubwayRoute;
import net.teamcadi.angelbrowser.Activity_Back.data.Timeline;
import net.teamcadi.angelbrowser.Activity_Back.service.Service;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by haams on 2017-12-31.
 */
// 지하철 관련 정보들 모두 관리하는 프록시
public class SubwayProxy {
    private Service service;

    public SubwayProxy(Retrofit retrofit) {
        service = retrofit.create(Service.class);
    }

    public void setSubwayRouter(String departure, String arrival, Callback<List<SubwayRoute>> callback) {
        Call<List<SubwayRoute>> call = service.setSubwayRouter(departure, arrival);
        call.enqueue(callback);
    }

    // 지하철 역이름과 호선으로 데이터 가져오기 (시설물 + 전화번호)
    public void getInfraInfoBySubwayInfo(String station, String subline, Callback<List<InfraStructure>> callback) {
        Call<List<InfraStructure>> call = service.getInfraInfoBySubwayInfo(station, subline);
        call.enqueue(callback);
    }

    // 엘레베이터에서 이미지로 역이름 가지고오기
    public void getStatNmByElevImage(String map, Callback<List<Elevator>> callback) {
        Call<List<Elevator>> call = service.getStatNmByElevImage(map);
        call.enqueue(callback);
    }

    // 엘레베이터 페이지에서 실제 사진 더보기
    public void getElevPicsFromServer(String subname, Callback<List<ElevatorPic>> callback) {
        Call<List<ElevatorPic>> call = service.getElevPicsFromServer(subname);
        call.enqueue(callback);
    }

    // 지하철 역 타임라인 가지고 오기
    public void getTimeLineFromServer(String statnNm, String subline, Callback<List<Timeline>> callback){
        Call<List<Timeline>> call = service.getTimeLineFromServer(statnNm,subline);
        call.enqueue(callback);
    }
    // 지하철 역 이름으로 역 호선 가지고 오기
    public void getSubLineBySubName(String subname, Callback<List<ClosestSubData>> callback){
        Call<List<ClosestSubData>> call = service.getSubLineBySubName(subname);
        call.enqueue(callback);
    }
}
