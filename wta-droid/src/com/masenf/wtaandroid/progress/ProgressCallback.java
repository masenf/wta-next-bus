package com.masenf.wtaandroid.progress;

import com.masenf.wtaandroid.async.callbacks.BaseCallback;

public class ProgressCallback extends BaseCallback {
	// basic progress reporting
	public void startProgress() { };
	public void stopProgress() { };
	public void updateError(String msg) { };
	
	// detailed updates
	public void onProgress(ProgressUpdate update) { };
}
