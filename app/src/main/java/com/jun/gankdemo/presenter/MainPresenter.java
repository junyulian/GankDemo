package com.jun.gankdemo.presenter;

import android.content.Context;

import com.jun.gankdemo.bean.PrettyGirl;
import com.jun.gankdemo.model.MainModel;
import com.jun.gankdemo.net.OnHttpResultListener;
import com.jun.gankdemo.viewlayer.MainView;

public class MainPresenter extends BasePresenter<MainView> {

    private MainModel mainModel;

    public MainPresenter(Context context){
        this.mainModel = new MainModel(context);
    }

    public void getImgUrl(PrettyGirl prettyGirl){
        mainModel.getImgUrl(prettyGirl, new OnHttpResultListener<String>() {
            @Override
            public void onResult(String url) {
                if(getView() != null){
                    getView().onImgUrl(url);
                }
            }
        });
    }

}
