package com.masenf.wtaandroid.data;

import com.masenf.wtaandroid.EntryClickHandler;
import com.masenf.wtaandroid.R;
import com.masenf.wtaandroid.WtaActivity;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

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
		
		private LocationEntry() {
		}
		
		public static LocationEntry fromRow(Cursor row) {
			LocationEntry le = new LocationEntry();
			try {
				le._id = row.getInt(row.getColumnIndexOrThrow(WtaDatastore.KEY_ID));
				le.stop_id = row.getInt(row.getColumnIndexOrThrow(WtaDatastore.KEY_STOPID));
				le.name = row.getString(row.getColumnIndexOrThrow(WtaDatastore.KEY_NAME));
				le.alias = row.getString(row.getColumnIndexOrThrow(WtaDatastore.KEY_ALIAS));
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
			if (alias == null)
				txt_location.setText(name);
			else
				txt_location.setText(alias);
			return super.updateView(convertView);
		}
		@Override
		public void handleClick(EntryClickHandler tg) {
			WtaActivity a = (WtaActivity) tg.getActivity();
			if (a != null) {
				WtaDatastore.getInstance(a).addRecent(stop_id, name);
				a.lookupTimesForStop(stop_id, name);
			}
		}
}
