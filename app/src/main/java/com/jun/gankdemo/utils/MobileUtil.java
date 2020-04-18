package com.jun.gankdemo.utils;

import android.content.Context;
import android.content.res.Configuration;

public class MobileUtil {

    /**
     * 手机是否竖屏
     * @param context
     * @return
     */
    public static boolean isOrientationPortrait(Context context){
        if(context.getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_PORTRAIT){
            return true;
        }
        return false;
    }

    /**
     * 手机是否横屏
     * @param context
     * @return
     */
    public static boolean isOrientationLandscape(Context context){
        if(context.getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_LANDSCAPE){
            return true;
        }
        return false;
    }
}
