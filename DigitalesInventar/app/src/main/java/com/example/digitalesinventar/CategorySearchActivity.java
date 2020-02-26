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

import java.util.ArrayList;

public class CategorySearchActivity extends AppCompatActivity {


	ArrayList<DataModelItemList> dataSet;
	ArrayList<DataModelItemList> filteredList = new ArrayList<>();
	ArrayAdapter adapter;
	ListView itemListView;
	long timestamp;


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
		Log.i("CategoryActivity", "handleIntent");
		String catName = getIntent().getStringExtra("catName");
		Log.i("handleIntent", "catName: " + catName);
		search(catName);
	}


	private void search(String catName){
		Log.i("SearchActivity", "catName: "+ catName);
		adapter = new ItemListAdapter(filteredList,this);
		itemListView.setAdapter(adapter);
		dataSet = DatabaseActivity.itemArray;
		Log.i("DoMySearch", "dataset: " + dataSet);

		if (catName.isEmpty()){
			filteredList = dataSet; //no empty cats
		} else {
			filteredList.clear();
			//Log.i("DoMySearch", "catName: "+catName);
			for (DataModelItemList row : dataSet) {
				//Log.i("DoMySearch", "all rows: " + row.getItemCategory());
				if (row.getItemCategory().toLowerCase().contains(catName.toLowerCase())) { //maybe equals better but cat names longer than buttons
				//Log.i("DoMySearch", "hit row: " + row.getItemCategory());
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
