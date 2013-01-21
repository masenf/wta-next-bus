package com.masenf.wtaandroid;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;

public class TabNavActivity extends Activity {

	private static final String TAG = "TabNavActivity";
	protected int sel_tab = 0;
	protected int prev_tab = -1;
	private ArrayList<IonBackButtonPressed> backButtonCallbacks;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate() - TabNavActivity created");
		if (savedInstanceState != null) {
			sel_tab = savedInstanceState.getInt("active_tab_idx", 0);
			prev_tab = savedInstanceState.getInt("prev_tab", -1);
		}
	}
	public void registerBackButtonCallback(IonBackButtonPressed cb) {
		if (backButtonCallbacks == null) {
			backButtonCallbacks = new ArrayList<IonBackButtonPressed>();
		}
		backButtonCallbacks.add(cb);
	}
	
    @Override
    public void onBackPressed() {
    	if (backButtonCallbacks != null) {
	    	for (IonBackButtonPressed cb : backButtonCallbacks) {
	    		if (cb.onBackPressed())
	    			return;
	    	}
    	}
    	if (prev_tab == -1) {
    		super.onBackPressed();
    	} else {
    		getActionBar().setSelectedNavigationItem(prev_tab);
    		prev_tab = -1;
    	}
    }	
    @Override
    public void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
		Log.d(TAG, "onSaveInstanceState() - writing active tab state");
    	outState.putInt("active_tab_idx", getActionBar().getSelectedNavigationIndex());
    	outState.putInt("prev_tab", prev_tab);
    }
}
