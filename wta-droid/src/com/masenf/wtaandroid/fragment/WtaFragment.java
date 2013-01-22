package com.masenf.wtaandroid.fragment;

import com.masenf.wtaandroid.R;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

public abstract class WtaFragment extends TabFragment {

	protected String TAG = "WtaFragment";
	private ListView lv;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setLayoutId(R.layout.browse_fragment);
	}
	@Override
	public void onViewCreated(View v, Bundle savedInstanceState) {
		super.onViewCreated(v, savedInstanceState);
		Log.d(TAG, "onViewCreated() - capturing ListView, lv for " + getClass().getName());
		lv = (ListView) v.findViewById(android.R.id.list);
	}
	@Override
	public void onResume() {
		super.onResume();
    	Bundle inState = getInstanceState();
    	if (inState.containsKey("list_state")) {
    		lv.onRestoreInstanceState(inState.getParcelable("list_state"));
    		Log.d(TAG,"onResume() - DeSerializing list_state for " + getClass().getName());
    	}
	}
	@Override
	public void onPause() {
		getInstanceState().putParcelable("list_state", lv.onSaveInstanceState());
		Log.d(TAG,"onSaveInstanceState() - Serializing list_state for " + getClass().getName());
		super.onPause();
	}
	
	public ListView getListView() {
		if (lv == null) {
			Log.v(TAG, "Oh no, ListView is null! " + getClass().getName());
		}
		return lv;
	}
}
