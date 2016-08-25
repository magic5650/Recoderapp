package com.magicrecoder.recoderapp;

import android.content.Context;

import java.lang.reflect.Field;

/**
 * Created by Administrator on 2016-08-25.
 */
public class CommonUtils {

    private static int statusBarHeight = 0;

    /**
     * get the statusbar height
     *
     * @param context
     * @return
     */
    public static int getStatusbarHeight(Context context) {
        if (statusBarHeight == 0) {
            try {
                Class<?> c = Class.forName("com.android.internal.R$dimen");
                Object o = c.newInstance();
                Field field = c.getField("status_bar_height");
                int x = (Integer) field.get(o);
                statusBarHeight = context.getResources().getDimensionPixelSize(x);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return statusBarHeight;
    }
}
