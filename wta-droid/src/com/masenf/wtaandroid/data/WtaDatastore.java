package com.masenf.wtaandroid.data;

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
	
	// tag_join table
	public static final String KEY_TAGID = "tag_id";
	public static final String KEY_FK = "fk";
	public static final String KEY_TYPE = "type";
	public enum TagEntryType {
		TAG_NAME, STOP
	}
	public static final String KEY_ORDER = "sorder";
	
	public static final String TAG_ROOT = "Root";
	public static final String TAG_FAVORITES = "Favorites";
	public static final String TAG_RECENT = "Recent";
	
	// tag_name table
	public static final String KEY_COLOR = "color";
	
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
	private static final int DATABASE_VERSION = 10; 
	private static final String TABLE_STOP = "stop";
	private static final String TABLE_TAG_JOIN = "tag_join";
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
				"name TEXT UNIQUE NOT NULL, " +
				"color TEXT DEFAULT NULL);",
				
			"CREATE TABLE tag_join ( " +
				"_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				"tag_id INTEGER NOT NULL, " +
				"fk INTEGER NOT NULL, " +
				"type INTEGER NOT NULL," +
				"sorder INTEGER NOT NULL )" };
		
		DatabaseHelper(Context ctx) {
			super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
			Resources res = ctx.getResources();
		}
		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.v(TAG, "Creating new database");
			for (String sql : SQL_DB_CREATE) {
				db.execSQL(sql);
			}
			ins.initData(db);
		}
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
			Log.w(TAG, "Upgrading database from ver " + oldVer +
					" --> " + newVer + ". Data will be destroyed.");
			db.execSQL("DROP TABLE IF EXISTS stop");
			db.execSQL("DROP TABLE IF EXISTS tag_join");
			db.execSQL("DROP TABLE IF EXISTS tag");
			db.execSQL("DROP TABLE IF EXISTS times");
			onCreate(db);
		}
	}

	// a Static factory function (this is just for fun)
	public static WtaDatastore getInstance(Context ctx) throws SQLException {
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
	void initData(SQLiteDatabase db) {
		// this will only run on db creation
		ins.db = db;
		
		// create default tags
		long root_id = createOrUpdateTag(null, TAG_ROOT, null, true);
		long favorites_id = createOrUpdateTag(null, TAG_FAVORITES, "red", true);
		long recent_id = createOrUpdateTag(null, TAG_RECENT, "grey", true);
		
		// create basic hierarchy
		setTag(favorites_id, (int) root_id, TagEntryType.TAG_NAME, 999);
		setTag(recent_id, (int) root_id, TagEntryType.TAG_NAME, 0);
	}
	// database access/manipulation functions
	public Cursor getAllTags() {
		return db.query(true, TABLE_TAG, new String[] {KEY_NAME}, null, null, null, null, null, null);
	}
	public Cursor getTagInfo(String tag) {
		return db.query(TABLE_TAG, new String[] {KEY_ID}, 
			    KEY_NAME + " = ?", new String[] {tag}, null, null, null);
	}
	public Integer getTagId(String tag) {
		Integer tag_id = null;
		Cursor c = getTagInfo(tag);
		while (c.moveToNext())
		{
			tag_id = Integer.valueOf(c.getInt(c.getColumnIndex(KEY_ID)));
		}
		return tag_id;
	}
	private String tagJoinQuery(String[] select, String join_table, String foreign_key, String where, TagEntryType type) {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT ");
		for (String s : select)
			sb.append(s + ",");
		sb.setCharAt(sb.length() - 1, ' ');		// blow that last comma away
		sb.append("FROM " + TABLE_TAG_JOIN + " as tj ");
		sb.append("JOIN " + join_table + " as j ");
		sb.append(String.format("ON tj.%s = j.%s and tj.%s = %s ", (Object[]) new String[]
				{ KEY_FK, foreign_key, KEY_TYPE, String.valueOf(type.ordinal()) } ));
		sb.append("WHERE " + where + " ");
		sb.append("ORDER BY tj." + KEY_ORDER);
		return sb.toString();
	}
	public Cursor getSubTags(String tag) {
		// return tag_id, tag_name, color
		Integer tag_id = getTagId(tag);
		if (tag_id == null)
			return null;
		String[] args = new String [] {tag_id.toString()};
		String query = tagJoinQuery(new String[] {"j." + KEY_ID + " as _id", KEY_NAME, KEY_COLOR }, TABLE_TAG, KEY_ID,
				KEY_TAGID + " = ?", TagEntryType.TAG_NAME);
		return db.rawQuery(query, args);
	}
	public Cursor getTagsForStop(int stop_id) {
		// return tag_id, tag_name, color
		String[] args = new String [] {String.valueOf(stop_id)};
		String query = tagJoinQuery(new String[] {"j." + KEY_ID + " as _id", KEY_NAME, KEY_COLOR }, TABLE_TAG, KEY_ID,
				KEY_FK + " = ?", TagEntryType.STOP);
		return db.rawQuery(query, args);
	}
	public boolean hasTag(int fk, String tag) {
		Log.v(TAG,"Is " + fk + " tagged with '" + tag + "' ??");
		Integer tag_id = getTagId(tag);
		if (tag_id == null) {
			Log.v(TAG,tag + " doesn't even exist@!");
			return false;
		}
		return hasTag(fk, tag_id);
	}
	private boolean hasTag(long fk, Integer tag_id) {
		Cursor c = db.query(TABLE_TAG_JOIN, new String[] { KEY_ID }, 
				KEY_TAGID + " = ? AND " + KEY_FK + " = ?" , new String[] {tag_id.toString(), String.valueOf(fk)},
				null,null,null);
		if (c.getCount() > 0) {
			return true;
		}
		return false;
	}
	public Cursor getLocations(String tag) {
		// return _id, stop_id, name, alias
		Integer tag_id = getTagId(tag);
		if (tag_id == null)
			return null;
		String[] args = new String [] {tag_id.toString()};
		String[] select = new String[] { "j." + KEY_ID + " as _id", KEY_STOPID, KEY_NAME, KEY_ALIAS };
		String query = tagJoinQuery(select, TABLE_STOP, KEY_STOPID, KEY_TAGID + " = ?",TagEntryType.STOP);
		return db.rawQuery(query, args);
	}
	public Cursor getStop(int stop_id) {
		// return _id, stop_id, name, alias
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
			setTag(stop_id, tag, TagEntryType.STOP, 10);
		}
		return _id;
	}
	public long createOrUpdateTag(Integer tag_id, String tag, String color, boolean replace) 
	{
		// set replace = true to overwrite the values of tag_id if it exists
		// return the tag id of tag
		ContentValues v = new ContentValues();
		
		// no id passed in, lookup the tag
		if (tag_id == null)
			tag_id = getTagId(tag);
		
		if (tag_id == null) {		// the tag doesn't exist
			v.put(KEY_NAME, tag);
			v.put(KEY_COLOR, color);
			return db.insert(TABLE_TAG, null, v);
		} else if (replace) {		// update the values
			v.put(KEY_NAME, tag);
			v.put(KEY_COLOR, color);
			db.update(TABLE_TAG, v, KEY_ID + " = ?", new String[] {tag_id.toString()});
		}
		return tag_id.longValue();
	}
	public long setTag(long fk, String tag, TagEntryType type, int sort_order)
	{
		// create the tag if it doesn't exist
		int tag_id = (int) createOrUpdateTag(null, tag, null, false);
		return setTag(fk, Integer.valueOf(tag_id), type, sort_order);
	}
	public long setTag(long fk, int tag_id, TagEntryType type, int sort_order)
	{
		ContentValues v = new ContentValues();
		v.put(KEY_TAGID, tag_id);
		v.put(KEY_FK, fk);
		v.put(KEY_TYPE, String.valueOf(type.ordinal()));
		v.put(KEY_ORDER, String.valueOf(sort_order));
		
		if (hasTag(fk, Integer.valueOf(tag_id))) {
			// update the type/sort order
			return db.update(TABLE_TAG_JOIN, v, KEY_TAGID + " = ? AND " + KEY_FK + " = ?", 
					new String[] {String.valueOf(tag_id), String.valueOf(fk)});
		}
		return db.insert(TABLE_TAG_JOIN, null, v);	
	}

	public long addTagToLocation(long _id, String tag) {
		return setTag(_id, tag, TagEntryType.STOP, 10);
	}
	public long rmTagFromLocation(int stop_id, String tag) {
		Integer tag_id = getTagId(tag);
		if (tag_id != null) {
			return db.delete(TABLE_TAG_JOIN, KEY_FK + " = ? AND " + KEY_TAGID + " = ?",
					new String[] {String.valueOf(stop_id), tag_id.toString()});
		}
		return 0;
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
		return hasTag(stop_id, TAG_FAVORITES);
	}
	public long addRecent(int stop_id, String name) {
		return addLocation(TAG_RECENT, stop_id, name, null);
	}
	public Cursor getRecent() {
		return getLocations(TAG_RECENT);
	}
	public void clrRecent() {
		Integer tag_id = getTagId(TAG_RECENT);
		if (tag_id != null) 
			db.delete(TABLE_TAG_JOIN, KEY_TAGID + " = ?", new String[] {tag_id.toString()});
	}
}
