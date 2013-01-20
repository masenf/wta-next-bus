package com.masenf.wtaandroid.fragment;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Stack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.masenf.wtaandroid.HierarchyListView;
import com.masenf.wtaandroid.IonBackButtonPressed;
import com.masenf.wtaandroid.JSONRequestTask;
import com.masenf.wtaandroid.LibraryUpdateTask;
import com.masenf.wtaandroid.ProgressCallback;
import com.masenf.wtaandroid.RequestCallback;
import com.masenf.wtaandroid.TaskCallback;
import com.masenf.wtaandroid.WtaDatastore;
import com.masenf.wtaandroid.WtaDatastore.TagEntryType;
import com.masenf.wtaandroid.Wta_main;
import com.masenf.wtaandroid.adapters.NestedTagListAdapter;

public class BrowseFragment extends WtaFragment<HierarchyListView> implements IonBackButtonPressed, 
														   RequestCallback<JSONObject>, 
														   ProgressCallback {

	private static final String TAG = "BrowseFragment";
	public static BrowseFragment cb = null;

	private NestedTagListAdapter ad;
	protected String root_tag = WtaDatastore.TAG_ROOT;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Wta_main a = (Wta_main) getActivity();
		a.registerBackButtonCallback(this);
	}
	@Override
	public void onResume() {
		if (tag.equals("")) {
			tag = TAG;
		}
		if (ad == null) {
			ad = new NestedTagListAdapter(this.getActivity());
		}
		
		HierarchyListView lv = getListView();
		lv.setActivity((Wta_main) getActivity());
		lv.setAdapter(ad);
		lv.setRoot(root_tag);
		
		// restore list view state
		String key = tag + "_stack_state";
		if (state.containsKey(key)) {
			lv.restoreStackState(state.getBundle(key));
			Log.v(TAG,"onResume() - Deserializing stack_state from " + key);
		}
		super.onResume();
		lv.setOnItemClickListener(lv);	// override the default handler
		
		cb = this;		// update the callback so that UI updates make it to the visible Fragment
		
		if (state.getBoolean("update_in_progress",false)) {
			this.startProgress(state.getInt("progress_max", 0));
			progress.setProgress(state.getInt("progress_val", 0));
			updateError("Continuing to unpack library data, please be patient");
		}
		
		SharedPreferences spref = getActivity().getSharedPreferences("global", Context.MODE_PRIVATE);
		if (spref.getBoolean("fetch_library", true)) {
			doFetchData();
		}
	}
	@Override
	public void onPause() {
		Bundle stack_state = ((HierarchyListView) getListView()).saveStackState();
		String key = tag + "_stack_state";
		state.putBundle(key, stack_state);
		Log.v(TAG,"onPause() - Serializing stack_state to " + key);
		if (state.getBoolean("update_in_progress",false)) {
			state.putInt("progress_max", progress.getMax());
			state.putInt("progress_val", progress.getProgress());
		}
		super.onPause();
	}
	@Override
	public boolean onBackPressed() {
		Log.v(TAG,"onBackPressed()");
		if (this.isVisible()) {
			if (((HierarchyListView) getListView()).up())
				return true;
		}
		return false;
	}
    public void doFetchData()
    {
    	String url = new String(Wta_main.wAPI);
		url = new String(url + "library");
		Log.v(TAG,"Query = " + url);
    	try {
			URL u = new URL(url);
			new JSONRequestTask(this).executeOnExecutor(JSONRequestTask.THREAD_POOL_EXECUTOR, u);
		} catch (MalformedURLException e) {
			Log.v(TAG,"Malformed url: " + url);
		}
    }
	@Override
	public void updateData(JSONObject result) {
		Log.v(TAG,"updateDate() - unpacking JSON data, this may take a while");
		SharedPreferences spref = getActivity().getSharedPreferences("global", Context.MODE_PRIVATE);
		Editor e = spref.edit();
		e.putBoolean("fetch_library", false);
		e.commit();
		WtaDatastore d = WtaDatastore.getInstance(getActivity());
		LibraryUpdateTask t = new LibraryUpdateTask(d);
		t.executeOnExecutor(LibraryUpdateTask.THREAD_POOL_EXECUTOR, result);
		state.putBoolean("update_in_progress", true);
		updateError("Unpacking library data on first use, please be patient. This will continue in the background");
	}
	@Override
	public void updateError(String msg) {
    	txt_error.setText(msg);
    	txt_error.setVisibility(View.VISIBLE);	
	}
	@Override
	public void notifyComplete() {
		txt_error.setVisibility(View.GONE);
		state.remove("update_in_progress");
	}
    public void startProgress(Integer max) {
    	startProgress();
    	if (max > 0) {
    		progress.setIndeterminate(false);
    		progress.setMax(max);
    	}
    }
	@Override
	public void onProgress(Integer sofar) {
		progress.setProgress(sofar);
	}
}
