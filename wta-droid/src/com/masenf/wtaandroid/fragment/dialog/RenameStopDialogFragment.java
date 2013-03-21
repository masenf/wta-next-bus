package com.masenf.wtaandroid.fragment.dialog;

import com.masenf.wtaandroid.R;
import com.masenf.wtaandroid.data.WtaDatastore;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;

public class RenameStopDialogFragment extends DialogFragment {
	
	private EditText edit_alias;
	private String alias;
	private int stop_id;
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
		// create an edit text to populate
		edit_alias = new EditText(getActivity());
		
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.rename_title);
        builder.setView(edit_alias);
        builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            	String url = getResources().getString(R.string.about_url);
            	Intent i = new Intent(Intent.ACTION_VIEW);
            	i.setData(Uri.parse(url));
            	startActivity(i);
            }
        });
        builder.setNeutralButton(R.string.dismiss, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
        // Create the AlertDialog object and return it
        return builder.create();
    }
	public void initialize(int stop_id, String alias)
	{
		this.stop_id = stop_id;
		this.alias = alias;
		this.edit_alias.setText(alias);
	}
	public void doSaveValues()
	{
	}
}
