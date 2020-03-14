package com.example.digitalesinventar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

import java.io.IOException;

//Activity for adding a new entry to the inventar/database
public class NewItemActivity extends AppCompatActivity {

	//UI-ELEMENTS
	//TEXT-VIEWS
	TextView textViewCategory;
	TextView textViewName;
	TextView textViewLocation;
	//EDIT-TEXTS
	EditText editTextName;
	EditText editTextLocation;
	//SPINNER
	static Spinner categorySpinner;
	//ADAPTER
	static ArrayAdapter<String> adapter;
	//BUTTONS
	ImageButton addImageByCamera;
	ImageButton addImageByPicker;
	Button editCategories;
	Button save;
	Button cancel;
	//IMAGE VIEW
	ImageView imgView;

	boolean newImage = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i("NewItemActivity", "onCreate");
		super.onCreate(savedInstanceState);
		setupView();
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 42) { //image
			Log.d("loadPicker", "2");
			if (data != null && data.getData() != null) {

				Uri uri = data.getData();
				try {
					Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
					//scale bitmap down before compressing to handle larger images
					//TODO crop to square
					bitmap = Bitmap.createScaledBitmap(bitmap, 200, 200, false);
					DatabaseActivity.setCachedBitmap(bitmap);
					imgView.setImageBitmap(bitmap);
					newImage = true;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else if (requestCode == 999) {
			try {
				Bundle extras = data.getExtras();
				Bitmap imageBitmap = (Bitmap) extras.get("data");
				//scale bitmap down before compressing to handle larger images
				//TODO crop to square
				imageBitmap = Bitmap.createScaledBitmap(imageBitmap, 200, 200, false);
				imgView.setImageBitmap(imageBitmap);
				DatabaseActivity.setCachedBitmap(imageBitmap);
				newImage = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void setupView() {
		Log.i("NewItemActivity", "setupView called");
		setContentView(R.layout.activity_add_item);
		Log.i("NewItemActivity", "xml file linked");
		initView();
		setupButtons();
		setupSpinner();
	}

	public void initView() {
		//UI-ELEMENTS
		//TEXT-VIEWS
		textViewCategory = findViewById(R.id.textViewCategory);
		textViewName = findViewById(R.id.textViewName);
		textViewLocation = findViewById(R.id.textViewLocation);
		//IMG_VIEW
		imgView = findViewById(R.id.imgView);
		//EDIT-TEXTS
		editTextName = findViewById(R.id.itemName);
		editTextLocation = findViewById(R.id.itemLocation);
		//SPINNER
		categorySpinner = (Spinner) findViewById(R.id.spinnerCategory);
		//BUTTONS
		addImageByCamera = findViewById(R.id.cameraButton);
		addImageByPicker = findViewById(R.id.pickerButton);
		editCategories = findViewById(R.id.addCatButton);
		save = findViewById(R.id.addItemSave);
		cancel = findViewById(R.id.addItemCancel);

		//setting input filters
		editTextName.setFilters(new InputFilter[] { InputChecker.filter });
		editTextLocation.setFilters(new InputFilter[] { InputChecker.filter });
	}

	public void setupButtons() {
		editCategories.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//start editCategories activity
				Intent intent = new Intent(getApplicationContext(),NewCategoryActivity.class);
				startActivity(intent);
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
				if (getNewItem()) {
					Toast.makeText(getApplicationContext(), "new Item added!", Toast.LENGTH_SHORT).show();
					//TODO wait for database to return toast(?)
					finish();
				}else{
					//show toast
					Toast.makeText(getApplicationContext(),"Please name your item!",Toast.LENGTH_SHORT).show();
				}
			}
		});

		addImageByCamera.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
					startActivityForResult(takePictureIntent, 999);
				}
			}
		});
		addImageByPicker.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(Intent.createChooser(intent, "Select Picture"), 42);
			}
		});
	}

	public void setupSpinner() {
		// Create an ArrayAdapter for the spinner using the string array and a default spinner layout
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, DatabaseActivity.categoryArray);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		categorySpinner.setAdapter(adapter);
	}

	//get new item from EditText to add new database entry
	public boolean getNewItem() {
		if (InputChecker.checkEmptyInput(editTextName.getText().toString())) {
			//get spinner input
			String selectedCategory = categorySpinner.getSelectedItem().toString();
			Log.i("selectedCategory: ", " " + selectedCategory);
			//add item to database
			DatabaseActivity.addEntry(editTextName.getText().toString(), selectedCategory, editTextLocation.getText().toString(), newImage);
			newImage = false;
			return true;
		} else {
			return false;
		}
	}

}
