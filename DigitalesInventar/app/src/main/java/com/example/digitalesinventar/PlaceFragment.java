package com.example.digitalesinventar;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class PlaceFragment extends Fragment {

	static RecyclerView itemListView;
	private static ArrayList<String> placeArray = new ArrayList<>();
	private static PlaceListAdapter placeArrayAdapter;
	String placeName;
	String[] searchArray;

	public PlaceFragment(){

	}

	public static void sortByNameDown() {
		Collections.sort(placeArray, new Comparator<String>() {
			@Override
			public int compare(String s1, String s2) {
				return s2.compareToIgnoreCase(s1);
			}
		});
		placeArrayAdapter.notifyDataSetChanged();
	}

	public static void sortByNameUp() {
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
		return view;
	}

	void setupSearchListener(final MaterialSearchView searchView){
		searchArray = placeArray.toArray(new String[0]);

		searchView.closeSearch();
		searchView.setSuggestions(searchArray);
		searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				Intent intent = new Intent(getActivity(), SearchActivity.class); //search with all items
				intent.putExtra("searchQuery", query);
				startActivity(intent);
				searchView.closeSearch();
				return true;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				return false;
			}
		});
		searchView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
															int position, long id) {
				Intent intent = new Intent(getActivity(), PlaceSearchActivity.class); //search specifically for place
				intent.putExtra("placeName", parent.getItemAtPosition(position).toString());
				startActivity(intent);
			}
		});
	}

	//link custom adapter with ListView for db entries
	public void setupList() {
		placeArray.clear();
		for (int i=0; i<DatabaseActivity.itemArray.size(); i++) {
			if (!placeArray.contains(DatabaseActivity.itemArray.get(i).itemLocation) &&
			DatabaseActivity.itemArray.get(i).itemLocation != "") {
				placeArray.add(DatabaseActivity.itemArray.get(i).itemLocation);
			}
		}
		placeArrayAdapter = new PlaceListAdapter(getActivity(), placeArray);
		itemListView.setAdapter(placeArrayAdapter);
	}

	public static void updateList() { //TODO call at add/update item in db
		placeArrayAdapter.notifyDataSetChanged();
		switch (MainActivity.spinnerPos) { //not really necessary as places dont update in this scope
			case 0:
				sortByNameUp();
				break;
			case 1:
				sortByNameDown();
				break;
		}
	}
}
