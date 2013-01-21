package com.masenf.wtaandroid.fragment;

import com.masenf.wtaandroid.data.WtaDatastore;

public class FavoritesFragment extends BrowseFragment {
	private static String TAG = "FavoritesFragment";
	
	@Override
	public void onResume() {
		root_tag = WtaDatastore.TAG_FAVORITES;
		super.onResume();
	}
}
