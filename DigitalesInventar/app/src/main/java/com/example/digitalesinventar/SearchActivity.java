package com.example.digitalesinventar;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class SearchActivity extends AppCompatActivity {


	ArrayList<DataModelItemList> dataSet;
	public ArrayList<DataModelItemList> filteredList = new ArrayList<>();
	ItemListAdapter adapter;
	RecyclerView itemListView;
	long timestamp;
	String searchquery;
	TextView result;
	Button backButton;


	//Important to handle Intent in onCreate AND onNewIntent!!
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);
		result = findViewById(R.id.searchresult);
		itemListView = findViewById(R.id.fragment_list);
		itemListView.setLayoutManager(new LinearLayoutManager(this));
		backButton = findViewById(R.id.backButton);
		backButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		handleIntent(getIntent());
	}

	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
		handleIntent(intent);
	}

	private void handleIntent(Intent intent) {
		Log.i("SearchActivity", "handleIntent");
		if (intent.getStringExtra("searchQuery") != null && !intent.getStringExtra("searchQuery").equals("")) {
			search(intent.getStringExtra("searchQuery"));
			searchquery = "";
		} else if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			Log.i("handleIntent", "query: " + query);
			searchquery = query;
			search(query);
		}
	}


	public void search(String query){
		result.setText("Suchergebnis für '" + query + "'");
		Log.i("SearchActivity", "query: "+ query);
		adapter = new ItemListAdapter(this, filteredList);
		itemListView.setAdapter(adapter);
		dataSet = DatabaseActivity.itemArray;
		Log.i("DoMySearch", "dataset: " + dataSet);

		if (query.isEmpty()){
			filteredList = dataSet; //search doesn't get called on empty input
		} else {
			filteredList.clear();
			for (DataModelItemList row : dataSet) {
				if (row.getItemName().toLowerCase().contains(query.toLowerCase())) {
					filteredList.add(row);
				} else if (row.getItemCategory().toLowerCase().contains(query.toLowerCase())) {
					filteredList.add(row);
				} else if (row.getItemLocation().toLowerCase().contains(query.toLowerCase())) {
					filteredList.add(row);
				}
			}
		}
		Log.i("DoMySearch", "filteredList: " + filteredList);
		adapter.notifyDataSetChanged();
	}

	private void launchViewItem() {
		Log.i("SearchActivity", "launchNewItemActivity called");
		Intent intent = new Intent(this, ViewItemActivity.class);
		Bundle extras = new Bundle();
		extras.putLong("itemTs",timestamp);
		extras.putString("searchQuery", searchquery);
		intent.putExtras(extras);
		Log.i("SearchActivity", "intent to start viewItem created");
		startActivity(intent);
		finish();
	}

}
