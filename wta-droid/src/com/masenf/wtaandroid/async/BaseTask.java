package com.masenf.wtaandroid.async;

import com.masenf.wtaandroid.IGlobalProgress;
import com.masenf.wtaandroid.async.callbacks.ProgressCallback;

import android.os.AsyncTask;
import android.util.Log;

public abstract class BaseTask<Params, Result> extends AsyncTask<Params, Integer, Result> {

	private static final String TAG = "BaseTask";
	private IGlobalProgress pg = null;
	private String error = "";
	
	public void setGlobalProgress(IGlobalProgress igpg) {
		pg = igpg;
	}
	protected void appendError(String msg) {
		error += msg;
	}
	protected boolean hasError() {
		if (error == "") 
			return false;
		return true;
	}
	protected void setProgressMax(Integer max) {
		pg.getGlobalProgressCallback().startProgress(max);
	}
	
	@Override
	protected void onPreExecute() {
		if (pg != null)
			pg.getGlobalProgressCallback().startProgress();
		Log.v(TAG,"onPreExecute() for " + getClass().getName());
	}
	
	@Override 
	protected void onProgressUpdate(Integer... progress) {
		int count = progress.length;
		for (int i = 0; i< count; i++) {
			pg.getGlobalProgressCallback().onProgress(progress[i]);
		}
	}
	
	@Override
	protected void onPostExecute(Result result) {
		if (pg != null) {
			ProgressCallback cb = pg.getGlobalProgressCallback();
			cb.stopProgress();
			if (hasError()) 
				cb.notifyComplete(true);
			else {
				cb.updateError(error);
				cb.notifyComplete(false);
			}
		}
		Log.v(TAG,"onPostExecute() for " + getClass().getName());
	}

}
