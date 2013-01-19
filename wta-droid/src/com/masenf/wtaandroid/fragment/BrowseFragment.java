package com.masenf.wtaandroid.fragment;

import java.net.MalformedURLException;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.masenf.wtaandroid.IonBackButtonPressed;
import com.masenf.wtaandroid.JSONRequestTask;
import com.masenf.wtaandroid.RequestCallback;
import com.masenf.wtaandroid.WtaDatastore;
import com.masenf.wtaandroid.WtaDatastore.TagEntryType;
import com.masenf.wtaandroid.Wta_main;
import com.masenf.wtaandroid.adapters.HierarchyListAdapter;

public class BrowseFragment extends WtaFragment implements IonBackButtonPressed, RequestCallback {

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
		} else {
			ad.setLevel(ad.getCurrentLevel());		// refresh the data
		}
		getListView().setAdapter(ad);
		super.onResume();
		getListView().setOnItemClickListener(ad);	// override the default handler
		
		SharedPreferences spref = getActivity().getSharedPreferences("global", Context.MODE_PRIVATE);
		if (spref.getBoolean("fetch_library", true)) {
			doFetchData();
		}
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
    public void doFetchData()
    {
    	String url = new String(Wta_main.wAPI);
		url = new String(url + "test");
		Log.v(TAG,"Query = " + url);
    	try {
			URL u = new URL(url);
			new JSONRequestTask(this).execute(u);
		} catch (MalformedURLException e) {
			Log.v(TAG,"Malformed url: " + url);
		}
    }
	@Override
	public void updateData(JSONObject result) {
		Log.v(TAG,"updateDate() - unpacking JSON data, this may take a while");
		startProgress();
		updateError("Unpacking JSON data, please be patient");
		WtaDatastore d = WtaDatastore.getInstance(getActivity());
		try {
			JSONArray tags = result.getJSONArray("tags");
			for (int i = 0; i < tags.length(); i++) {
				String t = tags.getString(i);
				JSONArray stops = result.getJSONArray(t);
				for (int j = 0; j < stops.length(); j++) {
					JSONArray stop = stops.getJSONArray(j);
					int stop_id = stop.getInt(0);
					String name = stop.getString(1);
					String alias = null;
					if (stop.length() > 2)
						alias = stop.getString(2);
					d.addLocation(t, stop_id, name, alias);
				}
				// add new datas to root
				d.setTag(d.getTagId(t), WtaDatastore.TAG_ROOT, TagEntryType.TAG_NAME, 20);
			}
			txt_error.setVisibility(View.GONE);
		} catch (JSONException e) {
			updateError("Error unpacking json file, try again under options");
		}
		stopProgress();
		SharedPreferences spref = getActivity().getSharedPreferences("global", Context.MODE_PRIVATE);
		Editor e = spref.edit();
		e.putBoolean("fetch_library", false);
		e.commit();
	}
	@Override
	public void updateError(String msg) {
		// TODO Auto-generated method stub
		
	}
}
