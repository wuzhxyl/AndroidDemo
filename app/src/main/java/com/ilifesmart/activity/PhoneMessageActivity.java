package com.ilifesmart.activity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.ilifesmart.androiddemo.R;
import com.ilifesmart.persistent.PersistentMgr;
import com.ilifesmart.ui.DetachableClickListener;
import com.ilifesmart.ui.DetachableDismissListener;
import com.ilifesmart.ui.ToastUtils;
import com.ilifesmart.util.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PhoneMessageActivity extends BaseActivity {

	public static final String TAG = "PhoneMessageActivity";

	@BindView(R.id.et_phone)
	EditText mPhone;
	@BindView(R.id.et_message)
	EditText mMessage;

	private final static String BOOLEAN_PERMISSIONS_CALL_PHONE = "CALL_PHONE";
	public static final String SMS_ACTION = "com.android.TinySMS.RESULT";

	private class SendReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent != null && intent.getAction().equals(SMS_ACTION)) {
				int resultCode = getResultCode();
				if (resultCode == RESULT_OK) {
					Toast.makeText(context, "短消息发送成功", Toast.LENGTH_SHORT).show();
				} else {
					ToastUtils.show(context, "短消息发送失败");
				}
			}
		}
	}

	private SendReceiver mSendReceiver;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_phone_message);
		ButterKnife.bind(this);
		mSendReceiver = new SendReceiver();
		registerReceiver(mSendReceiver, new IntentFilter(SMS_ACTION));

		if (getIntent() != null) {
			String phone = getIntent().getStringExtra("PHONE");
			mPhone.setText(phone);
		}
	}

	@OnClick({R.id.dial, R.id.message})
	public void onButtonClicked(View view) {
		switch (view.getId()) {
			case R.id.dial:
				Utils.dialMobile(this, mPhone.getText().toString());
				break;
			case R.id.message:
				if (Utils.checkPermissionGranted(new String[] {Utils.PERMISSIONS_SEND_MESSAGE, Utils.PERMISSIONS_READ_PHONE_STATE})) {
					Utils.sendMessage(this, mPhone.getText().toString(), mMessage.getText().toString());
				} else {
					Utils.requestPermissions(this, new String[] {Utils.PERMISSIONS_SEND_MESSAGE, Utils.PERMISSIONS_READ_PHONE_STATE}, true, Utils.PERMISSION_CODE_SEND_MESSAGE);
				}

				break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mSendReceiver);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == Utils.PERMISSION_CODE_SEND_MESSAGE) {
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
				Utils.sendMessage(this, mPhone.getText().toString(), mMessage.getText().toString());
			}
		}
	}

}
