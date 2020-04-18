package com.jun.gankdemo.adapter;

import android.content.Context;
import android.telecom.Call;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.MemoryCategory;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jakewharton.rxbinding3.view.RxView;
import com.jun.gankdemo.R;
import com.jun.gankdemo.bean.ImageBean;
import com.jun.gankdemo.databinding.GirlItemBinding;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Consumer;
import kotlin.Unit;

public class GirlAdapter extends RecyclerView.Adapter<GirlAdapter.GirlViewHolder> implements Consumer<List<ImageBean>> {

    private Context context;
    private List<ImageBean> list;
    private final Glide glide;
    private OnClickListener onClickListener;

    public GirlAdapter(Context context,List<ImageBean> list){
        this.context = context;
        this.list = list;
        glide = Glide.get(this.context);
        glide.setMemoryCategory(MemoryCategory.HIGH);
    }


    public void setOnClickListener(OnClickListener onClickListener){
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public GirlViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.girl_item,parent,false);
        GirlViewHolder holder = new GirlViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull GirlViewHolder holder, int position) {

        ImageBean bean = list.get(position);
        holder.bean = bean;//将数据设置给holder
        holder.binding.setImageBean(bean);//将数据设置给数据绑定器
        holder.binding.executePendingBindings();//需在主线程执行

        glide.with(context)
                .load(bean.getUrl())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.binding.image);
    }



    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void accept(List<ImageBean> imageBeans) throws Exception {
        notifyDataSetChanged();
    }

    class GirlViewHolder extends RecyclerView.ViewHolder{

        public GirlItemBinding binding;
        public ImageBean bean;//onBindViewHolder方法已经设置了bean

        public GirlViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);

            //防止抖动，连续点击打开多个页面
            RxView.clicks(binding.girlLayout)
                    .throttleFirst(1000, TimeUnit.MILLISECONDS)
                    .subscribe(unit ->{
                        if(onClickListener != null){
                            onClickListener.OnImageClick(binding.image,bean);
                        }
                    });

        }
    }


    public interface OnClickListener{
        void OnImageClick(View v,ImageBean imageBean);
    }

}
