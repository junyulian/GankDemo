package com.jun.gankdemo.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.jun.gankdemo.presenter.BasePresenter;
import com.jun.gankdemo.viewlayer.BaseView;
import com.trello.rxlifecycle3.components.support.RxAppCompatActivity;

public abstract class BaseActivity<V extends BaseView,P extends BasePresenter<V>> extends RxAppCompatActivity {

    private P presenter;
    private V view;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(this.presenter == null ){
            this.presenter = createPresenter();
        }
        if(this.view == null){
            this.view = createView();
        }
        if(this.presenter != null && this.view != null){
            this.presenter.attachView(view);
        }
        initData();
    }

    public P getPresenter(){
        return presenter;
    }
    public abstract P createPresenter();
    public abstract V createView();
    public abstract void initData();



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(this.presenter != null && this.view != null){
            this.presenter.detachView();
        }
    }
}
