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
	private static HierarchyListAdapter ad;
	private String root_tag = WtaDatastore.TAG_ROOT;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Wta_main a = (Wta_main) getActivity();
		a.registerBackButtonCallback(this);
	}
	@Override
	public void onResume() {
		super.onResume();
		if (ad == null) {
			ad = new HierarchyListAdapter((Context) getActivity(), root_tag){

				@Override
				protected void onLocationClick(int stop_id, String name) {
					Wta_main a = (Wta_main) getActivity();
					a.lookupTimesForStop(stop_id, name);
				}
			
			};
		}
		getListView().setAdapter(ad);
		getListView().setOnItemClickListener(ad);
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
