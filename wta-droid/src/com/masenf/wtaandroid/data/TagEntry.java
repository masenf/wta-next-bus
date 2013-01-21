package com.masenf.wtaandroid.data;

import com.masenf.wtaandroid.R;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

public class TagEntry extends BaseEntry {
		private static final String TAG = "TagEntry";

		// a simple data struct
		// which can be initialized from a cursor!
		public int _id = 0;
		public int tag_id = 0;
		public String name = "";
		public String color = "";
	
		private static Drawable folder;
		
		private TagEntry() {
		}
		
		public static TagEntry fromRow(Cursor row) {
			TagEntry te = new TagEntry();
			try {
				te.tag_id = row.getInt(row.getColumnIndexOrThrow(WtaDatastore.KEY_ID));
				te.name = row.getString(row.getColumnIndexOrThrow(WtaDatastore.KEY_NAME));
				te.color = row.getString(row.getColumnIndexOrThrow(WtaDatastore.KEY_COLOR));
			}
			catch (IllegalArgumentException ex) {
				Log.e(TAG,"Error resolving column indexes...This should never happen");
			}
			return te;
		}

		@Override
		public int getViewLayout() {
			return R.layout.hierarchy_item;
		}

		@Override
		public View updateView(View convertView) {
			TextView txt_left = (TextView) convertView.findViewById(R.id.item_left);
			TextView txt_right = (TextView) convertView.findViewById(R.id.item_right);

			txt_left.setText("");
			txt_left.setCompoundDrawables(folder, null, null, null);
			txt_right.setText(name);
			convertView.setTag(this);
			
			return convertView;
		}
		
		public static void createFolder(Context ctx) {
			folder = ctx.getResources().getDrawable(R.drawable.tag_white);
			float ratio = (float) folder.getIntrinsicWidth() / (float) folder.getIntrinsicHeight();
			int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 45, ctx.getResources().getDisplayMetrics());
			int width = (int) (ratio * (float) height);
			folder.setBounds(0,0,width,height);
		}
}
