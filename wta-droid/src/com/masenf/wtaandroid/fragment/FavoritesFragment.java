package com.masenf.wtaandroid.fragment;

import com.masenf.wtaandroid.R;
import com.masenf.wtaandroid.WtaDatastore;
import com.masenf.wtaandroid.Wta_main;
import android.app.ListFragment;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class FavoritesFragment extends ListFragment implements OnItemClickListener {

	private int scrollY = 0;	// the scroll position of the view
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		// Inflate the layout for this fragment
    	View v = inflater.inflate(R.layout.list_fragment, container, false);
        if (savedInstanceState != null)
        	scrollY = savedInstanceState.getInt("scrollY",0);
        return v;
    }
	@Override
	public void onResume() {
		super.onResume();
        // establish the cursor
		WtaDatastore d = WtaDatastore.getInstance(getActivity());
		Cursor c = d.getFavorites();
        String[] from = new String[]{WtaDatastore.KEY_STOPID, 
        							 WtaDatastore.KEY_NAME};
        int[] to = new int[]{R.id.item_stop_id, R.id.item_location};
        SimpleCursorAdapter items =	
        	new SimpleCursorAdapter(getActivity(), R.layout.location_item, c, from, to);
        setListAdapter(items);
    	getListView().setOnItemClickListener(this);
    	getListView().setScrollY(scrollY);
    	
	}
    @Override
    public void onSaveInstanceState(Bundle outState) {
    	outState.putInt("scrollY", getListView().getScrollY());
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

}
