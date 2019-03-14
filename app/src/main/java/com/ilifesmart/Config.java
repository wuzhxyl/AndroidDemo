package com.ilifesmart;

import com.alibaba.fastjson.annotation.JSONField;

public class Config {
	public static class WebDemoInfo {
		@JSONField(name="url")
		public String url;
		@JSONField(name="code")
		public int code;

		public WebDemoInfo() { }

		public WebDemoInfo(String _url, int _code) {
			this.url = _url;
			this.code = _code;
		}

		@Override
		public String toString() {
			return "[ url:"+url+"; code:"+code+"]";
		}
	}

	private WebDemoInfo mHomePageInfo;
	private WebDemoInfo mConfigUrlInfo;

	public void setConfigUrlInfo(WebDemoInfo configUrlInfo) {
		mConfigUrlInfo = configUrlInfo;
	}

	public WebDemoInfo getConfigUrlInfo() {
		return mConfigUrlInfo;
	}

	public void setHomePageInfo(WebDemoInfo homePageInfo) {
		mHomePageInfo = homePageInfo;
	}

	public WebDemoInfo getHomePageInfo() {
		return mHomePageInfo;
	}
}
