package com.masenf.wtaandroid.adapters;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.masenf.wtaandroid.R;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TimesListAdapter extends JSONListAdapter {
	
	private static JSONObject data;
	public TimesListAdapter(Context ctx, String key) {
		super(ctx, key);
		if (data != null) {
			setData(data);
		}
	}
	@Override
	public void setData(JSONObject d) {
		// cache the data locally
		super.setData(d);
		data = d;
	}

	private static final String TAG = "TimesListAdapter";
	
	@Override
	public View getView(int pos, View convertView, ViewGroup parent) {
		Log.v(TAG,"Fetching view for position " + pos);
		JSONArray item = getItem(pos);
		
		if (convertView==null)			// we're not recycling
		{
			LayoutInflater inf  = LayoutInflater.from(ctx);
			convertView = inf.inflate(R.layout.time_item, null);
		}
		TextView stop_id = (TextView) convertView.findViewById(R.id.item_time);
		TextView location = (TextView) convertView.findViewById(R.id.item_destination);
		
		try {
			stop_id.setText(item.getString(0));
			location.setText(item.getString(3));
		} catch (JSONException e) {
			Log.w(TAG,"Could not get time or destination from JSON");
		}
		return convertView;
	}

}
