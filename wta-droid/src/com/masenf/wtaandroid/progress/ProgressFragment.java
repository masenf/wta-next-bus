package com.masenf.wtaandroid.progress;

import com.masenf.wtaandroid.R;
import com.masenf.wtaandroid.adapters.TagListAdapter;
import com.masenf.wtaandroid.fragment.StateSavingFragment;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class ProgressFragment extends StateSavingFragment {
	private static final String TAG = "ProgressFragment";
	private ProgressListAdapter ad;
	private ListView lv;
	
	public ProgressFragment() {
		super();
	}
	public ProgressFragment (ProgressListAdapter ad) {
		super();
		this.ad = ad;
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		Log.v(TAG, "onCreateView() - inflating layout");
		// Inflate the layout for this fragment
    	View v = inflater.inflate(R.layout.list_fragment, container, false);
    	lv = (ListView) v.findViewById(android.R.id.list);
        return v;
    }
	
	@Override
	public void onResume() {
		// don't continue resuming if we don't have an adapter
		if (ad == null) {
			Log.v(TAG,"onResume() - adapter is null, this isn't going to work");
			return;
		}
		super.onResume();
		lv.setAdapter(ad);
	}
}
