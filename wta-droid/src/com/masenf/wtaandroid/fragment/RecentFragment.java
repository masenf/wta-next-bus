package com.masenf.wtaandroid.fragment;

import com.masenf.wtaandroid.WtaDatastore;
import android.database.Cursor;
import android.os.Bundle;

public class RecentFragment extends WtaFragment {
	private static final String TAG = "RecentFragment";

	@Override
	public void onResume() {
		tag = "RecentFragment";
		
        // establish the cursor
		WtaDatastore d = WtaDatastore.getInstance(getActivity());
		Cursor c = d.getRecent();
		establishListAdapter(c);
		super.onResume();
	}

}
