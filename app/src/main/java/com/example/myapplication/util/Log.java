package com.example.myapplication.util;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 上报日志到服务端
 */
public class Log {
    private static final String logURL = "http://10.220.57.142:8080/drive/log?data=";

    public static void log(final String data) {
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        final OkHttpClient client = new OkHttpClient();
                        Request request = new Request.Builder().url(logURL + data).build();
                        Call call = client.newCall(request);
                        try {
                            Response response = call.execute();
                            response.body().byteStream().close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).start();
    }
}
