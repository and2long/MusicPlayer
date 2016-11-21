package com.and2long.musicplayer.activity;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.and2long.musicplayer.R;
import com.and2long.musicplayer.service.AudioPlayService;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.and2long.musicplayer.activity.MainActivity.allSongs;
import static com.and2long.musicplayer.activity.MainActivity.currentSong;

/**
 * Created by L on 2016/11/2.
 * 音乐播放界面
 */

public class MusicPlayActivity extends AppCompatActivity {

    private static final String TAG = "MusicPlayActivity";
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_singer)
    TextView tvSinger;
    @BindView(R.id.iv_play)
    ImageView ivPlay;
    @BindView(R.id.activity_music_play)
    RelativeLayout activityMusicPlay;
    @BindView(R.id.tv_progress)
    TextView tvProgress;
    @BindView(R.id.seekBar)
    SeekBar seekBar;
    @BindView(R.id.tv_duration)
    TextView tvDuration;
    private AudioPlayService.MyBinder myBinder;
    private GestureDetector mGestureDetector;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_music_play);
        ButterKnife.bind(this);
        //创建手势识别监听器
        initGestureDetector();
        //绑定音乐服务
        Intent intentService = new Intent(this, AudioPlayService.class);
        bindService(intentService, new MyConn(), Service.BIND_AUTO_CREATE);
        //设置界面各控件的状态
        if (AudioPlayService.isPause) {
            ivPlay.setBackgroundResource(R.mipmap.btn_play);
        } else {
            ivPlay.setBackgroundResource(R.mipmap.btn_pause);
        }
        tvTitle.setText(allSongs.get(currentSong).getTitle());
        tvSinger.setText(allSongs.get(currentSong).getSinger());

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //让手势识别器识别手势事件。
        mGestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    /**
     * 初始化手势识别器
     */
    private void initGestureDetector() {
        mGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (Math.abs(velocityX) < 200) {
                    Log.i(TAG, "onFling: 移动太慢，无效操作。");
                    return true;
                }
                if (Math.abs(e2.getRawY() - e1.getRawY()) > 100) {
                    Log.i(TAG, "onFling: 垂直方向移动过大，无效操作");
                    return true;
                }
                if (e2.getRawX() - e1.getRawX() > 200) {
                    finish();
                    return true;
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        //activity关闭动画。
        overridePendingTransition(0, R.anim.left_exit);
    }

    @OnClick({R.id.iv_play, R.id.iv_previous, R.id.iv_next})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_play:
                if (AudioPlayService.isPause) {
                    ivPlay.setBackgroundResource(R.mipmap.btn_pause);
                    myBinder.start();
                } else {
                    ivPlay.setBackgroundResource(R.mipmap.btn_play);
                    myBinder.pause();
                }
                break;
            case R.id.iv_previous:
                playPreviousSong();
                break;
            case R.id.iv_next:
                playNextSong();
                break;
        }
    }


    /**
     * 播放下一首歌曲
     */
    private void playNextSong() {
        int nextSong = currentSong + 1;
        if (nextSong > allSongs.size() - 1) {
            nextSong = 0;
        }
        tvTitle.setText(allSongs.get(nextSong).getTitle());
        tvSinger.setText(allSongs.get(nextSong).getSinger());
        ivPlay.setBackgroundResource(R.mipmap.btn_pause);
        AudioPlayService.path = allSongs.get(nextSong).getFileUrl();
        myBinder.play(0);
        currentSong = nextSong;
    }

    /**
     * 播放上一首歌曲
     */
    private void playPreviousSong() {
        int previousSong = currentSong - 1;
        if (previousSong < 0) {
            previousSong = allSongs.size() - 1;
        }
        tvTitle.setText(allSongs.get(previousSong).getTitle());
        tvSinger.setText(allSongs.get(previousSong).getSinger());
        ivPlay.setBackgroundResource(R.mipmap.btn_pause);
        AudioPlayService.path = allSongs.get(previousSong).getFileUrl();
        myBinder.play(0);
        currentSong = previousSong;
    }

    private class MyConn implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myBinder = (AudioPlayService.MyBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }


}
