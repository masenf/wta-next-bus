package com.masenf.wtaandroid;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.masenf.core.ApplicationUpdater;
import com.masenf.core.GenericTabListener;
import com.masenf.core.TabNavActivity;
import com.masenf.wtaandroid.async.DataWriteTaskFactory;
import com.masenf.wtaandroid.data.WtaDatastore;
import com.masenf.wtaandroid.fragment.BrowseFragment;
import com.masenf.wtaandroid.fragment.FavoritesFragment;
import com.masenf.wtaandroid.fragment.NextBusFragment;
import com.masenf.wtaandroid.fragment.SearchFragment;
import com.masenf.wtaandroid.fragment.dialog.AboutDialogFragment;

public class WtaActivity extends TabNavActivity {

	private static final String TAG = "WtaActivity";
	
	private Tab favorites;
	private Tab search;
	private Tab nextbus;
	private Tab browse;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // initialize the database!
        WtaDatastore.initialize(this);
        
    	Log.d(TAG,"onCreate() - creating actionbar tabs");
        ActionBar ab = getActionBar();
        ab.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        
        favorites = ab.newTab();
        favorites.setTabListener(new GenericTabListener<FavoritesFragment>(this, FavoritesFragment.class));
        favorites.setText("Favorites");
        favorites.setTag(FavoritesFragment.class.getName());
        ab.addTab(favorites);
        
        browse = ab.newTab();
        browse.setTabListener(new GenericTabListener<BrowseFragment>(this, BrowseFragment.class));
        browse.setText("Browse");
        browse.setTag(BrowseFragment.class.getName());
        ab.addTab(browse);
        
        search = ab.newTab();
        search.setTabListener(new GenericTabListener<SearchFragment>(this, SearchFragment.class));
        search.setText("Search");
        search.setTag(SearchFragment.class.getName());
        ab.addTab(search);
        
        nextbus = ab.newTab();
        nextbus.setTabListener(new GenericTabListener<NextBusFragment>(this, NextBusFragment.class));
        nextbus.setText("Next Bus");
        nextbus.setTag(NextBusFragment.class.getName());
        ab.addTab(nextbus);
        
        ab.setSelectedNavigationItem(sel_tab);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
    	MenuInflater mi = getMenuInflater();
    	mi.inflate(R.menu.global_menu, menu);
		return true;
    }
    @Override
    public boolean onOptionsItemSelected (MenuItem item)
    {
    	switch (item.getItemId()) {
    	case R.id.menu_show_about:
    		AboutDialogFragment adf = new AboutDialogFragment();
    		adf.show(getFragmentManager(), "aboutBox");
    		break;
    	case R.id.menu_reload_library:
    		promptToReload();
    		break;
    	case R.id.menu_check_updates:
    		checkAndDoUpdate();
    		break;
    	default:
    		return false;
    	}
    	return true;
    }
    public void lookupTimesForStop(final int stop_id, final String location)
    {
    	final Handler h = new Handler();

    	DataWriteTaskFactory dwtf = new DataWriteTaskFactory(null);
    	dwtf.addRecent(stop_id, location);
    	prev_tab = getActionBar().getSelectedNavigationIndex();
    	getActionBar().selectTab(nextbus);
    	
    	// the fragment may not immediately be available, so we'll try to 
    	// get it periodically until we succeed. A run limit is set to 
    	// prevent runaway recursion, if the fragment does not exist or can't be loaded.
    	Runnable proxyLookup = (new Runnable() {
    		final int retry_delay_msec = 50;
    		final int RUN_LIMIT = 10;
    		int i = 0;
    		
			@Override
			public void run() {
				i++;
		    	FragmentManager fm = getFragmentManager();
		    	NextBusFragment nbf = (NextBusFragment) fm.findFragmentByTag(NextBusFragment.class.getName());
		    	if (nbf != null)
		    		nbf.lookupTimesForStop(stop_id, location);
		    	else if (i<RUN_LIMIT)
		    		h.postDelayed(this, retry_delay_msec);	// try again after delay
			}});

    	h.postDelayed(proxyLookup, 20);
    }
    public void lookupTimesForStop(int stop_id)
    {
    	lookupTimesForStop(stop_id, "");
    }
    private void checkAndDoUpdate()
    {
		final Context ctx = this;
		String updateURL = getResources().getString(R.string.api_endpoint) + "latest-client";
		ApplicationUpdater au = new ApplicationUpdater(updateURL, this){
			public void postFetch() {
				if (!updateAvailable()) {
					Toast.makeText(ctx, R.string.no_updates, Toast.LENGTH_SHORT).show();
					return;
				}
				AlertDialog.Builder bob = new AlertDialog.Builder(ctx);
				bob.setTitle(R.string.update_title);
				bob.setMessage(String.format(getResources().getString(R.string.update_message),
						getLatestVersionCode(), getApkURL().toString()));
				bob.setPositiveButton(R.string.proceed, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						downloadUpdate();
					}
				});
				bob.setNegativeButton(R.string.dismiss, null);
				bob.create().show();
			}
			public void postDownload() {
				installUpdate();
			}
		};

		au.fetchUpdateData();
    }
    private void promptToReload()
    {
		AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setTitle("Rebuild Library from Server");
		b.setMessage("This operation will update the Browse library with the latest version from the server. " +
				"Any aliases will be retained, renamed library tags will be duplicated. To reset these customizations, " +
				"cancel this dialog, open Settings > Application manager and \"Clear Data\" for the application. " +
				"The library will be automatically rebuilt the next time the application starts");
		b.setPositiveButton("Proceed", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// update the pref flag
				SharedPreferences spref = getSharedPreferences("global", Context.MODE_PRIVATE);
				Editor ed = spref.edit();
				ed.putBoolean("fetch_library", true);
				ed.commit();
				getActionBar().selectTab(favorites);
				getActionBar().selectTab(browse);
			}
		});
		b.setNegativeButton(R.string.dismiss, null);
		b.create().show();
    }
}
