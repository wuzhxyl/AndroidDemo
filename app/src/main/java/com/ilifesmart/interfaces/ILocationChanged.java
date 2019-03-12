package com.ilifesmart.interfaces;

public interface ILocationChanged {
	void onLocationChanged(double latitude, double longitude);
	void onLocationError(int errCode, String errInfo);
}
