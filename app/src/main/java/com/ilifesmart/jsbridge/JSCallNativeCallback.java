package com.ilifesmart.jsbridge;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.webkit.WebView;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.Locale;

/**
 * Created by fanxq on 2017/10/26.
 */

public class JSCallNativeCallback implements LSNCallback {
    private static final String CALLBACK_JS_FORMAT = JSBridgeUtil.getCallbackJsFormat();
    private WeakReference<WebView> mWebViewWeakRef;
    private String mPort;

    private JSCallNativeCallback(WebView webView, String port) {
        this.mWebViewWeakRef = new WeakReference<>(webView);
        this.mPort = port;
    }

    public static JSCallNativeCallback newInstance(WebView webView, String port) {
        return new JSCallNativeCallback(webView, port);
    }

    @Override
    public void onFinish(JSONObject respJsonObject) {
        final WebView webView = mWebViewWeakRef.get();
        if (webView == null) {
//            if (LSNStub.DEBUG)
//                Log.d(LSNStub.TAG, "The WebView related to the LSNCallback has been recycled!");
        }
        final String callbackJs = String.format(Locale.getDefault(), CALLBACK_JS_FORMAT, mPort, respJsonObject.toString());
        if (isMainThread()) {
            webView.loadUrl(callbackJs);
        } else {
            runOnMainThread(new Runnable() {
                @Override
                public void run() {
                    webView.loadUrl(callbackJs);
                }
            });
        }

    }

    public static void runOnMainThread(Runnable runnable) {
        if (runnable == null)
            return;
        new Handler(Looper.getMainLooper()).post(runnable);
    }

    public static boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

}
