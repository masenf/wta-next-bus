package com.masenf.wtaandroid.fragment;

import java.net.MalformedURLException;
import java.net.URL;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.masenf.wtaandroid.IGlobalProgress;
import com.masenf.wtaandroid.NestedTagManager;
import com.masenf.wtaandroid.TabNavActivity;
import com.masenf.wtaandroid.WtaActivity;
import com.masenf.wtaandroid.adapters.BaseTaskListAdapter;
import com.masenf.wtaandroid.async.JSONRequestTask;
import com.masenf.wtaandroid.async.LibraryUpdateTask;
import com.masenf.wtaandroid.async.callbacks.RequestCallback;
import com.masenf.wtaandroid.data.WtaDatastore;

public class BrowseFragment extends WtaFragment {
	/* A browse fragment exposes a NestedTagManager and a list view for navigating 
	 * potentially nested tag structures. This fragment contains the glue that 
	 * pulls the necessary components together.
	 * 
	 * It also contains the code for starting a library fetch */
	
	private static final String TAG = "BrowseFragment";
	private boolean long_update = false;

	private NestedTagManager nTm = null;
	private BaseTaskListAdapter ad;
	protected String root_tag = WtaDatastore.TAG_ROOT;

	@Override
	public void onResume() {
		Log.d(TAG,"onResume() - initializing list adapter and restoring values for " + getClass().getName());
		
		// create a new list adapter if we're coming up for the first time
		if (ad == null) {
			Log.v(TAG,"onResume() - ad BaseTaskListAdapter is null, creating new instance");
			ad = new BaseTaskListAdapter(this.getActivity());
		}
		// create a new NestedTagManager if we're coming up for the first time
		if (nTm == null) {
			Log.v(TAG,"onResume() - nTm NestedTagManaer is null, creating new instance for " + root_tag);
			nTm = new NestedTagManager(getActivity(), getListView(), ad, (WtaActivity) getActivity());
			
			// Register Back button callback with parent activity
			TabNavActivity a = (TabNavActivity) getActivity();
			nTm.setFragmentTag(getTag());		// so the dispatcher knows if this fragment is visible
			a.registerBackButtonCallback(nTm);	// we need the back button for folder navigation
		}
		
		// fetch saved state
		Bundle inState = getInstanceState();
		
		ListView lv = getListView();
		lv.setAdapter(ad);
		
		// Restore the stack state if it exists (descended hierarchy + current data)
		if (inState.containsKey("stack_state")) {
			Log.d(TAG,"onResume() - Deserializing stack_state from stack_state for " + getClass().getName());
			nTm.restoreStackState(inState.getBundle("stack_state"));
		} else {
			nTm.setLevel(root_tag);		// navigate to root if no state
		}
		
		// Begin fetching external library on first run of application
		SharedPreferences spref = getActivity().getSharedPreferences("global", Context.MODE_PRIVATE);
		if (spref.getBoolean("fetch_library", true)) {
			doFetchData();
		}
		
		super.onResume();
		lv.setOnItemClickListener(nTm);	// override the default handler
	}
	@Override
	public void onPause() {
		Log.d(TAG,"onPause() - Serializing stack_state and ad_state for " + root_tag);
		Bundle inState = getInstanceState();
		Bundle stack_state = nTm.saveStackState();
		inState.putBundle("stack_state", stack_state);
		
		if (long_update) {
			getProgressCallback().updateError("Continuing to unpack library data, please be patient");
		}
		super.onPause();
	}
    public void doFetchData()
    {
    	// this is a task within a task
    	
		Log.d(TAG,"doFetchData() - fetching library data");
    	String url = new String(WtaActivity.wAPI);
		url = new String(url + "library");
		Log.d(TAG,"doFetchData() - Created URL = " + url);
    	try {
			URL u = new URL(url);
			// fetch the JSON data
			new JSONRequestTask(new RequestCallback<JSONObject>() {
				@Override
				public void updateData(JSONObject result) {
					Log.d(TAG,"updateDate() - unpacking JSON data, this may take a while");
					// update the pref flag
					SharedPreferences spref = getActivity().getSharedPreferences("global", Context.MODE_PRIVATE);
					Editor e = spref.edit();
					e.putBoolean("fetch_library", false);
					e.commit();
					// call a LibraryUpdateTask to deserialize the JSON and commit the data
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
