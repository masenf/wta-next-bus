package com.masenf.wtaandroid;

public interface ProgressCallback extends TaskCallback {
	public void startProgress(Integer max);
	public void onProgress(Integer percent);
}
