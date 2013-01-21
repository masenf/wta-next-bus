package com.masenf.wtaandroid.async;


public interface RequestCallback<T> extends TaskCallback {
	public void updateData(T result);
}
