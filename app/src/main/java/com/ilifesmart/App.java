package com.ilifesmart;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.ilifesmart.interfaces.ILocationChanged;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;

public class App extends Application implements Application.ActivityLifecycleCallbacks {
	private static Context sContext;
	private static Handler sHandler; // 全局性的Handler
	private static boolean isTestVer = true;
	private static AMapLocationClient mLocationClient;
	private static AMapLocationClientOption mLocationClientOption;

	private static double mLatitude = 0;
	private static double mLongitude = 0;

	private AMapLocationListener mLocationListener = new AMapLocationListener() {
		@Override
		public void onLocationChanged(AMapLocation aMapLocation) {
			if (aMapLocation != null) {
				if (aMapLocation.getErrorCode() == 0) {
					mLatitude = aMapLocation.getLatitude();
					mLongitude = aMapLocation.getLongitude();
					if (sLocationCB != null) {
						sLocationCB.onLocationChanged(mLatitude, mLongitude);
					}
				} else {
					sLocationCB.onLocationError(aMapLocation.getErrorCode(), aMapLocation.getErrorInfo());
				}
			}
		}
	};

	public static double getLatitude() {
		return mLatitude;
	}

	public static double getLongitude() {
		return mLongitude;
	}

	public static String getLocation() {
		return mLongitude+","+mLatitude;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		sContext = getApplicationContext();
		sHandler = new Handler(Looper.getMainLooper());
		mLocationClient = new AMapLocationClient(sContext);
		mLocationClientOption = new AMapLocationClientOption();
		mLocationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
		mLocationClientOption.setOnceLocationLatest(true);
		mLocationClientOption.setInterval(1000);
		mLocationClientOption.setNeedAddress(false);
		mLocationClient.setLocationListener(mLocationListener);
		mLocationClient.setLocationOption(mLocationClientOption);

		UMConfigure.init(sContext, UMConfigure.DEVICE_TYPE_PHONE, null);

		registerActivityLifecycleCallbacks(this);
	}

	private static ILocationChanged sLocationCB;
	public static void startLocation(ILocationChanged locationChangeCB) {
		sLocationCB = locationChangeCB;
		mLocationClient.startLocation();
	}

	public static void stopLocation() {
		mLocationClient.stopLocation();
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

	@Override
	public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

	}

	@Override
	public void onActivityStarted(Activity activity) {

	}

	@Override
	public void onActivityResumed(Activity activity) {
		MobclickAgent.onResume(activity);
	}

	@Override
	public void onActivityPaused(Activity activity) {
		MobclickAgent.onPause(activity);
	}

	@Override
	public void onActivityStopped(Activity activity) {

	}

	@Override
	public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

	}

	@Override
	public void onActivityDestroyed(Activity activity) {

	}
}
