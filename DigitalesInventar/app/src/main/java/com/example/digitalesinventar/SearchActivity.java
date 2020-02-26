package com.example.digitalesinventar;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;


public class SearchActivity extends AppCompatActivity {


	ArrayList<DataModelItemList> dataSet;
	ArrayList<DataModelItemList> filteredList;
	ArrayAdapter adapter;
	ListView itemListView;


	//Important to handle Intent in onCreate AND onNewIntent!!
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);
		itemListView = findViewById(R.id.fragment_list);
		handleIntent(getIntent());
	}

	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
		handleIntent(intent);
	}

	private void handleIntent(Intent intent) {
		Log.i("SearchActivity", "handleIntent");
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			Log.i("handleIntent", "query: " + query);
			search(query);
		}
	}


	private void search(String query){
		Log.i("SearchActivity", "query: "+ query);
		adapter = new ItemListAdapter(DatabaseActivity.itemArray,this);
		//adapter.getFilter().filter(query);
		dataSet = DatabaseActivity.itemArray;
		Log.i("doMySearch", "dataset: " + dataSet);

		if (query.isEmpty()){
			filteredList = dataSet;
		} else {
			ArrayList<DataModelItemList> filteredData = new ArrayList<>();
			for (DataModelItemList row : dataSet) {
				if (row.getItemName().toLowerCase().contains(query.toLowerCase())) {
					filteredData.add(row);
				}
			}
			filteredList = filteredData;
		}

		Log.i("DoMySearch", "filteredList: " + filteredList.toString());
		adapter.notifyDataSetChanged();
	}

}
