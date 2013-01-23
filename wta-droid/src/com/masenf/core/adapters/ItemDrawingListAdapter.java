package com.masenf.core.adapters;

import java.util.ArrayList;
import java.util.List;

import com.masenf.core.DrawingItem;
import com.masenf.core.DrawingItemList;
import com.masenf.core.data.BaseEntry;
import com.masenf.core.data.EntryList;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class ItemDrawingListAdapter<T extends DrawingItemList<?>> extends BaseAdapter {
	private static final String TAG = "ItemDrawingListAdapter";
	protected T items;
	protected Context ctx;
	
	public ItemDrawingListAdapter(Context ctx) {
		this.ctx = ctx;
	}
	public void restoreAdapterState(Bundle ad_state) {
		if (ad_state != null) {
			if (ad_state.containsKey("items")) {
				setData((T) ad_state.getSerializable("items"));
				Log.d(TAG, "restoreAdapterState() - restored items for " + getClass().getName());
			}
		}
	}
	public Bundle saveAdapterState() {
		Bundle ad_state = new Bundle();
		if (items != null) {
			ad_state.putSerializable("items", items);
			Log.d(TAG, "saveAdapterState() - bundled items for " + getClass().getName());
		}
		return ad_state;
	}
	public void setData(T data) {
		items = data;
	}
	public T getData() {
		return items;
	}
	@Override
	public int getCount() {
		if (items == null)
			return 0;
		return items.size();
	}

	@Override
	public DrawingItem getItem(int i) {
		return items.get(i);
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public View getView(int pos, View convertView, ViewGroup parent) {
		//Log.v(TAG,"getView, position " + pos + " for " + getClass().getName());	
		DrawingItem item = getItem(pos);
		
		if (convertView==null || 
			convertView.getTag().getClass().equals(item.getClass()) == false)	// we're not recycling
		{
			LayoutInflater inf  = LayoutInflater.from((Context) ctx);
			convertView = inf.inflate(item.getViewLayout(), null);
		}
		return item.updateView(convertView);
	}
}
