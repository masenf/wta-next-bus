package com.masenf.wtaandroid.data;

import android.database.Cursor;
import android.util.Log;

public class TimeEntry extends BaseEntry {
	private static final String TAG = "TimeEntry";
	
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
	
	private TimeEntry() {
		
	}
	
	public TimeEntry fromRow(Cursor row) {
		TimeEntry te = new TimeEntry();
		try {
			te.timeid      = row.getInt(   row.getColumnIndexOrThrow(WtaDatastore.KEY_TIMEID));
			te.stop_id     = row.getInt(   row.getColumnIndexOrThrow(WtaDatastore.KEY_STOPID));
			te.name        = row.getString(row.getColumnIndexOrThrow(WtaDatastore.KEY_NAME));
			te.routenum    = row.getString(row.getColumnIndexOrThrow(WtaDatastore.KEY_ROUTENUM));
			te.destination = row.getString(row.getColumnIndexOrThrow(WtaDatastore.KEY_DESTINATION));
			te.dow         = row.getString(row.getColumnIndexOrThrow(WtaDatastore.KEY_DAY));
			te.updatedtime = row.getString(row.getColumnIndexOrThrow(WtaDatastore.KEY_UPDATEDTIME));
		}
		catch (IllegalArgumentException ex) {
			Log.e(TAG,"Error resolving column indexes...This should never happen");
		}
		return te;
	}
}