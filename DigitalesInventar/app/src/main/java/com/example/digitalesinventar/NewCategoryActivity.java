package com.example.digitalesinventar;

import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i("NewItemActivity", "onCreate");
		super.onCreate(savedInstanceState);
		setupView();
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

	public void setupView() {
		setContentView(R.layout.edit_categories);
		//UI-ELEMENTS
		//TEXT-VIEWS
		textViewOwnCat = findViewById(R.id.textViewAddCat);
		//EDIT-TEXTS
		editTextOwnCat = findViewById(R.id.editTextOwnCat);
		editTextOwnCat.setFilters(new InputFilter[] { filter });
		//SPINNER
		categorySpinner = (Spinner) findViewById(R.id.spinnerCategory);
		//BUTTONS
		addCatSave = findViewById(R.id.addCatSave);
		removeCat = findViewById(R.id.removeCategoryButton);
		addCatCancel = findViewById(R.id.addCatCancel);

		//adjust item-layouts to fit 1/3 of the screen dynamically
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		int width = displaymetrics.widthPixels;
		Log.i("displayMetrics", "width: " + width);
		int thirdWidth = width/3;

		textViewOwnCat.setWidth(thirdWidth);
		editTextOwnCat.setWidth(thirdWidth);
		addCatSave.setWidth(thirdWidth);
		removeCat.setWidth(thirdWidth);

		// Create an ArrayAdapter for spinner using the string array and a default spinner layout
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, DatabaseActivity.categoryArray);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		categorySpinner.setAdapter(adapter);

		addCatSave.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i("addCat", "add Button clicked");
				String category = editTextOwnCat.getText().toString();
				//update spinner
				//avoid empty input
				if (!category.equals("")) {
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
					editTextOwnCat.setText("");
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
				//TODO remove category from spinner
			}
		});
	}

	//these inputs are not allowed in editText for ItemName
	String blockCharacterSet = "\n";
	//input filter to avoid userinput problems
	 	InputFilter filter = new InputFilter() {
		@Override
		public CharSequence filter(CharSequence source, int start, int end,
															 Spanned dest, int dstart, int dend) {

			if (source != null && blockCharacterSet.contains(("" + source))) {
				return "";
			}
			return null;
		}
	};

}
