package com.masenf.wtaandroid;

import java.util.Stack;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.masenf.core.EntryClickHandler;
import com.masenf.core.IonBackButtonPressed;
import com.masenf.core.NullAnimationProto;
import com.masenf.core.StackItem;
import com.masenf.core.async.callbacks.DataReadCallback;
import com.masenf.core.data.EntryList;
import com.masenf.wtaandroid.adapters.TagListAdapter;
import com.masenf.wtaandroid.async.DataReadTaskFactory;
import com.masenf.wtaandroid.data.TagEntry;
import com.masenf.wtaandroid.data.WtaDatastore;

public class NestedTagManager extends EntryClickHandler implements OnItemClickListener, 
																   IonBackButtonPressed, 
																   OnItemLongClickListener {

	private static final String TAG = "NestedTagManager";
	private static final int slide_out_msec = 280;
	ListView lv;
	TagListAdapter ad;
	private String fragmentTag = "";
	private Activity act;
	
	private Stack<StackItem> s;

	private DataReadTaskFactory dtf;
	private StackItem current_item = null;
	
	public NestedTagManager(Activity act, final ListView lv, final TagListAdapter ad) {
		setListView(lv);
		setAdapter(ad);
		setActivity(act);

		Context ctx = (Context) act;
		TagEntry.createFolder(ctx);		// generate the folder icon
		dtf = new DataReadTaskFactory(new DataReadCallback() {
					@Override
					public void updateData(EntryList result) {
						if (ad != null) {
							ad.setData(result);
							ad.notifyDataSetChanged();
							lv.invalidate();
							Log.v(TAG,"Reset data to " + current_item.getLtag());
						}
						adjustStack();
					}
				});
		
		reset();
	}
	public void setFragmentTag(String ft) {
		fragmentTag = ft;
	}
	public void setListView(ListView lv) {
		this.lv = lv;
		lv.setOnItemLongClickListener(this);
	}
	public void setAdapter(TagListAdapter ad) {
		this.ad = ad;
	}
	public void setActivity(Activity act) {
		this.act = act;
	}
	protected void adjustStack() {
		if (lv == null)
			return;
		lv.setAdapter(ad);
		lv.setSelection(current_item.getListPos());
		Log.v(TAG, "adjustStack() - set list position to " + current_item.getListPos());
	}
	public void setLevel(String tag) {
		if (tag == current_item.getLtag())
			return;
		current_item = new StackItem(tag, 0);
		reloadData();
	}
	public void reset() {
		Log.v(TAG,"Instantiating new Stack for " + getClass().getName());
		s = new Stack<StackItem>();
		current_item = new StackItem(null,0);
	}
	public boolean pop() {
		if (s.isEmpty() == false) {
			current_item = s.pop();
			Log.v(TAG,"pop() - going up one level to " + current_item.getLtag());
			final Animation out = new TranslateAnimation(0, lv.getWidth(), 0, 0);
			out.setDuration(slide_out_msec);
			out.setInterpolator(new AccelerateDecelerateInterpolator());
			out.setAnimationListener(new NullAnimationProto() {
				@Override
				public void onAnimationEnd(Animation arg0) {
					reloadData();
				}
			});
			lv.startAnimation(out);
			Log.v(TAG,"pop() - started animation on " + lv.toString());
			return true;
		}
		return false;
	}
	private void reloadData() {
		Log.v(TAG,"reloadData() - started reloading data from " + current_item.getLtag());
		lv.setAdapter(null);
		dtf.getContents(current_item.getLtag());	// spawn the fetch task
	}
	public void push(final String next_ltag) {
		current_item.setListPos(lv.getFirstVisiblePosition());
		Log.v(TAG,"push() - saving state for " + current_item.getLtag() + ", " + current_item.getListPos());
		s.push(current_item);
		Log.v(TAG,"push() - state saved, smash the data into " + next_ltag);
		final Animation out = new TranslateAnimation(0, -lv.getWidth(), 0, 0);
		out.setDuration(slide_out_msec);
		out.setInterpolator(new AccelerateDecelerateInterpolator());
		out.setAnimationListener(new NullAnimationProto() {
			@Override
			public void onAnimationEnd(Animation arg0) {
				setLevel(next_ltag);
			}
		});
		lv.startAnimation(out);
		Log.v(TAG,"push() - started animation on " + lv.toString());

	}
	public StackItem getCurrentItem() {
		return current_item;
	}
	public Bundle saveStackState() {
		Bundle p = new Bundle();
		p.putBundle("ad_state", ad.saveAdapterState());
		current_item.setListPos(lv.getFirstVisiblePosition());
		p.putSerializable("current_item", current_item);
		p.putSerializable("stack", s);
		Log.v(TAG,"saveStackState() - saved stack state");
		return p;
	}

	/**
	 * restore the state saved by saveStackState.
	 * @param p
	 * @throws ClassCastException a bug I haven't yet tracked down in Android
	 * causes the Stack to end up looking like an ArrayList after being cached and restarted.
	 * The "ArrayList" can't be cast 
	 * directly from Serializable to Stack. When this exception is thrown, it's
	 * best to setLevel() and start over.
	 */
	@SuppressWarnings("unchecked")
	public void restoreStackState(Bundle p) throws ClassCastException {
		if (p != null) {
			if (p.containsKey("stack"))
				this.s = (Stack<StackItem>) p.getSerializable("stack");
			if (p.containsKey("current_item"))
				current_item = (StackItem) p.getSerializable("current_item");
			if (p.containsKey("ad_state")) {
				ad.restoreAdapterState(p.getBundle("ad_state"));
				adjustStack();
			}
			else
				reloadData();
			Log.v(TAG,"restoreStackState() - restored stack state");
		}
	}
	@Override
	public boolean onBackPressed() {
		Log.d(TAG,"onBackPressed()");
		if (pop())
			return true;
		return false;
	}
	@Override
	public String getFragmentTag() {
		return fragmentTag;
	}
	public Activity getActivity() {
		return act;
	}
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View v, int pos,
			long id) {
		// pop up rename dialog
		// TODO Auto-generated method stub
		return false;
	}
}
