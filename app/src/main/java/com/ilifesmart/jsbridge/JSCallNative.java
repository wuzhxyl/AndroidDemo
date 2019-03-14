package com.ilifesmart.jsbridge;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebView;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;

/**
 * Created by fanxq on 2017/10/26.
 */

public class JSCallNative {
    public static final String TAG = "JSCallNative";

    private static final String JS_BRIDGE_PROTOCOL_SCHEMA = JSBridgeUtil.getJSBridgeProtocolSchema();
    private static boolean CallOnMainThread = true;
    private String mClassName;
    private String mMethodName;
    private String mPort;
    private JSONObject mParams;

    private JSCallNative() {
    }

    public static JSCallNative newInstance() {
        return new JSCallNative();
    }

    /**
     * @param webView WebView
     * @param message hybrid://class:port/method?params
     */
    public void call(WebView webView, String message) {
        if (webView == null || TextUtils.isEmpty(message))
            return;
        parseMessage(message);
        invokeNativeMethod(webView);
    }

    private void parseMessage(String message) {
        if (!message.startsWith(JS_BRIDGE_PROTOCOL_SCHEMA))
            return;
        Uri uri = Uri.parse(message);
        mClassName = uri.getHost();
        String path = uri.getPath();
        if (!TextUtils.isEmpty(path)) {
            mMethodName = path.replace("/", "");
        } else {
            mMethodName = "";
        }
        mPort = String.valueOf(uri.getPort());
        try {
            mParams = new JSONObject(uri.getQuery());
        } catch (JSONException e) {
            e.printStackTrace();
            mParams = new JSONObject();
        }

        Log.d(TAG, "parseMessage: mClassName " + mClassName);
        Log.d(TAG, "parseMessage: mMethodName " + mMethodName);
        Log.d(TAG, "parseMessage: mParams " + mParams);
        Log.d(TAG, "parseMessage: mPort " + mPort);
    }

    private void invokeNativeMethod(WebView webView) {
        final Method method = JSBridge.getInstance().findMethod(mClassName, mMethodName);
        final JSCallNativeCallback jsCallback = JSCallNativeCallback.newInstance(webView, mPort);
        if (method == null) {
            String resultMessage = "Method (" + mMethodName + ") in this class (" + mClassName + ") not found!";
            jsCallback.onFinish(constructRespJsonObject(-1, resultMessage, null));
            return;
        }

        final Object[] objects = new Object[3];
        objects[0] = new LSNContext();
        objects[1] = mParams;
        objects[2] = jsCallback;

        if (CallOnMainThread) {
            try {
                method.invoke(null, objects);
            } catch (Exception e) {
                e.printStackTrace();

                String resultMessage = "Method (" + mMethodName + ") in this class (" + mClassName + ") cause exception!";
                jsCallback.onFinish(constructRespJsonObject(-1, resultMessage, null));
            }
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        method.invoke(null, objects);
                    } catch (Exception e) {
                        e.printStackTrace();
                        String resultMessage = "Method (" + mMethodName + ") in this class (" + mClassName + ") cause exception!";
                        jsCallback.onFinish(constructRespJsonObject(-1, resultMessage, null));
                    }
                }
            }).start();
        }
    }


    private JSONObject constructRespJsonObject(int resultCode, String resultMessage, JSONObject resultData) {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("code", resultCode);
            if (!TextUtils.isEmpty(resultMessage)) {
                jsonObj.put("message", resultMessage);
            } else {
                jsonObj.put("message", "");
            }
            if (resultData != null) {
                jsonObj.put("data", resultData);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObj;
    }
}
