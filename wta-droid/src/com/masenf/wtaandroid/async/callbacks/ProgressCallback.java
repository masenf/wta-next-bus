package com.masenf.wtaandroid.async.callbacks;



public class ProgressCallback extends BaseCallback {
	// basic progress reporting
	public void startProgress() { };
	public void stopProgress() { };
	public void updateError(String msg) { };
	
	// numeric progress
	public void startProgress(Integer max) { };
	public void onProgress(Integer percent) { };
}
