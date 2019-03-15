package com.ilifesmart.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.ess.filepicker.FilePicker;
import com.ess.filepicker.model.EssFile;
import com.ess.filepicker.util.Const;
import com.ilifesmart.androiddemo.R;
import com.ilifesmart.model.LocalFileData;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PageDetailsActivity extends AppCompatActivity {

	public static final int REQUEST_CODE_SEARCH_FILES = 102;
	@BindView(R.id.infos)
	TextView mInfos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_page_details);
		ButterKnife.bind(this);

		// !由于全磁盘查找所需类型会遍历所有文件夹，速度较慢，此处采用第三方方案.
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == REQUEST_CODE_SEARCH_FILES && resultCode == RESULT_OK) {
			List<LocalFileData> list = new ArrayList<>();
			StringBuilder builder = new StringBuilder();
			builder.append("[");
			ArrayList<EssFile> essFileList = data.getParcelableArrayListExtra(Const.EXTRA_RESULT_SELECTION);
			for (EssFile file : essFileList) {
				File zfile = file.getFile();
				builder.append("{path: ").append(zfile.getAbsolutePath()).append(",name: ").append(zfile.getName()).append(",size: ").append(String.format("%.2f", zfile.length() / 1024.0 / 1024) + " M}; ");
			}
			mInfos.setText(builder.append("]").toString());
		}
	}

	@OnClick({R.id.folder, R.id.category, R.id.pictures})
	public void onViewClicked(View view) {
		switch (view.getId()) {
			case R.id.folder:
				FilePicker.from(this)
								.chooseForBrowser()
								.setMaxCount(10)
								.setFileTypes("png", "doc", "mp3", "txt", "mp4")
								.requestCode(REQUEST_CODE_SEARCH_FILES)
								.start();
				break;
			case R.id.category:
				FilePicker.from(this)
								.chooseForMimeType()
								.setMaxCount(10)
								.setFileTypes("png", "doc", "xls", "mp4", "mp3")
								.requestCode(REQUEST_CODE_SEARCH_FILES)
								.start();
				break;
			case R.id.pictures:
				FilePicker
								.from(this)
								.chooseMedia()
								.enabledCapture(true)
								.setTheme(R.style.FilePicker_Dracula)
								.requestCode(REQUEST_CODE_SEARCH_FILES)
								.start();
				break;
		}
	}
}
