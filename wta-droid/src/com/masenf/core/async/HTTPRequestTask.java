package com.masenf.core.async;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Log;

public abstract class HTTPRequestTask<Params, Result> extends BaseTask<Params, Result> {

	private static final String TAG = "HTTPRequestTask";
	protected static int BufferSz = 1024;		// 1kb buffer
	protected HttpURLConnection urlConnection;

	protected BufferedInputStream makeRequest(URL url) {
		Log.v(TAG, "makeRequest() beginning async fetch of " + url.toString());
		BufferedInputStream bis = null;
		try {
			urlConnection = (HttpURLConnection) url.openConnection();
		} catch (IOException e1) {
			appendError("IOException creating connection: " + e1.getMessage());
			return bis;
		}
		try {
			if (urlConnection.getResponseCode() == 200)
			{
				Log.v(TAG, "makeRequest() request successful, data is " + urlConnection.getContentLength() + " bytes");
				postProgressMax(urlConnection.getContentLength());
				InputStream is = urlConnection.getInputStream();
				bis = new BufferedInputStream(is);
			} else {
				Log.v(TAG, "makeRequest() server returned non-200: " + urlConnection.getResponseCode() + " " + urlConnection.getResponseMessage());;
			}
		} catch (IOException e) {
			appendError("IOException reading response: " + e.toString());
			Log.v(TAG, "makeRequest() unsuccessful connection " + e.toString());
		}
		return bis;
	}
	protected String readToString(BufferedInputStream bis) {
		if (bis == null) {
			Log.w(TAG,"readToString() InputStream is null");
			return "";
		}
		StringBuilder res = new StringBuilder();	// the response
		byte[] raw = new byte[BufferSz];
		int total_bytes = 0;
		int bytes_read = 0;
		try {		
			while (bytes_read > -1)
			{
				total_bytes += bytes_read;
				bytes_read = bis.read(raw);
				for (int i=0;i<bytes_read;i++)
					res.append((char) raw[i]);
				postProgress(total_bytes);
				Log.v(TAG, "Just read " + bytes_read + " bytes");
			}
		} catch (IOException e) {
			appendError("IOException reading response: " + e.toString());
		} finally {
			urlConnection.disconnect();		// close the connection
		}
		return res.toString();
	}
}
