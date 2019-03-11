package com.ilifesmart.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.ilifesmart.androiddemo.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class QrcodeActivity extends AppCompatActivity {

    public static final String TAG = "QrcodeActivity";
    @BindView(R.id.qrcode_info)
    TextView mQrcodeInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
        ButterKnife.bind(this);

        new IntentIntegrator(this).setOrientationLocked(false).initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            String content = result.getContents();
            if (content == null) {
                // invalid
                mQrcodeInfo.setText("无效的数据");
            } else {
                mQrcodeInfo.setText(content);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
