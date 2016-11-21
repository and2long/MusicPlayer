package com.and2long.musicplayer.activity;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.and2long.musicplayer.R;
import com.and2long.musicplayer.adapter.MusicListAdapter;
import com.and2long.musicplayer.bean.SongBean;
import com.and2long.musicplayer.global.Constants;
import com.and2long.musicplayer.service.AudioPlayService;
import com.and2long.musicplayer.utils.AudioUtil;
import com.and2long.musicplayer.utils.FileUtils;
import com.and2long.musicplayer.view.MarqeeTextView;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    @BindView(R.id.rv_song_list)
    RecyclerView rvMusicList;
    @BindView(R.id.layout_song_list)
    LinearLayout layoutMusicList;
    @BindView(R.id.tv_scan)
    TextView tvScan;
    @BindView(R.id.layout_empty)
    RelativeLayout layoutEmpty;
    @BindView(R.id.pb_loading)
    ProgressBar pbLoading;
    @BindView(R.id.layout_loading)
    RelativeLayout layoutLoading;
    @BindView(R.id.tv_main_song_title)
    TextView tvSongTitle;
    @BindView(R.id.iv_main_play)
    ImageView ivMainPlay;
    public static ArrayList<SongBean> allSongs;
    @BindView(R.id.tv_main_song_singer)
    MarqeeTextView tvSongSinger;
    @BindView(R.id.pb_progress)
    ProgressBar pbProgress;
    private MusicListAdapter adapter;
    private AudioPlayService.MyBinder myBinder;
    private MyConn conn;
    private Intent intentAudioService;
    public static int currentSong = 0;
    private boolean isFirstClick = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setTitle(getString(R.string.activity_main));
        init();
    }

    private void init() {
        initCache();
        allSongs = FileUtils.readObjectFromFile(Constants.musicList);
        initView();
        //开启并绑定服务
        startAudioService();
    }

    private void initView() {
        adapter = new MusicListAdapter(this);
        adapter.setData(allSongs);
        adapter.setOnItemClickListener(new MusicListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //设置当前正在播放的歌曲。
                currentSong = position;
                //设置底部标题
                tvSongTitle.setText(allSongs.get(position).getTitle());
                tvSongSinger.setText(allSongs.get(position).getSinger());
                //播放音乐。
                AudioPlayService.path = allSongs.get(position).getFileUrl();
                myBinder.play(0);
                //更改按钮图标
                ivMainPlay.setBackgroundResource(R.mipmap.btn_pause);
            }
        });
        rvMusicList.setLayoutManager(new LinearLayoutManager(this));
        rvMusicList.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                refreshAndGetAllSongs();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //重新设置标题
        if (allSongs != null && allSongs.size() > 0) {
            showMusicList();
            tvSongTitle.setText(allSongs.get(currentSong).getTitle());
            tvSongSinger.setText(allSongs.get(currentSong).getSinger());
            AudioPlayService.path = allSongs.get(currentSong).getFileUrl();
        } else {
            showEmptyPage();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (conn != null) {
            unbindService(conn);
        }
        stopAudioService();
    }

    /**
     * 点击事件处理
     *
     * @param view
     */
    @OnClick({R.id.tv_scan, R.id.iv_main_play, R.id.iv_main_next, R.id.layout_title})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_scan:
                refreshAndGetAllSongs();
                break;
            case R.id.iv_main_play:
                playMusic();
                break;
            case R.id.iv_main_next:
                playNextSong();
                break;
            case R.id.layout_title:
                Intent intent = new Intent(this, MusicPlayActivity.class);
                intent.putExtra("songs", allSongs);
                startActivity(intent);
                overridePendingTransition(R.anim.bottom_enter, R.anim.top_exit);
                break;
        }
    }

    /**
     * 播放与暂停音乐；
     */
    private void playMusic() {
        if (isFirstClick && !AudioPlayService.isPause) {
            //设置播放按钮背景
            ivMainPlay.setBackgroundResource(R.mipmap.btn_pause);
            //播放音乐，返回音乐文件总时长
            myBinder.play(0);
            //更改是否第一次点击
            isFirstClick = false;
            return;
        }
        if (AudioPlayService.isPause) {
            //播放
            ivMainPlay.setBackgroundResource(R.mipmap.btn_pause);
            myBinder.start();
        } else {
            //暂停
            ivMainPlay.setBackgroundResource(R.mipmap.btn_play);
            myBinder.pause();
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
        tvSongTitle.setText(allSongs.get(nextSong).getTitle());
        tvSongSinger.setText(allSongs.get(nextSong).getSinger());
        ivMainPlay.setBackgroundResource(R.mipmap.btn_pause);
        AudioPlayService.path = allSongs.get(nextSong).getFileUrl();
        myBinder.play(0);
        currentSong = nextSong;
    }

    /**
     * 刷新媒体库，得到所有歌曲
     */
    private void refreshAndGetAllSongs() {
        //显示等待框
        showLoadingPage();
        //强制更新媒体库
        MediaScannerConnection.scanFile(getApplicationContext(), new String[]{Environment
                .getExternalStorageDirectory().getAbsolutePath()}, null, null);
        //得到所有歌曲文件
        allSongs = AudioUtil.getAllSongs(this);
        //将列表集合保存到本地
        FileUtils.writeObjectToFile(allSongs, Constants.musicList);
        //更新列表
        adapter.setData(allSongs);
        adapter.notifyDataSetChanged();
        //显示音乐列表
        showMusicList();
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

    private void initCache() {
        File cacheDir = new File(Constants.cacheDir);
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
    }


    private void showEmptyPage() {
        layoutMusicList.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.VISIBLE);
        layoutLoading.setVisibility(View.GONE);
    }

    private void showLoadingPage() {
        layoutMusicList.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.GONE);
        layoutLoading.setVisibility(View.VISIBLE);
    }

    private void showMusicList() {
        layoutMusicList.setVisibility(View.VISIBLE);
        layoutEmpty.setVisibility(View.GONE);
        layoutLoading.setVisibility(View.GONE);
    }

    private void startAudioService() {
        if (allSongs != null && allSongs.size() > 0) {
            AudioPlayService.path = allSongs.get(currentSong).getFileUrl();
        }
        intentAudioService = new Intent(this, AudioPlayService.class);
        startService(intentAudioService);
        Intent intentService = new Intent(MainActivity.this, AudioPlayService.class);
        conn = new MyConn();
        bindService(intentService, conn, Service.BIND_AUTO_CREATE);
    }

    private void stopAudioService() {
        stopService(intentAudioService);
    }


}
