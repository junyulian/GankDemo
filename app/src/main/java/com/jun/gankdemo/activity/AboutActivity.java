package com.jun.gankdemo.activity;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.jakewharton.rxbinding3.appcompat.RxToolbar;
import com.jakewharton.rxbinding3.view.RxView;
import com.jun.gankdemo.R;
import com.jun.gankdemo.databinding.ActivityAboutBinding;
import com.trello.rxlifecycle3.components.support.RxAppCompatActivity;

import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Consumer;
import kotlin.Unit;

public class AboutActivity extends RxAppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityAboutBinding binding =
                DataBindingUtil.setContentView(this, R.layout.activity_about);

        setSupportActionBar(binding.toolbar);//设置toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//返回按钮可见

        //返回
        RxToolbar.navigationClicks(binding.toolbar)
                .compose(bindToLifecycle())
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) throws Exception {
                        onBackPressed();
                    }
                });

        RxView.clicks(binding.cardView)//绑定点击事件
                .throttleFirst(1000, TimeUnit.MILLISECONDS)//防重复点击
                .compose(bindToLifecycle())//绑定生命周期
                .subscribe(new Consumer<Unit>() {//订阅
                    @Override
                    public void accept(Unit unit) throws Exception {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("https://github.com/zhangkekekeke/KeYeGank"));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });

        RxView.clicks(binding.cardGankio)
                .throttleFirst(1000,TimeUnit.MILLISECONDS)
                .compose(bindToLifecycle())
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) throws Exception {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("http://gank.io"));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });


    }
}
