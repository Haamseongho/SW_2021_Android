package net.teamcadi.angelbrowser.Activity_Back.service;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.teamcadi.angelbrowser.Activity_Back.service.proxy.ChatProxy;
import net.teamcadi.angelbrowser.Activity_Back.service.proxy.FbProxy;
import net.teamcadi.angelbrowser.Activity_Back.service.proxy.ReportProxy;
import net.teamcadi.angelbrowser.Activity_Back.service.proxy.SubwayProxy;
import net.teamcadi.angelbrowser.Activity_Back.service.proxy.UserProxy;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by haams on 2017-12-04.
 */

public class Network {
    public static Network network;
    private Retrofit retrofit;


    // 채팅 End-point >> 서버에 직접 연결할 메소드
    private ChatProxy chatProxy;
    // 유저 관련 End-point >> 서버에 직접 연결할 메소드
    private UserProxy userProxy;
    // FireBase 연동 >> FB토큰과 인덱스 보냄 >> 추 후에 엘레베이터 관련 푸시 알림을 보내기 위함 (인덱스와 FB토큰만으로 DB를 통해 보낼 예정)
    private FbProxy fbProxy;
    // 제보 보내기
    private ReportProxy reportProxy;
    private SubwayProxy subwayProxy;



    public static Network getNetworkInstance() {
        if (network == null) {
            network = new Network();
        }
        return network;
    }

    public Network() {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        Gson gson = new GsonBuilder().setLenient().create();
        // 서버 연결 (Java -> Json (GsonConverterFactory 연결)
        retrofit = new Retrofit.Builder().baseUrl("http://13.125.93.27:2721/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient.build()).build();

        chatProxy = new ChatProxy(retrofit);
        userProxy = new UserProxy(retrofit);
        reportProxy = new ReportProxy(retrofit);
        fbProxy = new FbProxy(retrofit);
        subwayProxy = new SubwayProxy(retrofit);


    }

    public SubwayProxy getSubwayProxy() {
        return subwayProxy;
    }

    public ChatProxy getChatProxy() {
        return chatProxy;
    }

    public UserProxy getUserProxy() {
        return userProxy;
    }

    public FbProxy getFbProxy() {
        return fbProxy;
    }

    public ReportProxy getReportProxy() {
        return reportProxy;
    }


}
