package com.masenf.wtaandroid;

import org.json.JSONObject;

public interface RequestCallback {
	public void startProgress();
	public void stopProgress();
	public void updateData(JSONObject result);
	public void updateError(String msg);
}
