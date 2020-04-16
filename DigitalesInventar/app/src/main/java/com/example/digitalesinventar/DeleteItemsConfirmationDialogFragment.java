package com.example.digitalesinventar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;

public class DeleteItemsConfirmationDialogFragment extends DialogFragment {

	public static final String ARG_TIMESTAMP = "timestamp";
	public static final String ARG_COUNT = "0";


	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		Bundle args = getArguments();
		final ArrayList<String> timestamps = args.getStringArrayList(ARG_TIMESTAMP);
		final String count = args.getString(ARG_COUNT);

		int totalCount = Integer.parseInt(count);

		if (totalCount > 1){
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setMessage("Wirklich alle Items löschen?")
				.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// The item gets deleted
						for (String timestamp: timestamps) {
							DatabaseActivity.deleteItemFromDatabase(timestamp);
						}
					}
				})
				.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// User cancelled the dialog
					}
				});
			return builder.create();
		} else {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setMessage("Wirklich dieses Item löschen?")
				.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// The item gets deleted
						if (timestamps.size() > 0) {
							DatabaseActivity.deleteItemFromDatabase(timestamps.get(0));
						}
					}
				})
				.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// User cancelled the dialog
					}
				});
			return builder.create();
		}
	}

}
