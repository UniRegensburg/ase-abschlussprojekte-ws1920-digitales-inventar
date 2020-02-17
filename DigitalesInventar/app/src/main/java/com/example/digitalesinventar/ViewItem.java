package com.example.digitalesinventar;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ViewItem extends AppCompatActivity {
	//UI-ELEMENTS
	//TEXT-VIEWS
	TextView textViewName;
	TextView textViewTime;
	TextView textViewCategory;
	TextView textViewLocation;
	TextView infoViewName;
	TextView infoViewTime;
	TextView infoViewCategory;
	TextView infoViewLocation;
	//BUTTONS
	Button edit;
	Button back;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i("NewItemActivity", "onCreate");
		super.onCreate(savedInstanceState);
		initView();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}


	public void initView() {
		setContentView(R.layout.item_view);
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
		//BUTTONS
		edit = findViewById(R.id.editItemButton);
		back = findViewById(R.id.backButton);

		Log.i("NewItemActivity", "initView called");
		//adjust item-layouts to fit 1/2 of the screen dynamically
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		int width = displaymetrics.widthPixels;
		Log.i("displayMetrics", "width: " + width);
		int halfWidth = width/2;

		textViewName.setWidth(halfWidth);
		textViewCategory.setWidth(halfWidth);
		textViewTime.setWidth(halfWidth);
		textViewLocation.setWidth(halfWidth);
		infoViewName.setWidth(halfWidth);
		infoViewCategory.setWidth(halfWidth);
		infoViewTime.setWidth(halfWidth);
		infoViewLocation.setWidth(halfWidth);

		back.setWidth(halfWidth);
		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//finish activity and go back to main
				finish();
			}
		});

		edit.setWidth(halfWidth);
		edit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
					//start editView
					finish();
			}
		});
		//set inputs
		//get data from intent
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		long itemID = extras.getLong("itemTs");
		//retrieve data from db
		DataModelItemList currentItem = DatabaseActivity.getItemFromDatabase(itemID);
		textViewName.setText(currentItem.getItemName());
		textViewCategory.setText(currentItem.getItemCategory());
		//format date
		long itemTs = Long.parseLong(String.valueOf(currentItem.getTimestamp()));
		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
		Date resultdate = new Date(itemTs);
		//set date
		textViewTime.setText(resultdate.toString());
		textViewLocation.setText(currentItem.getItemLocation());

	}
}
