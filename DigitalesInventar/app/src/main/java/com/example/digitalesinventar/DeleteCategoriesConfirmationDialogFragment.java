package com.example.digitalesinventar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

public class DeleteCategoriesConfirmationDialogFragment extends DialogFragment {

	public static final String ARG_CATEGORY = "category";


	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		Bundle args = getArguments();
		final String category = args.getString(ARG_CATEGORY);

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage("Wollen sie diese Kategorie mit allen Items löschen?")
			.setPositiveButton("Items löschen", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					// TODO delete all items with the called category
					DatabaseActivity.deleteItemsByCategory(category);
					//DatabaseActivity.deleteCategoryFromDatabase(category); //categories already deleted in deleteItemsByCategory
					Toast.makeText(getActivity(), "Kategorie " + category + " wurde mit allen Items gelöscht!", Toast.LENGTH_SHORT).show();
					Intent intentA = new Intent(getContext(), EditItemActivity.class);
					intentA.putExtra("itemTs", 0);
					startActivityForResult(intentA, 333);
					Intent intentB = new Intent(getContext(), ViewItemActivity.class);
					intentB.putExtra("itemTs", 0);
					startActivityForResult(intentB, 333);
					//was wenn wir in new Item sind?
				}
			})
			.setNegativeButton("Items behalten", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					//only delete category without items included
					DatabaseActivity.deleteCategoryFromDatabase(category);
					try {
						EditItemActivity.categorySpinner.setSelection(0); //in case spinner is not yet initialised
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						NewItemActivity.categorySpinner.setSelection(0); //in case spinner is not yet initialised
					} catch (Exception e) {
						e.printStackTrace();
					}
					Toast.makeText(getActivity(), "Kategorie " + category + " wurde gelöscht!", Toast.LENGTH_SHORT).show();
				}
			});
		return builder.create();
	}

}
