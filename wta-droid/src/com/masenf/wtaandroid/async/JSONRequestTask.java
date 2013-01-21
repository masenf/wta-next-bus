package com.masenf.wtaandroid.async;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import com.masenf.wtaandroid.IGlobalProgress;


import android.os.AsyncTask;
import android.util.Log;

public class JSONRequestTask extends BaseTask<URL, JSONObject> {
		
	public static final String TAG = "JSONRequestTask";
	private RequestCallback<JSONObject> cb = null;
	
	public JSONRequestTask(RequestCallback<JSONObject> cb, IGlobalProgress pg) {
		this.cb = cb;
		if (pg != null)
			setGlobalProgress(pg);
	}
	@Override
	protected JSONObject doInBackground(URL... params) {
		int count = params.length;
		JSONObject res_data = null;		        // the response data
		StringBuilder res = new StringBuilder();	// the response
		for (int i=0;i<count;i++)
		{
			HttpURLConnection urlConnection;
			try {
				urlConnection = (HttpURLConnection) params[i].openConnection();
			} catch (IOException e1) {
				appendError("IOException creating connection: " + e1.getMessage());
				return new JSONObject();
			}
			try {
				if (urlConnection.getResponseCode() == 200)
				{
					if (res_data == null)
					{
						InputStream is = urlConnection.getInputStream();
						BufferedInputStream bis = new BufferedInputStream(is);
						int c;
						while ((c = bis.read()) != -1)
						{
							res.append((char) c);
						}
						res_data = new JSONObject(res.toString());
						break;
					}
				} else {
					appendError("Error making API request: " + urlConnection.getResponseMessage());
					return new JSONObject();
				}
			} catch (IOException e) {
				appendError("IOException reading response: " + e.toString());
			} catch (JSONException e) {
				appendError("JSONException processing request: " + e.toString());
			} finally {
				if (res_data == null)
				{
					res_data = new JSONObject();
				}
				urlConnection.disconnect();
			}
		}
		return res_data;
	}
	@Override
	protected void onPostExecute(JSONObject result)
	{
		if (hasError() == false) { // no error
			Log.v(TAG,"Transfer complete, notifying the activity...");
			cb.updateData(result);
		}
		super.onPostExecute(result);
	}
}
