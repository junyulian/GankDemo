package com.jun.gankdemo.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.LruCache;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.jakewharton.rxbinding3.recyclerview.RecyclerViewScrollEvent;
import com.jakewharton.rxbinding3.recyclerview.RxRecyclerView;
import com.jakewharton.rxbinding3.swiperefreshlayout.RxSwipeRefreshLayout;
import com.jakewharton.rxbinding3.view.RxView;
import com.jun.gankdemo.R;
import com.jun.gankdemo.adapter.GirlAdapter;
import com.jun.gankdemo.bean.Girls;
import com.jun.gankdemo.bean.ImageBean;
import com.jun.gankdemo.bean.PrettyGirl;
import com.jun.gankdemo.databinding.ActivityMainBinding;
import com.jun.gankdemo.net.NetService;
import com.jun.gankdemo.net.Results;
import com.jun.gankdemo.net.RetrofitUtil;
import com.jun.gankdemo.utils.LogUtil;
import com.jun.gankdemo.utils.MobileUtil;
import com.jun.gankdemo.utils.NetUtil;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.trello.rxlifecycle3.components.support.RxAppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import kotlin.Unit;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.adapter.rxjava2.Result;

public class MainActivity extends RxAppCompatActivity {

    private List<ImageBean> mImages = new ArrayList<>();

    private ActivityMainBinding binding;//数据绑定器
    private GirlAdapter girlAdapter;

    private int page = 1;
    private boolean refreshing;//是否在刷新


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LogUtil.e("----------onNewIntent");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LogUtil.e("---------onCreate");

        //数据绑定
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main);
        //设置actionbar支持 需设置重写的方法onCreateOptionsMenu  onOptionsItemSelected才能有效
        setSupportActionBar(binding.toolbar);
        //RecyclerView设置
        setupRecyclerView();
        //点击toobar滑动到最上层
        flyToTop();
        //下拉刷新
        swipeRefresh();
        //列表图片点击
        onImageClick();
    }

    //列表图片点击
    private void onImageClick() {

        girlAdapter.setOnClickListener(new GirlAdapter.OnClickListener() {
            @Override
            public void OnImageClick(View v, ImageBean imageBean) {

                Intent intent = new Intent(getApplicationContext(),PictureActivity.class);
                intent.putExtra("url",imageBean.getUrl());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                //makeSceneTransitionAnimation两个activity中的某些view协同完成过渡动画
                //"girl"就是完成协同动画的view的标记，相当于在布局文件中android:transitionName="girl"
                ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        MainActivity.this,v,"girl");
                //启动activity的方式需要使用ActivityCompat.startActivity
                ActivityCompat.startActivity(MainActivity.this,intent,compat.toBundle());

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(NetUtil.checkNet(this)){
            //网络请求
            fetchGirlData();
        }else{
            Snackbar.make(binding.recyclerView,"没有网络不能获取图片数据",Snackbar.LENGTH_LONG).show();
        }

    }

    //点击toolbar移动到最上层
    private void flyToTop(){
        RxView.clicks(binding.toolbar)
                .compose(bindToLifecycle())
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) throws Exception {
                        binding.recyclerView.smoothScrollToPosition(0);
                    }
                });
    }

    //下拉刷新
    private void swipeRefresh(){
        RxSwipeRefreshLayout.refreshes(binding.refreshLayout)
                .compose(bindToLifecycle())
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) throws Exception {
                        page = 1;
                        refreshing = true;
                        fetchGirlData();
                    }
                });
    }


    private void setupRecyclerView() {
        //列表适配器
        girlAdapter = new GirlAdapter(this,mImages);

        //横坚屏分别设置瀑布流展示2列或3列
        int spanCount = 2;
        if(MobileUtil.isOrientationPortrait(this)){
            spanCount = 2;
        }else if(MobileUtil.isOrientationLandscape(this)){
            spanCount = 3;
        }
        //瀑布流布局管理器
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(spanCount,StaggeredGridLayoutManager.VERTICAL);
        //RecyclerView设置adapter和manager
        binding.recyclerView.setAdapter(girlAdapter);
        binding.recyclerView.setLayoutManager(layoutManager);


        RxRecyclerView.scrollEvents(binding.recyclerView)//绑定事件
                        .compose(bindToLifecycle())//绑定生命周期
                        .map(new Function<RecyclerViewScrollEvent, Boolean>() {//将滑动事件映射成bool值
                            @Override
                            public Boolean apply(RecyclerViewScrollEvent recyclerViewScrollEvent) throws Exception {
                                boolean isBottom = false;
                                if(MobileUtil.isOrientationLandscape(MainActivity.this)){
                                    isBottom = layoutManager.findLastCompletelyVisibleItemPositions(new int[2])[1]>mImages.size()-4;
                                }else if(MobileUtil.isOrientationLandscape(MainActivity.this)){
                                    isBottom = layoutManager.findLastCompletelyVisibleItemPositions(new int[3])[2] >= mImages.size()-4;
                                }
                                LogUtil.e(layoutManager.findLastCompletelyVisibleItemPositions(new int[2])[1]+"-------isBottom:"+isBottom+"---size:"+mImages.size());
                                return isBottom;
                            }
                        })
                        .filter(new Predicate<Boolean>() {
                            @Override
                            public boolean test(Boolean isBottom) throws Exception {
                                LogUtil.e("---129: isRefresh:"+binding.refreshLayout.isRefreshing()+"----"+isBottom);
                                return binding.refreshLayout.isRefreshing() && isBottom;
                            }
                        })
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean aBoolean) throws Exception {
                                //这么做的目的是一旦下拉刷新，RxRecyclerView scrollEvents 也会被触发，page就会加一
                                //所以要将page设为0，这样下拉刷新才能获取第一页的数据
                                if(refreshing){
                                    page = 0;
                                    refreshing = false;
                                }
                                page += 1;
                                binding.refreshLayout.setRefreshing(true);
                                fetchGirlData();
                            }
                        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_settings:
                //Snackbar.make(binding.appbar,"test",Snackbar.LENGTH_SHORT).show();
                Intent intent = new Intent(this,AboutActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



    private void fetchGirlData() {
        RetrofitUtil.getNetSrvice()
                .fetchPrettyGirl(page)//请求接口
                .compose(bindToLifecycle())//绑定生命周期 防内存泄露
                //.filter(Results.isSuccess())//过滤成功的
                .map(new Function<Girls, List<PrettyGirl>>() {//数据做转换处理
                    @Override
                    public List<PrettyGirl> apply(Girls girls) throws Exception {
                        LogUtil.e("---映射处理次数");
                        return girls.data;
                    }
                })
                .flatMap(imageFetcher)//再次处理
                .subscribeOn(Schedulers.io())//子线程执行网络请求 如果不设置，网络请求会报错
                .observeOn(AndroidSchedulers.mainThread())//回调在主线程
                .cache()
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        binding.refreshLayout.setRefreshing(false);
                    }
                })
                .subscribe(girlAdapter,dataError);

                
    }


    private final Function<List<PrettyGirl>, Observable<List<ImageBean>>> imageFetcher =
            prettyGirls -> {
                for(PrettyGirl girl : prettyGirls){

                    try {
                        OkHttpClient client = new OkHttpClient();
                        LogUtil.e("---url:"+girl.getUrl());
                        final Request myrequest = new Request.Builder()
                                .url(girl.getUrl())
                                .get()
                                .build();
                        Call acall = client.newCall(myrequest);
                        acall.enqueue(new okhttp3.Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                LogUtil.e("---fail"+e.getMessage());
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                LogUtil.e("---hhaa"+response.request().url());
                                String url = response.request().url().toString();
                                Bitmap bitmap = Picasso.get().load(url).get();
                                ImageBean imageBean = new ImageBean();
                                imageBean.setWidth(bitmap.getWidth());
                                imageBean.setHeight(bitmap.getHeight());
                                imageBean.setUrl(url);
                                mImages.add(imageBean);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        return Observable.error(e);
                    }

                }

                Thread.sleep(1000);
                LogUtil.e("------mImages:"+mImages.size());
                return Observable.just(mImages);
            };

//    private final Function<List<PrettyGirl>, Observable<List<ImageBean>>> imageFetcher =
//            new Function<List<PrettyGirl>, Observable<List<ImageBean>>>() {//list列表再处理
//                @Override
//                public  Observable<List<ImageBean>> apply(List<PrettyGirl> prettyGirls) throws Exception {
//
//                    for(PrettyGirl girl : prettyGirls){
//
//                        try {
//                            OkHttpClient client = new OkHttpClient();
//                            LogUtil.e("---url:"+girl.getUrl());
//                            final Request myrequest = new Request.Builder()
//                                    .url(girl.getUrl())
//                                    .get()
//                                    .build();
//                            Call acall = client.newCall(myrequest);
//                            acall.enqueue(new okhttp3.Callback() {
//                                @Override
//                                public void onFailure(Call call, IOException e) {
//                                    LogUtil.e("---fail"+e.getMessage());
//                                }
//
//                                @Override
//                                public void onResponse(Call call, Response response) throws IOException {
//                                    LogUtil.e("---hhaa"+response.request().url());
//
//                                    Bitmap bitmap = Picasso.get().load(girl.url).get();
//                                    ImageBean imageBean = new ImageBean();
//                                    imageBean.setWidth(bitmap.getWidth());
//                                    imageBean.setHeight(bitmap.getHeight());
//                                    imageBean.setUrl(girl.url);
//                                    mImages.add(imageBean);
//
//                                }
//                            });
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            return Observable.error(e);
//                        }
//
//                    }
//                    return Observable.just(mImages);
//                    /*
//                    for(PrettyGirl girl : prettyGirls){
//                        LogUtil.e("--girl:"+girl.url);
//                        girl.setUrl("https://ae01.alicdn.com/kf/U29ad1424fd024374bf8ba95a61a60d8ai.jpg");
//                        Bitmap bitmap = Picasso.get().load(girl.url).get();
//                        ImageBean imageBean = new ImageBean();
//                        imageBean.setWidth(bitmap.getWidth());
//                        imageBean.setHeight(bitmap.getHeight());
//                        imageBean.setUrl(girl.url);
//                        mImages.add(imageBean);
//                    }
//                    return Observable.just(mImages);
//                     */
//                }
//            };

    private Consumer<Throwable> dataError = new Consumer<Throwable>() {
        @Override
        public void accept(Throwable throwable) throws Exception {
            LogUtil.e("--erroe:"+throwable);
            throwable.printStackTrace();
            binding.refreshLayout.setRefreshing(false);
            Snackbar.make(binding.refreshLayout,throwable.toString(),Snackbar.LENGTH_LONG).show();
        }
    };
}
























