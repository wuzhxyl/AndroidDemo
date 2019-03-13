package com.ilifesmart.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.ilifesmart.interfaces.INetworkAccessableCB;

import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.CountDownLatch;

import static android.content.Context.WIFI_SERVICE;

public class NetworkUtils {


	/**
	 * 判断WIFI网络是否可用
	 *
	 * @param context
	 * @return
	 */
	public static boolean isWifiConnected(Context context) {
		if (context != null) {
			ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = manager.getActiveNetworkInfo();
			if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI)
				return networkInfo.isAvailable();
		}
		return false;
	}

	// 判断外网是否可用
	public static boolean isNetworkOnline(Context context) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkCapabilities networkCapabilities = manager.getNetworkCapabilities(manager.getActiveNetwork());
			return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
		} else {
			return true;
		}
	}

	// 判断网络是否可用(低版本兼容)
	public static boolean isNetworkAccessibleByPing() {
		Runtime runtime = Runtime.getRuntime();
		try {
			Process ipProcess = runtime.exec("ping -c 2 8.8.8.8"); // 114.114.114.114 -c次数 -i间隔时间(s)
			return (ipProcess.waitFor() == 0); // 0 有效; 1 需wifi验证; 2 不可用;
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		return false;
	}

	private static class Network {
		public boolean isNetWorkAble;
	}

	/**
	 * 判断是否有网络连接
	 * @param context
	 */
	public synchronized static boolean isNetworkConnected(Context context) {
		final Network net = new Network();
		net.isNetWorkAble = false;
		if (context != null) {
			ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = manager.getActiveNetworkInfo();
			net.isNetWorkAble = (networkInfo != null);
			net.isNetWorkAble = net.isNetWorkAble && networkInfo.isConnected() && isNetworkOnline(context);
		}

		if (net.isNetWorkAble) {
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
				final CountDownLatch latch = new CountDownLatch(1);
				new Thread(new Runnable() {
					@Override
					public void run() {
						net.isNetWorkAble = isNetworkAccessibleByPing();
						try {
							latch.countDown();
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}).start();
				try {
					latch.await();
				} catch(Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		return net.isNetWorkAble;
	}

	/**
	 * 判断是否有网络连接
	 * @param context
	 */
	public synchronized static void isNetworkConnected(Context context, INetworkAccessableCB cb) {
		if (cb != null) {
			cb.isNetWorkOnline(isNetworkConnected(context));
		}
	}

	/**
	 * 获取当前的网络状态 ：没有网络-0：WIFI网络1：4G网络-4：3G网络-3：2G网络-2
	 * 自定义
	 *
	 * @param context
	 * @return
	 */
	public static int getAPNType(Context context) {
		int netType = 0;
		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = manager.getActiveNetworkInfo();
		if (networkInfo == null) {
			return netType;
		}
		int nType = networkInfo.getType();
		if (nType == ConnectivityManager.TYPE_WIFI) {
			//WIFI
			netType = 1;
		} else if (nType == ConnectivityManager.TYPE_MOBILE) {
			int nSubType = networkInfo.getSubtype();
			TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			//3G   联通的3G为UMTS或HSDPA 电信的3G为EVDO
			if (nSubType == TelephonyManager.NETWORK_TYPE_LTE
							&& !telephonyManager.isNetworkRoaming()) {
				netType = 4;
			} else if (nSubType == TelephonyManager.NETWORK_TYPE_UMTS
							|| nSubType == TelephonyManager.NETWORK_TYPE_HSDPA
							|| nSubType == TelephonyManager.NETWORK_TYPE_EVDO_0
							&& !telephonyManager.isNetworkRoaming()) {
				netType = 3;
				//2G 移动和联通的2G为GPRS或EGDE，电信的2G为CDMA
			} else if (nSubType == TelephonyManager.NETWORK_TYPE_GPRS
							|| nSubType == TelephonyManager.NETWORK_TYPE_EDGE
							|| nSubType == TelephonyManager.NETWORK_TYPE_CDMA
							&& !telephonyManager.isNetworkRoaming()) {
				netType = 2;
			} else {
				netType = 2;
			}
		}
		return netType;
	}

	public static String getNetworkType(Context context) {
		int netType = getAPNType(context);
		String result = "";
		switch (netType) {
			case 0:
				result = "无网络";
				break;
			case 1:
				result = "WIFI";
				break;
			case 2:
				result = "2G";
				break;
			case 3:
				result = "3G";
				break;
			case 4:
				result = "4G";
				break;
			default:
				result = "其它";
		}
		return result;
	}

		/*
		* 当前网络名称.
		* */
	public static String getNetworkName(Context context) {
		String result = getNetworkType(context);

		if (isWifiConnected(context)) {

			WifiManager wifiManager = (WifiManager)context.getSystemService(WIFI_SERVICE);
			WifiInfo wifiInfo = wifiManager.getConnectionInfo();

			result = wifiInfo.getSSID();
		}

		return result;
	}
}
