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
	
	// stops table
	public static final String KEY_ID = "_id";
	public static final String KEY_STOPID = "stop_id";
	public static final String KEY_NAME = "name";
	public static final String KEY_ALIAS = "alias";
	
	// tag table
	public static final String KEY_TAG = "tag";
	public static final String KEY_FK = "fk";
	public static final String KEY_TYPE = "type";
	public enum TagEntryType {
		TAG, STOP
	}
	public static final String KEY_ORDER = "sorder";
	
	public static final String TAG_FAVORITES = "Favorites";
	public static final String TAG_RECENT = "Recent";
	
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
	private static final int DATABASE_VERSION = 6; 
	private static final String TABLE_STOP = "stop";
	private static final String TABLE_TAG = "tag";
	private static final String TABLE_TIMES = "times";
	
	// this class helps us open/create a new database
	private static class DatabaseHelper extends SQLiteOpenHelper {
		// this will generate the table structure
		private static final String[] SQL_DB_CREATE = new String[] {
			"CREATE TABLE times ( " +
				"_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				"stop_id INTEGER NOT NULL, " +
				"time TEXT NOT NULL, " +
				"routenum TEXT NOT NULL, "+
				"name TEXT NOT NULL, " +
				"destination TEXT, " +
				"dow TEXT NOT NULL, " +
				"updated_time TEXT NOT NULL, " +
				"updated INTEGER NOT NULL ); ",
			
			"CREATE TABLE stop ( " +
				"_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				"stop_id INTEGER UNIQUE NOT NULL, " +
				"name TEXT NOT NULL, " +
				"alias TEXT DEFAULT NULL);",
				
			"CREATE TABLE tag ( " +
				"_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				"tag TEXT NOT NULL, " +
				"fk INTEGER NOT NULL, " +
				"type INTEGER NOT NULL," +
				"sorder INTEGER NOT NULL )" };
		
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
			db.execSQL("DROP TABLE IF EXISTS stop");
			db.execSQL("DROP TABLE IF EXISTS tag");
			db.execSQL("DROP TABLE IF EXISTS times");
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
	public Cursor getAllTags() {
		return db.query(true, TABLE_TAG, new String[] {KEY_TAG}, null, null, null, null, null, null);
	}
	public Cursor getLocations(String tag) {
		// return _id, stop_id, name, type
		String[] args = new String [] {tag};
		return db.rawQuery("SELECT ( CASE WHEN s1._id THEN s1._id ELSE t1._id END ) AS _id, " +
						   		"s1.stop_id AS stop_id, " +
						   		"( CASE WHEN IFNULL(s1.alias, '') != '' THEN s1.alias " +
						   			"WHEN IFNULL(s1.name, '') != '' THEN s1.name " +
						   			"WHEN IFNULL(t1.tag, '') != '' THEN t1.tag ELSE '' END ) AS name, " +
						   		"t.type " +
						   	"FROM tag as t " +
						   	"LEFT JOIN stop AS s1 ON t.fk = s1._id AND t.type = 1 " +
						   	"LEFT JOIN tag AS t1 ON t.fk = t1._id AND t.type = 0 " +
						   	"WHERE t.tag = ? ORDER BY t.sorder", args);
	}
	public Cursor getLocation(String tag, int stop_id) {
		// return _id, stop_id, name, alias, type
		String[] args = new String [] {tag, String.valueOf(stop_id)};
		return db.rawQuery("SELECT s1._id AS _id, " +
						   		"s1.stop_id AS stop_id, " +
						   		"s1.name AS name, " + 
						   		"s1.alias AS alias " +
						   	"FROM tag as t " +
						   	"LEFT JOIN stop AS s1 ON t.fk = s1._id AND t.type = 1 " +
						   	"WHERE t.tag = ? AND stop_id = ? ORDER BY t.sorder", args);
	}
	public Cursor getStop(int stop_id) {
		return db.query(TABLE_STOP, new String[] {KEY_ID, KEY_STOPID, KEY_NAME, KEY_ALIAS}, KEY_STOPID + " = ?", 
				new String[] {String.valueOf(stop_id)}, null, null, null);
	}
	public long addLocation(String tag, int stop_id, String name, String alias) {
		long _id;
		ContentValues v = new ContentValues();
		v.put(KEY_STOPID, stop_id);
		v.put(KEY_NAME, name);
		v.put(KEY_ALIAS, alias);
		
		Cursor c = getStop(stop_id);
		
		if (c.getCount() > 0) {
			c.moveToFirst();
			_id = c.getLong(c.getColumnIndex(KEY_ID));
			db.update(TABLE_STOP, v, KEY_ID + " = ?", new String[] {String.valueOf(_id)});
		} else {
			_id = db.insert(TABLE_STOP, null, v);
		}
		if (tag != null) {
			addTag(_id, tag, TagEntryType.STOP, 10);
		}
		return _id;
	}
	public Cursor getTags(long fk) {
		return db.query(TABLE_TAG, new String[] {KEY_ID, KEY_TAG, KEY_FK, KEY_TYPE, KEY_ORDER}, KEY_FK + " = ?", 
				new String[] {String.valueOf(fk)}, null, null, null);
	}
	public long addTag(long fk, String tag, TagEntryType type, int sort_order)
	{
		ContentValues v = new ContentValues();
		v.put(KEY_TAG, tag);
		v.put(KEY_FK, fk);
		v.put(KEY_TYPE, String.valueOf(type.ordinal()));
		v.put(KEY_ORDER, String.valueOf(sort_order));
		
		Cursor c = getTags(fk);
		c.move(-1);
		while (c.moveToNext()) {
			if (c.getString(c.getColumnIndex(KEY_TAG)).equals(tag)) {
				long _id = c.getLong(c.getColumnIndex(KEY_ID));
				return db.update(TABLE_TAG, v, KEY_ID + " = ?", new String[] {String.valueOf(_id)});
			}
		}
		return db.insert(TABLE_TAG, null, v);	
	}
	public long addTagToLocation(long _id, String tag) {
		return addTag(_id, tag, TagEntryType.STOP, 10);
	}
	public long rmTagFromLocation(int stop_id, String tag) {
		return db.delete(TABLE_TAG, KEY_STOPID + " = ? AND " + KEY_TAG + " = ?",
				new String[] {String.valueOf(stop_id), tag});
	}
	public long rmFavorite(int stop_id) {
		return rmTagFromLocation(stop_id, TAG_FAVORITES);
	}
	public Cursor getFavorites(){
		return getLocations(TAG_FAVORITES);
	}
	public long addFavorite(int stop_id, String name) {
		return addLocation(TAG_FAVORITES, stop_id, name, null);
	}
	public boolean isFavorite(int stop_id) {
		Cursor c = getLocation(TAG_FAVORITES, stop_id);
		if (c.getCount() > 0) {
			return true;
		} else {
			return false;
		}
	}
	public long addRecent(int stop_id, String name) {
		return addLocation(TAG_RECENT, stop_id, name, null);
	}
	public Cursor getRecent() {
		return getLocations(TAG_RECENT);
	}
	public long clrRecent() {
		return db.delete(TABLE_TAG, KEY_TAG + " = ?", new String[] {TAG_RECENT});
	}
}
