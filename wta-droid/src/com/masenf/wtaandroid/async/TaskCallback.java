package com.masenf.wtaandroid.async;

import java.io.Serializable;

public interface TaskCallback extends Serializable {
	public static TaskCallback cb = null;		// the actual callback
	public void startProgress();
	public void stopProgress();
	public void updateError(String msg);
	public void notifyComplete();
}
