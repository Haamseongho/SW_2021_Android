package net.teamcadi.angelbrowser.Activity_Back.service.proxy;



import net.teamcadi.angelbrowser.Activity_Back.data.ChatMessage;
import net.teamcadi.angelbrowser.Activity_Back.data.Elevator;
import net.teamcadi.angelbrowser.Activity_Back.data.TransferMessage;
import net.teamcadi.angelbrowser.Activity_Back.service.Service;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by haams on 2017-12-04.
 */

public class ChatProxy {
    private Service service;

    public ChatProxy(Retrofit retrofit){
        service = retrofit.create(Service.class);
    }

    // 메시지 전송하는 것 (사용자 -> 서버 )
    public void sendMessageToServer(String message, Callback<String> callback){
        Call<String> call = service.sendMessage(message);
        call.enqueue(callback);
    }
    // 서버로부터 메시지를 받는 것 (사용자가 메시지를 보낼 때 그 이 후 답장으로 오는 것 - 1단계)
    public void receiveMessageFromServer(String message, Callback<List<ChatMessage>> callback){
        Call<List<ChatMessage>> call = service.rcvMessage(message);
        call.enqueue(callback);
    }
    // 사용자가 채팅 쪽으로 접속했을 경우 가장 먼저 뜨는 부분 (GET >> 인사 메시지 [서버 -> 사용자])
    public void getIntroMessage(String message, Callback<String> callback){
        Call<String> call = service.introMessage(message);
        call.enqueue(callback);
    }

    // 서버로부터 메시지를 받는 것 (사용자가 메시지를 보낼 때 그 이 후 답장이 오는 것 - 2단계)
    public void receiveTransferMsgFromServer(String message, Callback<List<TransferMessage>> callback){
        Call<List<TransferMessage>> call = service.rcvTransMessage(message);
        call.enqueue(callback);
    }

    public void receiveElevatorInfoFromServer(String message, Callback<List<Elevator>> callback){
        Call<List<Elevator>> call = service.rcvElevatorInfo(message);
        call.enqueue(callback);
    }
}
