package com.masenf.wtaandroid.adapters;

import java.util.ArrayList;

import com.masenf.wtaandroid.data.BaseEntry;
import com.masenf.wtaandroid.data.EntryListFactory;
import com.masenf.wtaandroid.data.LocationEntry;
import com.masenf.wtaandroid.data.TagEntry;
import com.masenf.wtaandroid.data.WtaDatastore;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class BaseTaskListAdapter extends BaseAdapter {

	private static final String TAG = "BaseTaskListAdapter";
	protected Context ctx;
	private ArrayList<BaseEntry> items;

	public void setData(ArrayList<BaseEntry> data) {
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
			LayoutInflater inf  = LayoutInflater.from(ctx);
			convertView = inf.inflate(item.getViewLayout(), null);
		}
		return item.updateView(convertView);
	}

}
