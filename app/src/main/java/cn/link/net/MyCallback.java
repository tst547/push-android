package cn.link.net;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import java.io.IOException;

/**
 * Created by hanyu on 2017/11/17 0017.
 */
public class MyCallback implements Callback {

    private Netable netable;

    public MyCallback(Netable netable){
        this.netable = netable;
    }

    @Override
    public void onFailure(Call call, IOException e) {
        e.printStackTrace();
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        netable.run(response.body().string());
    }

    public interface Netable {
        void run(String result);
    }
}