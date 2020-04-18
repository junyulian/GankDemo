package com.jun.gankdemo.model;

import android.content.Context;

import com.jun.gankdemo.bean.PrettyGirl;
import com.jun.gankdemo.net.OnHttpResultListener;
import com.jun.gankdemo.net.RetrofitUtil;
import com.jun.gankdemo.utils.LogUtil;

import io.reactivex.functions.Consumer;

public class MainModel extends BaseModel {

    public MainModel(Context context) {
        super(context);
    }

    public void getImgUrl(PrettyGirl prettyGirl, OnHttpResultListener onHttpResultListener){
        LogUtil.e("-----开始url:"+prettyGirl.url);
        int point = prettyGirl.getUrl().lastIndexOf('/');
        String code = prettyGirl.getUrl().substring(point);
        LogUtil.e("-----获取的code:"+code);
//        RetrofitUtil.getNetSrvice()
//                .fetchImgUrl(code)
//                .subscribe(new Consumer<String>() {
//                    @Override
//                    public void accept(String s) throws Exception {
//                        LogUtil.e("---res:"+s);
//                        if(onHttpResultListener != null){
//                            onHttpResultListener.onResult(s);
//                        }
//                    }
//                });
    }
}
