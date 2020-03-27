package com.example.digitalesinventar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PlaceSearchActivity extends AppCompatActivity {

	ArrayList<DataModelItemList> dataSet;
	ArrayList<DataModelItemList> filteredList = new ArrayList<>();
	ItemListAdapter adapter;
	RecyclerView itemListView;
	long timestamp;
	Toolbar toolbar;
	TextView result;
	Button backButton;
	Bitmap defaultBitmap;


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
		defaultBitmap = BitmapFactory.decodeResource(this.getResources(),
			R.drawable.img_holder);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
		handleIntent(intent);
	}

	private void handleIntent(Intent intent) {
		Log.i("PlaceActivity", "handleIntent");
		String placeName = getIntent().getStringExtra("placeName");
		Log.i("handleIntent", "placeName: " + placeName);
		search(placeName);
	}


	private void search(String placeName) {
		Log.i("SearchActivity", "placeName: " + placeName);
		result.setText("Ort '" + placeName + "':");
		adapter = new ItemListAdapter(this, filteredList, defaultBitmap);
		itemListView.setAdapter(adapter);
		dataSet = DatabaseActivity.itemArray;
		Log.i("DoMySearch", "dataset: " + dataSet);

		if (placeName.isEmpty()) {
			filteredList = dataSet; //no empty cats
		} else {
			filteredList.clear();
			//Log.i("DoMySearch", "catName: "+catName);
			for (DataModelItemList row : dataSet) {
				//Log.i("DoMySearch", "all rows: " + row.getItemCategory());
				if (row.getItemLocation().toLowerCase().contains(placeName.toLowerCase())) { //maybe equals better but cat names longer than buttons
					//Log.i("DoMySearch", "hit row: " + row.getItemCategory());
					filteredList.add(row);
				}
			}
		}

		Log.i("DoMySearch", "filteredList: " + filteredList);
		adapter.notifyDataSetChanged();
	/*	itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent,
															View view, int position, long id) {
				DataModelItemList itemTs = (DataModelItemList) parent.getItemAtPosition(position);
				timestamp = itemTs.getTimestamp();
				Log.i("SearchActItemOnClick", "" + timestamp);
				launchViewItem();
			}
		});
*/
	}
	/*private void launchViewItem() {
		Log.i("SearchActivity", "launchNewItemActivity called");
		Intent intent = new Intent(this, ViewItemActivity.class);
		Bundle extras = new Bundle();
		extras.putLong("itemTs",timestamp);
		intent.putExtras(extras);
		Log.i("SearchActivity", "intent to start viewItem created");
		startActivity(intent);
	}*/
}
