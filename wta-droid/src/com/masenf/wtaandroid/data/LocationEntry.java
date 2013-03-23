package com.masenf.wtaandroid.data;

import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.masenf.core.EntryClickHandler;
import com.masenf.core.data.BaseEntry;
import com.masenf.core.fragment.EditDialogFragment;
import com.masenf.wtaandroid.R;
import com.masenf.wtaandroid.WtaActivity;
import com.masenf.wtaandroid.async.DataWriteTaskFactory;

public class LocationEntry extends BaseEntry {
		/**
	 * 
	 */
	private static final long serialVersionUID = 8613795712669924913L;

		private static final String TAG = "LocationEntry";

		// a simple data struct
		// which can be initialized from a cursor!
		public int stop_id = 0;
		public String name = "";
		public String alias = "";
		public String display = "";
		
		private LocationEntry() {
		}
		
		public static LocationEntry fromRow(Cursor row) {
			LocationEntry le = new LocationEntry();
			try {
				le._id = row.getInt(row.getColumnIndexOrThrow(WtaDatastore.KEY_ID));
				le.stop_id = row.getInt(row.getColumnIndexOrThrow(WtaDatastore.KEY_STOPID));
				le.name = row.getString(row.getColumnIndexOrThrow(WtaDatastore.KEY_NAME));
				le.alias = row.getString(row.getColumnIndexOrThrow(WtaDatastore.KEY_ALIAS));
				le.display = le.alias == null ? le.name : le.alias;
			}
			catch (IllegalArgumentException ex) {
				Log.e(TAG,"Error resolving column indexes...This should never happen");
			}
			return le;
		}

		@Override
		public int getViewLayout() {
			return R.layout.hierarchy_item;
		}

		@Override
		public View updateView(View convertView) {
			TextView txt_stop_id = (TextView) convertView.findViewById(R.id.item_left);
			TextView txt_location = (TextView) convertView.findViewById(R.id.item_right);
			
			txt_stop_id.setText(String.valueOf(stop_id));
			txt_stop_id.setCompoundDrawables(null, null, null, null);
			txt_location.setText(display);
			return super.updateView(convertView);
		}
		@Override
		public void handleClick(EntryClickHandler tg) {
			WtaActivity a = (WtaActivity) tg.getActivity();
			if (a != null) {
				a.lookupTimesForStop(stop_id, display);
			}
		}
		@Override
		public boolean handleLongClick(EntryClickHandler tg) {
			EditDialogFragment dialog = new EditDialogFragment() {
				{
					// set up defaults for the dialog
					title = "Set Alias for Stop #" + stop_id;
					editHint = "Stop Alias";
					content = display;
				}
				@Override
				public void doSaveData() {

					if (content != null && content.isEmpty())
						content = null;							// blank input means, null the alias field
					
					display = alias = content;					// update display
					
					if (display == null) 						// reset display back to default name if null
						display = name;

					// commit final result to database
					DataWriteTaskFactory dwtf = new DataWriteTaskFactory(null);
					dwtf.setAlias(stop_id, content);
				}
			};
    		dialog.show(tg.getActivity().getFragmentManager(), "renameBox");
    		return true;
		}
}
