package com.ilifesmart;

import com.ilifesmart.persistent.PersistentMgr;

public class ConfigUtils {
	public static String EXTRA_TEXT_NONE = "file:///android_asset/web/index.html";
	private static final int VER_CODE_EXTRA_TEXT_HOME_PAGE = 0;
	private static final int VER_CODE_CONFIG_FILE_URL = 0;
	private static String config_file_url_test = "http://140.143.243.75:8090/config/androiddemo_web_config.txt";
	public static String download_apk_url = "http://140.143.243.75:8090/AndroidDemo/AndroidDemo.apk";
	public static final String lastest_version_url = "http://140.143.243.75:8090/config/lastest_version.txt";

	public static final String EXTRA_TEXT_HOME_PAGE = "HOME_PAGE";
	public static final String EXTRA_TEXT_HOME_PAGE_VER_CODE = "HOME_PAGE_VER_CODE";
	public static final String EXTRA_TEXT_CONFIG_FILE_URL = "CONFIG_FILE_URL";
	public static final String EXTRA_TEXT_CONFIG_FILE_URL_VER_CODE = "CONFIG_FILE_URL_VER_CODE";
	public static final String EXTRA_TEXT_DOWNLOAD_APK_URL = "DOWNLOAD_APK_URL";
	public static final String EXTRA_TEXT_LASTEST_VERSION_URL = "LASTEST_VERSION_APK_URL";

	public static final String EXTRA_JSONKEY_CONFIGGILE = "config_file_url";
	public static final String EXTRA_JSONKEY_HOMEPAGE = "home_page";

// 云端文件配置文件内容. 文件名:androiddemo_web_config.txt, 格式:json
// url:地址；code:当前云端版本(建议递增);
//	{
//		"config_file_url": {
//		"url": "http://140.143.243.75:8090/config/app_web_config.json",
//						"code": 1
//	},
//		"home_page": {
//		"url": "http://140.143.243.75:8090/",
//						"code": 1
//	}
//	}

	/*
	* 读取当前HomePage的url
	* 默认为null
	* */
	public static String getCurrentHomePage() {
		return PersistentMgr.readKV(EXTRA_TEXT_HOME_PAGE, EXTRA_TEXT_NONE);
	}

	/*
	 * 读取当前WebView的配置文件的url
	 * 默认为测试url.
	 * */
	public static String getConfigFileUrl() {
		return PersistentMgr.readKV(EXTRA_TEXT_CONFIG_FILE_URL, config_file_url_test);
	}

	/*
	 * 本地存储当前的HomePage的url
	 *
	 * */
	public static void putCurrentHomePage(String home_page_url) {
		PersistentMgr.putKV(EXTRA_TEXT_HOME_PAGE, home_page_url);
	}

	/*
	 * 本地存储当前的WebView配置文件的url
	 *
	 * */
	public static void putConfigFileUrl(String config_file_url) {
		PersistentMgr.putKV(EXTRA_TEXT_CONFIG_FILE_URL, config_file_url);
	}

	/*
	 * 读取本地存储HomePage的版本
	 * 默认为0
	 * */
	public static int getCurrentHomePageVersionCode() {
		return PersistentMgr.readKV(EXTRA_TEXT_HOME_PAGE_VER_CODE, VER_CODE_EXTRA_TEXT_HOME_PAGE);
	}

	/*
	 * 本地存储当前的WebView配置文件的版本
	 * 默认为0
	 * */
	public static int getConfigFileUrlVersionCode() {
		return PersistentMgr.readKV(EXTRA_TEXT_CONFIG_FILE_URL_VER_CODE, VER_CODE_CONFIG_FILE_URL);
	}

	/*
	 * 读取本地存储HomePage的版本
	 *
	 * */
	public static void putCurrentHomePageVersionCode(int home_page_ver_code) {
		PersistentMgr.putKV(EXTRA_TEXT_HOME_PAGE_VER_CODE, home_page_ver_code);
	}

	/*
	 * 存储当前的WebView配置文件的版本
	 *
	 * */
	public static void putConfigFileUrlVersionCode(int config_url_ver_code) {
		PersistentMgr.putKV(EXTRA_TEXT_CONFIG_FILE_URL_VER_CODE, config_url_ver_code);
	}

}
