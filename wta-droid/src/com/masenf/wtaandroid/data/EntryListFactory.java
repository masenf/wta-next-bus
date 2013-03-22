package com.masenf.wtaandroid.data;

import com.masenf.core.data.BaseEntry;
import com.masenf.core.data.EntryList;

import android.database.Cursor;

public class EntryListFactory {

	/**
	 * To reduce resource leaks, this method closes the cursor after use
	 * @param c Cursor to location results
	 * @return an EntryList containing LocationEntry
	 */
	public EntryList fromLocationCursor(Cursor c) {
		EntryList res = new EntryList();
		if (c != null) {
			c.move(-1);
			while (c.moveToNext()) {
				res.add((BaseEntry) LocationEntry.fromRow(c));
			}
			c.close();
		}
		return res;
	}
	/**
	 * To reduce resource leaks, this method closes the cursor after use
	 * @param c Cursor to tag results
	 * @return an EntryList containing TagEntry
	 */
	public EntryList fromTagCursor(Cursor c) {
		EntryList res = new EntryList();
		if (c != null) {
			c.move(-1);
			while (c.moveToNext()) {
				res.add((BaseEntry) TagEntry.fromRow(c));
			}
			c.close();
		}
		return res;
	}
}
