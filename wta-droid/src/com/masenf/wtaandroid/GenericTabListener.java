package com.masenf.wtaandroid;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.content.Context;

public class GenericTabListener<T extends Fragment> implements TabListener {

	private Fragment mFragment;
	private final Context mContext;
	private final String mTag;
	private final Class<T> mClass;
	
	public GenericTabListener(Context ctx, String tag, Class<T> frag_clz) {
		mContext = ctx;
		mTag = tag;
		mClass = frag_clz;
	}
	
	@Override
	public void onTabReselected(Tab t, FragmentTransaction ft) {

	}

	@Override
	public void onTabSelected(Tab t, FragmentTransaction ft) {
		if (mFragment == null) {
			mFragment = Fragment.instantiate(mContext, mClass.getName());
			ft.add(android.R.id.content, mFragment, mTag);
		} else {
			ft.attach(mFragment);
		}
	}

	@Override
	public void onTabUnselected(Tab t, FragmentTransaction ft) {
		ft.detach(mFragment);
	}

}
