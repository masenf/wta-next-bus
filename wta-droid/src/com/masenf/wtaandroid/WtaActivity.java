package com.masenf.wtaandroid;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.masenf.core.GenericTabListener;
import com.masenf.core.TabNavActivity;
import com.masenf.wtaandroid.fragment.AboutDialogFragment;
import com.masenf.wtaandroid.fragment.BrowseFragment;
import com.masenf.wtaandroid.fragment.FavoritesFragment;
import com.masenf.wtaandroid.fragment.NextBusFragment;
import com.masenf.wtaandroid.fragment.SearchFragment;

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
    	if (item.getItemId() == R.id.menu_show_about) {
    		AboutDialogFragment adf = new AboutDialogFragment();
    		adf.show(getFragmentManager(), "aboutBox");
    		return true;
    	}
    	return false;
    }
    public void lookupTimesForStop(final int stop_id, final String location)
    {
    	final Handler h = new Handler();
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
}
