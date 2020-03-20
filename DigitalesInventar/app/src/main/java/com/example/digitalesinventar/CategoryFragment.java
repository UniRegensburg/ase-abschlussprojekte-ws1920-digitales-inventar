package com.example.digitalesinventar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class CategoryFragment extends Fragment {

	static RecyclerView itemListView;
	private ArrayList<String> catArray = new ArrayList<>();
	private static CategoryListAdapter catArrayAdapter;
	String catName;

	public CategoryFragment(){

	}

	public static void sortByNameDown() {
		Log.d("catSort", "nameDownn");
		Collections.sort(DatabaseActivity.categoryArray, new Comparator<String>() {
			//kein fan davon die liste in der db zu sortieren
			//db liste in adapter allerdings nötig für updateList()
			@Override
			public int compare(String s1, String s2) {
				return s2.compareToIgnoreCase(s1);
			}
		});
		catArrayAdapter.notifyDataSetChanged();
	}

	public static void sortByNameUp() {
		Log.d("catSort", "nameUp");
		Collections.sort(DatabaseActivity.categoryArray, new Comparator<String>() {
			//kein fan davon die liste in der db zu sortieren
			//db liste in adapter allerdings nötig für updateList()
			@Override
			public int compare(String s1, String s2) {
				return s1.compareToIgnoreCase(s2);
			}
		});
		catArrayAdapter.notifyDataSetChanged();
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
													 Bundle savedInstanceState) {
		View view= inflater.inflate(R.layout.fragment_main, container, false);
		itemListView = view.findViewById(R.id.fragment_list);
		itemListView.setLayoutManager(new LinearLayoutManager(getContext()));
/*
		itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent,
															View view, int position, long id) {
				catName = (String) parent.getItemAtPosition(position);
				Log.i("catOnClick", "" + catName);
				launchCat();
			}
		});
*/
		/*
		launchSwipeMenu();
		itemListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
				switch (index) {
					case 0:
						// edit
						Log.i("onMenuItemClicked", "Edit");
						String category = (String) itemListView.getItemAtPosition(position);
						editCategory(category);
						break;
					case 1:
						// delete
						Log.i("onMenuItemClicked", "Delete");
						String itemCategory = (String) itemListView.getItemAtPosition(position);
						deleteCategory(itemCategory);
						break;
				}
				return false;
			}
		});
		 */

		setupList();
		return view;
	}

	//link custom adapter with ListView for db entries
	public void setupList() {
		Log.i("catActivityFragment", "setupList called");
		catArrayAdapter = new CategoryListAdapter(getActivity(), DatabaseActivity.categoryArray);
		itemListView.setAdapter(catArrayAdapter);
		Log.i("catActivityFragment", "listAdapter set");
	}

	public static void updateList() {
		Log.i("catActivityFragment", "adapter dataset changed");
		catArrayAdapter.notifyDataSetChanged();
	}

	private void editCategory(String category){
		if (category.equals("Unterhaltungselektronik") || category.equals("Haushaltsgegenstände")
			|| category.equals("Einrichtung") || category.equals("Hobby") || category.equals("Werkzeug")) {
			Toast.makeText(getContext(), "Die Standardkategorie " + category + " kann nicht umbenannt werden!", Toast.LENGTH_SHORT).show();
		}else {
			showChangeDialog(category);
		}
	}

	private void showChangeDialog(final String category){
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
		alertDialog.setTitle("Kategorie \"" + category + "\" umbenennen");
		alertDialog.setMessage("Gebe einen neuen Namen ein");

		final EditText input = new EditText(getActivity());
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
			LinearLayout.LayoutParams.MATCH_PARENT,
			LinearLayout.LayoutParams.MATCH_PARENT);
		input.setLayoutParams(lp);
		alertDialog.setView(input);

		alertDialog.setPositiveButton(R.string.edit,
			new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					String newCategory = input.getText().toString();
					if (InputChecker.checkEmptyInput(newCategory)) {
						Log.i("addCat", "input not empty");
						for (int i = 0; i < DatabaseActivity.categoryArray.size(); i++) {
							//avoid multiple entries
							if (newCategory.equals(DatabaseActivity.categoryArray.get(i))) {
								Toast.makeText(getContext(), "Kategorie " + newCategory + " existiert bereits!", Toast.LENGTH_SHORT).show();
								return;
							}
						}
						Log.i("addCat", "input not twice");
						DatabaseActivity.updateCategoryInDatabase(category, newCategory);
						//clear input
						input.setText("");
						//hide keyboard
						UIhelper.hideKeyboard(getActivity());
						Toast.makeText(getContext(), "Kategorie " + newCategory + " wurde erfolgreich umbenannt!", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(getContext(), "Sie müssen einen Namen eingeben", Toast.LENGTH_SHORT).show();
					}
				}
			});
		alertDialog.setNegativeButton(R.string.cancel,
			new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});
		alertDialog.show();
	}

	private void deleteCategory(String category){
		//make sure user does not try to delete predefined categories
		if (category.equals("Unterhaltungselektronik") || category.equals("Haushaltsgegenstände")
			|| category.equals("Einrichtung") || category.equals("Hobby") || category.equals("Werkzeug")) {
			Toast.makeText(getContext(), "Die Standardkategorie " + category + " kann nicht gelöscht werden!", Toast.LENGTH_SHORT).show();
		}else {
			showConfirmDialog(category);
		}
	}

	private void showConfirmDialog(String category){
		//Create Dialog
		Bundle args = new Bundle();
		args.putString(DeleteCategoriesConfirmationDialogFragment.ARG_CATEGORY, category);
		DialogFragment dialog = new DeleteCategoriesConfirmationDialogFragment();
		dialog.setArguments(args);
		dialog.show(getFragmentManager(),"tag");
	}

	private void launchCat() {
		Log.i("catActivity", "launchCatActivity called for " + catName);
		Intent intent = new Intent(getActivity(), SearchActivity.class);
		Bundle extras = new Bundle();
		extras.putString("searchQuery",catName);
		extras.putBoolean("fromMain", true);
		intent.putExtras(extras);
		Log.i("catctivity", "intent to start search created");
		startActivity(intent);
	}

	/*
	private void launchSwipeMenu(){
		Log.i("CategoryFragment", "launchSwipeMenu called");

		SwipeMenuCreator creator = new SwipeMenuCreator() {
			@Override
			public void create(SwipeMenu menu) {
				// create "edit" item
				SwipeMenuItem editItem = new SwipeMenuItem(getActivity());
				// set item background
				editItem.setBackground(R.color.primaryVariant);
				// set item width
				editItem.setWidth(250);
				// set item title
				editItem.setIcon(R.drawable.ic_edit);
				// add to menu
				menu.addMenuItem(editItem);

				// create "delete" item
				SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity());
				// set item background
				deleteItem.setBackground(R.color.error);
				// set item width
				deleteItem.setWidth(250);
				// set a icon
				deleteItem.setIcon(R.drawable.ic_delete);
				// add to menu
				menu.addMenuItem(deleteItem);
			}
		};
		//set creator
		itemListView.setMenuCreator(creator);
	}
*/

	private void doLiveUpdates(String query) {
		DatabaseActivity.loadBackup();
		ArrayList<DataModelItemList> dataSet = new ArrayList<>();
		dataSet.addAll(DatabaseActivity.itemArray);
		Log.i("DoMyFilter", "dataset: " + dataSet);
		catArray.clear();
		if (query.isEmpty()) {
			//filteredList.addAll(dataSet); //search doesn't get called on empty input
		} else {
			for (DataModelItemList row : dataSet) {
				//if (row.getItemName().toLowerCase().contains(query.toLowerCase())) {
				//	filteredList.add(row);
				//}// else
				if (row.getItemCategory().toLowerCase().contains(query.toLowerCase())) {
					catArray.add(row.getItemCategory());
				}// else if (row.getItemLocation().toLowerCase().contains(query.toLowerCase())) {
					//    filteredList.add(row);
					//}
				//}
			}

			Log.i("DoMyFilter", "catList: " + catArray);
			catArrayAdapter.notifyDataSetChanged();
		}
	}

}
