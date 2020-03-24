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
				return s2.compareToIgnoreCase(s1);
			}
		});
		placeArrayAdapter.notifyDataSetChanged();
	}

	public static void sortByNameUp() {
		Log.d("placeSort", "nameUp");
		Collections.sort(placeArray, new Comparator<String>() {
			@Override
			public int compare(String s1, String s2) {
				return s1.compareToIgnoreCase(s2);
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
		switch (MainActivity.spinnerPos) { //not really necessary as places dont update in this scope
			case 0:
				sortByNameDown();
				break;
			case 1:
				sortByNameUp();
				break;
		}
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
