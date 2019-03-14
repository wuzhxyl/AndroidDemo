package com.ilifesmart.jsbridge;

import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import org.json.JSONObject;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 *
 * LifeSmart JSBridge/WindVane使用规范
 * JS调用Native协定协议: schema://class:port/method?params
 * JS调用JSBridge Native协定协议: hybrid://class:port/method?params
 * JS调用WindVane Native协定协议: hybrid://class:port/method?encoded_jsonStr_params
    schema：协议名称, 为hybrid
    class：bridge module name，例如在Java中为类的名称
    port：端口号，标识一个请求的callback，回调会根据这个port找到相应callback
    method：方法名称，例如在Java中为类的方法名称
    params：调用参数，为JSON格式字符串

 * JS调用示例
 获取手机当前Wi-Fi信息：
    window.[JSBridge|WindVane].call('LSNStub', 'getSSID', {
    }, function (resultData) {
        if (resultData.code != 0 || !resultData.data || !resultData.data.ssid) {
            console.log('JSBridge Call Error:' + resultData.message || "NULL");
            that.onErrorMessage(resultData.message);
        } else {
            let wifiSsid = resultData.data.ssid;
            that.setState({errorMessage:null, wifiSsid: wifiSsid});
        }
    }, function(failureData) {
    });

 * Native回调方式(JSBridge): javascript:JSBridge.onComplete(port,respJsonStr);
 * Native回调方式(WindVane): javascript:WindVane.onSuccess(port,respJsonStr);
    port: 端口号，标识一个请求的callback
    respJsonStr: 返回的结果信息，为JSON格式字符串
    Java端调用示例：
        final String CALLBACK_JS_FORMAT = "javascript:JSBridge.onComplete(%s,%s);"; // JSBridge
        final String CALLBACK_JS_FORMAT = "javascript:WindVane.onSuccess(%s,%s);"; // WindVane
        final String callbackJs = String.format(Locale.getDefault(), CALLBACK_JS_FORMAT, mPort, resultObj.toString());
        if (AsyncTaskExecutor.isMainThread()) {
            webView.loadUrl(callbackJs);
        } else {
            AsyncTaskExecutor.runOnMainThread(new Runnable() {
                @Override
                public void run() {
                    webView.loadUrl(callbackJs);
                }
            });
        }

 * Native结果数据返回格式
    var resultData = {
        code: 0, // 0成功，其它错误码表示失败
        message: 'XXXX' // 失败时候的错误信息，成功可为空
        data: {} // 成功时候的返回数据（json object），无数据时可以为空
    };

 * Created by fanxq on 2017/10/26.
 */

public class JSBridge {

    private static JSBridge instance = new JSBridge();
    private ArrayMap<String, ArrayMap<String, Method>> mArrayMap = new ArrayMap<>();

    private JSBridge() {
    }

    public static JSBridge getInstance() {
        return instance;
    }


    public JSBridge registerClazz(String className, Class<?> clazz) {
        if (clazz == null)
            return this;
        if (className == null)
            className = clazz.getSimpleName();
        synchronized (mArrayMap) {
            putMethod(className, clazz);
        }
        return this;
    }

    public JSBridge unregisterClazz(String className, Class<?> clazz) {
        if (clazz == null)
            return this;
        if (className == null)
            className = clazz.getSimpleName();
        synchronized (mArrayMap) {
            mArrayMap.remove(className);
        }
        return this;
    }

    public Method findMethod(String className, String methodName) {
        if (TextUtils.isEmpty(className) || TextUtils.isEmpty(methodName))
            return null;
        synchronized (mArrayMap) {
            if (mArrayMap.containsKey(className)) {
                ArrayMap<String, Method> arrayMap = mArrayMap.get(className);
                if (arrayMap == null)
                    return null;
                if (arrayMap.containsKey(methodName)) {
                    return arrayMap.get(methodName);
                }
            }
        }

        return null;
    }

    private void putMethod(String className, Class<?> clazz) {
        if (clazz == null)
            return;
        ArrayMap<String, Method> arrayMap = new ArrayMap<>();
        Method method;
        Method[] methods = clazz.getDeclaredMethods();
        int length = methods.length;
        for (int i = 0; i < length; i++) {
            method = methods[i];
            int methodModifiers = method.getModifiers();
            if ((methodModifiers & Modifier.PUBLIC) != 0
                    && (methodModifiers & Modifier.STATIC) != 0
                    && method.getReturnType() == void.class) {
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes != null && parameterTypes.length == 3) {
                    if (LSNContext.class == parameterTypes[0] && JSONObject.class == parameterTypes[1] && LSNCallback.class == parameterTypes[2]) {
                        arrayMap.put(method.getName(), method);
                    }
                }
            }
        }
        mArrayMap.put(className, arrayMap);
    }
}
