package com.and2long.musicplayer.global;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.squareup.leakcanary.LeakCanary;

/**
 * Created by L on 2016/11/2.
 */

public class MyApplication extends Application {

    public static SharedPreferences sharedPreferences;
    public static Context context;

    @Override
    public void onCreate() {
        context = this;
        LeakCanary.install(this);
        sharedPreferences = getSharedPreferences("musicList", MODE_PRIVATE);
    }

}
