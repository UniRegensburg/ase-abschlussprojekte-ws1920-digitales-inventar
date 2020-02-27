package com.example.digitalesinventar;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;


public class SearchActivity extends AppCompatActivity {


	ArrayList<DataModelItemList> dataSet;
	ArrayList<DataModelItemList> filteredList = new ArrayList<>();
	ArrayAdapter adapter;
	ListView itemListView;
	Toolbar toolbar;
	long timestamp;


	//Important to handle Intent in onCreate AND onNewIntent!!
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);
		toolbar = findViewById(R.id.toolbar);

		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

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
		adapter = new ItemListAdapter(filteredList,this);
		itemListView.setAdapter(adapter);
		dataSet = DatabaseActivity.itemArray;
		Log.i("DdoMySearch", "dataset: " + dataSet);

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
		itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent,
															View view, int position, long id) {
				DataModelItemList itemTs = (DataModelItemList) parent.getItemAtPosition(position);
				timestamp = itemTs.getTimestamp();
				Log.i("SearchActItemOnClick", "" + timestamp);
				launchViewItem();
			}
		});
	}

	private void launchViewItem() {
		Log.i("SearchActivity", "launchNewItemActivity called");
		Intent intent = new Intent(this, ViewItemActivity.class);
		Bundle extras = new Bundle();
		extras.putLong("itemTs",timestamp);
		intent.putExtras(extras);
		Log.i("SearchActivity", "intent to start viewItem created");
		startActivity(intent);
	}

}
