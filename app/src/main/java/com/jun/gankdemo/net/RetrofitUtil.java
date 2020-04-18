package com.jun.gankdemo.net;

import com.jun.gankdemo.net.intercepter.Logintercepter;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitUtil {

    private static  NetService netService;
    private static final String BASE_URL = "https://gank.io/api/v2/";

    private RetrofitUtil(){
    }

    public static NetService getNetSrvice() {
        if (netService == null) {
            synchronized (RetrofitUtil.class) {
                if (netService == null) {
                    netService = new RetrofitUtil().getRetrofit();
                }
            }
        }
        return netService;
    }

    public NetService getRetrofit() {
        // 初始化NetService
        return initRetrofit().create(NetService.class);
    }

    private Retrofit initRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(initOkHttp())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    private OkHttpClient initOkHttp() {
        return new OkHttpClient().newBuilder()
                .addInterceptor(new Logintercepter())//添加打印拦截器
                .build();
    }

}
