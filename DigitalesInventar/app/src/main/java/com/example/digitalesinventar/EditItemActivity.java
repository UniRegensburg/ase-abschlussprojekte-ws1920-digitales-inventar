package com.example.digitalesinventar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

//Activity for editing existing entries in the database
public class EditItemActivity extends AppCompatActivity {

	//UI-ELEMENTS
	//TEXT-VIEWS
	TextView textViewName;
	TextView textViewTime;
	TextView textViewTimeInfo;
	TextView textViewLocation;
	TextView textViewCategory;
	//EDIT-TEXTS
	EditText editTextName;
	EditText editTextLocation;
	//SPINNER
	Spinner categorySpinner;
	//ADAPTER
	static ArrayAdapter<String> adapter;
	//BUTTONS
	ImageButton addImage;
	Button editCategories;
	Button save;
	Button cancel;
	//IMAGE VIEW
	ImageView imgView;
	//SCREEN WIDTH
	int screenWidth;
	DataModelItemList currentItem;
	//Context
	public static Context context;
	String searchquery;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i("EditItemActivity", "onCreate");
		super.onCreate(savedInstanceState);
		setupView();
	}

	public void setupView() {
		setContentView(R.layout.activity_edit_item);
		initView();
		setWidths();
		setupButtons();
		setupSpinner();
		assignDataFromIntent();
	}

	public void initView() {
		//UI-ELEMENTS
		//TEXT-VIEWS
		textViewName = findViewById(R.id.textViewName);
		textViewTime = findViewById(R.id.itemTime);
		textViewTimeInfo = findViewById(R.id.textViewTime);
		textViewLocation = findViewById(R.id.textViewLocation);
		textViewCategory = findViewById(R.id.textViewCategory);
		//IMG_VIEW
		imgView = findViewById(R.id.imgView);
		//EDIT-TEXTS
		editTextName = findViewById(R.id.itemName);
		editTextLocation = findViewById(R.id.itemLocation);
		//SPINNER
		categorySpinner = findViewById(R.id.spinnerCategory);
		//BUTTONS
		addImage = findViewById(R.id.imageButton);
		editCategories = findViewById(R.id.addCatButton);
		save = findViewById(R.id.addItemSave);
		cancel = findViewById(R.id.addItemCancel);


		//setting input filters
		editTextName.setFilters(new InputFilter[] { InputChecker.filter });
		editTextLocation.setFilters(new InputFilter[] { InputChecker.filter });
	}

	public void setWidths() {
		screenWidth = UIhelper.screenWidth(getWindowManager());
		textViewName.setWidth(screenWidth/2);
		textViewTime.setWidth(screenWidth/2);
		textViewTimeInfo.setWidth(screenWidth/2);
		textViewLocation.setWidth(screenWidth/2);
		textViewCategory.setWidth(screenWidth/2);
		editTextName.setWidth(screenWidth/2);
		editTextLocation.setWidth(screenWidth/2);
		editCategories.setWidth(screenWidth/2);
		save.setWidth(screenWidth/2);
		cancel.setWidth(screenWidth/2);
	}

	public void setupButtons() {
		//ClickListener BUTTONS
		editCategories.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//start editCategories activity
				Intent intent = new Intent(getApplicationContext(),NewCategoryActivity.class);
				startActivityForResult(intent, 69);
			}
		});

		cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		save.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				context = getApplicationContext();
				DatabaseActivity.updateEntry(Long.toString(currentItem.getTimestamp()), editTextName.getText().toString(), categorySpinner.getSelectedItem().toString(), editTextLocation.getText().toString(), currentItem.getTimestamp());
				Intent returnIntent = new Intent(context, ViewItemActivity.class);
				Bundle extras = new Bundle();
				extras.putLong("itemTs",currentItem.getTimestamp());
				extras.putString("searchQuery", searchquery);
				Log.d("editedItem", ":" + editTextName.getText());
				returnIntent.putExtras(extras);
				setResult(Activity.RESULT_OK, returnIntent);
				//set query back to def
				searchquery = "";
				finish();
			}
		});

		addImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//TODO ADD IMAGE
			}
		});
	}

	public static void showToast(boolean success) {
		if (success) {
			Toast.makeText(context, "Item successfully edited!", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(context, "Currently not working please check", Toast.LENGTH_SHORT).show();
		}
	}

	public void setupSpinner() {
		// Create an ArrayAdapter for the spinner using the string array and a default spinner layout
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, DatabaseActivity.categoryArray);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		categorySpinner.setAdapter(adapter);
	}

	public void assignDataFromIntent() {
		//get data from intent
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		long itemID = extras.getLong("itemTs");
		//set query for search
		searchquery = extras.getString("searchQuery");
		//retrieve data from db
		currentItem = DatabaseActivity.getItemFromDatabase(itemID);
		editTextName.setText(currentItem.getItemName());
		editTextLocation.setText(currentItem.getItemLocation());
//format and set date
		textViewTime.setText(InputChecker.formattedDate(currentItem).toString());

		//set spinner item
		for (int i=0; i < DatabaseActivity.categoryArray.size(); i++) {
			if (categorySpinner.getItemAtPosition(i).equals(currentItem.getItemCategory())) {
				categorySpinner.setSelection(i);
			}
		}

		//TODO: set current img
		//imgView.setImageBitmap();
	}

	//get new item from EditText to add new database entry
	public boolean getNewItem() {

		if (InputChecker.checkEmptyInput(editTextName.getText().toString())) {
			//get spinner input
			String selectedCategory = categorySpinner.getSelectedItem().toString();
			Log.i("selectedCategory: ", " " + selectedCategory);
			//add item to database
			DatabaseActivity.addEntry(editTextName.getText().toString(), selectedCategory, editTextLocation.getText().toString());
			return true;
			} else {
			return false;
		}
	}
}
