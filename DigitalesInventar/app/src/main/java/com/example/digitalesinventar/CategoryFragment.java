package com.example.digitalesinventar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.Collections;
import java.util.Comparator;

public class CategoryFragment extends Fragment {

	static RecyclerView itemListView;
	private static CategoryListAdapter catArrayAdapter;
	private SwipeController swipeController = null;
	String[] searchArray;

//empty default-constructor
	public CategoryFragment(){}

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
		setupList();
		setupSwipeController();
		return view;
	}

	//link custom adapter with ListView for db entries
	public void setupList() {
		Log.i("catActivityFragment", "setupList called");
		catArrayAdapter = new CategoryListAdapter(getActivity(), DatabaseActivity.categoryArray);
		itemListView.setAdapter(catArrayAdapter);
		itemListView.setLayoutManager(new LinearLayoutManager(getContext()));
		Log.i("catActivityFragment", "listAdapter set");
	}

	public static void updateList() {
		Log.i("catActivityFragment", "adapter dataset changed");
		catArrayAdapter.notifyDataSetChanged();
		switch (MainActivity.spinnerPos) {
			case 0:
				sortByNameUp();
				break;
			case 1:
				sortByNameDown();
				break;
		}
	}

	private void editCategory(String category){
		if (category.equals("Einrichtung") || category.equals("Hobby") || category.equals("Kleidung")) {
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
		input.setFilters(new InputFilter[] { InputChecker.filter });
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
			LinearLayout.LayoutParams.MATCH_PARENT,
			LinearLayout.LayoutParams.MATCH_PARENT);
		input.setLayoutParams(lp);
		input.setMaxLines(1);
		input.setInputType(InputType.TYPE_CLASS_TEXT);
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
		if (category.equals("Einrichtung") || category.equals("Hobby") || category.equals("Kleidung")) {
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

	void setupSearchListener(final MaterialSearchView searchView){
		Log.i("CatActivityFragment", "setupSearchListener");
		searchArray = DatabaseActivity.categoryArray.toArray(new String[0]);

		searchView.closeSearch();
		searchView.setSuggestions(searchArray);
		searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				Log.i("CatOnQueryTextSubmit", "Query: " + query);
				Intent intent = new Intent(getActivity(), SearchActivity.class); //search for all items
				intent.putExtra("searchQuery", query);
				Log.i("CategoryFragment", "intent to start search created");
				startActivity(intent);
				searchView.closeSearch();
				return true;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				Log.i("SetupSearchListener", "onQueryTextChange");
				return false;
			}
		});
		searchView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
															int position, long id) {
				Intent intent = new Intent(getActivity(), CategorySearchActivity.class); //search for category specifically
				intent.putExtra("catName", parent.getItemAtPosition(position).toString());
				Log.i("CatActivityFrag", "intent to start SearchActivity created w/ " + parent.getItemAtPosition(position).toString());
				startActivity(intent);
			}
		});
	}

	private void setupSwipeController() {
		swipeController = new SwipeController(getContext(), new SwipeControllerActions() {
			@Override
			public void onRightClicked(int position) {
				Log.i("onMenuItemClicked", "Delete");
				String itemCategory = catArrayAdapter.dataSet.get(position);
				deleteCategory(itemCategory);
				catArrayAdapter.notifyDataSetChanged();
			}

			@Override
			public void onLeftClicked(int position) {
				Log.i("onMenuItemClicked", "Edit");
				String category = catArrayAdapter.dataSet.get(position);
				editCategory(category);
			}
		});

		ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
		itemTouchhelper.attachToRecyclerView(itemListView);

		itemListView.addItemDecoration(new RecyclerView.ItemDecoration() {
			@Override
			public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
				swipeController.onDraw(c);
			}
		});
	}

}
