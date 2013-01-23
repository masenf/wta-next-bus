package com.masenf.core.async;

import java.net.URL;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import com.masenf.core.async.callbacks.RequestCallback;

import android.util.Log;

public class JSONRequestTask extends HTTPRequestTask<URL, JSONObject> {
		
	private static final String TAG = "JSONRequestTask";
	private RequestCallback<JSONObject> cb = null;
	
	public JSONRequestTask(RequestCallback<JSONObject> cb) {
		this.cb = cb;
		setTag(UUID.randomUUID().toString());
	}
	@Override
	protected JSONObject doInBackground(URL... params) {
		JSONObject res_data = null;		        // the response data
		try {
			res_data = new JSONObject(readToString(makeRequest(params[0])));
		} catch (JSONException e) {
			appendError("JSONException processing request: " + e.toString());
		} finally {
			if (res_data == null)
				res_data = new JSONObject();
		}
		return res_data;
	}
	@Override
	protected void onPostExecute(JSONObject result)
	{
		if (hasError() == false) { // no error
			Log.v(TAG,"Transfer complete, notifying the activity...");
			cb.updateData(result);
		} else {
			Log.v(TAG,"Error in transfer: " + getError());
		}
		super.onPostExecute(result);
	}
}
