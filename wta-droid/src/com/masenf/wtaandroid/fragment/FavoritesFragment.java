package com.masenf.wtaandroid.fragment;

import com.masenf.wtaandroid.data.WtaDatastore;

public class FavoritesFragment extends BrowseFragment {
	@SuppressWarnings("unused")
	private static String TAG = "FavoritesFragment";
	
	@Override
	public void onResume() {
		root_tag = WtaDatastore.TAG_FAVORITES;
		super.onResume();
		nTm.reset();
		nTm.setLevel(root_tag);		// always reload the data
	}
}
