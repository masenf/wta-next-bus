package com.masenf.core.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;

public abstract class StateSavingFragment extends Fragment {

	private static final String TAG = "StateSavingFragment";
	
	private static Bundle globalState;		// globalState is applicable to all TabFragments
	protected Bundle instanceState; 		// instanceState is passed in from onActivityCreated

	public Bundle getInstanceState() {
		if (instanceState == null)
			instanceState = new Bundle();		// prevent null checking everwhere
		return instanceState;
	}
	public Bundle getGlobalState() {
		if (globalState == null)
			globalState = new Bundle();
		return globalState;
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Log.d(TAG, "onCreate() - storing instanceState in class");
		if (savedInstanceState != null) {
			if (savedInstanceState.containsKey("globalState")) {
				Log.v(TAG,"onActivityCreated() - retrieving globalState");
				globalState = savedInstanceState.getBundle("globalState");	
			} else {
				globalState = null;
			}
			if (savedInstanceState.containsKey("instanceState")) {
				Log.v(TAG,"onActivityCreated() - retrieving instanceState");
				instanceState = savedInstanceState.getBundle("instanceState");	
			} else {
				instanceState = null;
			}
		}
	}
	public void onSaveInstanceState(Bundle outState) {
		Log.d(TAG, "onSaveInstanceState() - persist instanceState/globalState");
		Bundle gs = getGlobalState();
		Bundle is = getInstanceState();
		outState.putBundle("globalState", gs);
		outState.putBundle("instanceState", is);
		super.onSaveInstanceState(outState);
	}
}
