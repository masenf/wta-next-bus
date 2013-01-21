package com.masenf.wtaandroid.adapters;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;

import com.masenf.wtaandroid.R;
import com.masenf.wtaandroid.WtaActivity;
import com.masenf.wtaandroid.data.BaseEntry;
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
	protected Context ctx;
	
	public NestedTagListAdapter (Context ctx) {
		this.ctx = ctx;
		TagEntry.createFolder(ctx);		// generate the folder icon
	}
	private ArrayList<BaseEntry> mergeResults(ArrayList<TagEntry> ate, ArrayList<LocationEntry> ale) {
		if (ate == null)							// round off edge cases
			ate = new ArrayList<TagEntry>(0);
		if (ale == null)
			ale = new ArrayList<LocationEntry>(0);
		
		int len = ate.size() + ale.size();
		ArrayList<BaseEntry> result = new ArrayList<BaseEntry>(len);
		for (BaseEntry e : ate) {
			if (e != null)
				result.add(e);
		}
		for (BaseEntry e : ale) {
			if (e != null)
				result.add(e);
		}
		return result;
		
	}
	public void setLevel(String tag) {
		WtaDatastore d = WtaDatastore.getInstance(ctx);
		ArrayList<TagEntry> tags = EntryListFactory.fromTagCursor(d.getSubTags(tag));
		ArrayList<LocationEntry> locations = EntryListFactory.fromLocationCursor(d.getLocations(tag));
		setData(mergeResults(tags, locations));
		notifyDataSetChanged();
		Log.v(TAG,"Reset data to " + tag);
	}
}
