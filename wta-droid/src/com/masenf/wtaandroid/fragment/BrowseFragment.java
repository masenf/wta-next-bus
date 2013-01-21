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
import com.masenf.wtaandroid.IGlobalProgress;
import com.masenf.wtaandroid.IonBackButtonPressed;
import com.masenf.wtaandroid.TabNavActivity;
import com.masenf.wtaandroid.WtaActivity;
import com.masenf.wtaandroid.adapters.NestedTagListAdapter;
import com.masenf.wtaandroid.async.JSONRequestTask;
import com.masenf.wtaandroid.async.LibraryUpdateTask;
import com.masenf.wtaandroid.async.ProgressCallback;
import com.masenf.wtaandroid.async.RequestCallback;
import com.masenf.wtaandroid.data.WtaDatastore;
import com.masenf.wtaandroid.data.WtaDatastore.TagEntryType;

public class BrowseFragment extends WtaFragment implements IonBackButtonPressed {
	private static final String TAG = "BrowseFragment";
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
			ad = new NestedTagListAdapter(this.getActivity(), (IGlobalProgress) this.getActivity());
		}
		
		Bundle inState = getInstanceState();
		Bundle gState = getGlobalState();
		
		HierarchyListView lv = (HierarchyListView) getListView();
		lv.setActivity((WtaActivity) getActivity());
		if (inState.containsKey("ad_state")) {
			Log.d(TAG,"onResume() - Deserializing ad_state from ad_state for " + getClass().getName());
			ad.restoreAdapterState(inState.getBundle("ad_state"));
		}
		lv.setAdapter(ad);
		lv.setRoot(root_tag);

		// Restore the hierarchy stack state if it exists
		if (inState.containsKey("stack_state")) {
			Log.d(TAG,"onResume() - Deserializing stack_state from stack_state for " + getClass().getName());
			lv.restoreStackState(inState.getBundle("stack_state"));
		} else {
			lv.setLevel(root_tag);		// navigate to root if no state
		}
		SharedPreferences spref = getActivity().getSharedPreferences("global", Context.MODE_PRIVATE);
		if (spref.getBoolean("fetch_library", true)) {
			doFetchData();
		}
		
		// restore list view state
		super.onResume();
		lv.setOnItemClickListener(lv);	// override the default handler
	}
	@Override
	public void onPause() {
		Log.d(TAG,"onPause() - Serializing stack_state and ad_state for " + root_tag);
		Bundle inState = getInstanceState();
		Bundle stack_state = ((HierarchyListView) getListView()).saveStackState();
		Bundle ad_state =  ad.saveAdapterState();
		inState.putBundle("stack_state", stack_state);
		inState.putBundle("ad_state", ad_state);
		
		if (long_update) {
			getProgressCallback().updateError("Continuing to unpack library data, please be patient");
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
			new JSONRequestTask(new RequestCallback<JSONObject>() {
				@Override
				public void updateData(JSONObject result) {
					Log.d(TAG,"updateDate() - unpacking JSON data, this may take a while");
					SharedPreferences spref = getActivity().getSharedPreferences("global", Context.MODE_PRIVATE);
					Editor e = spref.edit();
					e.putBoolean("fetch_library", false);
					e.commit();
					WtaDatastore d = WtaDatastore.getInstance(getActivity());
					LibraryUpdateTask t = new LibraryUpdateTask(d, (IGlobalProgress) getActivity());
					t.executeOnExecutor(LibraryUpdateTask.THREAD_POOL_EXECUTOR, result);
					getProgressCallback().updateError("Unpacking library data on first use, please be patient. This will continue in the background");
					long_update = true;
				}
			}, (IGlobalProgress) getActivity()).executeOnExecutor(JSONRequestTask.THREAD_POOL_EXECUTOR, u);
		} catch (MalformedURLException e) {
			Log.e(TAG,"Malformed url: " + url);
		}
    }
}
