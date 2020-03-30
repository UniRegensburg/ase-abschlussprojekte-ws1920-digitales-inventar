package com.example.digitalesinventar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import java.io.IOException;

//Activity for adding a new entry to the inventar/database
public class NewItemActivity extends AppCompatActivity {

	//UI-ELEMENTS
	//TEXT-VIEWS
	TextView textViewCategory;
	TextView textViewName;
	TextView textViewLocation;
	static TextView textViewBuyDate;
	TextView textViewValue;
	//EDIT-TEXTS
	EditText editTextName;
	EditText editTextLocation;
	EditText editTextValue;
	//SPINNER
	static Spinner categorySpinner;
	//ADAPTER
	static ArrayAdapter<String> adapter;
	//BUTTONS
	ImageButton addImageByCamera;
	ImageButton addImageByPicker;
	Button editCategories;
	Button deleteCategory;
	Button addBuyDate;
	Button save;
	Button cancel;
	//IMAGE VIEW
	ImageView imgView;
//IMG-HELPER
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
		textViewBuyDate = findViewById(R.id.itemBuyDate);
		textViewValue = findViewById(R.id.textViewValue);
		//IMG_VIEW
		imgView = findViewById(R.id.imgView);
		//EDIT-TEXTS
		editTextName = findViewById(R.id.itemName);
		editTextLocation = findViewById(R.id.itemLocation);
		editTextValue = findViewById(R.id.itemValue);
		//SPINNER
		categorySpinner = (Spinner) findViewById(R.id.spinnerCategory);
		//BUTTONS
		addImageByCamera = findViewById(R.id.cameraButton);
		addImageByPicker = findViewById(R.id.pickerButton);
		editCategories = findViewById(R.id.addCatButton);
		deleteCategory = findViewById(R.id.deleteCat);
		addBuyDate = findViewById(R.id.addBuyDateButton);
		save = findViewById(R.id.addItemSave);
		cancel = findViewById(R.id.addItemCancel);

		//setting input filters
		editTextName.setFilters(new InputFilter[] { InputChecker.filter });
		editTextLocation.setFilters(new InputFilter[] { InputChecker.filter });
		//numbers only for value
		editTextValue.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
	}

	public void setupButtons() {
		editCategories.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder alertDialog = new AlertDialog.Builder(NewItemActivity.this);
				alertDialog.setTitle("Kategorie hinzufügen");
				alertDialog.setMessage("Gebe einen Namen ein");

				final EditText input = new EditText(NewItemActivity.this);
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.MATCH_PARENT);
				input.setLayoutParams(lp);
				alertDialog.setView(input);

				alertDialog.setPositiveButton(R.string.add,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							String category = input.getText().toString();
							if (InputChecker.checkEmptyInput(category)) {
								Log.i("addCat", "input not empty");
								for (int i = 0; i < DatabaseActivity.categoryArray.size(); i++) {
									//avoid multiple entries
									if (category.equals(DatabaseActivity.categoryArray.get(i))) {
										Toast.makeText(getApplicationContext(), "Kategorie " + category + " existiert bereits!", Toast.LENGTH_SHORT).show();
										return;
									}
								}
								Log.i("addCat", "input not twice");
								DatabaseActivity.addCategory(category);
								//TODO set spinner selection to added category
								//clear input
								input.setText("");
								//hide keyboard
								UIhelper.hideKeyboard(NewItemActivity.this);
								Toast.makeText(getApplicationContext(), "Kategorie " + category + " wurde erfolgreich hinzugefügt!", Toast.LENGTH_SHORT).show();
							} else {
								Toast.makeText(getApplicationContext(), "Sie müssen einen Namen eingeben", Toast.LENGTH_SHORT).show();
							}
						}
					});
				 alertDialog.setNegativeButton(R.string.cancel,
					 new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog, int which) {
												dialog.cancel();
											}
										});

				 alertDialog.show();
									}

				});

		deleteCategory.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String selectedCategory = categorySpinner.getSelectedItem().toString();
				//make sure user does not try to delete predefined categories
				if (selectedCategory.equals("Unterhaltungselektronik") || selectedCategory.equals("Haushaltsgegenstände")
					|| selectedCategory.equals("Einrichtung") || selectedCategory.equals("Hobby") || selectedCategory.equals("Werkzeug")) {
					Toast.makeText(getApplicationContext(), "Die Standardkategorie " + selectedCategory + " kann nicht gelöscht werden!", Toast.LENGTH_SHORT).show();
				}else {
					showConfirmDialog(selectedCategory);
				}
			}
		});


		addBuyDate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
						DialogFragment newFragment = new DatePickerFragment();
						newFragment.show(getSupportFragmentManager(), "datePicker");
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
					Toast.makeText(getApplicationContext(), "Neues Item hinzugefügt!", Toast.LENGTH_SHORT).show();
					finish();
				}else{
					//show toast
					Toast.makeText(getApplicationContext(),"Benenne das Item!",Toast.LENGTH_SHORT).show();
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
			String selectedCategory = "";
			if (categorySpinner.getSelectedItem() != null) {
				selectedCategory = categorySpinner.getSelectedItem().toString();
				Log.i("selectedCategory: ", " " + selectedCategory);
			}
			//add item to database
			double value;
			if (editTextValue.getText().toString().equals("")) {
				value = 0;
			}else {
				value = Double.parseDouble(editTextValue.getText().toString());
			}
			DatabaseActivity.addEntry(editTextName.getText().toString(), selectedCategory, editTextLocation.getText().toString(), textViewBuyDate.getText().toString(), value, newImage);
			newImage = false;
			return true;
		} else {
			return false;
		}
	}

	public static TextView getDateView() {
		return textViewBuyDate;
	}

	private void showConfirmDialog(String category){
		//Create Dialog
		Bundle args = new Bundle();
		args.putString(DeleteCategoriesConfirmationDialogFragment.ARG_CATEGORY, category);
		DialogFragment dialog = new DeleteCategoriesConfirmationDialogFragment();
		dialog.setArguments(args);
		dialog.show(getSupportFragmentManager(),"tag");
	}
}
