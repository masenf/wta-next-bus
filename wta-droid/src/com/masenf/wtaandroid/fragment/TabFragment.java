package com.masenf.wtaandroid.fragment;

import com.masenf.wtaandroid.R;
import com.masenf.wtaandroid.async.callbacks.ProgressCallback;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

public class TabFragment extends Fragment {

	private static final String TAG = "TabFragment";
	
	private static Bundle globalState;		// globalState is applicable to all TabFragments
	protected Bundle instanceState; 		// instanceState is passed in from onActivityCreated
	
	private ProgressCallback mypg = null;		// the callback for THIS instance of the fragment
	
	private int times_resumed = 0;
	
	private int layout_id;
	private ProgressBar progress;
	private TextView txt_error;
	
	public void setLayoutId(int layout) {
		layout_id = layout;
	}
	public ProgressCallback getProgressCallback() {
		return mypg;
	}
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
		createProgressCallback();
		
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
		times_resumed = 0;
	}
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView() - inflating layout id=" + layout_id);
		
		// Inflate the layout for this fragment
    	View v = inflater.inflate(layout_id, container, false);
    	v.setTag(layout_id);
        return v;
    }
	@Override
	public void onViewCreated(View v, Bundle savedInstanceState) {
		Log.d(TAG, "onViewCreated() - View v=" + v.getTag() + "; storing refs to interface objects");
		
    	progress = (ProgressBar) v.findViewById(R.id.progress_flat);
    	txt_error = (TextView) v.findViewById(R.id.txt_error);	
	}
	@Override
	public void onResume() {
		super.onResume();
		times_resumed += 1;
		Log.d(TAG, "onResume() - called " + times_resumed + " time(s)");
		
		Bundle gs = getGlobalState();
		if (gs.getBoolean("progress_inprogress", false)) {
			Log.d(TAG, "onPause() - restoring progress and error values");
			mypg.startProgress(gs.getInt("progress_max", 0));
			mypg.onProgress(gs.getInt("progress_sofar", 0));
		}
		if (gs.getBoolean("error_displayed", false)) {
			mypg.updateError(gs.getString("error_message"));
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
	public void hideError() {
		Log.d(TAG,"hideError() - hiding error message");
		Bundle gs = getGlobalState();
		gs.putBoolean("error_displayed",false);
		gs.putString("error_message", "");
		txt_error.setVisibility(View.GONE);
	}
	public void createProgressCallback() {
		Log.d(TAG, "createProgressCallback() - generating progress callback for " + getClass().getName());
		mypg = new ProgressCallback() {
			@Override
		    public void startProgress() {
				//Log.v(TAG, "startProgress() - initializing progress_flat");
				progress.setVisibility(View.VISIBLE);
				progress.setEnabled(true);
				progress.setProgress(0);
				progress.setIndeterminate(true);
				Bundle gs = getGlobalState();
				gs.putBoolean("progress_inprogress",true);
				gs.putInt("progress_sofar", 0);
				gs.putInt("progress_max",0);
		    }
			@Override
		    public void startProgress(Integer max) {
		    	startProgress();
				//Log.v(TAG, "startProgress(Integer max) - progress.setMax(" + max + ")");
		    	if (max != null && max > 1) {
		    		progress.setIndeterminate(false);
		    		progress.setMax(max);
		    		getGlobalState().putInt("progress_max", max);
		    	}
		    }
			@Override
			public void onProgress(Integer sofar) {
				if (sofar != null) {
					getGlobalState().putInt("progress_sofar", sofar);
					progress.setProgress(sofar);
				}
			}
			@Override
		    public void stopProgress() {
				//Log.v(TAG, "stopProgress() - disabling progress_flat");
				getGlobalState().putBoolean("progress_inprogress", false);
		    	progress.setEnabled(false);
		    	progress.setVisibility(View.GONE);
		    }
			@Override
			public void updateError(String msg) {
				//Log.v(TAG, "updateError() - setting error message to '" + msg + "'");
				Bundle gs = getGlobalState();
				gs.putBoolean("error_displayed",true);
				gs.putString("error_message", msg);
		    	txt_error.setText(msg);
		    	txt_error.setVisibility(View.VISIBLE);	
			}
			@Override
			public void notifyComplete(boolean success) {
				if (success)
					hideError();
			}
		};
	}
}
