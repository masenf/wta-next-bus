package com.masenf.wtaandroid;

import java.util.Stack;

import com.masenf.wtaandroid.adapters.NestedTagListAdapter;
import com.masenf.wtaandroid.data.LocationEntry;
import com.masenf.wtaandroid.data.TagEntry;
import com.masenf.wtaandroid.data.WtaDatastore;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class HierarchyListView extends ListView implements OnItemClickListener  {
	private static final String TAG = "HierarchyListView";
	private Stack<StackItem> s;
	private NestedTagListAdapter ad;
	private WtaActivity a = null;
	private String current_level = "";
	private String root = "";
	
	public HierarchyListView(Context context) {
		super(context, null, 0);
	}
	public HierarchyListView(Context context, AttributeSet attrs) {
		super(context, attrs, 0);
	}
	public HierarchyListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		root = "";
		s = new Stack<StackItem>();
		Log.v(TAG,"Instantiating new HierarchyListView");
	}

	private class StackItem {
		String s;
		Parcelable state;		// the state of the list view on navigation
		public StackItem (String s, Parcelable state) {
			this.s = s;
			this.state = state;
		}
	}
	public void setActivity(WtaActivity a) {
		this.a = a;
	}
	public void setRoot(String root) {
		this.root = root;
		setLevel(root);
		s = new Stack<StackItem>();		// blow the stack away
		Log.v(TAG,"setRoot() set root to " + root);
	}
	public boolean up() {
		if (s.isEmpty() == false) {
			Log.v(TAG,"up() - going up one level");
			StackItem si = s.pop();
			setLevel(si.s);
			onRestoreInstanceState(si.state);
			return true;
		}
		return false;
	}
	public void descend(String tag) {
		Log.v(TAG,"descend() - descending to " + tag);
		s.push(new StackItem(current_level, onSaveInstanceState()));
		setLevel(tag);
	}
	public String getCurrentLevel() {
		return current_level;
	}
	
	public Bundle saveStackState() {
		Bundle p = new Bundle();
		p.putString("current_level", current_level);
		p.putSerializable("stack", s);
		return p;
	}
	
	public void restoreStackState(Bundle p) {
		ad = (NestedTagListAdapter) this.getAdapter();
		if (p.containsKey("stack"))
			this.s = (Stack<StackItem>) p.getSerializable("stack");
		if (p.containsKey("current_level")) {
			current_level = p.getString("current_level");
			ad.setLevel(p.getString("current_level"));
		}
		Log.v(TAG,"restoreStackState() - restored stack state");
	}
	
	public void setLevel(String tag) {
		current_level = tag;
		ad = (NestedTagListAdapter) this.getAdapter();
		ad.setLevel(tag);
		setSelectionFromTop(0, 0);
		Log.v(TAG,"setLevel() - set current_level to "+ tag);
	}
	@Override
	public void onItemClick(AdapterView<?> adView, View target, int pos, long id) {
		Log.v(TAG,"onItemClick() - " + target.getTag().toString());
		Object entry = target.getTag();
		if (entry.getClass() == TagEntry.class)
			descend(((TagEntry) entry).name);
		else if (entry.getClass() == LocationEntry.class) {
			int stop_id = ((LocationEntry) entry).stop_id;
			String name = ((LocationEntry) entry).name;
			onLocationClick(stop_id, name);
		}
	}
	protected void onLocationClick(int stop_id, String name) {
		if (a != null) {
			WtaDatastore.getInstance(a).addRecent(stop_id, name);
			a.lookupTimesForStop(stop_id, name);
		}
	}
}
