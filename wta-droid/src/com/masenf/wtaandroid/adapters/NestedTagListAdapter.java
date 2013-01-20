package com.masenf.wtaandroid.adapters;

import java.util.ArrayList;
import java.util.Stack;

import com.masenf.wtaandroid.EntryFactory;
import com.masenf.wtaandroid.LocationEntry;
import com.masenf.wtaandroid.R;
import com.masenf.wtaandroid.TagEntry;
import com.masenf.wtaandroid.WtaDatastore;
import com.masenf.wtaandroid.Wta_main;

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

public class NestedTagListAdapter extends BaseAdapter {
	private static final String TAG = "HierarchyListAdapter";
	protected Context ctx;
	private Drawable folder;
	private ArrayList<TagEntry> tags;
	private ArrayList<LocationEntry> locations;
	
	public NestedTagListAdapter (Context ctx) {
		this.ctx = ctx;
		folder = ctx.getResources().getDrawable(R.drawable.tag_white);
		float ratio = (float) folder.getIntrinsicWidth() / (float) folder.getIntrinsicHeight();
		int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 45, ctx.getResources().getDisplayMetrics());
		int width = (int) (ratio * (float) height);
		folder.setBounds(0,0,width,height);
		tags = new ArrayList<TagEntry>();
		locations = new ArrayList<LocationEntry>();
	}
	public void setLevel(String tag) {
		WtaDatastore d = WtaDatastore.getInstance(ctx);
		tags = EntryFactory.fromTagCursor(d.getSubTags(tag));
		locations = EntryFactory.fromLocationCursor(d.getLocations(tag));
		notifyDataSetChanged();
		Log.v(TAG,"Reset data to " + tag);
	}

	@Override
	public int getCount() {
		return tags.size() + locations.size();
	}

	@Override
	public Object getItem(int i) {
		if (i > tags.size() - 1) {
			return locations.get(i - tags.size());
		} else {
			return tags.get(i);
		}
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public View getView(int pos, View convertView, ViewGroup parent) {
		Log.v(TAG,"Fetching view for position " + pos);
		
		if (convertView==null)			// we're not recycling
		{
			LayoutInflater inf  = LayoutInflater.from(ctx);
			convertView = inf.inflate(R.layout.hierarchy_item, null);
		}

		TextView stop_id = (TextView) convertView.findViewById(R.id.item_left);
		TextView location = (TextView) convertView.findViewById(R.id.item_right);
		
		Object item = getItem(pos);
		if (item.getClass() == TagEntry.class) {
			final TagEntry te = (TagEntry) item;
			stop_id.setText("");
			stop_id.setCompoundDrawables(folder, null, null, null);
			location.setText(te.name);
			convertView.setTag(te);
			Log.v(TAG, "getView() - Created TagEntry view for " + te.name);
		} else if (item.getClass() == LocationEntry.class) {
			final LocationEntry le = (LocationEntry) item;
			stop_id.setText(String.valueOf(le.stop_id));
			stop_id.setCompoundDrawables(null, null, null, null);
			if (le.alias == null)
				location.setText(le.name);
			else
				location.setText(le.alias);
			convertView.setTag(le);
			Log.v(TAG, "getView() - Created LocationEntry view for " + le.name);
		}
		return convertView;
	}
}
