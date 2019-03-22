package com.ilifesmart.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.ess.filepicker.FilePicker;
import com.ess.filepicker.model.EssFile;
import com.ess.filepicker.util.Const;
import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.github.lzyzsd.jsbridge.DefaultHandler;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.ilifesmart.App;
import com.ilifesmart.Config;
import com.ilifesmart.ConfigUtils;
import com.ilifesmart.androiddemo.MainActivity;
import com.ilifesmart.androiddemo.R;
import com.ilifesmart.broadcast.MessageReceiver;
import com.ilifesmart.broadcast.NetworkChangedReceiver;
import com.ilifesmart.interfaces.ILocationChanged;
import com.ilifesmart.interfaces.INetworkAccessableCB;
import com.ilifesmart.ui.DemoToolBar;
import com.ilifesmart.util.DisplayUtils;
import com.ilifesmart.util.NetworkUtils;
import com.ilifesmart.util.Utils;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class H5Activity extends BaseActivity {

	@BindView(R.id.H5_webview)
	BridgeWebView mWeb;
	@BindView(R.id.toolbar)
	DemoToolBar mToolbar;
	@BindView(R.id.progress)
	ProgressBar mProgress;

	private String sendmsg_phone = "";
	private String sendmsg_message = "";
	private HashMap<String, CallBackFunction> cbMaps = new HashMap<>();
	public static final String JSBRIDGE_METHOD_SCANCODE = "scanQrcode";

	public static final int REQUEST_CODE_SCAN_CODE = 1986;
	public static final int REQUEST_CODE_SEARCH_FILES = 1987;
    private ValueCallback<Uri[]> mValueCallback;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_h5);
		ButterKnife.bind(this);

		initData();
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
		mToolbar.setLeftText("");
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

		mWeb.setDefaultHandler(new DefaultHandler());
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

            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                mValueCallback = filePathCallback;

                FilePicker
                        .from(H5Activity.this)
                        .chooseMedia()
                        .enabledCapture(true)
                        .setTheme(R.style.FilePicker_Dracula)
                        .isSingle()
                        .onlyShowImages()
                        .requestCode(REQUEST_CODE_SEARCH_FILES)
                        .start();

                return true;
            }
        });

		mWeb.registerHandler("sendMessage", new BridgeHandler() {
			@Override
			public void handler(String data, CallBackFunction function) {
				try {
					JSONObject object = new JSONObject(data);
					sendmsg_phone = object.getString("mobile");
					sendmsg_message = object.getString("message");
					Utils.setPermissionsSendMessage(H5Activity.this, sendmsg_phone, sendmsg_message);
				} catch (Exception ex) {
					ex.printStackTrace();
				}

				function.onCallBack("Message");
			}
		});
		mWeb.registerHandler("dialMobile", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                Log.d("Handler", "handler: data " + data);
                try {
                    JSONObject object = new JSONObject(data);
                    Utils.dialMobile(H5Activity.this, object.getString("mobile"));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
				function.onCallBack("Phone");
            }
        });
		mWeb.registerHandler(JSBRIDGE_METHOD_SCANCODE, new BridgeHandler() {
			@Override
			public void handler(String data, CallBackFunction function) {
				cbMaps.put("scanQrcode", function);
				new IntentIntegrator(H5Activity.this).setOrientationLocked(false).setRequestCode(REQUEST_CODE_SCAN_CODE).initiateScan();
			}
		});
        mWeb.registerHandler("getMobileInfo", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                String content = Utils.getDevInfo();
                function.onCallBack(content);
            }
        });
		mWeb.registerHandler("shareInApp", new BridgeHandler() {
			@Override
			public void handler(String data, CallBackFunction function) {
				try {
					JSONObject jsonObject = new JSONObject(data);
					String content = jsonObject.getString("content");
					Utils.onSendText(H5Activity.this, content);
				} catch (Exception ex) {
					ex.printStackTrace();
				}

				function.onCallBack("Share");
			}
		});
		mWeb.registerHandler("getClipboardInfo", new BridgeHandler() {
			@Override
			public void handler(String data, CallBackFunction function) {
				String content = Utils.getClipboardContent();
				function.onCallBack(content);
			}
		});
		mWeb.registerHandler("getLocationInfo", new BridgeHandler() {
			@Override
			public void handler(String data, CallBackFunction function) {
				cbMaps.put("getLocationInfo", function);
				if (Utils.checkPermissionGranted(Utils.PERMISSIONS_ACCESS_FINE_LOCATION)) {
					startLocation();
				} else {
					Utils.requestPermissions(H5Activity.this, Utils.PERMISSIONS_ACCESS_FINE_LOCATION, true, Utils.PERMISSION_CODE_ACCESS_FINE_LOCATION);
				}
			}
		});

		loadWeb();
	}

	private void startLocation() {
		App.startLocation(new ILocationChanged() {
			@Override
			public void onLocationChanged(double latitude, double longitude) {
			    CallBackFunction function = cbMaps.get("getLocationInfo");
				if (function != null) {
				    function.onCallBack("Lat:" + latitude + ",Lon:" + longitude);
                }
			}

			@Override
			public void onLocationError(int errCode, String errInfo) {
                CallBackFunction function = cbMaps.get("getLocationInfo");
                if (function != null) {
                    function.onCallBack("errCode " + errCode + ";errInfo " + errInfo);
                }
			}
		});
	}

	private void getNetworkInfo(boolean isOnline) {
		StringBuilder builder = new StringBuilder();
		builder.append("网络状态: ").append(isOnline ? "可用" : "不可用").append("; ")
				.append("网络类型: ").append(NetworkUtils.getNetworkType(H5Activity.this)).append("; ");

		if (NetworkUtils.isWifiConnected(H5Activity.this)) {
			builder.append("网络名称: ").append(NetworkUtils.getNetworkName(H5Activity.this));
		}
		String text = builder.toString();

		// 当前网络信息，需要UI进行状态显示，不做为JS调用.
		Log.d("NetworkInfo", "getNetworkInfo: 网络信息 " + text);
	}

	private MessageReceiver mMessageReceiver;
	private NetworkChangedReceiver mNetworkChangedReceiver;
	private void initData() {
		mMessageReceiver = new MessageReceiver();
		registerReceiver(mMessageReceiver, new IntentFilter(MessageReceiver.SMS_ACTION));

		mNetworkChangedReceiver = new NetworkChangedReceiver(new INetworkAccessableCB() {
			@Override
			public void isNetWorkOnline(boolean isOnline) {
                Log.d("isOnline", "isNetWorkOnline: isOnline " + isOnline);
				getNetworkInfo(isOnline);
			}
		});
		registerReceiver(mNetworkChangedReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mMessageReceiver);
		unregisterReceiver(mNetworkChangedReceiver);
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

        if (Utils.checkPermissionGranted(new String[]{Utils.PERMISSIONS_ACCESS_FINE_LOCATION})) {
            startLocation();
        } else {
            Utils.requestPermissions(this, new String[]{Utils.PERMISSIONS_ACCESS_FINE_LOCATION}, true, Utils.PERMISSION_CODE_ACCESS_FINE_LOCATION);
        }

		new DownloadTask().execute(); //自动更新
	}

	@Override
	protected void onPause() {
		super.onPause();
		mWeb.onPause();
	}

	private final String MENU_ITEM_RELOAD = "重新加载";
	private final String MENU_ITEM_UPGRADE = "在线升级";
	private final String MENU_ITEM_SETTINGS = "其它";
	private void onMore(View anchor) {
		String[] texts = new String[] {MENU_ITEM_RELOAD, MENU_ITEM_UPGRADE, MENU_ITEM_SETTINGS};
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
					case MENU_ITEM_SETTINGS:
                        popupWindow.dismiss();
						Utils.startActivity(H5Activity.this, MainActivity.class);
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

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);

		if (requestCode == Utils.PERMISSION_CODE_SEND_MESSAGE || requestCode == Utils.PERMISSION_CODE_ACCESS_FINE_LOCATION) {
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
				if (requestCode == Utils.PERMISSION_CODE_SEND_MESSAGE) {
					Utils.setPermissionsSendMessage(this, sendmsg_phone, sendmsg_message);
				} else if (requestCode == Utils.PERMISSION_CODE_ACCESS_FINE_LOCATION) {
					startLocation();
				}
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(resultCode, data);
		super.onActivityResult(requestCode, resultCode, data);
		if (result != null ) {
			if (requestCode == REQUEST_CODE_SCAN_CODE) {
				String content = result.getContents(); // 扫码返回内容.\

				CallBackFunction func = cbMaps.get(JSBRIDGE_METHOD_SCANCODE);
				if (func != null) {
				    func.onCallBack(content);
                }
			} else if (requestCode == Utils.PERMISSION_CODE_ACCESS_FINE_LOCATION) {
                if (cbMaps.get("getLocationInfo") != null) {
                    startLocation();
                }
            } else if (requestCode == REQUEST_CODE_SEARCH_FILES) {
                List<EssFile> lists = data.getParcelableArrayListExtra(Const.EXTRA_RESULT_SELECTION);
                EssFile file = lists.get(0);
                String path = file.getAbsolutePath();
                Uri tmpUri = Uri.fromFile(new File(path));
			    if (mValueCallback != null) {
			        mValueCallback.onReceiveValue(new Uri[] {tmpUri});
			        mValueCallback = null;



                }
            }
		}
	}

}
