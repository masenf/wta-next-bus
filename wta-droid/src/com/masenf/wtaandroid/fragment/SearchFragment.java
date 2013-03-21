package com.masenf.wtaandroid.fragment;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONObject;

import com.masenf.core.async.JSONRequestTask;
import com.masenf.core.async.callbacks.RequestCallback;
import com.masenf.wtaandroid.R;
import com.masenf.wtaandroid.WtaActivity;
import com.masenf.wtaandroid.adapters.ResultsListAdapter;
import com.masenf.wtaandroid.data.WtaDatastore;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.TextView;

public class SearchFragment extends WtaFragment implements OnItemClickListener {
	private static final String TAG = "SearchFragment";
	
	private ResultsListAdapter ad = null;
	private EditText search_box = null;
	private WtaDatastore d = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setLayoutId(R.layout.search_fragment);
	}
    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        search_box = (EditText) v.findViewById(R.id.search_box);
        search_box.setOnEditorActionListener(new TextView.OnEditorActionListener() {	
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					doSearch();
					return true;
				}
				return false;
			}
		});
        if (savedInstanceState != null) {
        	search_box.setText(savedInstanceState.getString("search_box_value",""));
        }
    }
    @Override
    public void onResume() {
		Log.d(TAG,"onResume() - initializing list adapter and restoring values");
    	if (ad == null) {
    		Log.d(TAG,"onResume() - ResultsListAdapter ad is null, creating new instance");
    		ad = new ResultsListAdapter(this.getActivity(), "stops");
    	}
        d = WtaDatastore.getInstance(this.getActivity());
        getListView().setAdapter(ad);
        getListView().setOnItemClickListener(this);
    	super.onResume();
    }
    @Override
    public void onPause() {
    	Log.d(TAG,"onPause() - saving search box value");
    	getInstanceState().putString("search_box_value", search_box.getText().toString());
    	super.onPause();
    }
    public void doSearch() {
    	doFetchData(search_box.getText().toString());
    }
    public void doFetchData(String query)
    {
    	Log.d(TAG,"doFetchData() - fetching 'qlocation' data for " + query);
    	String url = getResources().getString(R.string.api_endpoint);
		try {
			url = new String(url + "qlocation?q=" + URLEncoder.encode(query,"UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			Log.e(TAG,"Unsupported Encoding type when URLEncoding query");
			return;
		}
		Log.d(TAG,"doFetchData() - Created URL = " + url);
    	try {
			URL u = new URL(url);
			new JSONRequestTask(new RequestCallback<JSONObject>() {
				@Override
				public void updateData(JSONObject data) {
			    	if (data != null) {
			    		Log.v(TAG,"updateData() - Received JSONObject from thread");
				    	ad.setData(data);
				    	//hide the keyboard now
				    	InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(
				    		      Context.INPUT_METHOD_SERVICE);
				    	imm.hideSoftInputFromWindow(search_box.getWindowToken(), 0);
			    	} else {
			    		Log.d(TAG,"updateData() - Received null object from thread");
			    	}
				}
				
			}).executeOnExecutor(JSONRequestTask.THREAD_POOL_EXECUTOR, u);
		} catch (MalformedURLException e) {
			Log.d(TAG,"Malformed url: " + url);
		}
    }
	@Override
	public void onItemClick(AdapterView<?> adView, View target, int pos, long id) {
		WtaActivity a = (WtaActivity) getActivity();
		TextView txt_stop_id = (TextView) target.findViewById(R.id.item_stop_id);
		TextView txt_name = (TextView) target.findViewById(R.id.item_location);
		int stop_id = Integer.parseInt(txt_stop_id.getText().toString());
		String name = txt_name.getText().toString();
		d.addRecent(stop_id, name);
		a.lookupTimesForStop(stop_id, name);
	}
}