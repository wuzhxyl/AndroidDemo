package com.ilifesmart.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.ilifesmart.androiddemo.R;

public class QrcodeActivity extends AppCompatActivity {

	public static final String TAG = "QrcodeActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_qrcode);

		new IntentIntegrator(this).initiateScan();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {


		IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
		if (result != null) {
			String content = result.getContents();
			if (content == null) {
				// invalid
			} else {
				Log.d(TAG, "onActivityResult: content " + content);
			}
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}
}
