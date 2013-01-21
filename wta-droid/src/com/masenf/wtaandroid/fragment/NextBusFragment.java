package com.masenf.wtaandroid.fragment;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONObject;

import com.masenf.wtaandroid.IGlobalProgress;
import com.masenf.wtaandroid.R;
import com.masenf.wtaandroid.WtaActivity;
import com.masenf.wtaandroid.adapters.TimesListAdapter;
import com.masenf.wtaandroid.async.JSONRequestTask;
import com.masenf.wtaandroid.async.RequestCallback;
import com.masenf.wtaandroid.data.WtaDatastore;

import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class NextBusFragment extends WtaFragment {
	
	private static final String TAG = "NextBusFragment";

	private TextView stop_id_label;
	private TextView location_label;
	
	private Button btn_mod_fav;
	private TimesListAdapter ad;
	private WtaDatastore d;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setLayoutId(R.layout.nextbus_fragment);
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
        d = WtaDatastore.getInstance(getActivity());
        
        getListView().setAdapter(ad);	// set the list adapter before calling super!
		
		super.onResume();
		
		WtaActivity a = (WtaActivity) getActivity();
		final int stop_id = a.getSelected_stop();
		final String location = a.getSelected_location();
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
			}, (IGlobalProgress) getActivity()).executeOnExecutor(JSONRequestTask.THREAD_POOL_EXECUTOR, u);
		} catch (MalformedURLException e) {
			Log.e(TAG,"Malformed url: " + url);
		}
    }
    private void updateStopInfoViews(final int stop_id, final String location) {
		String fav_label = "";
		stop_id_label.setText((CharSequence) String.valueOf(stop_id));
		location_label.setText((CharSequence) location);
		
		if (d.isFavorite(stop_id)) {
			fav_label = "Remove Favorite";
			btn_mod_fav.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					d.rmFavorite(stop_id);
					updateStopInfoViews(stop_id, location);
				}
			});
		} else {
			fav_label = "Add Favorite";
			btn_mod_fav.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					d.addFavorite(stop_id, location);
					updateStopInfoViews(stop_id, location);
				}
			});
		}
		btn_mod_fav.setText(fav_label);
		
		Log.d(TAG,"updateStopInfoViews() - stop_id_label = " + stop_id +
				  ", location_label = " + location + ", fav_label = " + fav_label);
    }
}
