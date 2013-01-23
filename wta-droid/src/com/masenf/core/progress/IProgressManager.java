package com.masenf.core.progress;


public interface IProgressManager {
	public ProgressCallback getProgressCallback(String tag);
	public ProgressCallback createProgressCallback(String tag);
}
