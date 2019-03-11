package com.ilifesmart.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.view.ViewTreeObserver;

public final class DetachableClickListener implements DialogInterface.OnClickListener {

	// 因DialogInterface.OnClickListener存在内存泄漏,修改为在Detached时候手动置null.

	public static DetachableClickListener wrap(DialogInterface.OnClickListener delegate) {
		return new DetachableClickListener(delegate);
	}

	private DialogInterface.OnClickListener delegateOrNull;

	private DetachableClickListener(DialogInterface.OnClickListener delegate) {
		this.delegateOrNull = delegate;
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		if (delegateOrNull != null) {
			delegateOrNull.onClick(dialog, which);
		}
	}

	public void clearOnDetach(Dialog dialog) {
		dialog.getWindow().getDecorView().getViewTreeObserver().addOnWindowAttachListener(new ViewTreeObserver.OnWindowAttachListener() {
			@Override
			public void onWindowAttached() { }

			@Override
			public void onWindowDetached() {
				delegateOrNull = null;
			}
		});
	}
}
