package com.masenf.core;

import android.view.View;

public interface DrawingItem {
	// This is the interface for a self-drawing item. Implement getViewLayout to return
	// the layout Id for this object's view. 
	// Implement updateView to manipulate the view before it is returned to the ListView.
	// convertView will always be a non-null, inflated instance of the Id returned.
	
	// the adapter will inflate whichever view this function returns
	public int getViewLayout();
	
	// update the view for this entry
	// note: the view's tag MUST be set to the Item's class for recycling purposes
	public View updateView(View convertView);
}
