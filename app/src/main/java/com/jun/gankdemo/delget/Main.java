package com.jun.gankdemo.delget;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class Main {

    public static void main(String[] args){

        //先创建一个真实对象
        Qiangge qiangge = new Qiangge();

        //静态代理
        //创建一个代理对象
        /*
        Songdaili songdaili = new Songdaili(qiangge);
        songdaili.film();
        */

        /**
         * classLoader 用来加载class文件
         * Class<?>[] 需要实现的业务接口
         * InvocationHandler 调用真实对象的业务方法的地方
         */
        //生成动态代理对象
        Iplay proxy = (Iplay) Proxy.newProxyInstance(
                qiangge.getClass().getClassLoader(),
                new Class[]{Iplay.class}, new InvocationHandler() {
                    @Override
                    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
                        System.out.println("经济人公司派遣小张处理强哥业务 约好时间地点拍电影");
                        qiangge.film();
                        return null;
                    }
                });

        proxy.film();
    }
}
