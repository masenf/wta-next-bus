package com.masenf.wtaandroid;

import java.util.ArrayList;

import com.masenf.wtaandroid.data.WtaDatastore;
import com.masenf.wtaandroid.fragment.FavoritesFragment;
import com.masenf.wtaandroid.fragment.NextBusFragment;
import com.masenf.wtaandroid.fragment.SearchFragment;
import com.masenf.wtaandroid.fragment.BrowseFragment;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

public class WtaActivity extends TabNavActivity {

	private static final String TAG = "WtaActivity";
	public static final String wAPI = "http://mashed-potatoes.with-linux.com:8080/";
	
	private int selected_stop;
	private String selected_location;
	private boolean reload = false;
	
	private Tab favorites;
	private Tab search;
	private Tab nextbus;
	private Tab browse;
	
	public int getSelected_stop() {
		return selected_stop;
	}
	public String getSelected_location() {
		return selected_location;
	}
	public boolean isReload() {
		return reload;
	}
    public void setReload(boolean reload)
    {
    	this.reload = reload;
    }
	
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
        favorites.setTag("Favorites");
        ab.addTab(favorites);
        
        browse = ab.newTab();
        browse.setTabListener(new GenericTabListener<BrowseFragment>(this, BrowseFragment.class));
        browse.setText("Browse");
        browse.setTag("Browse");
        ab.addTab(browse);
        
        search = ab.newTab();
        search.setTabListener(new GenericTabListener<SearchFragment>(this, SearchFragment.class));
        search.setText("Search");
        search.setTag("Search");
        ab.addTab(search);
        
        nextbus = ab.newTab();
        nextbus.setTabListener(new GenericTabListener<NextBusFragment>(this, NextBusFragment.class));
        nextbus.setText("Next Bus");
        nextbus.setTag("NextBus");
        ab.addTab(nextbus);
        
        ab.setSelectedNavigationItem(sel_tab);
        
        // restore instance state
        if (savedInstanceState != null) {
        	selected_stop = savedInstanceState.getInt("selected_stop",0);
        	selected_location = savedInstanceState.getString("selected_location", "");
        }
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
    	Log.d(TAG,"onSaveInstanceState() - serializing stuff");
    	outState.putInt("selected_stop", selected_stop);
    	outState.putString("selected_location", selected_location);
    	super.onSaveInstanceState(outState);
    }
    @Override
    public void onDestroy() {
    	Log.d(TAG,"onDestroy() - closing database");
    	WtaDatastore.getInstance(this).close();
    	super.onDestroy();
    }
    public void lookupTimesForStop(int stop_id, String location)
    {
    	prev_tab = getActionBar().getSelectedNavigationIndex();
    	selected_stop = stop_id;
    	selected_location = location;
    	reload = true;
    	getActionBar().selectTab(nextbus);
    }
    public void lookupTimesForStop(int stop_id)
    {
    	lookupTimesForStop(stop_id, "");
    }
}