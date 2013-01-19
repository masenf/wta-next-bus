package com.masenf.wtaandroid;

import android.database.Cursor;
import android.util.Log;

public class LocationEntry implements IEntry {
		private static final String TAG = "LocationEntry";

		// a simple data struct
		// which can be initialized from a cursor!
		public long _id = 0;
		public int stop_id = 0;
		public String name = "";
		public String alias = "";
		
		private LocationEntry() {
		}
		
		public static LocationEntry fromRow(Cursor row) {
			LocationEntry le = new LocationEntry();
			try {
				le._id = row.getInt(row.getColumnIndexOrThrow(WtaDatastore.KEY_ID));
				le.stop_id = row.getInt(row.getColumnIndexOrThrow(WtaDatastore.KEY_STOPID));
				le.name = row.getString(row.getColumnIndexOrThrow(WtaDatastore.KEY_NAME));
				le.alias = row.getString(row.getColumnIndexOrThrow(WtaDatastore.KEY_ALIAS));
			}
			catch (IllegalArgumentException ex) {
				Log.e(TAG,"Error resolving column indexes...This should never happen");
			}
			return le;
		}
}
