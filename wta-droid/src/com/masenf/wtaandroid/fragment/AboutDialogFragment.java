package com.masenf.wtaandroid.fragment;

import com.masenf.wtaandroid.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class AboutDialogFragment extends DialogFragment {

	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.about_title);
        builder.setMessage(R.string.about_content);
        builder.setPositiveButton(R.string.about_visit, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            	String url = getResources().getString(R.string.about_url);
            	Intent i = new Intent(Intent.ACTION_VIEW);
            	i.setData(Uri.parse(url));
            	startActivity(i);
            }
        });
        builder.setNeutralButton(R.string.about_dismiss, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
        // Create the AlertDialog object and return it
        return builder.create();
    }

}
