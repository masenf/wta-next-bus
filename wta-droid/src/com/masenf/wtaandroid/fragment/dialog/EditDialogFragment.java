package com.masenf.wtaandroid.fragment.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.masenf.wtaandroid.R;

/**
 * create a simple dialog to edit a string of text
 * @author masen
 *
 */
public abstract class EditDialogFragment extends DialogFragment {
	
	private EditText edit_box;
	
	/**
	 * Displayed in the edit field if nothing else is present
	 */
	protected String editHint = "";
	
	/**
	 * Initial text of the edit field. After clicking save, 
	 * stores the updated text of the edit field.
	 */
	protected String content = "";
	
	/**
	 * Title of the dialog box
	 */
	protected String title = "";
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
		// create an edit text to populate
		edit_box = new EditText(getActivity());
		edit_box.setText(content);
		
        // Construct the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setView(edit_box);
        builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            	content = edit_box.getText().toString();
            	doSaveData();
            }
        });
        builder.setNeutralButton(R.string.dismiss, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
        // Create the AlertDialog object and return it
        final Dialog d = builder.create();
		edit_box.setOnFocusChangeListener(new View.OnFocusChangeListener() {
		    @Override
		    public void onFocusChange(View v, boolean hasFocus) {
		        if (hasFocus) {
		            d.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		            edit_box.setSelection(0,edit_box.length());
		        }
		    }
		});
        return d;
    }
	public abstract void doSaveData();
}
