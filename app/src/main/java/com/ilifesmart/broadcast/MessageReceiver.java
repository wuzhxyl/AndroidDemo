package com.ilifesmart.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.ilifesmart.ui.ToastUtils;

import static android.app.Activity.RESULT_OK;

public class MessageReceiver extends BroadcastReceiver {
    public static final String SMS_ACTION = "com.android.TinySMS.RESULT";
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && intent.getAction().equals(SMS_ACTION)) {
            int resultCode = getResultCode();
            if (resultCode == RESULT_OK) {
                Toast.makeText(context, "短消息发送成功", Toast.LENGTH_SHORT).show();
            } else {
                ToastUtils.show(context, "短消息发送失败");
            }
        }
    }
}
