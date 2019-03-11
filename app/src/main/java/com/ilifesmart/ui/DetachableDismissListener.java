package com.ilifesmart.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.view.ViewTreeObserver;

public class DetachableDismissListener implements DialogInterface.OnDismissListener {

	public static DetachableDismissListener wrap(DialogInterface.OnDismissListener delegate) {
		return new DetachableDismissListener(delegate);
	}

	private DialogInterface.OnDismissListener delegateOrNull;

	private DetachableDismissListener(DialogInterface.OnDismissListener delegate) {
		this.delegateOrNull = delegate;
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		if (delegateOrNull != null) {
			delegateOrNull.onDismiss(dialog);
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
