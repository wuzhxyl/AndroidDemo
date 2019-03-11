package com.ilifesmart.ui;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ilifesmart.App;

/**
 * 一个全局的 Toast 对象，这样可以避免连续显示 Toast 时不能取消上一次 Toast 消息的情况
 */
public class ToastUtils {
    private static Toast toast;
    private static final String TAG = "ToastUtils";

    //默认样式
    public static void show(Context context, String msg) {
        show(context, msg, Toast.LENGTH_SHORT);
    }

    public static void show(Context context, int stringResId) {
        ToastUtils.show(context, App.getContext().getResources().getString(stringResId));
    }

    public static void show(Context context, final String msg, int duration) {
        if (TextUtils.isEmpty(msg)) {
            Log.w(TAG, "empty toast content");
            return;
        }
        if (toast != null) {
            toast.cancel();
        }

        Log.d(TAG, "show: ---------------> ");
        toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
    }
}
