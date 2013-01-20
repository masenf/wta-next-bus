package com.masenf.wtaandroid.fragment;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONObject;

import com.masenf.wtaandroid.JSONRequestTask;
import com.masenf.wtaandroid.R;
import com.masenf.wtaandroid.RequestCallback;
import com.masenf.wtaandroid.WtaDatastore;
import com.masenf.wtaandroid.Wta_main;
import com.masenf.wtaandroid.adapters.TimesListAdapter;

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

public class NextBusFragment extends ListFragment implements RequestCallback<JSONObject> {
	
	private static final String TAG = "NextBusFragment";

	private ProgressBar progress;
	private TextView stop_id_label;
	private TextView location_label;
	private TextView txt_error;
	private Button btn_mod_fav;
	private TimesListAdapter ad;
	private WtaDatastore d;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		// Inflate the layout for this fragment
    	View v = inflater.inflate(R.layout.nextbus_fragment, container, false);
        progress = (ProgressBar) v.findViewById(R.id.progress_flat);
        stop_id_label = (TextView) v.findViewById(R.id.stop_id_label);
        location_label = (TextView) v.findViewById(R.id.location_label);
        btn_mod_fav = (Button) v.findViewById(R.id.btn_mod_fav);
        txt_error = (TextView) v.findViewById(R.id.txt_error);
    	
        ad = new TimesListAdapter(this.getActivity(), "times");
        
        d = WtaDatastore.getInstance(getActivity());
        
        return v;
    }
	
	@Override
	public void onResume() {
        this.setListAdapter(ad);	// set the list adapter before calling super!
		
		super.onResume();
        
        txt_error.setVisibility(View.GONE);
		
		Wta_main a = (Wta_main) getActivity();
		final int stop_id = a.getSelected_stop();
		final String location = a.getSelected_location();
		
		if (a.isReload()) {
			doFetchData(stop_id);
		}
		stop_id_label.setText((CharSequence) String.valueOf(stop_id));
		location_label.setText((CharSequence) location);
		
		updateFavoriteButton(stop_id, location);
	}

    public void startProgress() {
    	if (progress.getVisibility() != View.VISIBLE)
      	{
  			progress.setVisibility(View.VISIBLE);
  			progress.setEnabled(true);
  			progress.setProgress(0);
  			progress.setIndeterminate(true);
      	}
      }
    public void stopProgress() {
    	if (progress.getVisibility() != View.GONE)
      	{
      		progress.setEnabled(false);
      		progress.setVisibility(View.GONE);
      	}
    }
    public void doFetchData(int stop_id)
    {
    	ad.setData(null);	// blank out the list while loading
    	String url = new String(Wta_main.wAPI);
		try {
			url = new String(url + "times?stopid=" + URLEncoder.encode(String.valueOf(stop_id),"UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			Log.v(TAG,"Unsupported Encoding type when URLEncoding query");
			return;
		}
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
		ad.setData(result);
		Wta_main a = (Wta_main) getActivity();
		if (a != null)
			a.setReload(false);		// don't reload the data next time
	}
    @Override
    public void updateError(String message) {
    	txt_error.setText(message);
    	txt_error.setVisibility(View.VISIBLE);
    }
    private void updateFavoriteButton(final int stop_id, final String location) {
		if (d.isFavorite(stop_id)) {
			btn_mod_fav.setText("Remove Favorite");
			btn_mod_fav.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					d.rmFavorite(stop_id);
					updateFavoriteButton(stop_id, location);
				}
			});
		} else {
			btn_mod_fav.setText("Add Favorite");
			btn_mod_fav.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					d.addFavorite(stop_id, location);
					updateFavoriteButton(stop_id, location);
				}
			});
		}
    }

	@Override
	public void notifyComplete() {		
	}

}
