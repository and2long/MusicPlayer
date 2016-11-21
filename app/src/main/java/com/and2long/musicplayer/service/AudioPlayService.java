package com.and2long.musicplayer.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.IOException;

/**
 * Created by L on 2016/11/2.
 * 音频播放服务。
 */

public class AudioPlayService extends Service {

    private MediaPlayer mediaPlayer = new MediaPlayer();
    public static String path = "";             //音乐文件路径
    public static boolean isPause;              //播放器状态

    public class MyBinder extends Binder {

        public void play(int position) {
            playMusic(position);
        }

        public void pause() {
            pauseMusic();
        }

        public void stop() {
            stopMusic();
        }

        public void start() {
            startMusic();
        }

    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }


    /**
     * 播放
     */
    public void playMusic(final int position) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.seekTo(position);
                    mp.start();
                }
            });
            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    mediaPlayer.reset();
                    return true;
                }
            });
            isPause = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 继续播放
     */
    public void startMusic() {
        mediaPlayer.start();
        isPause = false;
    }


    /**
     * 暂停
     */
    public void pauseMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPause = true;
        }
    }

    /**
     * 停止音乐
     */
    public void stopMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            isPause = true;
            try {
                mediaPlayer.prepare();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }
}
