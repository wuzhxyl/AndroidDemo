package com.ilifesmart.model;

import android.app.Activity;
import android.content.Intent;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class CustomIntentInteegrator extends IntentIntegrator {

    private static int selfRequestCode = 0;
    public CustomIntentInteegrator(Activity activity) {
        super(activity);
    }

    public void startActivityForResult(int requestCode) {
        selfRequestCode = requestCode;
        startActivityForResult(createScanIntent(), requestCode);
    }

    public static IntentResult parseActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQUEST_CODE || requestCode == selfRequestCode) {
            return parseActivityResult(resultCode, intent);
        }
        return null;
    }


}
