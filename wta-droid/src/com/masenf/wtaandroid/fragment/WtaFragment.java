package com.masenf.wtaandroid.fragment;

import com.masenf.core.fragment.StateSavingFragment;
import com.masenf.wtaandroid.R;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public abstract class WtaFragment extends StateSavingFragment {

	private static final String TAG = "WtaFragment";
	private int layout_id = com.masenf.core.R.layout.list_fragment;
	private ListView lv;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView() - inflating layout id=" + layout_id);
		
		// Inflate the layout for this fragment
    	View v = inflater.inflate(layout_id, container, false);
    	v.setTag(layout_id);
        return v;
    }
	@Override
	public void onViewCreated(View v, Bundle savedInstanceState) {
		super.onViewCreated(v, savedInstanceState);
		lv = (ListView) v.findViewById(android.R.id.list);
		Log.d(TAG, "onViewCreated() - captured ListView, lv = " + lv.toString() + " for " + getClass().getName());
	}
	
	public void setLayoutId(int layout) {
		layout_id = layout;
	}
	public ListView getListView() {
		if (lv == null) {
			Log.v(TAG, "Oh no, ListView is null! " + getClass().getName());
		}
		return lv;
	}
}
