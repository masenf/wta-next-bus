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
import com.masenf.wtaandroid.adapters.ResultsListAdapter;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SearchFragment extends WtaFragment implements RequestCallback {
	private static final String TAG = "SearchFragment";
	
	private ResultsListAdapter ad = null;
	private ProgressBar progress = null;
	private EditText search_box = null;
	private TextView txt_error = null;
	private WtaDatastore d = null;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
    	View v = inflater.inflate(R.layout.search_fragment, container, false);
        progress = (ProgressBar) v.findViewById(R.id.search_progress);
    	
        ad = new ResultsListAdapter(this.getActivity(), "stops");
        
        d = WtaDatastore.getInstance(this.getActivity());
        
        txt_error = (TextView) v.findViewById(R.id.txt_error);
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
        return v;
    }
    @Override
    public void onResume() {
		tag = "SearchFragment";
        this.setListAdapter(ad);
    	super.onResume();
    	txt_error.setVisibility(View.GONE);
    	search_box.setText((CharSequence) state.getString("search_box_value",""));
    }
    @Override
    public void onPause() {
    	state.putString("search_box_value", search_box.getText().toString());
    	super.onPause();
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
    public void doSearch() {
    	doFetchData(search_box.getText().toString());
    }
    public void doFetchData(String query)
    {
    	String url = new String(Wta_main.wAPI);
		try {
			url = new String(url + "location?q=" + URLEncoder.encode(query,"UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			Log.v(TAG,"Unsupported Encoding type when URLEncoding query");
			return;
		}
		Log.v(TAG,"Query = " + url);
    	try {
			URL u = new URL(url);
			new JSONRequestTask(this).execute(u);
		} catch (MalformedURLException e) {
			Log.v(TAG,"Malformed url: " + url);
		}
    }
    public void updateData(JSONObject data)
    {
    	if (data != null) {
	    	ad.setData(data);
	    	//hide the keyboard now
	    	InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(
	    		      Context.INPUT_METHOD_SERVICE);
	    	imm.hideSoftInputFromWindow(search_box.getWindowToken(), 0);
    	}
    }
    @Override
    public void updateError(String message) {
    	txt_error.setText(message);
    	txt_error.setVisibility(View.VISIBLE);
    }
	@Override
	public void onItemClick(AdapterView<?> adView, View target, int pos, long id) {
		Wta_main a = (Wta_main) getActivity();
		TextView txt_stop_id = (TextView) target.findViewById(R.id.item_stop_id);
		TextView txt_name = (TextView) target.findViewById(R.id.item_location);
		int stop_id = Integer.parseInt(txt_stop_id.getText().toString());
		String name = txt_name.getText().toString();
		d.addRecent(stop_id, name);
		a.lookupTimesForStop(stop_id, name);
	}
}