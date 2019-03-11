package com.ilifesmart.activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

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
			v.getLocationInWindow(leftTop);
			int left = leftTop[0];
			int top = leftTop[1];

			int bottom = top + v.getHeight();
			int right = left + v.getWidth();

			return !(ev.getX() > left && ev.getX() < right && ev.getY() > top && ev.getY() < bottom);
		}

		return false;
	}

}
