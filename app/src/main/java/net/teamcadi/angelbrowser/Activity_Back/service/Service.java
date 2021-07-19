package net.teamcadi.angelbrowser.Activity_Back.service;


import net.teamcadi.angelbrowser.Activity_Back.data.ChatMessage;
import net.teamcadi.angelbrowser.Activity_Back.data.ClosestSubData;
import net.teamcadi.angelbrowser.Activity_Back.data.ElevLocation;
import net.teamcadi.angelbrowser.Activity_Back.data.Elevator;
import net.teamcadi.angelbrowser.Activity_Back.data.ElevatorPic;
import net.teamcadi.angelbrowser.Activity_Back.data.InfraStructure;
import net.teamcadi.angelbrowser.Activity_Back.data.Push;
import net.teamcadi.angelbrowser.Activity_Back.data.ReportData;
import net.teamcadi.angelbrowser.Activity_Back.data.ReportDetailInfo;
import net.teamcadi.angelbrowser.Activity_Back.data.SubwayRoute;
import net.teamcadi.angelbrowser.Activity_Back.data.Timeline;
import net.teamcadi.angelbrowser.Activity_Back.data.TransferMessage;
import net.teamcadi.angelbrowser.Activity_Back.data.User;
import net.teamcadi.angelbrowser.Activity_Back.data.UserInfo;
import net.teamcadi.angelbrowser.Activity_Front.data.CommonSubData;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * Created by haams on 2017-12-04.
 */

public interface Service {
    /*
    Chat (Client -> Server[chatbot server])
     */
    // 초기 입장 시 나오는 인사 메세지 (server -> client)

    @GET("/api/messages")
    public Call<String> introMessage(
            @Query("name") String name
    );


    // 클라이언트에서 서버로 메시지 전송
    @FormUrlEncoded
    @POST("/api/messages")
    public Call<String> sendMessage(
            @Field("botSend") String botSend
    );


    // 서버로부터 온 데이터 내용을 메시지로 받는 부분. (1단계)
    @FormUrlEncoded
    @POST("/api/messages")
    public Call<List<ChatMessage>> rcvMessage(
            @Field("botSend") String botSend
    );

    // 서버로부터 온 데이터 내용을 메시지로 받는 부분. (2단계)
    @FormUrlEncoded
    @POST("/api/messages")
    public Call<List<TransferMessage>> rcvTransMessage(
            @Field("botSend") String botSend
    );

    //  서버로부터 온 데이터 내용을 메시지로 받는 부분 (3단계)
    @FormUrlEncoded
    @POST("/api/messages")
    public Call<List<Elevator>> rcvElevatorInfo(
            @Field("botSend") String botSend
    );

    /*
    유저 정보 (회원 가입 시에 내용 보내기
     */
    @FormUrlEncoded
    @POST("/users")
    public Call<User> sendUserInfoToServer(
            @Field("TOKEN_TAG") String token,
            @Field("USER_NAME") String name,
            @Field("USER_SEX") String sex,
            @Field("DISABLED_YN") String disabled,
            @Field("USER_ID") String id,
            @Field("USER_PW") String pw,
            @Field("KAKAO_ID") String kakaoID,
            @Field("WHEEL_TAG") String wheelTAG,
            @Field("GPS_YN") String gpsYN
    );
    /*
    푸시 메시지 보내기 이 전 Firebase 토큰 과 인덱스를 DB에 저장하고자 보내는 과정
     */

    @FormUrlEncoded
    @POST("/token/save")
    public Call<Push> sendTokenToServer(
            @Field("fbToken") String fbToken,
            @Field("fbIndex") int fbIndex
    );

    // 메시지 전송
    @Multipart
    @POST("/report/upload")
    public Call<ResponseBody> sendReportToServer(
            @Part("title") RequestBody title,
            @Part("subname") RequestBody subName,
            @Part("subline") RequestBody subLine,
            @Part("contents") RequestBody contents,
            @Part("fbtoken") RequestBody fbtoken,
            @Part("name") RequestBody name,
            @Part MultipartBody.Part photo
    );

    // 메시지 전송 (이미지 파일 2개)
    @Multipart
    @POST("/report/upload2")
    public Call<ResponseBody> sendReport2ToServer(
            @Part("title") RequestBody title,
            @Part("subname") RequestBody subName,
            @Part("subline") RequestBody subLine,
            @Part("contents") RequestBody contents,
            @Part("fbtoken") RequestBody fbtoken,
            @Part("name") RequestBody name,
            @Part MultipartBody.Part photo,
            @Part MultipartBody.Part photo2
    );

    // 메시지 전송 (이미지 파일 3개)
    @Multipart
    @POST("/report/upload3")
    public Call<ResponseBody> sendReport3ToServer(
            @Part("title") RequestBody title,
            @Part("subname") RequestBody subName,
            @Part("subline") RequestBody subLine,
            @Part("contents") RequestBody contents,
            @Part("fbtoken") RequestBody fbtoken,
            @Part("name") RequestBody name,
            @Part MultipartBody.Part photo,
            @Part MultipartBody.Part photo2,
            @Part MultipartBody.Part photo3
    );

    // StationInfoActivity에서 최근 제보 데이터를 가지고 올 때 자세히보기 눌렀을 때 관련 정보 가지고 오게 구현
    // 시작역 + 도착역 설정 >> 그에 따라 최단 경로 소개
    // station/info >> StationInfoActivity를 의미 , detail -> report에 대한 정보를 자세히 가지고 오겠다.

    @FormUrlEncoded
    @POST("/report/station/info/detail")
    public Call<ReportDetailInfo> getDetailReportInfoFromServer(
            @Field("title") String title,
            @Field("name") String name,
            @Field("time") String time
    );

    @FormUrlEncoded
    @POST("/subway/route")
    public Call<List<SubwayRoute>> setSubwayRouter(
            @Field("departure") String departure,
            @Field("arrival") String arrival
    );

    // 지하철 역 이름 이랑 호선 넣어주면 그거에 대한 시설물 과 번호 보여줌
    @FormUrlEncoded
    @POST("/infra")
    public Call<List<InfraStructure>> getInfraInfoBySubwayInfo(
            @Field("station") String station,
            @Field("subline") String subline
    );

    // 역정보 페이지에서 실시간 제보 정보 가지고 오기
    @FormUrlEncoded
    @POST("/report/subinfo")
    public Call<List<ReportData>> getSubInfoReportFromServer(
            @Field("subname") String subname,
            @Field("subline") String subline
    );
// 맵 이미지로 역이름 가지고오기
    @FormUrlEncoded
    @POST("/detail/image/elevator")
    public Call<List<Elevator>> getStatNmByElevImage(
            @Field("map") String map
    );

    // subname으로(지하철 역이름) 데이터 찾아오기 >> 제목이랑 사진 , 등록 시간
    @GET("/detail/image/elevator")
    public Call<List<ElevatorPic>> getElevPicsFromServer(
            @Query("subname") String subname
    );

    // 시간표 가지고 오기 (실시간)
    @FormUrlEncoded
    @POST("/subInfo/timeline")
    public Call<List<Timeline>> getTimeLineFromServer(
            @Field("statNm") String statNm, // 역 이름으로 모든 걸 검색
            @Field("subline") String subline
    );

    // 자주가는 역에 대한 정보 보내주기 >> 역 이름 , 역 호선 + 가까운 역에도 쓰일 예정
    @FormUrlEncoded
    @POST("/report/common")
    public Call<List<CommonSubData>> getCommonSubDataFromServer(
            @Field("subname") String subname
    );
    // 엘레베이터 지상 위치 알려주기 위함
    @GET("/report/common")
    public Call<List<ElevLocation>> getElevLocationFromServer(
            @Query("station") String station
    );

    // 역 이름으로 호선 가지고 오기
    // ClosestSubData >> Activity_Back 에 있는 내용임
    @FormUrlEncoded
    @POST("/closest/subway")
    public Call<List<ClosestSubData>> getSubLineBySubName(
            @Field("subname") String subname
    );


    // 사용자 계정 서버에 전송
    @GET("/user/info")
    public Call<UserInfo> sendUserProfileToServer(
            @Query("userID") String userID,
            @Query("userPW") String userPW,
            @Query("token") String token,
            @Query("kakaoId") String kakaoId,
            @Query("userIndex") String userIndex,
            @Query("wheelIndex") String wheelIndex,
            @Query("profile") String profile

    );

    @Multipart
    @POST("/user/info/basic")
    public Call<ResponseBody> sendGeneralUserToServer(
            @Part("userID") RequestBody userID,
            @Part("userPW") RequestBody userPW,
            @Part("token") RequestBody token,
            @Part("kakaoId") RequestBody kakaoID,
            @Part("userIndex") RequestBody userIndex,
            @Part("wheelIndex") RequestBody wheelIndex,
            @Part MultipartBody.Part profile
    );

    // 토큰으로 유저 정보 가지고 오기
    @FormUrlEncoded
    @POST("/user/info")
    public Call<UserInfo> getUserProfileFromServer(
            @Field("token") String token
    );

    @GET("/user/token")
    public Call<UserInfo> getGenTokenFromServer(
            @Query("userID") String userID,
            @Query("kakaoId") String kakaoId
    );

    @GET("/user/fbtoken/check")
    public Call<UserInfo> getFacebookTokenFromServer(
            @Query("fbtoken") String fbtoken
    );
}
