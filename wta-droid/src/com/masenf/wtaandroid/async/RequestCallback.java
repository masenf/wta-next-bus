package com.masenf.wtaandroid.async;


public abstract class RequestCallback<T> {
	public abstract void updateData(T result);
}
