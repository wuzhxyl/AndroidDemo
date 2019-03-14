package com.ilifesmart.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.ilifesmart.Config;
import com.ilifesmart.ConfigUtils;
import com.ilifesmart.androiddemo.R;
import com.ilifesmart.jsbridge.JSBridgeWebChromeClient;
import com.ilifesmart.ui.DemoToolBar;
import com.ilifesmart.util.DisplayUtils;

import org.json.JSONObject;

import java.util.Map;
import java.util.zip.Inflater;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class H5Activity extends AppCompatActivity {

	@BindView(R.id.H5_webview)
	WebView mWeb;
	@BindView(R.id.toolbar)
	DemoToolBar mToolbar;
	@BindView(R.id.progress)
	ProgressBar mProgress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_h5);
		ButterKnife.bind(this);

		initialize();
	}

	private void initialize() {
		mToolbar.setLeftOnClick(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

		mToolbar.setRightText("更多");
		mToolbar.setRightOnClick(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onMore(v);
			}
		});

		WebSettings webSettings = mWeb.getSettings();
		webSettings.setJavaScriptEnabled(true);    // 支持JS
		webSettings.setAppCacheEnabled(false);     // 不缓存
		webSettings.setUseWideViewPort(true);      // 自适应手机屏幕
		webSettings.setBuiltInZoomControls(true);  // 缩放
		webSettings.setSupportZoom(true);          // 支持缩放
		webSettings.setLoadWithOverviewMode(true); // 当页面宽度大于WebView宽度时，缩小使页面宽度等于WebView宽度
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			// 用户是否需要通过手势播放媒体(不会自动播放)，默认值 true
			webSettings.setMediaPlaybackRequiresUserGesture(true);
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			// 5.0以上允许加载http和https混合的页面(5.0以下默认允许，5.0+默认禁止)
			webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			// 是否在离开屏幕时光栅化(会增加内存消耗)，默认值 false
			webSettings.setOffscreenPreRaster(false);
		}

		mWeb.setWebViewClient(new WebViewClient() {
			@Override
			// @return true to cancel the current load, otherwise return false.
			public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
				String url = request.getUrl().toString();
				if (url == null) return true;

				Uri uri = Uri.parse(url);
				String scheme = uri.getScheme();

				if (!TextUtils.isEmpty(scheme) && scheme.contains("http")) {
					return false;
				} else if (!TextUtils.isEmpty(scheme)) {
					Intent i = new Intent(Intent.ACTION_VIEW, uri);
					if (i.resolveActivity(getPackageManager()) != null) {
						startActivity(i);
					}
				}
				return true;
			}

		});

		mWeb.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				super.onProgressChanged(view, newProgress);
				if (newProgress != 100) {
					mProgress.setVisibility(View.VISIBLE);
					mProgress.setProgress(newProgress);
				} else {
					mProgress.setVisibility(View.INVISIBLE);
				}
			}

			@Override
			public void onReceivedTitle(WebView view, String title) {
				super.onReceivedTitle(view, title);
				mToolbar.setTitle(title);
			}
		});

		loadWeb();
	}

	@Override
	public void onBackPressed() {
		if (mWeb.canGoBack()) {
			mWeb.goBack();
			return;
		}
		super.onBackPressed();
	}

	private void loadWeb() {
		String url = ConfigUtils.getCurrentHomePage();
		if (!TextUtils.isEmpty(url)) {
			mWeb.loadUrl(url);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		mWeb.onResume();
		new DownloadTask().execute();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mWeb.onPause();
	}


	private final String MENU_ITEM_RELOAD = "重新加载";
	private final String MENU_ITEM_UPGRADE = "在线升级";
	private void onMore(View anchor) {
		String[] texts = new String[] {MENU_ITEM_RELOAD, MENU_ITEM_UPGRADE};
		View v = LayoutInflater.from(this).inflate(R.layout.webview_popup_window, null);
		ListView listView = v.findViewById(R.id.popup_listview);
		listView.setAdapter(new BaseAdapter() {
			@Override
			public int getCount() {
				return texts.length;
			}

			@Override
			public Object getItem(int position) {
				return texts[position];
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				if (convertView == null) {
					convertView = LayoutInflater.from(H5Activity.this).inflate(R.layout.popupwind_listview_item, parent, false); //加载布局
				}

				((TextView)convertView.findViewById(R.id.text)).setText(texts[position]);

				return convertView;
			}
		});
		PopupWindow popupWindow = new PopupWindow(v, DisplayUtils.dp2px(this, 150), ViewGroup.LayoutParams.WRAP_CONTENT);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String chooser = texts[position];

				switch (chooser) {
					case MENU_ITEM_RELOAD:
						popupWindow.dismiss();
						loadWeb();
						break;
					case MENU_ITEM_UPGRADE:
						popupWindow.dismiss();
						new DownloadTask().execute();
						break;
				}
			}
		});
		popupWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#F8F8F8")));
		popupWindow.setTouchable(true);
		popupWindow.setOutsideTouchable(true);
		popupWindow.update();
		popupWindow.showAsDropDown(anchor, 10, 10);
	}

	private class DownloadTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... voids) {
			String url = ConfigUtils.getConfigFileUrl();
			OkHttpClient client = new OkHttpClient();
			Request request = new Request.Builder()
							.url(url)
							.build();
			Call call = client.newCall(request);
			try {
				Response response = call.execute();
				return response.body().string();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String json) {
			super.onPostExecute(json);

			int currHomePageVerCode = ConfigUtils.getCurrentHomePageVersionCode();
			int currConfigFileVerCode = ConfigUtils.getConfigFileUrlVersionCode();
			boolean isNew = false;
			if (TextUtils.isEmpty(json)) {
				Toast.makeText(H5Activity.this, "无效的数据", Toast.LENGTH_SHORT).show();
			} else {
				try {
					JSONObject jsonObject = new JSONObject(json);
					Config.WebDemoInfo home = JSON.parseObject(jsonObject.getJSONObject(ConfigUtils.EXTRA_JSONKEY_HOMEPAGE).toString(), Config.WebDemoInfo.class);
					Config.WebDemoInfo config = JSON.parseObject(jsonObject.getJSONObject(ConfigUtils.EXTRA_JSONKEY_CONFIGGILE).toString(), Config.WebDemoInfo.class);

					if (home.code > currHomePageVerCode) {
						isNew = true;
						ConfigUtils.putCurrentHomePage(home.url);
						ConfigUtils.putCurrentHomePageVersionCode(home.code);
					}

					if (config.code > currConfigFileVerCode) {
						isNew = true;
						ConfigUtils.putConfigFileUrl(config.url);
						ConfigUtils.putConfigFileUrlVersionCode(config.code);
					}
 				} catch (Exception ex) {
					ex.printStackTrace();
				}

				String text = isNew ? "已更新" : "当前已是最新";
				if (isNew) {
					loadWeb();
				}
				Toast.makeText(H5Activity.this, text, Toast.LENGTH_LONG).show();
			}
		}
	}

}
