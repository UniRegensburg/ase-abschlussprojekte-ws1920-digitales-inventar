package com.example.digitalesinventar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
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
import java.util.ArrayList;

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
	ImageButton addImage;
	Button addBuyDate;
	Button save;
	Button cancel;
	//IMAGE VIEW
	ImageView imgView;
	Bitmap defaultImage;
	//IMG-HELPER
	boolean newImage = false;
	//CATEGORY-ARRAY
	ArrayList<String> catArray = new ArrayList<>();
	//ACTIVITY
	static Activity activity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = NewItemActivity.this;
		setupView();
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 42) { //image
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
		setContentView(R.layout.activity_add_item);
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
		defaultImage = BitmapFactory.decodeResource(this.getResources(),
			R.drawable.img_holder);
		//EDIT-TEXTS
		editTextName = findViewById(R.id.itemName);
		editTextLocation = findViewById(R.id.itemLocation);
		editTextValue = findViewById(R.id.itemValue);
		//SPINNER
		categorySpinner = (Spinner) findViewById(R.id.spinnerCategory);
		//BUTTONS
		addImage = findViewById(R.id.imageButton);
		addBuyDate = findViewById(R.id.addBuyDateButton);
		save = findViewById(R.id.addItemSave);
		cancel = findViewById(R.id.addItemCancel);
		//setting input filters
		editTextName.setFilters(new InputFilter[] { InputChecker.filter });
		editTextLocation.setFilters(new InputFilter[] { InputChecker.filter });
		//numbers only for value
		editTextValue.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

		final EditText [] editTexts = new EditText[]{editTextName, editTextLocation, editTextName};

		for (EditText editText : editTexts) {
			editText.setOnKeyListener(new View.OnKeyListener() {
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					// If the event is a key-down event on the "enter" button
					if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
						(keyCode == KeyEvent.KEYCODE_ENTER)) {
						UIhelper.hideKeyboard(NewItemActivity.this);
						return true;
					}
					return false;
				}
			});
		}
	}

	public static void hideKeyboard() {
		UIhelper.hideKeyboard(activity);
	}

	public void setupButtons() {
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

		addImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				LayoutInflater layoutInflater = LayoutInflater.from(NewItemActivity.this);
				View imgView = layoutInflater.inflate(R.layout.add_image, null);

				AlertDialog.Builder builder = new AlertDialog.Builder(NewItemActivity.this);
				final AlertDialog alertDialog = builder.create();
				alertDialog.setTitle("Bild auswählen");
				ImageButton delete = imgView.findViewById(R.id.deleteButton);
				ImageButton camera = imgView.findViewById(R.id.cameraButton);
				ImageButton gallery = imgView.findViewById(R.id.galleryButton);

				camera.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
						if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
							startActivityForResult(takePictureIntent, 999);
						}
						alertDialog.dismiss();
					}
				});

				gallery.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent();
						intent.setType("image/*");
						intent.setAction(Intent.ACTION_GET_CONTENT);
						startActivityForResult(Intent.createChooser(intent, "Select Picture"), 42);
						alertDialog.dismiss();
					}
				});

				delete.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						//set imgView back to default
						DatabaseActivity.setCachedBitmap(null);
						resetShownImage();
						alertDialog.dismiss();
					}
				});

				alertDialog.setView(imgView);
				alertDialog.show();
			}
		});
	}

	private void resetShownImage() {
		imgView.setImageBitmap(defaultImage);
	}

	private void setupAddCat() {
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
						for (int i = 0; i < DatabaseActivity.categoryArray.size(); i++) {
							//avoid multiple entries
							if (category.equals(DatabaseActivity.categoryArray.get(i))) {
								Toast.makeText(getApplicationContext(), "Kategorie " + category + " existiert bereits!", Toast.LENGTH_SHORT).show();
								return;
							}
						}
						DatabaseActivity.addCategory(category);
						catArray.add(category);
						adapter.notifyDataSetChanged();
						categorySpinner.setSelection(catArray.size()); //letzter index des spinners
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

	public void setupSpinner() {
		// Create an ArrayAdapter for the spinner using the string array and a default spinner layout
		catArray.clear();
		catArray.add("neue Kategorie hinzufügen");
		catArray.addAll(DatabaseActivity.categoryArray);
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, catArray);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		categorySpinner.setAdapter(adapter);
		categorySpinner.setSelection(1);
		categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
		{
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
			{
				String selectedItem = parent.getItemAtPosition(position).toString();
				if(selectedItem.equals("neue Kategorie hinzufügen"))
				{
					setupAddCat();
				}
			} // to close the onItemSelected
			public void onNothingSelected(AdapterView<?> parent)
			{

			}
		});
	}

	//get new item from EditText to add new database entry
	public boolean getNewItem() {
		if (InputChecker.checkEmptyInput(editTextName.getText().toString())) {
			//get spinner input
			String selectedCategory = "";
			if (categorySpinner.getSelectedItem() != null) {
				selectedCategory = categorySpinner.getSelectedItem().toString();
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
