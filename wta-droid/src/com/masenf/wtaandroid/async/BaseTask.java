package com.masenf.wtaandroid.async;

import com.masenf.wtaandroid.progress.IProgressManager;
import com.masenf.wtaandroid.progress.ProgressCallback;
import com.masenf.wtaandroid.progress.ProgressItem;
import com.masenf.wtaandroid.progress.ProgressUpdate;

import android.os.AsyncTask;
import android.util.Log;

public abstract class BaseTask<Params, Result> extends AsyncTask<Params, ProgressUpdate, Result> {

	private static final String TAG = "BaseTask";
	protected IProgressManager pg = null;
	private String tag = "Default";
	private String error = "";
	
	public void setProgressManager(IProgressManager ipm, String tag) {
		pg = ipm;
		this.tag = tag;
	}
	private ProgressCallback getProgressCallback() {
		// this stays private to prevent stupid errors like updating the progress from the bg thread
		// use publishProgress(ProgressUpdate) to update the UI safely
		if (pg != null)
			return pg.getProgressCallback(tag);
		return new ProgressCallback();
	}
	protected void postError(String msg) {
		publishProgress(new ProgressUpdate(msg));
	}
	protected void appendError(String msg) {
		error += msg;
		postError(error);
	}
	protected boolean hasError() {
		if (error == "") 
			return false;
		return true;
	}
	protected void postProgressMax(Integer max) {
		publishProgress(new ProgressUpdate(0,max));
	}
	protected void postProgress(Integer sofar) {
		publishProgress(new ProgressUpdate(sofar));
	}
	
	@Override
	protected void onPreExecute() {
		if (pg != null) {
			ProgressCallback p = pg.createProgressCallback(tag);
			p.startProgress();
			ProgressUpdate up = new ProgressUpdate();
			up.setLabel(getClass().getName());
			p.onProgress(up);
		}
		Log.v(TAG,"onPreExecute() for " + getClass().getName());
	}
	
	@Override 
	protected void onProgressUpdate(ProgressUpdate... progress) {
		int count = progress.length;
		for (int i = 0; i< count; i++) {
			getProgressCallback().onProgress(progress[i]);
		}
	}
	
	@Override
	protected void onPostExecute(Result result) {
		if (pg != null) {
			ProgressCallback cb = getProgressCallback();
			cb.stopProgress();
			if (hasError()) 
				cb.notifyComplete(true, tag);
			else {
				cb.updateError(error);
				cb.notifyComplete(false, tag);
			}
		}
		Log.v(TAG,"onPostExecute() for " + getClass().getName());
	}

}
