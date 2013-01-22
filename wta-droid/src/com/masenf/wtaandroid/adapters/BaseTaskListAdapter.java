package com.masenf.wtaandroid.adapters;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.masenf.wtaandroid.data.BaseEntry;
import com.masenf.wtaandroid.data.EntryList;

public class BaseTaskListAdapter extends BaseAdapter {

	private static final String TAG = "BaseTaskListAdapter";
	private EntryList items = new EntryList();
	protected Context ctx;
	
	public BaseTaskListAdapter(Context ctx) {
		this.ctx = ctx;
	}

	public void restoreAdapterState(Bundle ad_state) {
		if (ad_state != null) {
			if (ad_state.containsKey("items")) {
				setData((EntryList) ad_state.getSerializable("items"));
				Log.d(TAG, "restoreAdapterState() - restored items for " + getClass().getName());
			}
		}
	}
	public Bundle saveAdapterState() {
		Bundle ad_state = new Bundle();
		ad_state.putSerializable("items", items);
		Log.d(TAG, "saveAdapterState() - bundled items for " + getClass().getName());
		return ad_state;
	}
	public void setData(EntryList data) {
		items = data;
	}
	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public BaseEntry getItem(int i) {
		return items.get(i);
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public View getView(int pos, View convertView, ViewGroup parent) {
		//Log.v(TAG,"Fetching view for position " + pos);	
		BaseEntry item = getItem(pos);
		
		if (convertView==null || 
			convertView.getTag().getClass().equals(item.getClass()) == false)	// we're not recycling
		{
			LayoutInflater inf  = LayoutInflater.from((Context) ctx);
			convertView = inf.inflate(item.getViewLayout(), null);
		}
		return item.updateView(convertView);
	}
}
