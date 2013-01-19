package com.masenf.wtaandroid.fragment;

import com.masenf.wtaandroid.WtaDatastore;
import com.masenf.wtaandroid.adapters.HierarchyListAdapter;

import android.database.Cursor;
import android.os.Bundle;

public class FavoritesFragment extends BrowseFragment {
	private static String TAG = "FavoritesFragment";
	
	@Override
	public void onResume() {
		if (tag.equals("")) {
			tag = TAG;
		}
		root_tag = WtaDatastore.TAG_FAVORITES;
		super.onResume();
	}
}
