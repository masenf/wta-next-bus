package com.masenf.wtaandroid.fragment;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.masenf.core.async.JSONRequestTask;
import com.masenf.core.async.callbacks.DataReadCallback;
import com.masenf.core.async.callbacks.RequestCallback;
import com.masenf.core.data.EntryList;
import com.masenf.wtaandroid.R;
import com.masenf.wtaandroid.WtaActivity;
import com.masenf.wtaandroid.adapters.TimesListAdapter;
import com.masenf.wtaandroid.async.DataReadTaskFactory;
import com.masenf.wtaandroid.data.WtaDatastore;

public class NextBusFragment extends WtaFragment {
	
	private static final String TAG = "NextBusFragment";

	private TextView stop_id_label;
	private TextView location_label;
	private boolean refresh = false;		// set to true when a refresh is in progress
	private boolean favorite = false;
	
	private int stop_id;
	private String location;
	private String timesData;
	
	private MenuItem menu_favorite_toggle;
	private TimesListAdapter ad;
	private WtaDatastore d;
	
	private DataReadTaskFactory favoritesTasks;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setLayoutId(R.layout.nextbus_fragment);
		
		// set up a task factory which returns the favorite status
		favoritesTasks = new DataReadTaskFactory(WtaDatastore.getInstance(getActivity()),
								new DataReadCallback () {
									@Override
									public void updateData(EntryList result) {
										if (result.size() > 0)
											isFavorite(true);
										else
											isFavorite(false);
									}
								});	// don't update progress for this task
		this.setHasOptionsMenu(true);
	}
	@Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
		// Get controls from view
        stop_id_label = (TextView) v.findViewById(R.id.stop_id_label);
        location_label = (TextView) v.findViewById(R.id.location_label);
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
		
		// load the last location, if there was one
		Bundle inState = getInstanceState();
		if (inState.containsKey("stop_id")) {
			stop_id = inState.getInt("stop_id");
			location = inState.getString("location","");
			if (inState.containsKey("timesData")) {
				timesData = inState.getString("timesData");
				try {
					ad.setData(new JSONObject(timesData));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		updateStopInfoViews(this.stop_id, this.location);
	}
	@Override
	public void onPause() {
		Log.d(TAG,"onPause() - Saving current stop details");
		Bundle inState = getInstanceState();
		inState.putInt("stop_id", stop_id);
		inState.putString("location", location);
		if (timesData != null)
			inState.putString("timesData", timesData);

		super.onPause();
	}
	public void lookupTimesForStop(int stop_id, String location)
	{
		getInstanceState().remove("stop_id");		// prevent onResume() from loading cached data
		this.stop_id = stop_id;
		this.location = location;
		
		doFetchData(this.stop_id);
		
		updateStopInfoViews(this.stop_id, this.location);
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
					timesData = result.toString();
				}
			}){
				@Override
				protected void onPostExecute(JSONObject result)
				{
					super.onPostExecute(result);
					refresh = false;	// mark that the refresh is complete
				}
			}.executeOnExecutor(JSONRequestTask.THREAD_POOL_EXECUTOR, u);
			refresh = true;
		} catch (MalformedURLException e) {
			Log.e(TAG,"Malformed url: " + url);
		}
    }
    private void updateStopInfoViews(final int stop_id, final String location) {
		stop_id_label.setText((CharSequence) String.valueOf(stop_id));
		location_label.setText((CharSequence) location);
		favoritesTasks.isFavorite(stop_id);
		Log.d(TAG,"updateStopInfoViews() - stop_id_label = " + stop_id +
				  ", location_label = " + location);
    }
    private void isFavorite(boolean fav) {
		if (fav) {
			favorite = true;
			menu_favorite_toggle.setIcon(R.drawable.rm_favorite);
		} else {
			favorite = false;
			menu_favorite_toggle.setIcon(R.drawable.add_favorite);
		}
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
    	Log.v(TAG,"onCreateOptionsMenu() adding actionBar items");
        inflater.inflate(R.menu.nextbus_menu, menu);
        menu_favorite_toggle = menu.findItem(R.id.menu_favorite_toggle);
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected (MenuItem item)
    {
    	if (item.getItemId() == R.id.menu_refresh) {
    		if (!refresh)
    			doFetchData(stop_id);
    		return true;
    	} else if (item.getItemId() == R.id.menu_favorite_toggle) {
    		if (favorite) {
				d.rmFavorite(stop_id);
				isFavorite(false);
    		} else {
				d.addFavorite(stop_id, location);
				isFavorite(true);
    		}
    	}
    	return false;
    }
}
