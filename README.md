AndroidDemo

# 功能备注

H5应用需要一个app壳。 
壳APP框架功能需求： 
1、 根据配置文件切换H5应用链接，配置文件可以在线更新并本地保存 ✔️
2、 支持拍照、扫码、录音接口；✔️ 
3、 支持微信＼微博分享接口； ✔
4、 尽可能适配低版本系统（考虑使用人员有许多老手机），屏幕大小自适应。 
5、 提供APP升级功能。 ✔
6、 获取设备信息接口：手机网络状态＼手机系统信息＼位置信息＼剪贴板＼系统文件\通讯录 ✔️ 
7、 拨打电话＼发送短信息  ✔️
8、 选择文件以供上传 

##时间截止为3.20 11：50
#所有功能请提供调用示例，注释清楚，需要源代码。

1. 手机网络状态 >  wifi开关和已选wifi名称ssid， 或4G，或没网络，✅
2. 位置信息    >  当前经纬度 ✅
3. 剪贴板      >  获取剪贴板文字内容 ✅
4. 系统文件    >  哪些系统文件，主要是word和excel,pdf，我们用来上传报告用的。
5. 手机系统信息 >  手机型号和操作系统版本即可。✅

选择文件供上传 
1. (图片、视频和录音)
2.word和excel


## H5

```
config_file_url=http://www.xxx.com/config/config.txt（可以是其它文件名，下载后保存为config.txt）
home_page=http://www.xxx.com/index.html
--------------------------------------
配置文件名：config.txt
每次启动时:如果没有这个文件，从一个缺省config_file_url下载，
config_file_url下载配置文件保存为config.txt，下载后打开home_page页面
config.txt如果没有config_file_url配置项，从一个缺省config_file_url下载，
下载下来的config.txt里没有home_page配置项时，提示关闭应用，再重新启动一次app;
```

## 测试Demo如下:

- config_file_url: http://140.143.243.75:8090/config/app_web_config.json
- home_page_url: http://140.143.243.75:8090/index.html

```
[url:地址；code:当前云端版本(建议递增)]
云端文件配置文件内容. 文件名:androiddemo_web_config.txt, 格式:json;

{
	"config_file_url": {
	"url": "http://140.143.243.75:8090/config/app_web_config.json",
					"code": 1
},
	"home_page": {
	"url": "http://140.143.243.75:8090/",
					"code": 1
}
}
```
