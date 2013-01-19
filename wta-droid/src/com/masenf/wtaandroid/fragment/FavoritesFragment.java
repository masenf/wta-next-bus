package com.masenf.wtaandroid.fragment;

import com.masenf.wtaandroid.WtaDatastore;

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
