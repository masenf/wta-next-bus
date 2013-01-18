package com.masenf.wtaandroid;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

public class JSONRequestTask extends AsyncTask<URL, Integer, JSONObject> {
		
	public static final String TAG = "JSONRequestTask";
	private RequestCallback cb = null;
	private String message = "";
	
	public JSONRequestTask(RequestCallback cb) {
		this.cb = cb;
	}
	@Override
	protected void onPreExecute() {
		cb.startProgress();
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
				message = "IOException creating connection: " + e1.getMessage();
				Log.v(TAG, message);
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
					message = "Error making API request: " + urlConnection.getResponseMessage();
					Log.v(TAG, message);
					return new JSONObject();
				}
			} catch (IOException e) {
				message = "IOException reading response: " + e.toString();
				Log.v(TAG, message);
			} catch (JSONException e) {
				message = "JSONException processing request: " + e.toString();
				Log.v(TAG, message);
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
		if (message.equals("")) { // no error
			Log.v(TAG,"Transfer complete, notifying the activity...");
			cb.updateData(result);
		} else {
			cb.updateError(message);
		}
		cb.stopProgress();
	}
}
