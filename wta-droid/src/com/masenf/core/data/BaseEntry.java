package com.masenf.core.data;

import java.io.Serializable;

import com.masenf.core.DrawingItem;
import com.masenf.core.EntryClickHandler;

import android.database.Cursor;
import android.view.View;

public abstract class BaseEntry implements Serializable, DrawingItem {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8315724082631937273L;
	
	public long _id = 0;
	
	// create the class from a single cursor row
	public static BaseEntry fromRow(Cursor c) {
		return null;
	}
	@Override
	public View updateView(View convertView) {
		convertView.setTag(this);
		return convertView;
	}
	public abstract void handleClick(EntryClickHandler entryClickHandler);
}
