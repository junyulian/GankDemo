package com.jun.gankdemo.presenter;

import com.jun.gankdemo.viewlayer.BaseView;

public abstract class BasePresenter<V extends BaseView> {
    private V view;
    public V getView(){
        return view;
    }
    public void attachView(V view){
        this.view = view;
    }

    public void detachView(){
        view = null;
    }
}
