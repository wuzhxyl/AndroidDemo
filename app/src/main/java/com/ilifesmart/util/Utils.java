package com.ilifesmart.util;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.ilifesmart.App;
import com.ilifesmart.activity.BaseActivity;
import com.ilifesmart.activity.PhoneMessageActivity;
import com.ilifesmart.model.OwnFileProvider;
import com.ilifesmart.ui.ToastUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

	// 获取操作系统版本号
	public static String getOsVersion() {
		return Build.VERSION.RELEASE;
	}

	// 获取设备型号
	public static String getMobileModel() {
		return Build.MODEL;
	}

	public static String getDevInfo() {
		return "Android版本: " + getOsVersion() + ";\n型号: " + getMobileModel();
	}

	private static Uri getFileUri(String apkPath) {
		File file = new File(apkPath);
		return Uri.fromFile(file);
	}

	public static void startDownload(Context context, String apkPath) {
		String url = "http://140.143.243.75:8090/AndroidDemo/AndroidDemo.apk"; // 下载地址
		DownloadManager downloadManager = (DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
		Uri uri = Uri.parse(url);
		DownloadManager.Request request = new DownloadManager.Request(uri);
		request.setVisibleInDownloadsUi(true);
		request.setTitle("应用更新");
		request.setDescription("测试更新apk");
		request.setMimeType("application/vnd.android.package-archive");
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
			request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
		}

		apkPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
		apkPath += File.separator + "AndroidDemo" + ".apk";
		Uri fileUri = getFileUri(apkPath);
		request.setDestinationUri(fileUri);
		downloadManager.enqueue(request);
	}

//	private static

	private static boolean isMatchWeixinAndWeibo(ResolveInfo info) {
		String packagename_weixin = "com.tencent.mm";
		String packagename_weixin_favorite = "com.tencent.mm.ui.tools.AddFavoriteUI";
		String packagename_weixin_friend = "com.tencent.mm.ui.tools.ShareImgUI";

		String packagename_weibo = "com.sina.weibo";
		String packagename_weibo_sendweibo = "com.sina.weibo.composerinde.ComposerDispatchActivity"; // 发布新微博
		String packagename_weibo_private = "com.sina.weibo.weiyou.share.WeiyouShareDispatcher";    // 私信


		boolean isMatch = false;
		if (info != null) {
			String packageName = info.activityInfo.packageName.toLowerCase();
			String className = info.activityInfo.name.toLowerCase();

			Log.d(TAG, "isMatchWeixinAndWeibo: packageName " + packageName);
			Log.d(TAG, "isMatchWeixinAndWeibo: classname " + className);
			isMatch = ((packageName.contains(packagename_weibo) && (className.contains(packagename_weibo_sendweibo) || className.contains(packagename_weibo_private))) || (packageName.contains(packagename_weixin) && (className.contains(packagename_weixin_favorite) || className.contains(packagename_weixin_friend))));
		}

		return isMatch;
	}

	public static void onSendText(Context context, String text) {
		Intent share = new Intent(Intent.ACTION_SEND);
		share.setType("*/*");
		share.putExtra(Intent.EXTRA_SUBJECT, ""); // 主题
		share.putExtra(Intent.EXTRA_TEXT, ""); // 正文字符串
		share.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		List<ResolveInfo> infos = context.getPackageManager().queryIntentActivities(share, 0);
		if (infos != null) {
			List<Intent> targetIntents = new ArrayList<>();
			PackageManager pm = context.getPackageManager();
			for(ResolveInfo info:infos) {

//				2019-03-13 18:42:00.253 6581-6581/? D/resolveInfos: packageName--->com.tencent.mm  name--->com.tencent.mm.ui.tools.ShareImgUI label--->微信 activity label--->微信 intent-filter label--->微信
//				2019-03-13 18:42:00.253 6581-6581/? D/resolveInfos: packageName--->com.tencent.mm  name--->com.tencent.mm.ui.tools.AddFavoriteUI label--->微信 activity label--->微信 intent-filter label--->微信
//				2019-03-13 18:42:00.253 6581-6581/? D/resolveInfos: packageName--->com.tencent.mm  name--->com.tencent.mm.ui.tools.ShareToTimeLineUI label--->微信 activity label--->微信 intent-filter label--->微信

//				2019-03-13 18:42:00.245 6581-6581/? D/resolveInfos: packageName--->com.sina.weibo  name--->com.sina.weibo.composerinde.ComposerDispatchActivity label--->微博 activity label--->发布新微博 intent-filter label--->发布新微博
//				2019-03-13 18:42:00.245 6581-6581/? D/resolveInfos: packageName--->com.sina.weibo  name--->com.sina.weibo.story.publisher.StoryDispatcher label--->微博 activity label--->微博 intent-filter label--->微博
//				2019-03-13 18:42:00.245 6581-6581/? D/resolveInfos: packageName--->com.sina.weibo  name--->com.sina.weibo.weiyou.share.WeiyouShareDispatcher label--->微博 activity label--->微博 intent-filter label--->微博

				Log.d("resolveInfos","packageName--->" + info.activityInfo.packageName + "  name--->" + info.activityInfo.name+" label--->"+info.activityInfo.applicationInfo.loadLabel(pm).toString()+" activity label--->"+info.activityInfo.loadLabel(pm).toString()+" intent-filter label--->"+info.activityInfo.loadLabel(pm).toString());
				if (!isMatchWeixinAndWeibo(info)) {
					continue;
				}
				Intent i = new Intent(Intent.ACTION_SEND);
				i.setType("text/plain");
				i.putExtra(Intent.EXTRA_SUBJECT, "分享"); // 主题
				i.putExtra(Intent.EXTRA_TEXT, text); // 正文字符串
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				i.setPackage(info.activityInfo.packageName);
				i.setClassName(info.activityInfo.packageName, info.activityInfo.name);
				Intent j = new LabeledIntent(i, info.activityInfo.packageName, info.loadLabel(context.getPackageManager()), info.icon);
				targetIntents.add(j);
			}

			Intent chooserIntent = Intent.createChooser(targetIntents.remove(0), "Select");
			if (chooserIntent == null) {
				return;
			}

			chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetIntents.toArray(new Parcelable[] {}));
			if (chooserIntent.resolveActivity(context.getPackageManager()) != null) {
				context.startActivity(chooserIntent);
			} else {
				Toast.makeText(context, "未找到微信/微博", Toast.LENGTH_SHORT).show();
			}
		}
	}
}


