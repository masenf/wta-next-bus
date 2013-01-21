package com.masenf.wtaandroid.fragment;

import com.masenf.wtaandroid.R;
import com.masenf.wtaandroid.WtaActivity;
import com.masenf.wtaandroid.data.WtaDatastore;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public abstract class WtaFragment extends TabFragment implements OnItemClickListener {

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
    	lv.setOnItemClickListener(this);
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
	@Override
	public void onItemClick(AdapterView<?> adView, View target, int pos, long id) {
		WtaActivity a = (WtaActivity) getActivity();
		TextView txt_stop_id = (TextView) target.findViewById(R.id.item_stop_id);
		TextView txt_name = (TextView) target.findViewById(R.id.item_location);
		int stop_id = Integer.parseInt(txt_stop_id.getText().toString());
		String name = txt_name.getText().toString();
		a.lookupTimesForStop(stop_id, name);
	}
	
	public ListView getListView() {
		if (lv == null) {
			Log.v(TAG, "Oh no, ListView is null! " + getClass().getName());
		}
		return lv;
	}
}
