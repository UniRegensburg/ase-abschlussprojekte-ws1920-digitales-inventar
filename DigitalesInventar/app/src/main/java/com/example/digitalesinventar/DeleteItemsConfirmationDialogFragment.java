package com.example.digitalesinventar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

public class DeleteItemsConfirmationDialogFragment extends DialogFragment {

	public static final String ARG_TIMESTAMP = "timestamp";


	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		Bundle args = getArguments();
		final String timestamp = args.getString(ARG_TIMESTAMP);


		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage("Do you really want to delete this item?")
			.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					// The item gets deleted
					DatabaseActivity.deleteItemFromDatabase(timestamp);
				}
			})
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					// User cancelled the dialog
				}
			});
		return builder.create();
	}

}
