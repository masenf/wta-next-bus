package com.masenf.wtaandroid.fragment;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONObject;

import com.masenf.core.async.JSONRequestTask;
import com.masenf.core.async.callbacks.DataReadCallback;
import com.masenf.core.async.callbacks.RequestCallback;
import com.masenf.core.data.EntryList;
import com.masenf.wtaandroid.R;
import com.masenf.wtaandroid.WtaActivity;
import com.masenf.wtaandroid.adapters.TimesListAdapter;
import com.masenf.wtaandroid.async.DataReadTaskFactory;
import com.masenf.wtaandroid.async.DataWriteTaskFactory;
import com.masenf.wtaandroid.data.WtaDatastore;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class NextBusFragment extends WtaFragment {
	
	private static final String TAG = "NextBusFragment";

	private TextView stop_id_label;
	private TextView location_label;
	private int stop_id;
	private String location;
	
	private Button btn_mod_fav;
	private TimesListAdapter ad;
	private WtaDatastore d;
	
	private DataReadTaskFactory checkFavoritesTask;
	private DataWriteTaskFactory updateFavoritesTask;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setLayoutId(R.layout.nextbus_fragment);
		
		// set up a task factory which returns the favorite status
		checkFavoritesTask = new DataReadTaskFactory(new DataReadCallback () {
									@Override
									public void updateData(EntryList result) {
										if (result.size() > 0)
											isFavorite(true);
										else
											isFavorite(false);
									}
								});	// don't update progress for this task
		updateFavoritesTask = new DataWriteTaskFactory(null);
	}
	@Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
		// Get controls from view
        stop_id_label = (TextView) v.findViewById(R.id.stop_id_label);
        location_label = (TextView) v.findViewById(R.id.location_label);
        btn_mod_fav = (Button) v.findViewById(R.id.btn_mod_fav);
    }
	
	@Override
	public void onResume() {
		Log.d(TAG,"onResume() - initializing list adapter and restoring values");
		if (ad == null) {
			Log.v(TAG,"onResume() - TimesListAdapter ad is null, creating new instance");
			ad = new TimesListAdapter(this.getActivity(), "times");
		}
        
        getListView().setAdapter(ad);	// set the list adapter before calling super!
		
		super.onResume();
		
		WtaActivity a = (WtaActivity) getActivity();
		stop_id = a.getSelected_stop();
		location = a.getSelected_location();
		if (a.isReload()) {
			doFetchData(stop_id);
		}
		
		updateStopInfoViews(stop_id, location);
	}

    public void doFetchData(int stop_id)
    {
		Log.d(TAG,"doFetchData() - fetching data for " + stop_id);
    	ad.setData(null);	// blank out the list while loading
    	String url = new String(WtaActivity.wAPI);
		try {
			url = new String(url + "times?stopid=" + URLEncoder.encode(String.valueOf(stop_id),"UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			Log.e(TAG,"Unsupported Encoding type when URLEncoding query");
			return;
		}
		Log.d(TAG,"doFetchData() - Created URL = " + url);
    	try {
			URL u = new URL(url);
			new JSONRequestTask(new RequestCallback<JSONObject>() {

				@Override
				public void updateData(JSONObject result) {
					Log.d(TAG,"updateData() - Received JSONObject from thread");
					ad.setData(result);
					WtaActivity a = (WtaActivity) getActivity();
					if (a != null)
						a.setReload(false);		// don't reload the data next time
				}
			}).executeOnExecutor(JSONRequestTask.THREAD_POOL_EXECUTOR, u);
		} catch (MalformedURLException e) {
			Log.e(TAG,"Malformed url: " + url);
		}
    }
    private void updateStopInfoViews(final int stop_id, final String location) {
		stop_id_label.setText((CharSequence) String.valueOf(stop_id));
		location_label.setText((CharSequence) location);
		checkFavoritesTask.isFavorite(stop_id);
		Log.d(TAG,"updateStopInfoViews() - stop_id_label = " + stop_id +
				  ", location_label = " + location);
    }
    private void isFavorite(boolean fav) {
		String fav_label = "";
		
		if (fav) {
			fav_label = "Remove Favorite";
			btn_mod_fav.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					updateFavoritesTask.rmFavorite(stop_id);
					isFavorite(false);
				}
			});
		} else {
			fav_label = "Add Favorite";
			btn_mod_fav.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					updateFavoritesTask.addFavorite(stop_id, location);
					isFavorite(true);
				}
			});
		}
		btn_mod_fav.setText(fav_label);
		Log.v(TAG,"isFavorite? fav_label = " + fav_label);
    }
}
