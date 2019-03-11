package com.ilifesmart;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

public class App extends Application {
	private static Context sContext;
	private static Handler sHandler; // 全局性的Handler
	private static boolean isTestVer = false;

	@Override
	public void onCreate() {
		super.onCreate();

		sContext = getApplicationContext();
		sHandler = new Handler(Looper.getMainLooper());
	}

	public static Context getContext() {
		return sContext;
	}

	public static void postRunnable(Runnable runnable) {
		sHandler.post(runnable);
	}

	public static boolean isTestVer() {
		return isTestVer;
	}
}
