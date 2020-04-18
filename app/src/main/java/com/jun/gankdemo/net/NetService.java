package com.jun.gankdemo.net;

import com.jun.gankdemo.bean.Girls;


import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface NetService {

    @GET("data/category/Girl/type/Girl/page/{page}/count/10")
    Observable<Girls> fetchPrettyGirl(@Path("page") int page);


}
