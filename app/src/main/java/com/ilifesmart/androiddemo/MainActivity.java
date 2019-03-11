package com.ilifesmart.androiddemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.ilifesmart.App;
import com.ilifesmart.activity.PhoneMessageActivity;
import com.ilifesmart.activity.SnapQrcodeVoiceActivity;
import com.ilifesmart.util.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

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


}
