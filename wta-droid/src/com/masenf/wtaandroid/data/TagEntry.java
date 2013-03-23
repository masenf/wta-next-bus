package com.masenf.wtaandroid.data;

import java.util.ArrayList;

import com.masenf.core.EntryClickHandler;
import com.masenf.core.async.callbacks.DataWriteCallback;
import com.masenf.core.data.BaseEntry;
import com.masenf.core.fragment.EditDialogFragment;
import com.masenf.wtaandroid.NestedTagManager;
import com.masenf.wtaandroid.R;
import com.masenf.wtaandroid.async.DataWriteTaskFactory;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class TagEntry extends BaseEntry {
		/**
	 * 
	 */
	private static final long serialVersionUID = 3777012130328693830L;

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
			
			return super.updateView(convertView);
		}
		@Override
		public void handleClick(EntryClickHandler ad) {
			((NestedTagManager) ad).push(name);
		}
		@Override
		public boolean handleLongClick(EntryClickHandler tg) {
			EditDialogFragment dialog = new EditDialogFragment() {
				{
					// set up defaults for the dialog
					title = "Rename Tag";
					editHint = "Tag Name";
					content = name;
				}
				@Override
				public void doSaveData() {
					// commit final result to database
					DataWriteTaskFactory dwtf = new DataWriteTaskFactory(new DataWriteCallback() {
						@Override
						public void updateData(ArrayList<Long> result) {
							if (result.size() < 1 || result.get(0) < 0) {	// error condition
								String err_message = "Error saving tag name";
								if (result.size() > 0) {
									switch (result.get(0).intValue()) {
									case WtaDatastore.ERR_DUPLICATE_NAME:
										err_message = "Name already exists, choose a different name";
										break;
									case WtaDatastore.ERR_NO_NAME:
										err_message = "A name is required to rename a tag";
										break;
									case WtaDatastore.ERR_SYSTEM_TAG:
										err_message = "Sorry, cannot rename system tags (in this version)";
										break;
									}
								}
								Toast.makeText(getActivity(), err_message, Toast.LENGTH_LONG).show();
							} else {
								if (content == null || content.isEmpty())
									return;						// delete not implemented
								name = content;					// update display
							}
						}
					});
					dwtf.renameTag(tag_id, content);
				}
			};
    		dialog.show(tg.getActivity().getFragmentManager(), "renameBox");
    		return true;
		}
		/**
		 * This is probably one of the most poorly named methods of all time.
		 * 
		 * Uses ctx to load a drawable resource into a static variable 'folder',
		 * then proceeds to scale it according to some magic numbers. 
		 * Plenty of room to clean this up.
		 * @param ctx
		 */
		public static void createFolder(Context ctx) {
			folder = ctx.getResources().getDrawable(R.drawable.tag_white);
			float ratio = (float) folder.getIntrinsicWidth() / (float) folder.getIntrinsicHeight();
			int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 45, ctx.getResources().getDisplayMetrics());
			int width = (int) (ratio * (float) height);
			folder.setBounds(0,0,width,height);
		}
}
