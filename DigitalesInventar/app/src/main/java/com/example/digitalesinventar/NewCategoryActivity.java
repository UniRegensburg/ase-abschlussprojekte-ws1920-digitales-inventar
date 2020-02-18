package com.example.digitalesinventar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class NewCategoryActivity extends AppCompatActivity {
	//TEXT-VIEWs
	TextView textViewOwnCat;
	//EDIT-TEXTs
	EditText editTextOwnCat;
	//BUTTONS
	Button addCatSave;
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
		setContentView(R.layout.add_category);
		//UI-ELEMENTS
		//TEXT-VIEWS
		textViewOwnCat = findViewById(R.id.textViewAddCat);
		//EDIT-TEXTS
		editTextOwnCat = findViewById(R.id.editTextOwnCat);
		editTextOwnCat.setFilters(new InputFilter[] { filter });
		//BUTTONS
		addCatSave = findViewById(R.id.addCatSave);
		addCatCancel = findViewById(R.id.addCatCancel);

		//adjust item-layouts to fit 1/2 of the screen dynamically
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		int width = displaymetrics.widthPixels;
		Log.i("displayMetrics", "width: " + width);
		int halfWidth = width/2;

		textViewOwnCat.setWidth(halfWidth);
		editTextOwnCat.setWidth(halfWidth);
		addCatCancel.setWidth(halfWidth);
		addCatSave.setWidth(halfWidth);

		addCatSave.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent resultIntent = new Intent();
				String category = editTextOwnCat.getText().toString();
				resultIntent.putExtra("category", category);
				setResult(Activity.RESULT_OK, resultIntent);
				finish();
			}
		});
		addCatCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
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
