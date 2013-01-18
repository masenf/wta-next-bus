package com.masenf.wtaandroid;

import android.database.Cursor;
import android.util.Log;

public class TimeEntry {
	private static final String TAG = "TimeEntry";
	
	// favorites/recent table
	public static final String KEY_STOPID = "stop_id";
	public static final String KEY_NAME = "name";
	public static final String KEY_UPDATED = "updated";
	
	// times table
	public static final String KEY_ROUTENUM = "routenum";
	public static final String KEY_DESTINATION = "destination";
	public static final String KEY_TIME = "time";
	public static final String KEY_DAY = "dow";
	public static final String KEY_UPDATEDTIME = "updated_time";
	public static final String KEY_TIMEID = "_id";
	
	// a simple data struct
	// which can be initialized from a cursor!
	public long timeid = 0;
	public int stop_id = 0;
	public String routenum = "";
	public String name = "";
	public String destination = "";
	public String dow = "";
	public String updatedtime = "";
	public int updated = 0;
	
	public TimeEntry(Cursor row) {
		try {
			int timeid_col = row.getColumnIndexOrThrow(KEY_TIMEID);
			int stop_id_col = row.getColumnIndexOrThrow(KEY_STOPID);
			int name_col = row.getColumnIndexOrThrow(KEY_NAME);
			int routenum_col = row.getColumnIndexOrThrow(KEY_ROUTENUM);
			int destination_col = row.getColumnIndexOrThrow(KEY_DESTINATION);
			int dow_col = row.getColumnIndexOrThrow(KEY_DAY);
			int updated_col = row.getColumnIndexOrThrow(KEY_UPDATED);
			int updatedtime_col = row.getColumnIndexOrThrow(KEY_UPDATEDTIME);
			
			timeid = row.getInt(timeid_col);
			stop_id = row.getInt(stop_id_col);
			name = row.getString(name_col);
			routenum = row.getString(routenum_col);
			destination = row.getString(destination_col);
			dow = row.getString(dow_col);
			updated = row.getInt(updated_col);
			updatedtime = row.getString(updatedtime_col);
		}
		catch (IllegalArgumentException ex) {
			Log.e(TAG,"Error resolving column indexes...This should never happen");
		}
	}
}
