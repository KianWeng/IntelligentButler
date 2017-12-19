package com.kian.intelligentbutler.util;

import android.app.Application;

/**
 * Created by Kian on 2017/12/18.
 */

public class ContextUtil extends Application{
    private static ContextUtil instance;

    public static ContextUtil getInstance(){
        return instance;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        instance = this;
    }
}
