package com.example.digitalesinventar;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ViewItemActivity extends AppCompatActivity {
	//UI-ELEMENTS
	//TEXT-VIEWS
	static TextView textViewName;
	static TextView textViewTime;
	static TextView textViewCategory;
	static TextView textViewLocation;
	TextView infoViewName;
	TextView infoViewTime;
	TextView infoViewCategory;
	TextView infoViewLocation;
	//IMAGE VIEW
	static ImageView imgView;
	//BUTTONS
	Button edit;
	Button back;
	//SCREEN WIDTH
	int screenWidth;
	String searchquery;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i("NewItemActivity", "onCreate");
		super.onCreate(savedInstanceState);
		//make sure no old values are in searchquery
		searchquery = "";
		setupView();
	}

	public void setupView() {
		setContentView(R.layout.activity_view_item);
		initView();
		setWidths();
		setupButtons();
		assignDataFromIntent(getIntent());
	}

	public void initView() {
		//UI-ELEMENTS
		//TEXT-VIEWS
		textViewName = findViewById(R.id.Name);
		textViewCategory = findViewById(R.id.Category);
		textViewTime = findViewById(R.id.Time);
		textViewLocation = findViewById(R.id.Location);
		infoViewName = findViewById(R.id.Name_Item);
		infoViewCategory = findViewById(R.id.Category_Item);
		infoViewTime = findViewById(R.id.Time_Item);
		infoViewLocation = findViewById(R.id.Location_Item);
		//IMG_VIEW
		imgView = findViewById(R.id.imgView);
		//BUTTONS
		edit = findViewById(R.id.editItemButton);
		back = findViewById(R.id.backButton);
	}

	public void setWidths() {
		screenWidth = UIhelper.screenWidth(getWindowManager());
		textViewName.setWidth(screenWidth/2);
		textViewCategory.setWidth(screenWidth/2);
		textViewTime.setWidth(screenWidth/2);
		textViewLocation.setWidth(screenWidth/2);
		infoViewName.setWidth(screenWidth/2);
		infoViewCategory.setWidth(screenWidth/2);
		infoViewTime.setWidth(screenWidth/2);
		infoViewLocation.setWidth(screenWidth/2);
		back.setWidth(screenWidth/2);
		edit.setWidth(screenWidth/2);
	}

	public void setupButtons() {
		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//finish activity and go back to main
				if (searchquery != null &&!searchquery.equals("")) {
					Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
					intent.putExtra("searchQuery", searchquery);
					startActivity(intent);
				}
				finish();
			}
		});

		edit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//start editView
				Intent intentA = getIntent();
				Bundle extras = intentA.getExtras();
				Intent intentB = new Intent(getApplicationContext(),EditItemActivity.class);
				//get bundle from MainActivity and pass the timestamp to EditItemActivity
				Log.d("extras","extras: " + extras.toString());
				intentB.putExtras(extras);
				startActivityForResult(intentB, 666);
			}
		});
	}

	public void assignDataFromIntent(Intent intent) {
		//set inputs
		//get data from intent
		Bundle extras = intent.getExtras();
		long itemID = extras.getLong("itemTs");
		searchquery = extras.getString("searchQuery");
		//retrieve data from db
		DataModelItemList currentItem = DatabaseActivity.getItemFromDatabase(itemID);
		textViewName.setText(currentItem.getItemName());
		textViewCategory.setText(currentItem.getItemCategory());
		//format and set date
		textViewTime.setText(InputChecker.formattedDate(currentItem).toString());
		textViewLocation.setText(currentItem.getItemLocation());
		Log.d("Intent data: ",  "" + currentItem.getItemName());

		DatabaseActivity.downloadImage(String.valueOf(currentItem.getTimestamp()), imgView);
	}

	public static void updateDataAfterEdit(DataModelItemList currentItem, boolean newImage, Bitmap bitmap) {
		Log.d("updateItemView", "1");
		//name & cat
		textViewName.setText(currentItem.getItemName());
		textViewCategory.setText(currentItem.getItemCategory());
		//format and set date
		textViewTime.setText(InputChecker.formattedDate(currentItem).toString());
		textViewLocation.setText(currentItem.getItemLocation());
		if (newImage) { //check logs for updateItemView: image is cached, bool works
			//but view is only sometimes updated, sometimes even w/ no new img log
			Log.d("updateItemView", "2 new img");
			imgView.setImageBitmap(bitmap);
		} else {
			Log.d("updateItemView", "2 no new img");
		}
		//TODO also check for deleted images (compare edititem todo)
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d("onActivityResult", "called");
		// Check which request we're responding to
		if (requestCode == 666) {
			// Make sure the request was successful
			if (resultCode == Activity.RESULT_OK) {
				Bundle extras = data.getExtras();
				Log.d("onActivityResult", "Intent data" + extras.toString());
				assignDataFromIntent(data);
			}
		}
	}
}
