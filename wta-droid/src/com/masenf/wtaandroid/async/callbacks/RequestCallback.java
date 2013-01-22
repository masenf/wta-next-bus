package com.masenf.wtaandroid.async.callbacks;


public abstract class RequestCallback<T> {
	public abstract void updateData(T result);
}
