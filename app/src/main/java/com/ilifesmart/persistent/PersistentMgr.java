package com.ilifesmart.persistent;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.ilifesmart.App;

import java.util.ArrayList;

public class PersistentMgr {
	static private PersistentMgr s_inst;

	public PersistentMgr inst() {
		if (s_inst == null)
			s_inst = new PersistentMgr();
		return s_inst;
	}

	public static SharedPreferences getPrivateSharedPreferenceFile() {
		return App.getContext().getSharedPreferences("android_persist", Context.MODE_PRIVATE);
	}

	public static String readKV(String key) {
		SharedPreferences sharedPreferences = getPrivateSharedPreferenceFile();
		return sharedPreferences.getString(key, null);
	}

	public static String readKV(String key, String def) {
		SharedPreferences sharedPreferences = getPrivateSharedPreferenceFile();
		return sharedPreferences.getString(key, def);
	}

	public static boolean removeKV(String key) {
		SharedPreferences sharedPreferences = getPrivateSharedPreferenceFile();
		sharedPreferences.edit().remove(key);
		sharedPreferences.edit().commit();

		return true;
	}

	public static boolean putKV(String key, Object value) {
		try {
			SharedPreferences auroraconfig = getPrivateSharedPreferenceFile();
			SharedPreferences.Editor editor = auroraconfig.edit();
			editor.putString(key, (value != null) ? value.toString() : null);
			editor.apply();
		} catch (Exception exp) {
			exp.printStackTrace();
			return false;
		}

		return true;
	}
}
