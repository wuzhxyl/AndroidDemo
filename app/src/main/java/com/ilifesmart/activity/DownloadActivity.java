package com.ilifesmart.activity;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.ilifesmart.androiddemo.R;
import com.ilifesmart.model.OwnFileProvider;
import com.ilifesmart.util.InstallUtil;
import com.ilifesmart.util.Utils;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DownloadActivity extends BaseActivity {

    @BindView(R.id.download)
    Button mDownload;

    public static final String TAG = "DownloadActivity";
    private class DownloadReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: apkPath " + apkPath);
            isLoading = false;
            popupDialog("应用更新", "已下载完毕，请前往通知栏点击安装");
        }
    }

    private DownloadReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        ButterKnife.bind(this);

        mReceiver = new DownloadReceiver();
        registerReceiver(mReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    boolean isLoading = false;
    protected String apkPath;

    /*
    * apkPath: apk下载位置 /Download.
    * isLoading: 正在载入中，避免多次点击.
    * */
    @OnClick(R.id.download)
    public void onDownloadClicked() {
        if (Utils.checkPermissionGranted(Utils.PERMISSIONS_WRITE_EXTERNAL_STORAGE)) {
            onDownloadApk();
        } else {
            Utils.requestPermissions(this, Utils.PERMISSIONS_WRITE_EXTERNAL_STORAGE, true, Utils.PERMISSIONS_CODE_WRITE_EXTERNAL_STORAGE);
        }
    }

    private void onDownloadApk() {
        if (isLoading) {
            return;
        }
        Utils.startDownload(this, apkPath);
        isLoading = true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == Utils.PERMISSIONS_CODE_WRITE_EXTERNAL_STORAGE) {
            boolean isAllGranted = true;

            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    isAllGranted = false;
                    break;
                }
            }

            if (!isAllGranted) {
                alertPermissionRequest(permissions);
            } else {
                onDownloadApk();
            }
        }
    }
}
