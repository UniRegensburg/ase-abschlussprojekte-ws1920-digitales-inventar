package com.example.digitalesinventar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class PlaceFragment extends Fragment {

	static RecyclerView itemListView;
	private static ArrayList<String> placeArray = new ArrayList<>();
	private static PlaceListAdapter placeArrayAdapter;
	String placeName;

	public PlaceFragment(){

	}

	public static void sortByNameDown() {
		Log.d("placeSort", "nameDownn");
		Collections.sort(placeArray, new Comparator<String>() {
			@Override
			public int compare(String s1, String s2) {
				return s1.compareToIgnoreCase(s2);
			}
		});
		placeArrayAdapter.notifyDataSetChanged();
	}

	public static void sortByNameUp() {
		Log.d("placeSort", "nameUp");
		Collections.sort(placeArray, new Comparator<String>() {
			@Override
			public int compare(String s1, String s2) {
				return s2.compareToIgnoreCase(s1);
			}
		});
		placeArrayAdapter.notifyDataSetChanged();
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
													 Bundle savedInstanceState) {
		View view= inflater.inflate(R.layout.fragment_main, container, false);
		itemListView = view.findViewById(R.id.fragment_list);
		itemListView.setLayoutManager(new LinearLayoutManager(getContext()));
		//itemListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		//launchMultipleItemSelection();

		/*itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent,
															View view, int position, long id) {
				placeName = (String) parent.getItemAtPosition(position);
				Log.i("placeOnClick", "" + placeName);
				launchPlace();
			}
		});
*/
		//launchSwipeMenu();
		/*itemListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
				switch (index) {
					case 0:
						// edit
						Log.i("onMenuItemClicked", "Edit");
						String catName = (String) itemListView.getItemAtPosition(position);

						Intent intent = new Intent(getActivity(),EditItemActivity.class);
						Bundle extras = new Bundle();
						extras.putString("catName",catName);
						extras.putBoolean("fromMain", true);
						intent.putExtras(extras);
						startActivityForResult(intent, 666);
						break;
					case 1:
						// delete
						Log.i("onMenuItemClicked", "Delete");
						DataModelItemList itemDelete = (DataModelItemList) itemListView.getItemAtPosition(position);
						String itemTimestamp = String.valueOf(itemDelete.getTimestamp());

						ArrayList<String> delete_list = new ArrayList<>();
						delete_list.add(itemTimestamp);
						showConfirmDialog(delete_list,"1");
						break;
				}
				return false;
			}
		});
		 */

		setupList();
		Log.i("placeActivityFragment", "setupList called");
		return view;
	}

	//link custom adapter with ListView for db entries
	public void setupList() {
		Log.i("placeActivityFragment", "setupList called");
		for (int i=0; i<DatabaseActivity.itemArray.size(); i++) {
			if (!placeArray.contains(DatabaseActivity.itemArray.get(i).itemLocation) &&
			DatabaseActivity.itemArray.get(i).itemLocation != "") {
				placeArray.add(DatabaseActivity.itemArray.get(i).itemLocation);
			}
		}
		placeArrayAdapter = new PlaceListAdapter(getActivity(), placeArray);
		itemListView.setAdapter(placeArrayAdapter);
		Log.i("placeActivityFragment", "listAdapter set");
	}

	public static void updateList() { //TODO call at add/update item in db
		Log.i("placeActivityFragment", "adapter dataset changed");
		placeArrayAdapter.notifyDataSetChanged();
	}

	private void launchPlace() {
		Log.i("placeActivity", "launchPlaceActivity called for " + placeName);
		Intent intent = new Intent(getActivity(), SearchActivity.class);
		Bundle extras = new Bundle();
		extras.putString("searchQuery",placeName);
		extras.putBoolean("fromMain", true);
		intent.putExtras(extras);
		Log.i("placeActivity", "intent to start search created");
		startActivity(intent);
	}

	/*
	private void launchSwipeMenu(){
		Log.i("MainActivityFragment", "launchSwipeMenu called");

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
		placeArray.clear();
		if (query.isEmpty()) {
			//filteredList.addAll(dataSet); //search doesn't get called on empty input
		} else {
			for (DataModelItemList row : dataSet) {
				//if (row.getItemName().toLowerCase().contains(query.toLowerCase())) {
				//	filteredList.add(row);
				//}// else
				//if (row.getItemCategory().toLowerCase().contains(query.toLowerCase())) {
				//	catArray.add(row.getItemCategory());
				//} else
				if (row.getItemLocation().toLowerCase().contains(query.toLowerCase())) {
					    placeArray.add(row.getItemLocation());
					//}
				}
			}

			Log.i("DoMyFilter", "placeList: " + placeArray);
			placeArrayAdapter.notifyDataSetChanged();
		}
	}
}
