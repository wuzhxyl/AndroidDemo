package com.ilifesmart.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Button;

import com.ilifesmart.App;
import com.ilifesmart.androiddemo.R;
import com.ilifesmart.interfaces.ILocationChanged;
import com.ilifesmart.interfaces.INetworkAccessableCB;
import com.ilifesmart.util.NetworkUtils;
import com.ilifesmart.util.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DevicesInfoActivity extends BaseActivity {

	@BindView(R.id.network)
	Button mNetwork;
	@BindView(R.id.latitude)
	Button mLatitude;

	private NetworkChangedReceiver mNetworkChangedReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_devices_info);
		ButterKnife.bind(this);

		mNetworkChangedReceiver = new NetworkChangedReceiver();
		registerReceiver(mNetworkChangedReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mNetworkChangedReceiver);
	}

	private void popupDialog(String title, String message) {
		new AlertDialog.Builder(this).setTitle(title)
						.setCancelable(true)
						.setMessage(message).show();
	}

	private void getNetworkInfo(final boolean isOnline) {
		App.postRunnable(new Runnable() {
			@Override
			public void run() {
				StringBuilder builder = new StringBuilder();
				builder.append("网络状态: ").append(isOnline ? "可用" : "不可用").append("; ")
								.append("网络类型: ").append(NetworkUtils.getNetworkType(DevicesInfoActivity.this)).append("; ");

				if (NetworkUtils.isWifiConnected(DevicesInfoActivity.this)) {
					builder.append("网络名称: ").append(NetworkUtils.getNetworkName(DevicesInfoActivity.this));
				}
				String text = builder.toString();
				mNetwork.setText(text);
			}
		});
	}

	@OnClick(R.id.clipboard)
	public void onClipboardClicked() {
		popupDialog("剪贴板内容", Utils.getClipboardContent());
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d("Amap", "onResume: --------------> ");
		if (Utils.checkPermissionGranted(new String[]{Utils.PERMISSIONS_WRITE_EXTERNAL_STORAGE, Utils.PERMISSIONS_READ_PHONE_STATE, Utils.PERMISSIONS_ACCESS_FINE_LOCATION})) {
			startLocation();
		} else {
			Utils.requestPermissions(this, new String[]{Utils.PERMISSIONS_WRITE_EXTERNAL_STORAGE, Utils.PERMISSIONS_READ_PHONE_STATE, Utils.PERMISSIONS_ACCESS_FINE_LOCATION}, true, Utils.PERMISSION_CODE_ACCESS_FINE_LOCATION);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		stopLocation();
	}

//	@OnClick(R.id.latitude)
//	public void onLocationClicked() {
//		if (Utils.checkPermissionGranted(new String[]{Utils.PERMISSIONS_WRITE_EXTERNAL_STORAGE, Utils.PERMISSIONS_READ_PHONE_STATE, Utils.PERMISSIONS_ACCESS_FINE_LOCATION})) {
//			startLocation();
//		} else {
//			Utils.requestPermissions(this, new String[]{Utils.PERMISSIONS_WRITE_EXTERNAL_STORAGE, Utils.PERMISSIONS_READ_PHONE_STATE, Utils.PERMISSIONS_ACCESS_FINE_LOCATION}, true, Utils.PERMISSION_CODE_ACCESS_FINE_LOCATION);
//		}
//	}

	private boolean isLoading = false;
	private void startLocation() {
		App.startLocation(new ILocationChanged() {
			@Override
			public void onLocationChanged(double latitude, double longitude) {
				mLatitude.setText("Lat:" + latitude +",Lon:" + longitude);
			}

			@Override
			public void onLocationError(int errCode, String errInfo) {
				Log.d("ILocationChanged", "onLocationError: errCode " + errCode);
				Log.d("ILocationChanged", "onLocationError: errInfo " + errInfo);
			}
		});
	}

	private void stopLocation() {
		App.stopLocation();
	}

	private class NetworkChangedReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(final Context context, Intent intent) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					NetworkUtils.isNetworkConnected(context, new INetworkAccessableCB() {
						@Override
						public void isNetWorkOnline(boolean isOnline) {
							DevicesInfoActivity.this.getNetworkInfo(isOnline);
						}
					});
				}
			}).start();
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
				startLocation();
			}
		}
	}
}
