package com.masenf.wtaandroid;

import java.util.ArrayList;

import com.masenf.wtaandroid.fragment.FavoritesFragment;
import com.masenf.wtaandroid.fragment.NextBusFragment;
import com.masenf.wtaandroid.fragment.SearchFragment;
import com.masenf.wtaandroid.fragment.BrowseFragment;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

public class Wta_main extends Activity {

	private static final String TAG = "Wta_main";
	
	private int selected_stop;
	private String selected_location;
	private boolean reload = false;
	private int prev_tab = -1;
	private ArrayList<IonBackButtonPressed> backButtonCallbacks;
	public static final String wAPI = "http://mashed-potatoes.with-linux.com:8080/";
	private Tab favorites;
	private Tab recent;
	private Tab search;
	private Tab nextbus;
	private Tab browse;
	
	public void registerBackButtonCallback(IonBackButtonPressed cb) {
		if (backButtonCallbacks == null) {
			backButtonCallbacks = new ArrayList<IonBackButtonPressed>();
		}
		backButtonCallbacks.add(cb);
	}
	
	public int getSelected_stop() {
		return selected_stop;
	}
	public String getSelected_location() {
		return selected_location;
	}
	public boolean isReload() {
		return reload;
	}
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        ActionBar ab = getActionBar();
        Context ctx = (Context) this;
        ab.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        
        favorites = ab.newTab();
        favorites.setTabListener(new GenericTabListener<FavoritesFragment>(ctx, "Favorites", FavoritesFragment.class) {});
        favorites.setText("Favorites");
        favorites.setTag("Favorites");
        ab.addTab(favorites);
        
//        recent = ab.newTab();
//        recent.setTabListener(new GenericTabListener<RecentFragment>(ctx, "Recent", RecentFragment.class) {});
//        recent.setText("Recent");
//        recent.setTag("Recent");
//        ab.addTab(recent);
        
        browse = ab.newTab();
        browse.setTabListener(new GenericTabListener<BrowseFragment>(ctx, "Browse", BrowseFragment.class) {});
        browse.setText("Browse");
        browse.setTag("Browse");
        ab.addTab(browse);
        
        search = ab.newTab();
        search.setTabListener(new GenericTabListener<SearchFragment>(ctx, "Search", SearchFragment.class) {});
        search.setText("Search");
        search.setTag("Search");
        ab.addTab(search);
        
        nextbus = ab.newTab();
        nextbus.setTabListener(new GenericTabListener<NextBusFragment>(ctx, "NextBus", NextBusFragment.class) {});
        nextbus.setText("Next Bus");
        nextbus.setTag("NextBus");
        ab.addTab(nextbus);
        
        if (savedInstanceState != null) {
        	selected_stop = savedInstanceState.getInt("selected_stop",0);
        	selected_location = savedInstanceState.getString("selected_location", "");
        	int seltab = savedInstanceState.getInt("active_tab_idx", 0);
        	ab.setSelectedNavigationItem(seltab);
        	prev_tab = savedInstanceState.getInt("prev_tab", -1);
        }
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
    	ActionBar ab = getActionBar();
    	outState.putInt("active_tab_idx", ab.getSelectedNavigationIndex());
    	outState.putInt("selected_stop", selected_stop);
    	outState.putString("selected_location", selected_location);
    	outState.putInt("prev_tab", prev_tab);
    }
    @Override
    public void onDestroy() {
    	WtaDatastore.getInstance(this).close();
    	super.onDestroy();
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
    public void setReload(boolean reload)
    {
    	this.reload = reload;
    }
}
