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
		builder.setMessage("Do you want do delete this category with all items?")
			.setPositiveButton("delete including items", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					// TODO delete all items with the called category
					DatabaseActivity.deleteItemsByCategory(category);
					DatabaseActivity.deleteCategoryFromDatabase(category);
					NewCategoryActivity.categorySpinner.setSelection(0);
					Toast.makeText(getActivity(), "Category " + category + " with all items removed!", Toast.LENGTH_SHORT).show();
					Intent intentA = new Intent(getContext(), EditItemActivity.class);
					intentA.putExtra("itemTs", 0);
					startActivityForResult(intentA, 333);
					Intent intentB = new Intent(getContext(), ViewItemActivity.class);
					intentB.putExtra("itemTs", 0);
					startActivityForResult(intentB, 333);
				}
			})
			.setNegativeButton("delete without items", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					//only delete category without items included
					DatabaseActivity.deleteCategoryFromDatabase(category);
					NewCategoryActivity.categorySpinner.setSelection(0);
					Toast.makeText(getActivity(), "Category " + category + " removed!", Toast.LENGTH_SHORT).show();
				}
			});
		return builder.create();
	}

}
