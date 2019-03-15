package com.ilifesmart.model;

public class LocalFileData {

	public String mPath;
	public String mName;
	public String mDispName;
	public long mSize;

	public LocalFileData() {}

	public LocalFileData(String path, String name, String disp_name, long size) {
		mPath = path;
		mName = name;
		mDispName = disp_name;
		mSize = size;
	}

	public String getCategory() {
		return "LocalFileData";
	};
	/*
	*{path='/storage/emulated/0/Quark/Download/CloudDrive/15ddba3b880886c199a6110d669ef835/壁纸_8.jpg', name='壁纸_8', dispname='壁纸_8.jpg', size=2245317}
	**/
	@Override
	public String toString() {
		return getCategory() + "{" +
						"path='" + mPath + '\'' +
						", name='" + mName + '\'' +
						", disp_name='" + mDispName + '\'' +
						", size=" + mSize +
						'}';
	}
}
