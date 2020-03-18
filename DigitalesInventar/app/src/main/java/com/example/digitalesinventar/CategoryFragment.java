package com.example.digitalesinventar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import java.util.ArrayList;

public class CategoryFragment extends Fragment {

	static SwipeMenuListView itemListView;
	private ArrayList<String> catArray = new ArrayList<>();
	private static CategoryListAdapter catArrayAdapter;
	String catName;

	public CategoryFragment(){

	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
													 Bundle savedInstanceState) {
		View view= inflater.inflate(R.layout.fragment_main, container, false);
		itemListView = view.findViewById(R.id.fragment_list);

		//itemListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		//launchMultipleItemSelection();

		itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent,
															View view, int position, long id) {
				catName = (String) parent.getItemAtPosition(position);
				Log.i("catOnClick", "" + catName);
				launchCat();
			}
		});

		launchSwipeMenu();
		itemListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
				switch (index) {
					case 0:
						// delete
						Log.i("onMenuItemClicked", "Delete");
						String itemCategory = (String) itemListView.getItemAtPosition(position);
						deleteCategory(itemCategory);
				}
				return false;
			}
		});

		setupList();
		return view;
	}

	//link custom adapter with ListView for db entries
	public void setupList() {
		Log.i("catActivityFragment", "setupList called");
		/*for (int i=0; i<DatabaseActivity.itemArray.size(); i++) {
			if (!catArray.contains(DatabaseActivity.itemArray.get(i).itemCategory)) {
				Log.i("cat_setupList", "bin da" + DatabaseActivity.itemArray.get(i).itemCategory );
				catArray.add(DatabaseActivity.itemArray.get(i).itemCategory);
			}
			Log.i("cat_setupList", "catArray:" + catArray);
		}*/
		//catArray = DatabaseActivity.categoryArray;
		catArrayAdapter = new CategoryListAdapter(DatabaseActivity.categoryArray, getActivity());
		itemListView.setAdapter(catArrayAdapter);
		Log.i("catActivityFragment", "listAdapter set");
	}

	public static void updateList() {
		Log.i("catActivityFragment", "adapter dataset changed");
		catArrayAdapter.notifyDataSetChanged();
	}

	private void deleteCategory(String category){
		//make sure user does not try to delete predefined categories
		if (category.equals("Unterhaltungselektronik") || category.equals("Haushaltsgegenstände")
			|| category.equals("Einrichtung") || category.equals("Hobby") || category.equals("Werkzeug")) {
			Toast.makeText(getContext(), "Default category " + category + " can't be removed!", Toast.LENGTH_SHORT).show();
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

	private void launchSwipeMenu(){
		Log.i("CategoryFragment", "launchSwipeMenu called");

		SwipeMenuCreator creator = new SwipeMenuCreator() {
			@Override
			public void create(SwipeMenu menu) {
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
