package com.ilifesmart.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ilifesmart.androiddemo.R;
import com.ilifesmart.util.DisplayUtils;

public class DemoToolBar extends LinearLayout {

	public static final String TAG = "DemoToolBar";

	public static class DemoToolbarTheme {
		public int backgroundColor;
		public int buttonBackgroundDrawableId;
		public float buttonTextSize;
		public int buttonTextDrawableId;
		public int titleTextColor;
		public float titleTextSize;

		public DemoToolbarTheme(int backgroundColor,
														int buttonBackgroundDrawableId, int buttonTextDrawableId, float buttonTextSize,
														int titleTextColor, float titleTextSize) {
			this.backgroundColor = backgroundColor;
			this.buttonBackgroundDrawableId = buttonBackgroundDrawableId;
			this.buttonTextDrawableId = buttonTextDrawableId;
			this.buttonTextSize = buttonTextSize;
			this.titleTextColor = titleTextColor;
			this.titleTextSize = titleTextSize;
		}
	}
	public static DemoToolbarTheme LightTheme = new DemoToolbarTheme(0xFF333333, R.drawable.light_button, R.color.light_button_text, 12, Color.WHITE, 15);

	private Button mLeftButton;
	private Button mRightButton;
	private TextView mTitle;

	public DemoToolBar(Context context) {
		this(context, null);
	}

	public DemoToolBar(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public DemoToolBar(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		setBackgroundColor(Color.parseColor("#FF333333"));
		initialize(context);
	}

	private void initialize(Context context) {
		mLeftButton = new Button(context);
		mLeftButton.setBackground(null);
		LayoutParams leftArgs = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, DisplayUtils.dp2px(context, 50));
		leftArgs.gravity = Gravity.LEFT|Gravity.CENTER_VERTICAL;
		leftArgs.setMargins(DisplayUtils.dp2px(context, 5), 0, DisplayUtils.dp2px(context, 5), 0);
		mLeftButton.setText("后退");
		addView(mLeftButton, leftArgs);

		mTitle = new TextView(context);
		LayoutParams titleArgs = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, DisplayUtils.dp2px(context, 50));
		titleArgs.weight = 1;
		titleArgs.gravity = Gravity.CENTER;
		mTitle.setGravity(Gravity.CENTER);
		mTitle.setSingleLine();
		mTitle.setText("H5");
		addView(mTitle, titleArgs);

		mRightButton = new Button(context);
		mRightButton.setBackground(null);
		LayoutParams rightArgs = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, DisplayUtils.dp2px(context, 50));
		rightArgs.gravity = Gravity.RIGHT|Gravity.CENTER_VERTICAL;
		rightArgs.setMargins(DisplayUtils.dp2px(context, 5), 0, DisplayUtils.dp2px(context, 5), 0);
		mRightButton.setText("");
		addView(mRightButton, rightArgs);

		setLeftOnClick(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (context instanceof Activity) {
					Log.d(TAG, "onClick: finished ..");
					((Activity) context).finish();
				}
			}
		});

		applyTheme(LightTheme);
	}

	public void setLeftText(String text) {
		mLeftButton.setText(text);
	}

	public void setRightText(String text) {
		mRightButton.setText(text);
	}

	public void setTitle(String title) {
		mTitle.setText(title);
	}

	public void setLeftOnClick(OnClickListener onClick) {
		mLeftButton.setOnClickListener(onClick);
	}

	public void setRightOnClick(OnClickListener onClick) {
		mRightButton.setOnClickListener(onClick);
	}

	public void setRightVisibility(boolean vis) {
		if (vis) {
			mRightButton.setVisibility(VISIBLE);
		} else {
			mRightButton.setVisibility(INVISIBLE);
		}
	}

	public void applyTheme(DemoToolbarTheme theme) {
		if (theme != null) {
			this.setBackgroundColor(theme.backgroundColor);
			this.mLeftButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, theme.buttonTextSize);
			this.mLeftButton.setTextColor(getResources().getColorStateList(theme.buttonTextDrawableId));
			this.mLeftButton.setBackground(getResources().getDrawable(theme.buttonBackgroundDrawableId));
			this.mRightButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, theme.buttonTextSize);
			this.mRightButton.setTextColor(getResources().getColorStateList(theme.buttonTextDrawableId));
			this.mRightButton.setBackground(getResources().getDrawable(theme.buttonBackgroundDrawableId));
			this.mTitle.setTextColor(theme.titleTextColor);
			this.mTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, theme.titleTextSize);
		}
	}

}
