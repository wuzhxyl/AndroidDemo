package com.ilifesmart.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ilifesmart.App;
import com.ilifesmart.activity.DevicesInfoActivity;
import com.ilifesmart.interfaces.INetworkAccessableCB;
import com.ilifesmart.util.NetworkUtils;

public class NetworkChangedReceiver extends BroadcastReceiver {

    public INetworkAccessableCB icb;

    public NetworkChangedReceiver(INetworkAccessableCB cb) {
        this.icb = cb;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkUtils.isNetworkConnected(context, icb); //new INetworkAccessableCB() {
            }
        }).start();
    }
}
