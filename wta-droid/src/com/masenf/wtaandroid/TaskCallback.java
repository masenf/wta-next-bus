package com.masenf.wtaandroid;

public interface TaskCallback {
	public static TaskCallback cb = null;
	public void startProgress();
	public void stopProgress();
	public void updateError(String msg);
	public void notifyComplete();
}
