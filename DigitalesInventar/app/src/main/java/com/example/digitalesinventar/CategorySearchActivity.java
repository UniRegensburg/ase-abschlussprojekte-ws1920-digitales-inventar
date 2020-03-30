package com.example.digitalesinventar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CategorySearchActivity extends AppCompatActivity {


	ArrayList<DataModelItemList> dataSet;
	ArrayList<DataModelItemList> filteredList = new ArrayList<>();
	ItemListAdapter adapter;
	RecyclerView itemListView;
	TextView result;
	Button backButton;


	//Important to handle Intent in onCreate AND onNewIntent!!
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);
		itemListView = findViewById(R.id.fragment_list);
		itemListView.setLayoutManager(new LinearLayoutManager(this));
		result = findViewById(R.id.searchresult);
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
		Log.i("CategoryActivity", "handleIntent");
		Log.i("handleIntent", "catName: " + catName);
		search(catName);
	}


	private void search(String catName) {
		Log.i("SearchActivity", "catName: " + catName);
		result.setText("Kategorie '" + catName + "':");
		adapter = new ItemListAdapter(this, filteredList);
		itemListView.setAdapter(adapter);
		dataSet = DatabaseActivity.itemArray;
		Log.i("DoMySearch", "dataset: " + dataSet);

		if (catName.isEmpty()) {
			filteredList = dataSet; //no empty cats
		} else {
			filteredList.clear();
			for (DataModelItemList row : dataSet) {
				if (row.getItemCategory().toLowerCase().contains(catName.toLowerCase())) {
					filteredList.add(row);
				}
			}
		}
		Log.i("DoMySearch", "filteredList: " + filteredList);
		adapter.notifyDataSetChanged();
	}

}
