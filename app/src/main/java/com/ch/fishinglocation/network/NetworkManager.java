package com.ch.fishinglocation.network;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.MediaType;
import okhttp3.Call;
import okhttp3.Callback;

import java.io.IOException;

public class NetworkManager {

    private static NetworkManager instance;
    private OkHttpClient client;

    // MediaType for JSON format
    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

    // 私有构造函数
    private NetworkManager() {
        client = new OkHttpClient();
    }

    // 获取NetworkManager的单例实例
    public static synchronized NetworkManager getInstance() {
        if (instance == null) {
            instance = new NetworkManager();
        }
        return instance;
    }

    // 发送GET请求
    public void get(String url, Callback callback) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    // 发送POST请求
    public void post(String url, String json, Callback callback) {
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        Call call = client.newCall(request);
        call.enqueue(callback);
    }

//    // 发起GET请求
//    String url = "https://api.example.com/data";
//NetworkManager.getInstance().get(url, new Callback() {
//        @Override
//        public void onFailure(Call call, IOException e) {
//            // 请求失败的处理
//        }
//
//        @Override
//        public void onResponse(Call call, okhttp3.Response response) throws IOException {
//            if (response.isSuccessful()) {
//                String responseData = response.body().string();
//                // 请求成功的处理
//            } else {
//                // 请求失败的处理
//            }
//        }
//    });
//
//    // 发起POST请求
//    String postUrl = "https://api.example.com/data";
//    String json = "{\"key\":\"value\"}";
//NetworkManager.getInstance().post(postUrl, json, new Callback() {
//        @Override
//        public void onFailure(Call call, IOException e) {
//            // 请求失败的处理
//        }
//
//        @Override
//        public void onResponse(Call call, okhttp3.Response response) throws IOException {
//            if (response.isSuccessful()) {
//                String responseData = response.body().string();
//                // 请求成功的处理
//            } else {
//                // 请求失败的处理
//            }
//        }
//    });
}

