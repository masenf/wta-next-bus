package com.masenf.wtaandroid;

import java.util.ArrayList;

import com.masenf.wtaandroid.progress.IProgressManager;
import com.masenf.wtaandroid.progress.ProgressCallback;
import com.masenf.wtaandroid.progress.ProgressFragment;
import com.masenf.wtaandroid.progress.ProgressListAdapter;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;

public class TabNavActivity extends Activity implements IProgressManager{

	private static final String TAG = "TabNavActivity";
	protected int sel_tab = 0;
	protected int prev_tab = -1;
	private ProgressListAdapter progressAdapter;
	private ArrayList<IonBackButtonPressed> backButtonCallbacks;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate() - TabNavActivity created");
		this.setContentView(R.layout.tabnav_activity);
		
		if (savedInstanceState != null) {
			sel_tab = savedInstanceState.getInt("active_tab_idx", 0);
			prev_tab = savedInstanceState.getInt("prev_tab", -1);
		}

		progressAdapter = new ProgressListAdapter(this);
		if (savedInstanceState != null && savedInstanceState.containsKey("ad_state")) {
			progressAdapter.restoreAdapterState(savedInstanceState.getBundle("ad_state"));
		}
		
	}
	@Override
	public void onStart() {
		super.onStart();
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		if (fm.findFragmentByTag("GlobalProgress") == null) {
			ft.add(R.id.progress_fragment_placeholder, new ProgressFragment(progressAdapter), "GlobalProgress");
		} else {
			ft.attach(fm.findFragmentByTag("GlobalProgress"));
		}
		ft.commit();
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
    		FragmentManager fm = getFragmentManager();
	    	for (IonBackButtonPressed cb : backButtonCallbacks) {
	    		Fragment thisF = fm.findFragmentByTag(cb.getFragmentTag());
	    		if (thisF != null && thisF.isVisible()) {
	    			if (cb.onBackPressed())
	    				return;
	    		}
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
    	outState.putBundle("ad_state", progressAdapter.saveAdapterState());
    }
	@Override
	public ProgressCallback getProgressCallback(String tag) {
		return progressAdapter.getCallbackByTag(tag);
	}
	@Override
	public ProgressCallback createProgressCallback(String tag) {
		return progressAdapter.newProgress(tag);
	}
}
