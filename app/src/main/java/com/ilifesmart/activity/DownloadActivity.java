package com.ilifesmart.activity;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
    @OnClick(R.id.download)
    public void onViewClicked() {
        if (isLoading) {
            return;
        }
        Utils.startDownload(this, apkPath);
        isLoading = true;
    }

//    public static void installNormal(Context context, String apkPath) {
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        //版本在7.0以上是不能直接通过uri访问的
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
//            File file = (new File(apkPath));
//            // 由于没有在Activity环境下启动Activity,设置下面的标签
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            //参数1:上下文, 参数2:Provider主机地址 和配置文件中保持一致,参数3:共享的文件
//            Uri apkUri = FileProvider.getUriForFile(context, "com.xxxxx.fileprovider", file);
//            //添加这一句表示对目标应用临时授权该Uri所代表的文件
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
//        } else {
//            intent.setDataAndType(Uri.fromFile(new File(apkPath)),
//                    "application/vnd.android.package-archive");
//        }
//        context.startActivity(intent);
//    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (resultCode == InstallUtil.UNKNOWN_CODE) {
//            new InstallUtil(DownloadActivity.this, DownloadActivity.this.apkPath).install();
//        }
//    }
}
