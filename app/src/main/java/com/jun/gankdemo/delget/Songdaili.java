package com.jun.gankdemo.delget;

public class Songdaili implements Iplay {

    //代理类需要有真实对象
    Qiangge qiangge;

    public Songdaili(Qiangge qiangge){
        this.qiangge = qiangge;
    }

    @Override
    public void film() {
        System.out.println("宋先跟公司谈好，约定时间 地点");
        qiangge.film();
    }
}
