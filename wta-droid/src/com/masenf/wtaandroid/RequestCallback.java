package com.masenf.wtaandroid;

public interface RequestCallback<T> extends TaskCallback {
	public void updateData(T result);
}
