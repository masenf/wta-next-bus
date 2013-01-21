package com.masenf.wtaandroid.adapters;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;

import com.masenf.wtaandroid.IGlobalProgress;
import com.masenf.wtaandroid.R;
import com.masenf.wtaandroid.WtaActivity;
import com.masenf.wtaandroid.async.DataReadCallback;
import com.masenf.wtaandroid.async.DataReadTaskFactory;
import com.masenf.wtaandroid.data.BaseEntry;
import com.masenf.wtaandroid.data.EntryList;
import com.masenf.wtaandroid.data.EntryListFactory;
import com.masenf.wtaandroid.data.LocationEntry;
import com.masenf.wtaandroid.data.TagEntry;
import com.masenf.wtaandroid.data.WtaDatastore;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class NestedTagListAdapter extends BaseTaskListAdapter {
	private static final String TAG = "NestedTagListAdapter";
	private DataReadTaskFactory dtf;
	private String current_tag = "";
	private String next_tag = "";
	private boolean fetching = false;
	
	public NestedTagListAdapter (Context ctx, IGlobalProgress pg) {
		this.ctx = ctx;
		TagEntry.createFolder(ctx);		// generate the folder icon
		dtf = new DataReadTaskFactory(WtaDatastore.getInstance(ctx), 
				new DataReadCallback() {
					@Override
					public void updateData(EntryList result) {
						setData(result);
						notifyDataSetChanged();
						Log.v(TAG,"Reset data to " + next_tag);
						current_tag = next_tag;
						fetching = false;
					}
				}, pg);
	}
	@Override
	public Bundle saveAdapterState() {
		Bundle ad_state = super.saveAdapterState();
		ad_state.putString("current_tag", current_tag);
		return ad_state;
	}
	@Override
	public void restoreAdapterState(Bundle ad_state) {
		if (ad_state != null) {
			current_tag = ad_state.getString("current_tag","");
			Log.v(TAG,"restoreAdapterState() - recovered current_tag of " + current_tag);
		}
		super.restoreAdapterState(ad_state);
	}
	public void setLevel(String tag) {
		if (tag == current_tag)
			return;
		next_tag = tag;
		fetching = true;
		dtf.getTagsAndLocations(tag);
	}
	public boolean isFetching() {
		return fetching;
	}
}
