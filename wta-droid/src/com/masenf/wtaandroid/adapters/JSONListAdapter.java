package com.masenf.wtaandroid.adapters;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import android.widget.BaseAdapter;

public abstract class JSONListAdapter extends BaseAdapter {

	private static final String TAG = "JSONListAdapter";
	// the array of data that we're interested in
	private JSONArray res = new JSONArray();
	
	protected Context ctx = null;
	private String key = null;		// the key to lookup in the data object

	public JSONListAdapter(Context ctx, String key) {
		this.ctx = ctx;
		this.key = key;
	}
	public void setData(JSONObject d)
	{
		if (d == null) {				// null object passed
			res = new JSONArray();
		} else {
			try {
				res = d.getJSONArray(key);
			} catch (JSONException e) {
				Log.v(TAG,"Error, no key in the JSON data. this.key = '" + key + "'");
			}
		}
		this.notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return res.length();
	}

	@Override
	public JSONArray getItem(int pos) {
		try {
			return res.getJSONArray(pos);
		} catch (JSONException e) {
			Log.v(TAG,"getItem: item @ " + pos + " does not exist");
			return new JSONArray();
		}
	}

	@Override
	public long getItemId(int pos) {
		return pos;
	}

}
