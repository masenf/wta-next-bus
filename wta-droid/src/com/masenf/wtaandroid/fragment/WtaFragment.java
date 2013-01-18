package com.masenf.wtaandroid.fragment;

import com.masenf.wtaandroid.R;
import com.masenf.wtaandroid.WtaDatastore;
import com.masenf.wtaandroid.Wta_main;

import android.app.ListFragment;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class WtaFragment extends ListFragment implements OnItemClickListener {

	private static final String TAG = "WtaFragment";
	protected static Bundle state;
	String tag = "WtaFragment";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
    	if (state == null)
    		state = new Bundle();
	}
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		// Inflate the layout for this fragment
    	View v = inflater.inflate(R.layout.list_fragment, container, false);
    	
        return v;
    }
	@Override
	public void onResume() {
		super.onResume();
    	getListView().setOnItemClickListener(this);
    	if (state.containsKey(tag + "_list_state")) {
    		getListView().onRestoreInstanceState(state.getParcelable(tag + "_list_state"));
    		Log.v(TAG,"DeSearializing list_state to " + tag + "_list_state");
    	}
	}
    @Override
    public void onPause() {
    	Log.v(TAG,"Searializing list_state to " + tag + "_list_state");
    	state.putParcelable(tag + "_list_state", getListView().onSaveInstanceState());
    	super.onPause();
    }
	@Override
	public void onItemClick(AdapterView<?> adView, View target, int pos, long id) {
		Wta_main a = (Wta_main) getActivity();
		TextView txt_stop_id = (TextView) target.findViewById(R.id.item_stop_id);
		TextView txt_name = (TextView) target.findViewById(R.id.item_location);
		int stop_id = Integer.parseInt(txt_stop_id.getText().toString());
		String name = txt_name.getText().toString();
		a.lookupTimesForStop(stop_id, name);
	}
	public void establishListAdapter(Cursor c) {
        String[] from = new String[]{WtaDatastore.KEY_STOPID, 
        							 WtaDatastore.KEY_NAME};
        int[] to = new int[]{R.id.item_stop_id, R.id.item_location};
        SimpleCursorAdapter items =	
        	new SimpleCursorAdapter(getActivity(), R.layout.location_item, c, from, to);
        setListAdapter(items);
	}

}
