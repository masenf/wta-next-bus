package com.masenf.wtaandroid.fragment;

import com.masenf.wtaandroid.R;
import com.masenf.wtaandroid.WtaDatastore;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.SimpleCursorAdapter;

public class RecentFragment extends WtaFragment {
	protected String TAG = "RecentFragment";

	@Override
	public void onResume() {
		
        // establish the cursor
		WtaDatastore d = WtaDatastore.getInstance(getActivity());
		Cursor c = d.getRecent();
		establishListAdapter(c);
		super.onResume();
	}
}
