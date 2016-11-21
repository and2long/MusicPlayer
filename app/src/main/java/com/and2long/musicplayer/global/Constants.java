package com.and2long.musicplayer.global;

import android.os.Environment;

/**
 * Created by L on 2016/11/2.
 */

public class Constants {

    public static String sdCard = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static String cacheDir = sdCard + "/MusicPlayer";
    public static String musicList = cacheDir + "/mList.dat";
}
