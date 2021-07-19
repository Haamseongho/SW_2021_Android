package net.teamcadi.angelbrowser.Activity_Back.service.proxy;

import net.teamcadi.angelbrowser.Activity_Back.data.Push;
import net.teamcadi.angelbrowser.Activity_Back.service.Service;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by haams on 2017-12-11.
 */

public class FbProxy {
    private Service service;

    public FbProxy(Retrofit retrofit) {
        service = retrofit.create(Service.class);
    }

    public void sendFBTokenToServer(String fbToken, int fbIndex, Callback<Push> callback) {
        Call<Push> call = service.sendTokenToServer(fbToken, fbIndex);
        call.enqueue(callback);
    }
}
