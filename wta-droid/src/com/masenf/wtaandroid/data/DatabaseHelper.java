package com.masenf.wtaandroid.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
	private static final String TAG = "DatabaseHelper";
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
		super(ctx, WtaDatastore.DATABASE_NAME, null, WtaDatastore.DATABASE_VERSION);
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.v(TAG, "Creating new database");
		for (String sql : SQL_DB_CREATE) {
			db.execSQL(sql);
		}
		WtaDatastore.onCreate(db);
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
