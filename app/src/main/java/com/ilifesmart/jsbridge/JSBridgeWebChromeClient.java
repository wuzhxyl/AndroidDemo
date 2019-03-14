package com.ilifesmart.jsbridge;

import android.util.Log;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

/**
 * Created by fanxq on 2017/10/26.
 */

public class JSBridgeWebChromeClient extends WebChromeClient {
    public static final String TAG = "JSBridgeWebChromeClient";

    @Override
    public final boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
        Log.d(TAG, "onJsPrompt: url " + url);
        Log.d(TAG, "onJsPrompt: message " + message);

        result.confirm();
        JSCallNative.newInstance().call(view, message);
        return true;
    }

    @Override
    public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
        return super.onJsConfirm(view, url, message, result);
    }

    @Override
    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
        return super.onJsAlert(view, url, message, result);
    }
}
