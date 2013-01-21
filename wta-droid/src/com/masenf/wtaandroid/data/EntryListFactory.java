package com.masenf.wtaandroid.data;

import java.util.ArrayList;


import android.database.Cursor;

public class EntryListFactory {

	public static ArrayList<LocationEntry> fromLocationCursor(Cursor c) {
		ArrayList<LocationEntry> res = new ArrayList<LocationEntry>();
		if (c != null) {
			c.move(-1);
			while (c.moveToNext()) {
				res.add(LocationEntry.fromRow(c));
			}
		}
		return res;
	}
	public static ArrayList<TagEntry> fromTagCursor(Cursor c) {
		ArrayList<TagEntry> res = new ArrayList<TagEntry>();
		if (c != null) {
			c.move(-1);
			while (c.moveToNext()) {
				res.add(TagEntry.fromRow(c));
			}
		}
		return res;
	}
}
