package com.ilifesmart.jsbridge;

import org.json.JSONObject;

import java.io.Serializable;

public interface LSNCallback extends Serializable {
	void onFinish(JSONObject var1);
}
