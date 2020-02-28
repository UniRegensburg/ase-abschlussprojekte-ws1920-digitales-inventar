package com.example.digitalesinventar;

import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class NewCategoryActivity extends AppCompatActivity {
	//TEXT-VIEWs
	TextView textViewOwnCat;
	//EDIT-TEXTs
	EditText editTextOwnCat;
	//SPINNER
	Spinner categorySpinner;
	//ADAPTER
	static ArrayAdapter<String> adapter;
	//BUTTONS
	Button addCatSave;
	Button removeCat;
	Button addCatCancel;
	//SCREEN WIDTH
	int screenWidth;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i("NewItemActivity", "onCreate");
		super.onCreate(savedInstanceState);
		setupView();
	}

	public void setupView() {
		setContentView(R.layout.activity_edit_categories);
		initView();
		setWidths();
		setupSpinner();
		setupButtons();
	}

	public void initView() {
		//UI-ELEMENTS
		//TEXT-VIEWS
		textViewOwnCat = findViewById(R.id.textViewAddCat);
		//EDIT-TEXTS
		editTextOwnCat = findViewById(R.id.editTextOwnCat);
		editTextOwnCat.setFilters(new InputFilter[] { InputChecker.filter });
		//SPINNER
		categorySpinner = (Spinner) findViewById(R.id.spinnerCategory);
		//BUTTONS
		addCatSave = findViewById(R.id.addCatSave);
		removeCat = findViewById(R.id.removeCategoryButton);
		addCatCancel = findViewById(R.id.addCatCancel);
	}

	public void setupButtons() {
		addCatSave.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i("addCat", "add Button clicked");
				String category = editTextOwnCat.getText().toString();
				//update spinner
				//avoid empty input
				if (InputChecker.checkEmptyInput(category)) {
					Log.i("addCat", "input not empty");
					for (int i = 0; i < DatabaseActivity.categoryArray.size(); i++) {
						//avoid multiple entries
						if (category.equals(DatabaseActivity.categoryArray.get(i))) {
							Toast.makeText(getApplicationContext(), "Category " + category + " already exists!", Toast.LENGTH_SHORT).show();
							return;
						}
					}
					Log.i("addCat", "input not twice");
					DatabaseActivity.addCategory(category);
					//TODO set spinner selection to added category
					//clear input
					editTextOwnCat.setText("");
					//hide keyboard
					UIhelper.hideKeyboard(NewCategoryActivity.this);
					Toast.makeText(getApplicationContext(), "Category " + category + " was successfully added!", Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(getApplicationContext(), "please enter a category", Toast.LENGTH_SHORT).show();
				}

			}
		});
		addCatCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		removeCat.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String selectedCategory = categorySpinner.getSelectedItem().toString();
				//make sure user does not try to delete predefined categories
				if (selectedCategory.equals("Unterhaltungselektronik") || selectedCategory.equals("Haushaltsgegenstände")
					|| selectedCategory.equals("Einrichtung") || selectedCategory.equals("Hobby") || selectedCategory.equals("Werkzeug")) {
					Toast.makeText(getApplicationContext(), "Default category " + selectedCategory + " can't be removed!", Toast.LENGTH_SHORT).show();
				}else {
					DatabaseActivity.deleteCategoryFromDatabase(selectedCategory);
					categorySpinner.setSelection(0);
					Toast.makeText(getApplicationContext(), "Category " + selectedCategory + " removed!", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	public void setupSpinner() {
		// Create an ArrayAdapter for spinner using the string array and a default spinner layout
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, DatabaseActivity.categoryArray);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		categorySpinner.setAdapter(adapter);
	}

	public void setWidths() {
		screenWidth = UIhelper.screenWidth(getWindowManager());
		textViewOwnCat.setWidth(screenWidth/3);
		editTextOwnCat.setWidth(screenWidth/3);
		addCatSave.setWidth(screenWidth/3);
		removeCat.setWidth(screenWidth/3);
	}

}
