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
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.ilifesmart.App;
import com.ilifesmart.activity.BaseActivity;
import com.ilifesmart.activity.PhoneMessageActivity;
import com.ilifesmart.activity.SnapQrcodeVoiceActivity;
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
    // 权限
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

    // 检查权限是否授予.
    public static boolean checkPermissionGranted(String permission) {
        if (isVersionAfterM()) {
            return (App.getContext().checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
        }

        return true;
    }

    // 检查权限是否授予.
    public static boolean checkPermissionGranted(String[] permissions) {
        boolean granted = true;
        if (isVersionAfterM()) {
            for (String permission : permissions) {
                granted &= checkPermissionGranted(permission);
            }
        }

        return granted;
    }

    public static final int PERMISSION_CODE_SEND_MESSAGE = 10087;
    public static final int PERMISSION_CODE_CAMERA = 10088;
    public static final int PERMISSION_CODE_RECORD_AUDIO = 10089;
    public static final int PERMISSION_CODE_ACCESS_FINE_LOCATION = 10090;
    public static final int PERMISSION_CODE_READ_CONTACTS = 10091;
    public static final int PERMISSIONS_CODE_WRITE_EXTERNAL_STORAGE = 10092;

    // 权限请求.
    @TargetApi(23)
    public static void requestPermissions(Activity context, String permission, boolean firstRequest, int requestCode) {
        if (!checkPermissionGranted(permission)) {
            context.requestPermissions(new String[]{permission}, requestCode);
        }
    }

    // 权限请求.
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
     * 此方法需要获取拨打电话的权限，并且会直接拨号(故忽略)
     * 需权限CALL_PHONE
     * */
    public static void callPhone(Context context, String phone) {
//		Intent i = new Intent(Intent.ACTION_CALL);
//		i.setData(Uri.parse("tel:"+phone));
//		context.startActivity(i);
    }

    /*
     * context 上下文
     * targetClass 目标activity
     * 进入class所代表的界面.
     * */
    public static void startActivity(Context context, Class targetClass) {
        context.startActivity(new Intent(context, targetClass));
    }

    /*
	 * context 	   上下文
	 * targetPhone 目标手机号
	 * message     短信内容
	 * @RequiresPermission(allOf = {
            android.Manifest.permission.READ_PHONE_STATE,
            android.Manifest.permission.SEND_SMS
    	})
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
     * currentPhotoPath: 新创建的图片存储路径，每次都会更新.
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
    * context 页面上下文
    * 获取图片显示并存储图片
    * 所需权限参见SnapQrcodeVoiceActivity.java
    * */
    public static void takeCameraSnap(BaseActivity context) {
        File image = null;
        try {
            image = Utils.createImageFile();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        if (image != null) {
            Uri photoUri = FileProvider.getUriForFile(context, "com.example.android.fileprovider", image);
            i.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        }
        if (i.resolveActivity(context.getPackageManager()) != null) {
            context.startActivityForResult(i, SnapQrcodeVoiceActivity.REQUEST_CODE_CAMERA);
        } else {
            Toast.makeText(context, "不支持", Toast.LENGTH_SHORT).show();
        }
    }

    /*
     * context 上下文
     * 目的:触发MediaScanner更新显示新增图片.
     * */
    public static void galleryUpdate(Context context) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }

    public static String getCurrentPhotoPath() {
        return currentPhotoPath;
    }

    /**
     * 将图片的旋转角度置为0  ，此方法可以解决某些机型拍照后图像，出现了旋转情况
     *
     * @param path
     * @return void
     * @Title: setPictureDegreeZero
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

    // 获取剪贴板内容.
    public static String getClipboardContent() {
        ClipboardManager clipboardManager = (ClipboardManager) App.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData data = clipboardManager.getPrimaryClip();
        String result = "无内容";
        if (data != null && data.getItemCount() > 0) {
            ClipData.Item item = data.getItemAt(0);
            result = item.getText().toString();
        }
        return result;
    }

    // 获取操作系统版本号
    public static String getOsVersion() {
        return Build.VERSION.RELEASE;
    }

    // 获取设备型号
    public static String getMobileModel() {
        return Build.MODEL;
    }

    // 测试打印输出.
    public static String getDevInfo() {
        return "Android版本: " + getOsVersion() + ";\n型号: " + getMobileModel();
    }

    // 获取文件Uri.
    private static Uri getFileUri(String apkPath) {
        File file = new File(apkPath);
        return Uri.fromFile(file);
    }

    /*
     * @cotext:  activity上下文.
     * @apkPath: 安装包路径. 位置是Download目录.
     * 下载后的包名:AndroidDemo.apk
     * */
    public static void startDownload(Context context, String apkPath) {
        String url = "http://140.143.243.75:8090/AndroidDemo/AndroidDemo.apk"; // 下载地址
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
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

    /*
     * 匹配微信及微博的分享目标组件
     *
     * */
    private static boolean isMatchWeixinAndWeibo(ResolveInfo info) {
        String packagename_weixin = "com.tencent.mm".toLowerCase(); // 微信包名
        String packagename_weixin_favorite = "com.tencent.mm.ui.tools.AddFavoriteUI".toLowerCase(); //微信收藏
        String packagename_weixin_friend = "com.tencent.mm.ui.tools.ShareImgUI".toLowerCase(); // 发送给朋友
        // 微信朋友圈高版本已禁止仅发送文案到朋友圈，此处屏蔽朋友圈!

        String packagename_weibo = "com.sina.weibo".toLowerCase(); // 微博包名
        String packagename_weibo_sendweibo = "com.sina.weibo.composerinde.ComposerDispatchActivity".toLowerCase(); // 发布新微博
        String packagename_weibo_private = "com.sina.weibo.weiyou.share.WeiyouShareDispatcher".toLowerCase();    // 私信

        boolean isMatch = false;
        if (info != null) {
            String packageName = info.activityInfo.packageName.toLowerCase();
            String className = info.activityInfo.name.toLowerCase();

            isMatch = ((packageName.contains(packagename_weibo) && (className.contains(packagename_weibo_sendweibo) || className.contains(packagename_weibo_private))) || (packageName.contains(packagename_weixin) && (className.contains(packagename_weixin_favorite) || className.contains(packagename_weixin_friend))));
        }

        return isMatch;
    }

    /*
     *
     * text:待发送的内容.
     * */
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
            for (ResolveInfo info : infos) {
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
                Intent j = new LabeledIntent(i, info.activityInfo.packageName, info.loadLabel(pm), info.icon); // 正确显示分享组件名称.
                targetIntents.add(j);
            }

            if (targetIntents.size() == 0) {
                Toast.makeText(context, "未找到微信/微博", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent chooserIntent = Intent.createChooser(targetIntents.remove(0), "Select");
            if (chooserIntent == null) {
                Toast.makeText(context, "未找到微信/微博", Toast.LENGTH_SHORT).show();
                return;
            }

            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetIntents.toArray(new Parcelable[]{}));
            try {
                context.startActivity(chooserIntent);
            } catch (Exception ex) {
                Toast.makeText(context, "未找到微信/微博", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "未找到微信/微博", Toast.LENGTH_SHORT).show();
        }
    }


    private static MediaRecorder mRecorder;
    private static File currentFile; // 目标文件.

    /*
     * 开始录音.
     *
     * */
    public static void startRecord() {
        if (mRecorder == null) {
            File dir = new File(Environment.getExternalStorageDirectory(), "sounds");
            if (!dir.exists()) {
                try {
                    dir.mkdirs();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            currentFile = new File(dir, System.currentTimeMillis() + ".amr");
            if (!currentFile.exists()) {
                try {
                    currentFile.createNewFile();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_WB); // 50－7000Hz，16KHz采样
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);
            mRecorder.setOutputFile(currentFile.getAbsolutePath());

            try {
                mRecorder.prepare();
                mRecorder.start();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /*
     * 停止录音.
     *
     * */
    public static void stopRecord() {
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
    }


    /*
     * 当前音频文件路径.
     *
     * */
    public static File getCurrentFile() {
        return currentFile;
    }

    public static final int EXTRA_MIME_NONE = 0;
    public static final int EXTRA_MIME_IMAGE = 1;
    public static final int EXTRA_MIME_DOC = 2;
    public static final int EXTRA_MIME_PDF = 3;
    public static final int EXTRA_MIME_VIDEO = 4;
    public static final int EXTRA_MIME_VOICE = 5;

    public static int isMatchMIMEType(String filename) {
        Log.d(TAG, "isMatchMIMEType: filename " + filename);
        int pos = filename.indexOf(".");
        if (pos == -1 || filename.charAt(0) == '.') {
            return EXTRA_MIME_NONE;
        }
        String lastName = filename.substring(pos).toLowerCase();
        Log.d(TAG, "isMatchMIMEType: lastName " + lastName);

        if (lastName.equalsIgnoreCase(".jpg")
        || lastName.equalsIgnoreCase(".png")) {
            return EXTRA_MIME_IMAGE;
        } else if (lastName.equalsIgnoreCase(".amr")) {
            return EXTRA_MIME_VOICE;
        } else if (lastName.equalsIgnoreCase(".pdf")) {
            return EXTRA_MIME_PDF;
        } else if (lastName.equalsIgnoreCase(".doc")
        || lastName.equalsIgnoreCase(".docx")
        || lastName.equalsIgnoreCase(".xls")
        || lastName.equalsIgnoreCase("xlsx")) {
            return EXTRA_MIME_DOC;
        } else if (lastName.equalsIgnoreCase(".mp4")
        || lastName.equalsIgnoreCase(".rmvb")
        || lastName.equalsIgnoreCase(".avi")
        || lastName.equalsIgnoreCase(".3gp")
        || lastName.equalsIgnoreCase(".mkv")) {
            return EXTRA_MIME_VIDEO;
        }

        return EXTRA_MIME_NONE;
    }
}


