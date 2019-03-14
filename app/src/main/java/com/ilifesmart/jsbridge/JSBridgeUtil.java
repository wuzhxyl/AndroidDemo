package com.ilifesmart.jsbridge;

/**
 * Created by fanxq on 2017/11/9.
 */

public class JSBridgeUtil {
    public static final int Mode_JSBridge = 1;
    public static final int Mode_WindVane = 2;
    public static final int Mode = Mode_WindVane;

    public static final String getJSBridgeProtocolSchema() {
        if (Mode == Mode_JSBridge) return "hybrid";
        else if (Mode == Mode_WindVane) return "hybrid";
        return "hybrid";
    }

    public static final String getCallbackJsFormat() {
        if (Mode == Mode_JSBridge) return "javascript:JSBridge.onComplete(%s,%s);";
        else if (Mode == Mode_WindVane) return "javascript:WindVane.onSuccess(%s,%s);";
        return "javascript:JSBridge.onComplete(%s,%s);";
    }
}
