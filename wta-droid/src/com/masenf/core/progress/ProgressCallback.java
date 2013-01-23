package com.masenf.core.progress;

import com.masenf.core.async.callbacks.BaseCallback;

public class ProgressCallback extends BaseCallback {
	// basic progress reporting
	public void startProgress() { };
	public void stopProgress() { };
	public void updateError(String msg) { };
	
	// detailed updates
	public void onProgress(ProgressUpdate update) { };
}
