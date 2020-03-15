package com.example.digitalesinventar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

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
		Log.i("deleteItemConfirmDialog", "totalCount" + totalCount);

		if (totalCount > 1){
			Log.i("deleteItemConfirmDialog", "hier" + totalCount);
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setMessage("Do you really want to delete all items?")
				.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// The item gets deleted
						Log.i("deleteItemConfirmDialog", "timestamps" + timestamps);
						for (String timestamp: timestamps) {
							DatabaseActivity.deleteItemFromDatabase(timestamp);
						}
					}
				})
				.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// User cancelled the dialog
					}
				});
			return builder.create();
		} else {
			Log.i("deleteItemConfirmDialog", "dort" + totalCount);
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setMessage("Do you really want to delete this item?")
				.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// The item gets deleted
						Log.i("deleteItemConfirmDialog", "timestamps" + timestamps);
						DatabaseActivity.deleteItemFromDatabase(timestamps.get(0));
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

}
