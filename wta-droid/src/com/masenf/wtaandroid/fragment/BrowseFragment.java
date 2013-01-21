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
import com.masenf.wtaandroid.TabNavActivity;
import com.masenf.wtaandroid.WtaActivity;
import com.masenf.wtaandroid.adapters.NestedTagListAdapter;
import com.masenf.wtaandroid.async.JSONRequestTask;
import com.masenf.wtaandroid.async.LibraryUpdateTask;
import com.masenf.wtaandroid.async.ProgressCallback;
import com.masenf.wtaandroid.async.RequestCallback;
import com.masenf.wtaandroid.async.TaskCallback;
import com.masenf.wtaandroid.data.WtaDatastore;
import com.masenf.wtaandroid.data.WtaDatastore.TagEntryType;

public class BrowseFragment extends WtaFragment implements IonBackButtonPressed, 
														   RequestCallback<JSONObject>, 
														   ProgressCallback {
	private static final String TAG = "BrowseFragment";
	public static BrowseFragment cb = null;
	private boolean long_update = false;

	private NestedTagListAdapter ad;
	protected String root_tag = WtaDatastore.TAG_ROOT;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		// Register Back button callback with parent activity
		TabNavActivity a = (TabNavActivity) getActivity();
		a.registerBackButtonCallback(this);		// we need the back button for navigation
	}
	@Override
	public void onResume() {
		Log.d(TAG,"onResume() - initializing list adapter and restoring values for " + root_tag);
		if (ad == null) {
			Log.v(TAG,"onResume() - ad NestedTagListAdapter is null, creating new instance for " + root_tag);
			ad = new NestedTagListAdapter(this.getActivity());
		}
		
		HierarchyListView lv = (HierarchyListView) getListView();
		lv.setActivity((WtaActivity) getActivity());
		lv.setAdapter(ad);
		lv.setRoot(root_tag);
		
		// restore list view state
		super.onResume();
		lv.setOnItemClickListener(lv);	// override the default handler
		
		cb = this;		// update the callback so that UI updates make it to the visible Fragment

		Bundle inState = getInstanceState();
		Bundle gState = getGlobalState();
		// Restore the hierarchy stack state if it exists
		if (inState.containsKey("stack_state")) {
			Log.d(TAG,"onResume() - Deserializing stack_state from stack_state for " + root_tag);
			lv.restoreStackState(inState.getBundle("stack_state"));
		}
		if (gState.getBoolean("progress_inprogress", false)) {
			cb = this;
		}
		SharedPreferences spref = getActivity().getSharedPreferences("global", Context.MODE_PRIVATE);
		if (spref.getBoolean("fetch_library", true)) {
			doFetchData();
		}
	}
	@Override
	public void onPause() {
		Log.d(TAG,"onPause() - Serializing stack_state for " + root_tag);
		Bundle stack_state = ((HierarchyListView) getListView()).saveStackState();
		getInstanceState().putBundle("stack_state", stack_state);
		
		if (long_update) {
			updateError("Continuing to unpack library data, please be patient");
		}
		super.onPause();
	}
	@Override
	public boolean onBackPressed() {
		Log.d(TAG,"onBackPressed()");
		if (this.isVisible()) {
			if (((HierarchyListView) getListView()).up())
				return true;
		}
		return false;
	}
    public void doFetchData()
    {
		Log.d(TAG,"doFetchData() - fetching library data");
    	String url = new String(WtaActivity.wAPI);
		url = new String(url + "library");
		Log.d(TAG,"doFetchData() - Created URL = " + url);
    	try {
			URL u = new URL(url);
			new JSONRequestTask(this).executeOnExecutor(JSONRequestTask.THREAD_POOL_EXECUTOR, u);
		} catch (MalformedURLException e) {
			Log.e(TAG,"Malformed url: " + url);
		}
    }
	@Override
	public void updateData(JSONObject result) {
		Log.d(TAG,"updateDate() - unpacking JSON data, this may take a while");
		SharedPreferences spref = getActivity().getSharedPreferences("global", Context.MODE_PRIVATE);
		Editor e = spref.edit();
		e.putBoolean("fetch_library", false);
		e.commit();
		WtaDatastore d = WtaDatastore.getInstance(getActivity());
		LibraryUpdateTask t = new LibraryUpdateTask(d);
		t.executeOnExecutor(LibraryUpdateTask.THREAD_POOL_EXECUTOR, result);
		updateError("Unpacking library data on first use, please be patient. This will continue in the background");
		long_update = true;
	}
	@Override
	public void notifyComplete() {
		hideError();
	}
}
