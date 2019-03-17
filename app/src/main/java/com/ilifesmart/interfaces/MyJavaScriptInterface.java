package com.ilifesmart.interfaces;

import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;

public class MyJavaScriptInterface {
    private Context mContext;

    public MyJavaScriptInterface(Context context) {
        this.mContext = context;
    }

    @JavascriptInterface
    public void androidNativeMethod() {
        Log.d("TTTTT", "androidNativeMethod: Hello,JS!");
    }
}
