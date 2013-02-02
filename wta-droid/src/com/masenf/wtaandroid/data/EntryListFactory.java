package com.masenf.wtaandroid.data;

import com.masenf.core.data.BaseEntry;
import com.masenf.core.data.EntryList;

import android.database.Cursor;

public class EntryListFactory {

	public EntryList fromLocationCursor(Cursor c) {
		EntryList res = new EntryList();
		if (c != null) {
			c.move(-1);
			while (c.moveToNext()) {
				res.add((BaseEntry) LocationEntry.fromRow(c));
			}
		}
		return res;
	}
	public EntryList fromTagCursor(Cursor c) {
		EntryList res = new EntryList();
		if (c != null) {
			c.move(-1);
			while (c.moveToNext()) {
				res.add((BaseEntry) TagEntry.fromRow(c));
			}
		}
		return res;
	}
}
