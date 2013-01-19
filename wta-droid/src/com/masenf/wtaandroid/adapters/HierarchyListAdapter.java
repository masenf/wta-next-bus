package com.masenf.wtaandroid.adapters;

import java.util.ArrayList;
import java.util.Stack;

import org.json.JSONArray;
import org.json.JSONException;

import com.masenf.wtaandroid.EntryFactory;
import com.masenf.wtaandroid.IEntry;
import com.masenf.wtaandroid.LocationEntry;
import com.masenf.wtaandroid.R;
import com.masenf.wtaandroid.TagEntry;
import com.masenf.wtaandroid.WtaDatastore;
import com.masenf.wtaandroid.WtaDatastore.TagEntryType;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

public abstract class HierarchyListAdapter extends BaseAdapter implements OnItemClickListener {
	private static final String TAG = "HierarchyListAdapter";
	private Context ctx;
	private String root;
	private String current_level;
	private Drawable folder;
	private ArrayList<TagEntry> tags;
	private ArrayList<LocationEntry> locations;
	private Stack<String> s;
	public HierarchyListAdapter (Context ctx, String root) {
		s = new Stack<String>();
		this.root = root;
		this.ctx = ctx;
		setLevel(root);
		folder = ctx.getResources().getDrawable(R.drawable.tag_white);
		float ratio = (float) folder.getIntrinsicWidth() / (float) folder.getIntrinsicHeight();
		int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 45, ctx.getResources().getDisplayMetrics());
		int width = (int) (ratio * (float) height);
		folder.setBounds(0,0,width,height);
	}
	public Bundle saveAdapterState() {
		Bundle p = new Bundle();
		p.putString("current_level", current_level);
		p.putSerializable("stack", s);
		return p;
	}
	public void restoreAdapterState(Bundle p) {
		if (p.containsKey("stack"))
			s = (Stack<String>) p.getSerializable("stack");
		if (p.containsKey("current_level"))
			setLevel(p.getString("current_level"));
	}
	public boolean up() {
		if (s.isEmpty() == false) {
			Log.v(TAG,"up() - going up one level");
			setLevel(s.pop());
			return true;
		}
		return false;
	}
	public void descend(String tag) {
		Log.v(TAG,"descend() - descending to " + tag);
		s.push(current_level);
		setLevel(tag);
	}
	public String getCurrentLevel() {
		return current_level;
	}
	public void setLevel(String tag) {
		this.current_level = tag;
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
			convertView.setTag(TagEntryType.TAG_NAME);
			Log.v(TAG, "getView() - Created TagEntry view for " + te.name);
		} else if (item.getClass() == LocationEntry.class) {
			final LocationEntry le = (LocationEntry) item;
			stop_id.setText(String.valueOf(le.stop_id));
			stop_id.setCompoundDrawables(null, null, null, null);
			if (le.alias == null)
				location.setText(le.name);
			else
				location.setText(le.alias);
			convertView.setTag(TagEntryType.STOP);
			Log.v(TAG, "getView() - Created LocationEntry view for " + le.name);
		}
		return convertView;
	}
	@Override
	public void onItemClick(AdapterView<?> adView, View target, int pos, long id) {
		Log.v(TAG,"onItemClick() - " + target.getTag().toString());
		if (target.getTag().equals(TagEntryType.TAG_NAME))
			descend(((TextView) target.findViewById(R.id.item_right)).getText().toString());
		else {
			int stop_id = Integer.parseInt(((TextView) target.findViewById(R.id.item_left)).getText().toString());
			String name = ((TextView) target.findViewById(R.id.item_right)).getText().toString();
			onLocationClick(stop_id, name);
		}
	}
	protected abstract void onLocationClick(int stop_id, String name);

}
