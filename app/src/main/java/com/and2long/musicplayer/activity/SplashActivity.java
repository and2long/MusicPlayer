package com.and2long.musicplayer.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.and2long.musicplayer.R;
import com.zhy.m.permission.MPermissions;
import com.zhy.m.permission.PermissionDenied;
import com.zhy.m.permission.PermissionGrant;
import com.zhy.m.permission.ShowRequestPermissionRationale;

/**
 * Created by L on 2016/11/4.
 */

public class SplashActivity extends AppCompatActivity {


    private static final int REQUECT_CODE_SDCARD = 100;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        if (!MPermissions.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUECT_CODE_SDCARD)) {
            MPermissions.requestPermissions(this, REQUECT_CODE_SDCARD, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        } else {
            intoHome();
        }
    }

    @PermissionGrant(REQUECT_CODE_SDCARD)
    public void requestSdcardSuccess() {
        intoHome();
    }

    @PermissionDenied(REQUECT_CODE_SDCARD)
    public void requestSdcardFailed() {
        showHelpDialog();
    }

    @ShowRequestPermissionRationale(REQUECT_CODE_SDCARD)
    public void whyNeedSdCard() {
        showHelpDialog();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        MPermissions.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * 进入主界面。
     */
    private void intoHome() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2000);

    }

    private void showHelpDialog() {
        //提示用户需要权限
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.help)
                .setCancelable(false)
                .setMessage(R.string.message_need_permission)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .setPositiveButton(R.string.setting, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //进入设置中的应用信息详情页，让用户手动授权
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        startActivity(intent);
                        finish();
                    }
                })
                .create()
                .show();
    }
}
