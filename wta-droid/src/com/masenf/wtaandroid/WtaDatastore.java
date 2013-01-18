package com.masenf.wtaandroid;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class WtaDatastore {
	
	// interfaces with an SQLite database for storing information 

	private static final String TAG = "WtaDatastore";
	
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
	
	private DatabaseHelper dbHelper;
	private SQLiteDatabase db;
	private static WtaDatastore ins;
	
	private static final String DATABASE_NAME = "data";
	private static final int DATABASE_VERSION = 5; 
	private static final String TABLE_FAVORITES = "favorites";
	private static final String TABLE_RECENT = "recent";
	private static final String TABLE_TIMES = "times";
	
	// this class helps us open/create a new database
	private static class DatabaseHelper extends SQLiteOpenHelper {
		// this will generate the table structure
		private static final String[] SQL_DB_CREATE =
				new String[]{"CREATE TABLE times ( " +
				"_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				"stop_id INTEGER NOT NULL, " +
				"time TEXT NOT NULL, " +
				"routenum TEXT NOT NULL, "+
				"name TEXT NOT NULL, " +
				"destination TEXT, " +
				"dow TEXT NOT NULL, " +
				"updated_time TEXT NOT NULL, " +
				"updated INTEGER NOT NULL ); ",
				"CREATE TABLE favorites ( " +
				"_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				"stop_id INTEGER NOT NULL, " +
				"name TEXT NOT NULL, " +
				"updated INTEGER NOT NULL );",
				"CREATE TABLE recent ( " +
				"_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				"stop_id INTEGER NOT NULL, " +
				"name TEXT NOT NULL," +
				"updated INTEGER NOT NULL )"};
		
		DatabaseHelper(Context ctx) {
			super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
			Log.v(TAG,"Instantiating new database helper");
			Resources res = ctx.getResources();
		}
		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.v(TAG, "Creating new database");
			for (String sql : SQL_DB_CREATE) {
				db.execSQL(sql);
			}
		}
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
			Log.w(TAG, "Upgrading database from ver " + oldVer +
					" --> " + newVer + ". Data will be destroyed.");
			db.execSQL("DROP TABLE IF EXISTS favorites");
			db.execSQL("DROP TABLE IF EXISTS times");
			db.execSQL("DROP TABLE IF EXISTS recent");
			onCreate(db);
		}
	}

	// a Static factory function (this is just for fun)
	public static WtaDatastore getInstance(Context ctx) throws SQLException {
		Log.v(TAG,"getInstance called");
		if (ins == null) {
			ins = new WtaDatastore();
			ins.dbHelper = new DatabaseHelper(ctx);
			ins.db = ins.dbHelper.getWritableDatabase();
		}
		return ins;
	}
	public void close() {
		ins.dbHelper.close();
		ins = null;
	}
	
	// database access/manipulation functions
	private Cursor getLocations(String table) {
		return db.query(table, 
				new String[] {KEY_TIMEID, KEY_STOPID, KEY_NAME, KEY_UPDATED},
				null, null, null, null, KEY_UPDATED);

	}
	private long addLocation(String table, int stop_id, String name) {
		ContentValues v = new ContentValues();
		v.put(KEY_STOPID, stop_id);
		v.put(KEY_NAME, name);
		v.put(KEY_UPDATED, System.currentTimeMillis() / 1000L);
		return db.insert(table, null, v);		
	}
	public long rmFavorite(int stop_id) {
		return db.delete(TABLE_FAVORITES, KEY_STOPID + " = " + stop_id, null);
	}
	public Cursor getFavorites(){
		return getLocations(TABLE_FAVORITES);
	}
	public long addFavorite(int stop_id, String name) {
		return addLocation(TABLE_FAVORITES, stop_id, name);
	}
	public boolean isFavorite(int stop_id) {
		Cursor c = db.query(TABLE_FAVORITES, 
				new String[] {KEY_TIMEID, KEY_STOPID, KEY_NAME, KEY_UPDATED},
				KEY_STOPID + " = ?", new String[] {String.valueOf(stop_id)}, null, null, null);
		if (c.getCount() > 0) {
			return true;
		} else {
			return false;
		}
	}
	public long addRecent(int stop_id, String name) {
		return addLocation(TABLE_RECENT, stop_id, name);
	}
	public Cursor getRecent() {
		return getLocations(TABLE_RECENT);
	}
	public long clrRecent() {
		return db.delete(TABLE_RECENT, null, null);
	}
}
