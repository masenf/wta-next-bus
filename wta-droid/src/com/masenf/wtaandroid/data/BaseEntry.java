package com.masenf.wtaandroid.data;

import java.io.Serializable;

import com.masenf.wtaandroid.EntryClickHandler;
import android.database.Cursor;
import android.view.View;

public abstract class BaseEntry implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8315724082631937273L;
	
	public long _id = 0;
	
	// create the class from a single cursor row
	public static BaseEntry fromRow(Cursor c) {
		return null;
	}
	
	// the adapter will inflate whichever view this function returns
	public abstract int getViewLayout();
	// update the view for this entry
	public abstract View updateView(View convertView);

	public abstract void handleClick(EntryClickHandler entryClickHandler);
}
