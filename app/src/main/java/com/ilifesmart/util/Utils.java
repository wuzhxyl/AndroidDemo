package com.ilifesmart.util;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;

import com.ilifesmart.App;
import com.ilifesmart.activity.PhoneMessageActivity;
import com.ilifesmart.ui.ToastUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Utils {

	public static final String TAG = "Utils";
	public static final String PERMISSIONS_CALL_PHONE = Manifest.permission.CALL_PHONE;
	public static final String PERMISSIONS_SEND_MESSAGE = Manifest.permission.SEND_SMS;
	public static final String PERMISSIONS_READ_PHONE_STATE = Manifest.permission.READ_PHONE_STATE;
	public static final String PERMISSIONS_CAMERA = Manifest.permission.CAMERA;
	public static final String PERMISSIONS_WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
	public static final String PERMISSIONS_RECORD_AUDIO = Manifest.permission.RECORD_AUDIO;
	public static final String PERMISSIONS_ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
	public static final String PERMISSIONS_READ_CONTACTS = Manifest.permission.READ_CONTACTS;

	public static boolean isVersionAfterM() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
	}

	public static boolean checkPermissionGranted(String permission) {
		if (isVersionAfterM()) {
			return (App.getContext().checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
		}

		return true;
	}

	public static boolean checkPermissionGranted(String[] permissions) {
		boolean granted = true;
		if (isVersionAfterM()) {
			for (String permission:permissions) {
				granted &= checkPermissionGranted(permission);
			}
		}

		return granted;
	}

	public static final int PERMISSION_CODE_CALL_PHONE = 10086;
	public static final int PERMISSION_CODE_SEND_MESSAGE = 10087;
	public static final int PERMISSION_CODE_CAMERA = 10088;
	public static final int PERMISSION_CODE_RECORD_AUDIO = 10089;
	public static final int PERMISSION_CODE_ACCESS_FINE_LOCATION = 10090;
	public static final int PERMISSION_CODE_READ_CONTACTS = 10091;

	@TargetApi(23)
	public static void requestPermissions(Activity context, String permission, boolean firstRequest, int requestCode) {
		if (!checkPermissionGranted(permission)) {
			context.requestPermissions(new String[] {permission}, requestCode);
		}
	}

	@TargetApi(23)
	public static void requestPermissions(Activity context, String[] permissions, boolean firstRequest, int requestCode) {
		if (!checkPermissionGranted(permissions)) {
			context.requestPermissions(permissions, requestCode);
		}
	}

	/*
	* context 上下文
	* phone 目标手机号码
	* 此方法不需要获取拨打电话的权限
	* */
	public static void dialMobile(Context context, String phone) {
		Intent intent = new Intent(Intent.ACTION_DIAL);
		Uri data = Uri.parse("tel:" + phone);
		intent.setData(data);
		context.startActivity(intent);
	}

	/*
	 * context 上下文
	 * phone 目标手机号码
	 * 此方法需要获取拨打电话的权限，并且会直接拨号
	 * */
	public static void callPhone(Context context, String phone) {
//		Intent i = new Intent(Intent.ACTION_CALL);
//		i.setData(Uri.parse("tel:"+phone));
//		context.startActivity(i);
	}

	/*
	 * context 上下文
	 * targetClass 目标activity
	 *
	 * */

	public static void startActivity(Context context, Class targetClass) {
		context.startActivity(new Intent(context, targetClass));
	}

	/*
	 * context 		 上下文
	 * targetPhone 目标手机号
	 * message     短信内容
	 * */
	public static void sendMessage(Context context, String targetPhone, String message) {
		if (TextUtils.isEmpty(targetPhone) || TextUtils.isEmpty(message)) {
			ToastUtils.show(context, "无效的信息");
			return;
		}

		ArrayList<String> messages = SmsManager.getDefault().divideMessage(message);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, new Intent(PhoneMessageActivity.SMS_ACTION), PendingIntent.FLAG_ONE_SHOT);
		for (String text : messages) {
			SmsManager.getDefault().sendTextMessage(targetPhone, null, text, pendingIntent, null);
		}
	}


	/*
	* 创建图片路径。文件名格式为JPEG_20180921_143030_
	*
	* */
	private static String currentPhotoPath;
	public static File createImageFile() throws IOException {
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = "JPEG_" + timeStamp + "_";
		File storeDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

		File image = File.createTempFile(imageFileName, ".jpg", storeDir);
		currentPhotoPath = image.getAbsolutePath();
		Log.d(TAG, "createImageFile: currentPhotoPath " + currentPhotoPath);
		return image;
	}

	/*
	* context 上下文
	* 触发MEDIA扫描图库将已存的照片显示出来.
	* */
	public static void galleryUpdate(Context context) {
		Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		File f = new File(currentPhotoPath);
		Uri contentUri = Uri.fromFile(f);
		Log.d(TAG, "galleryUpdate: f.isEmpty " + (f != null));
		mediaScanIntent.setData(contentUri);
		context.sendBroadcast(mediaScanIntent);
	}

	public static String getCurrentPhotoPath() {
		return currentPhotoPath;
	}

	/**
	 * 将图片的旋转角度置为0  ，此方法可以解决某些机型拍照后图像，出现了旋转情况
	 *
	 * @Title: setPictureDegreeZero
	 * @param path
	 * @return void
	 * @date 2012-12-10 上午10:54:46
	 */
	public static void setPictureDegreeZero(String path) {
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			// 修正图片的旋转角度，设置其不旋转。这里也可以设置其旋转的角度，可以传值过去，
			// 例如旋转90度，传值ExifInterface.ORIENTATION_ROTATE_90，需要将这个值转换为String类型的
			exifInterface.setAttribute(ExifInterface.TAG_ORIENTATION, "no");
			exifInterface.saveAttributes();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static String getClipboardContent() {
		ClipboardManager clipboardManager = (ClipboardManager) App.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
		ClipData data = clipboardManager.getPrimaryClip();
		ClipData.Item item = data.getItemAt(0);
		return item.getText().toString();
	}

	public static void getDevInfo() {
		Log.d(TAG, "getDevInfo: ModelType " + Build.MODEL);
		Log.d(TAG, "getDevInfo: OSVer " + Build.VERSION.RELEASE);
	}
}
