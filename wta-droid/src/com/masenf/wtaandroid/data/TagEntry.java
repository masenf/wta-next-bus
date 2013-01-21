package com.masenf.wtaandroid.data;

import android.database.Cursor;
import android.util.Log;

public class TagEntry extends BaseEntry {
		private static final String TAG = "TagEntry";

		// a simple data struct
		// which can be initialized from a cursor!
		public int _id = 0;
		public int tag_id = 0;
		public String name = "";
		public String color = "";
		
		private TagEntry() {
		}
		
		public static TagEntry fromRow(Cursor row) {
			TagEntry te = new TagEntry();
			try {
				te.tag_id = row.getInt(row.getColumnIndexOrThrow(WtaDatastore.KEY_ID));
				te.name = row.getString(row.getColumnIndexOrThrow(WtaDatastore.KEY_NAME));
				te.color = row.getString(row.getColumnIndexOrThrow(WtaDatastore.KEY_COLOR));
			}
			catch (IllegalArgumentException ex) {
				Log.e(TAG,"Error resolving column indexes...This should never happen");
			}
			return te;
		}
}
