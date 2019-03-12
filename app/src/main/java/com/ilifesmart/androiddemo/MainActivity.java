package com.ilifesmart.androiddemo;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.ilifesmart.App;
import com.ilifesmart.activity.BaseActivity;
import com.ilifesmart.activity.DevicesInfoActivity;
import com.ilifesmart.activity.PhoneMessageActivity;
import com.ilifesmart.activity.SnapQrcodeVoiceActivity;
import com.ilifesmart.util.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    @BindView(R.id.H5)
    Button mH5;
    @BindView(R.id.device_info)
    Button mDeviceInfo;
    @BindView(R.id.device_phone_msg)
    Button mDevicePhoneMsg;
    @BindView(R.id.snap_scancode_voice)
    Button mSnapScancodeVoice;
    @BindView(R.id.share)
    Button mShare;
    @BindView(R.id.upgrade)
    Button mUpgrade;
    @BindView(R.id.ftp)
    Button mFtp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if (!App.isTestVer()) {
            mH5.setVisibility(View.GONE);
            mDeviceInfo.setVisibility(View.GONE);
            mShare.setVisibility(View.GONE);
            mUpgrade.setVisibility(View.GONE);
            mFtp.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.H5, R.id.device_info, R.id.device_phone_msg, R.id.snap_scancode_voice, R.id.share, R.id.upgrade, R.id.ftp})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.H5:
                break;
            case R.id.device_info:
                if (Utils.checkPermissionGranted(Utils.PERMISSIONS_ACCESS_FINE_LOCATION)) {
                    Utils.startActivity(this, DevicesInfoActivity.class);
                } else {
                    Utils.requestPermissions(this, Utils.PERMISSIONS_ACCESS_FINE_LOCATION, true, Utils.PERMISSION_CODE_ACCESS_FINE_LOCATION);
                }

                break;
            case R.id.device_phone_msg:
                Utils.startActivity(this, PhoneMessageActivity.class);
                break;
            case R.id.snap_scancode_voice:
                Utils.startActivity(this, SnapQrcodeVoiceActivity.class);
                break;
            case R.id.share:
                break;
            case R.id.upgrade:
                break;
            case R.id.ftp:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == Utils.PERMISSION_CODE_ACCESS_FINE_LOCATION) {
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
                Utils.startActivity(this, DevicesInfoActivity.class);
            }
        }
    }
}
