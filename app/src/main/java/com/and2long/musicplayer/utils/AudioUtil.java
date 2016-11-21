package com.and2long.musicplayer.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.and2long.musicplayer.bean.SongBean;

import java.util.ArrayList;

/**
 * Created by L on 2016/11/1.
 * 音频文件的帮助类
 */

public class AudioUtil {

    /**
     * 获取所有歌曲
     *
     * @param context
     * @return
     */
    public static ArrayList<SongBean> getAllSongs(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Media._ID,
                        MediaStore.Audio.Media.DISPLAY_NAME,
                        MediaStore.Audio.Media.TITLE,
                        MediaStore.Audio.Media.DURATION,
                        MediaStore.Audio.Media.ARTIST,
                        MediaStore.Audio.Media.ALBUM,
                        MediaStore.Audio.Media.MIME_TYPE,
                        MediaStore.Audio.Media.SIZE,
                        MediaStore.Audio.Media.DATA},
                MediaStore.Audio.Media.MIME_TYPE + "=? or "
                        + MediaStore.Audio.Media.MIME_TYPE + "=?",
                new String[]{"audio/mpeg", "audio/x-ms-wma"},
                null);
        ArrayList<SongBean> songs = new ArrayList<>();

        if (cursor.moveToFirst()) {

            SongBean song = null;

            do {
                song = new SongBean();
                // 文件名
                song.setFileName(cursor.getString(1));
                // 歌曲名
                song.setTitle(cursor.getString(2));
                // 时长
                song.setDuration(cursor.getInt(3));
                // 歌手名
                String singer = cursor.getString(4).replace("unknown", "未知");
                song.setSinger(singer);
                // 专辑名
                song.setAlbum(cursor.getString(5));
                // 歌曲格式
                if ("audio/mpeg".equals(cursor.getString(6).trim())) {
                    song.setType("mp3");
                } else if ("audio/x-ms-wma".equals(cursor.getString(6).trim())) {
                    song.setType("wma");
                }
                // 文件大小
                if (cursor.getString(7) != null) {
                    float size = cursor.getInt(7) / 1024f / 1024f;
                    song.setSize((size + "").substring(0, 4) + "M");
                } else {
                    song.setSize("未知");
                }
                // 文件路径
                if (cursor.getString(8) != null) {
                    song.setFileUrl(cursor.getString(8));
                }
                songs.add(song);
            } while (cursor.moveToNext());

            cursor.close();

        }
        return songs;

    }


    /**
     * 整数(秒数)转换为时分秒格式(xx:xx:xx)
     *
     * @param time
     * @return
     */
    public static String secToTime(int time) {
        String timeStr = null;
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (time <= 0)
            return "00:00";
        else {
            minute = time / 60;
            if (minute < 60) {
                second = time % 60;
                timeStr = unitFormat(minute) + ":" + unitFormat(second);
            } else {
                hour = minute / 60;
                if (hour > 99)
                    return "99:59:59";
                minute = minute % 60;
                second = time - hour * 3600 - minute * 60;
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
            }
        }
        return timeStr;
    }

    public static String unitFormat(int i) {
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "0" + Integer.toString(i);
        else
            retStr = "" + i;
        return retStr;
    }
}
