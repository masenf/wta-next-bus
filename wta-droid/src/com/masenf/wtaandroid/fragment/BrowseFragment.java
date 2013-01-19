package com.masenf.wtaandroid.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.masenf.wtaandroid.IonBackButtonPressed;
import com.masenf.wtaandroid.R;
import com.masenf.wtaandroid.WtaDatastore;
import com.masenf.wtaandroid.Wta_main;
import com.masenf.wtaandroid.adapters.HierarchyListAdapter;

public class BrowseFragment extends WtaFragment implements IonBackButtonPressed {

	private static final String TAG = "BrowseFragment";
	private HierarchyListAdapter ad;
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
		super.onResume();
		if (ad == null) {
			ad = new HierarchyListAdapter((Context) getActivity(), root_tag){

				@Override
				protected void onLocationClick(int stop_id, String name) {
					Wta_main a = (Wta_main) getActivity();
					a.lookupTimesForStop(stop_id, name);
				}
			
			};
			// restore adapter state
			String key = tag + "_ad_state";
			if (state.containsKey(key)) {
				ad.restoreAdapterState(state.getBundle(key));
				Log.v(TAG,"onResume() - Deserializing ad_state from " + key);
			}
		}
		getListView().setAdapter(ad);
		getListView().setOnItemClickListener(ad);
	}
	@Override
	public void onPause() {
		Bundle ad_state = ad.saveAdapterState();
		String key = tag + "_ad_state";
		state.putBundle(key, ad_state);
		Log.v(TAG,"onPause() - Serializing ad_state to " + key);
		super.onPause();
	}
	@Override
	public boolean onBackPressed() {
		Log.v(TAG,"onBackPressed()");
		if (this.isVisible()) {
			if (ad.up())
				return true;
		}
		return false;
	}
}
