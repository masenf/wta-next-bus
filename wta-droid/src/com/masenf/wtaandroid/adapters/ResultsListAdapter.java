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

public class ResultsListAdapter extends JSONListAdapter {
	
	private static JSONObject data;			// cache the raw data
	
	public ResultsListAdapter(Context ctx, String key) {
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

	private static final String TAG = "ResultsListAdapter";

	@Override
	public View getView(int pos, View convertView, ViewGroup parent) {
		Log.v(TAG,"Fetching view for position " + pos);
		JSONArray item = getItem(pos);
		
		if (convertView==null)			// we're not recycling
		{
			LayoutInflater inf  = LayoutInflater.from(ctx);
			convertView = inf.inflate(R.layout.location_item, null);
		}
		TextView txt_stop_id = (TextView) convertView.findViewById(R.id.item_stop_id);
		TextView txt_location = (TextView) convertView.findViewById(R.id.item_location);
		
		try {
			int stop_id = item.getInt(0);
			String location = item.getString(1);

			txt_stop_id.setText((CharSequence) String.valueOf(stop_id));
			txt_location.setText(location);
		} catch (JSONException e) {
			Log.w(TAG,"Could not get stop_id or location from JSON");
		}
		
		return convertView;
	}
}
