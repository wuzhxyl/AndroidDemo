package com.ilifesmart.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ilifesmart.androiddemo.R;
import com.ilifesmart.util.Utils;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SnapQrcodeVoiceActivity extends BaseActivity {

    public static final String TAG = "SnapQrcodeVoiceActivity";
    public final static int REQUEST_CODE_CAMERA = 2047;
    @BindView(R.id.imageview)
    ImageView mImageview;
    @BindView(R.id.voice)
    Button mVoice;
    @BindView(R.id.sound_path)
    TextView mSoundPath;

    private boolean isRecord = false;
    private MediaRecorder mRecorder;

    private class CameraGalleryUpdate extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: 更新完毕.. ");
        }
    }

    private CameraGalleryUpdate mUpdate = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snap_qrcode_voice);
        ButterKnife.bind(this);

        mUpdate = new CameraGalleryUpdate();
        registerReceiver(mUpdate, new IntentFilter(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mUpdate);
        stopRecord();
    }

    @OnClick(R.id.snap)
    public void onSnapClicked() {
        if (Utils.checkPermissionGranted(new String[]{Utils.PERMISSIONS_CAMERA, Utils.PERMISSIONS_WRITE_EXTERNAL_STORAGE})) {
            takePhoto();
        } else {
            Utils.requestPermissions(this, new String[]{Utils.PERMISSIONS_CAMERA, Utils.PERMISSIONS_WRITE_EXTERNAL_STORAGE}, true, Utils.PERMISSION_CODE_CAMERA);
        }
    }

    private void takePhoto() {
        mImageview.setVisibility(View.VISIBLE);
        mSoundPath.setVisibility(View.GONE);
        Utils.takeCameraSnap(this);
    }

    @OnClick(R.id.qrcode)
    public void onQrcodeClicked() {
        Utils.startActivity(this, QrcodeActivity.class);
    }

    @OnClick(R.id.voice)
    public void onVoiceClicked() {
        mImageview.setVisibility(View.GONE);
        setVoiceText();
        if (isRecord) {
            mSoundPath.setText("");
            if (Utils.checkPermissionGranted(new String[]{Utils.PERMISSIONS_RECORD_AUDIO, Utils.PERMISSIONS_WRITE_EXTERNAL_STORAGE})) {
                startRecord();
            } else {
                Utils.requestPermissions(this, new String[]{Utils.PERMISSIONS_RECORD_AUDIO, Utils.PERMISSIONS_WRITE_EXTERNAL_STORAGE}, true, Utils.PERMISSION_CODE_RECORD_AUDIO);
            }
        } else {
            stopRecord();
        }
    }

    private void setVoiceText() {
        isRecord = !isRecord;
        if (isRecord) {
            mVoice.setText("停止录音");
        } else {
            mVoice.setText("开始录音");
        }
    }

    private void startRecord() {
        Utils.startRecord();
    }

    private void stopRecord() {
        Utils.stopRecord();
        mImageview.setVisibility(View.GONE);
        mSoundPath.setVisibility(View.VISIBLE);
        File currentFile = Utils.getCurrentFile();
        if (currentFile != null) {
            mSoundPath.setText("音频存储路径：\r\n" + currentFile.getAbsolutePath());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CAMERA && RESULT_OK == resultCode) {
            Bundle bundle = data.getExtras();
            Bitmap aphoto = null;
            if (bundle != null && bundle.get("data") != null) {
                aphoto = (Bitmap) bundle.get("data");
                mImageview.setImageBitmap(aphoto);   // 缩略图
            } else {
                aphoto = BitmapFactory.decodeFile(Utils.getCurrentPhotoPath());
//				Utils.setPictureDegreeZero("90"); // TODO:预览图显示为旋转90度
                mImageview.setImageBitmap(aphoto);  // 缩略图
            }
            Utils.galleryUpdate(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == Utils.PERMISSION_CODE_CAMERA || requestCode == Utils.PERMISSION_CODE_RECORD_AUDIO) {
            boolean isAllGranted = true;

            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    isAllGranted = false;
                    break;
                }
            }

            if (isAllGranted) {
                if (requestCode == Utils.PERMISSION_CODE_CAMERA) {
                    takePhoto();
                } else if (requestCode == Utils.PERMISSION_CODE_RECORD_AUDIO) {
                    startRecord();
                }
            } else {
                alertPermissionRequest(permissions);
            }
        }
    }
}
