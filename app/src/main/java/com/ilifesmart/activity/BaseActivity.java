package com.ilifesmart.activity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.ilifesmart.androiddemo.R;
import com.ilifesmart.ui.DetachableClickListener;
import com.ilifesmart.ui.DetachableDismissListener;

public class BaseActivity extends AppCompatActivity {

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			View v = getCurrentFocus();
			if (isShouldHideInput(v, event)) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				if (imm.isActive()) {
					imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				}

				v.clearFocus();
			}
		}
		return super.dispatchTouchEvent(event);
	}

	public static boolean isShouldHideInput(View v, MotionEvent ev) {
		if (v != null && (v instanceof EditText)) {
			int[] leftTop = {0, 0};
			v.getLocationInWindow(leftTop); // 当前窗口内的绝对地址，相对于Group的左上角
			int left = leftTop[0];
			int top = leftTop[1];

			int bottom = top + v.getHeight();
			int right = left + v.getWidth();

			return !(ev.getX() > left && ev.getX() < right && ev.getY() > top && ev.getY() < bottom);
		}

		return false;
	}

	private int PERMISSION_CODE_MANUAL = 2;
	private AlertDialog permissonDialog;

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	protected void alertPermissionRequest(String[] permissions) {
		if (permissonDialog == null) {
			DetachableClickListener OnPositiveListener = DetachableClickListener.wrap(new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent intent = new Intent();
					intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
					intent.addCategory(Intent.CATEGORY_DEFAULT);
					intent.setData(Uri.parse("package:" + getPackageName()));
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
					intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
					startActivityForResult(intent, PERMISSION_CODE_MANUAL);
				}
			});

			DetachableDismissListener OnDismissListener = DetachableDismissListener.wrap(new DialogInterface.OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface dialogInterface) {
				}
			});
			permissonDialog = new AlertDialog.Builder(this).setMessage(getResources().getText(R.string.auth))
							.setTitle(getResources().getText(R.string.warn))
							.setPositiveButton(getResources().getText(R.string.maunalauth), OnPositiveListener)
							.setOnDismissListener(OnDismissListener)
							.create();
			OnPositiveListener.clearOnDetach(permissonDialog);
			OnDismissListener.clearOnDetach(permissonDialog);
		}
		permissonDialog.show();
	}

	protected void popupDialog(String title, String message) {
		new AlertDialog.Builder(this).setTitle(title)
						.setCancelable(true)
						.setMessage(message).show();
	}

}
