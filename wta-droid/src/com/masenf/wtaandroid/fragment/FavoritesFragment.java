package com.masenf.wtaandroid.fragment;

import com.masenf.wtaandroid.WtaDatastore;
import android.database.Cursor;
import android.os.Bundle;

public class FavoritesFragment extends WtaFragment {
	
	@Override
	public void onResume() {
        // establish the cursor
		WtaDatastore d = WtaDatastore.getInstance(getActivity());
		Cursor c = d.getFavorites();
		establishListAdapter(c);
    	
		super.onResume();
	}
}
