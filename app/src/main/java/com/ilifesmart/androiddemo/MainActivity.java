package com.ilifesmart.androiddemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.ilifesmart.activity.PhoneMessageActivity;
import com.ilifesmart.activity.SnapQrcodeVoiceActivity;
import com.ilifesmart.util.Utils;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);
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
