package com.example.digitalesinventar;

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

public class PlaceSearchActivity extends AppCompatActivity {

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
		String placeName = getIntent().getStringExtra("placeName");
		search(placeName);
	}


	private void search(String placeName) {
		result.setText("Ort '" + placeName + "':");
		adapter = new ItemListAdapter(this, filteredList);
		itemListView.setAdapter(adapter);
		dataSet = DatabaseActivity.itemArray;
		if (placeName.isEmpty()) {
			filteredList = dataSet; //no empty cats
		} else {
			filteredList.clear();
			for (DataModelItemList row : dataSet) {
				if (row.getItemLocation().toLowerCase().contains(placeName.toLowerCase())) {
					filteredList.add(row);
				}
			}
		}
		adapter.notifyDataSetChanged();
	}
}
