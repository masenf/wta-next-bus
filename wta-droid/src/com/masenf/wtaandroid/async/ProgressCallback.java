package com.masenf.wtaandroid.async;


public interface ProgressCallback extends TaskCallback {
	public void startProgress(Integer max);
	public void onProgress(Integer percent);
}
