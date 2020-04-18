package com.jun.gankdemo.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.databinding.DataBindingUtil;

import com.github.chrisbanes.photoview.OnViewTapListener;
import com.github.chrisbanes.photoview.PhotoViewAttacher;
import com.jun.gankdemo.R;
import com.jun.gankdemo.databinding.ActivityPictureBinding;
import com.jun.gankdemo.utils.LogUtil;
import com.jun.gankdemo.widget.PullBackLayout;
import com.squareup.picasso.Picasso;

public class PictureActivity extends AppCompatActivity implements PullBackLayout.PullCallBack {

    private boolean systemUiIsShow = true;
    private ColorDrawable background;
    private PhotoViewAttacher mViewAttacher;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        ActivityPictureBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_picture);

        String picUrl = getIntent().getExtras().getString("url");

        //"girl"就是完成协同动画的view的标记，相当于在布局文件中android:transitionName="girl"
        ViewCompat.setTransitionName(binding.ivPhoto,"girl");

        Picasso.get().load(picUrl).into(binding.ivPhoto);

        background = new ColorDrawable(Color.BLACK);
        binding.pullBackLayout.getRootView().setBackground(background);

        mViewAttacher = new PhotoViewAttacher(binding.ivPhoto);

        binding.pullBackLayout.setPullCallBack(this);
        mViewAttacher.setOnViewTapListener(new OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                if(systemUiIsShow){
                    hideSystemUI();
                    systemUiIsShow = false;
                }else{
                    showSystemUI();
                    systemUiIsShow = true;
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        showSystemUI();
        return super.onKeyDown(keyCode, event);
    }

    private void hideSystemUI(){
        getWindow().getDecorView().setSystemUiVisibility(FLAG_HIDE_SYSTEM_UI);
    }

    private void showSystemUI(){
        getWindow().getDecorView().setSystemUiVisibility(FLAG_SHOW_SYSTEM_UI);
    }

    @Override
    public void onPullStart() {
        showSystemUI();
    }

    @Override
    public void onPull(float progress) {
        showSystemUI();
        background.setAlpha((int)(0Xff*(1f-progress)));
    }

    @Override
    public void onPullCompleted() {
        showSystemUI();
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mViewAttacher = null;
    }

    private static final int FLAG_HIDE_SYSTEM_UI = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
            | View.SYSTEM_UI_FLAG_IMMERSIVE;

    private static final int FLAG_SHOW_SYSTEM_UI = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
}
