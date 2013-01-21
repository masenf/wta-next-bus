package com.masenf.wtaandroid.data;

import android.database.Cursor;
import android.view.View;

public abstract class BaseEntry {
	
	// create the class from a single cursor row
	public static BaseEntry fromRow(Cursor c) {
		return null;
	}
	
	// the adapter will inflate whichever view this function returns
	public abstract int getViewLayout();
	// update the view for this entry
	public abstract View updateView(View convertView);
}
