package com.jun.gankdemo.model;

import android.content.Context;
import android.content.res.Resources;

public class BaseModel {

    public Context context;

    public BaseModel(Context context){
        this.context = context;
    }

    protected String getStringRes(int resId) {
        return context.getResources().getString(resId);
    }

    protected Resources getResources(){
        return context.getResources();
    }

}
