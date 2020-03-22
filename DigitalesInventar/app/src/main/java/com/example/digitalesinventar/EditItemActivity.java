package com.example.digitalesinventar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

//Activity for editing existing entries in the database
public class EditItemActivity extends AppCompatActivity {

	//UI-ELEMENTS
	//TEXT-VIEWS
	TextView textViewName;
	TextView textViewTime;
	TextView textViewTimeInfo;
	TextView textViewLocation;
	TextView textViewCategory;
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
	ImageButton deleteImage;
	Button editBuyDate;
	Button editCategories;
	Button deleteCategory;
	Button save;
	Button cancel;
	//IMAGE VIEW
	ImageView imgView;
	//IMAGE
	Bitmap defaultImage;
	DataModelItemList currentItem;
	//Context
	public static Context context;
	String searchquery;

	boolean newImage = false;
	//Bitmap cachedBitmap;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i("EditItemActivity", "onCreate");
		super.onCreate(savedInstanceState);
		setupView();
	}

	public void setupView() {
		setContentView(R.layout.activity_edit_item);
		initView();
		setupButtons();
		setupSpinner();
		assignDataFromIntent();
	}

	public void initView() {
		//UI-ELEMENTS
		//TEXT-VIEWS
		textViewName = findViewById(R.id.itemName);
		textViewTime = findViewById(R.id.itemTime);
		//textViewTimeInfo = findViewById(R.id.textViewTime);
		textViewLocation = findViewById(R.id.textViewLocation);
		textViewCategory = findViewById(R.id.textViewCategory);
		textViewBuyDate = findViewById(R.id.itemBuyDate);
		textViewValue =findViewById(R.id.textViewValue);
		//IMG_VIEW
		imgView = findViewById(R.id.imgView);
		//IMAGE
		defaultImage = BitmapFactory.decodeResource(this.getResources(),
			R.drawable.img_holder);
		//EDIT-TEXTS
		editTextName = findViewById(R.id.itemName);
		editTextLocation = findViewById(R.id.itemLocation);
		editTextValue = findViewById(R.id.itemValue);
		//SPINNER
		categorySpinner = findViewById(R.id.spinnerCategory);
		//BUTTONS
		addImageByCamera = findViewById(R.id.cameraButton);
		addImageByPicker = findViewById(R.id.pickerButton);
		deleteImage = findViewById(R.id.deleteButton);
		editCategories = findViewById(R.id.addCatButton);
		deleteCategory = findViewById(R.id.deleteCat);
		editBuyDate = findViewById(R.id.addBuyDateButton);
		save = findViewById(R.id.addItemSave);
		cancel = findViewById(R.id.addItemCancel);

		//setting input filters
		editTextName.setFilters(new InputFilter[] { InputChecker.filter });
		editTextLocation.setFilters(new InputFilter[] { InputChecker.filter });
		//numbers only for value
		editTextValue.setInputType(InputType.TYPE_CLASS_NUMBER);
	}

	public void setupButtons() {
		//ClickListener BUTTONS
		editCategories.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder alertDialog = new AlertDialog.Builder(EditItemActivity.this);
				alertDialog.setTitle("Kategorie hinzufügen");
				alertDialog.setMessage("Geben Sie eine Kategorie ein");

				final EditText input = new EditText(EditItemActivity.this);
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
								UIhelper.hideKeyboard(EditItemActivity.this);
								Toast.makeText(getApplicationContext(), "Kategorie " + category + " erfolgreich hinzugefügt!", Toast.LENGTH_SHORT).show();
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

		editBuyDate.setOnClickListener(new View.OnClickListener() {
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
				context = getApplicationContext();
				//Log.d("new-ui-debug", "edit save, ts "+ currentItem.getTimestamp() + "; name " + editTextName.getText() + "; cat " + categorySpinner.getSelectedItem() + "; ort " + editTextLocation.getText() + "; date " + textViewBuyDate.getText() + "; wert " + editTextValue.getText());
				DatabaseActivity.updateEntry(editTextName.getText().toString(), categorySpinner.getSelectedItem().toString(), editTextLocation.getText().toString(), textViewBuyDate.getText().toString(), editTextValue.getText().toString(), currentItem.getTimestamp(), newImage);
				//^ TODO crashes     java.lang.NullPointerException: Attempt to invoke virtual method 'java.lang.String java.lang.Object.toString()' on a null object reference
				//reproduce: edit item -> edit kat -> new cat -> back -> save item (no new cat) -> edit item -> edit cat -> del new cat w/ items -> save
				//zur not: try & catch
				newImage = false;
				Intent returnIntent = new Intent(context, ViewItemActivity.class);
				Bundle extras = new Bundle();
				extras.putLong("itemTs",currentItem.getTimestamp());
				extras.putString("searchQuery", searchquery);
				extras.putBoolean("fromMain", false);
				Log.d("editedItem", ":" + editTextName.getText());
				returnIntent.putExtras(extras);
				setResult(Activity.RESULT_OK, returnIntent);
				//set query back to def
				searchquery = "";
				finish();
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

		deleteImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DatabaseActivity.deleteImage(String.valueOf(currentItem.getTimestamp()));
				//set imgView back to default
				DatabaseActivity.setCachedBitmap(null);
				newImage = true;
				imgView.setImageBitmap(defaultImage);
			}
		});
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
				Bitmap bitmap = (Bitmap) extras.get("data");
				//scale bitmap down before compressing to handle larger images
				//TODO crop to square
				bitmap = Bitmap.createScaledBitmap(bitmap, 200, 200, false);
				DatabaseActivity.setCachedBitmap(bitmap);
				imgView.setImageBitmap(bitmap);
				newImage = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (requestCode == 333){ //code 333
			//ohne if wird aufgerufen
			Log.d("delkat", "edit code 333");
			finishAndRemoveTask();
		}
	}

	public static void showToast(boolean success) {
		if (success) {
			Toast.makeText(context, "Item erfolgreich geändert!", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(context, "Hoppla, hier funktioniert etwas nicht", Toast.LENGTH_SHORT).show();
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
		//check if item was deleted
		if (itemID != 0) {
			//retrieve data from db
			currentItem = DatabaseActivity.getItemFromDatabase(itemID);
			editTextName.setText(currentItem.getItemName());
			editTextLocation.setText(currentItem.getItemLocation());
			//format and set date
			textViewTime.setText(InputChecker.formattedDate(currentItem).toString());
			textViewBuyDate.setText(currentItem.getItemBuyDate());
			editTextValue.setText(Double.toString(currentItem.getItemValue()));
			//set spinner item
			for (int i = 0; i < DatabaseActivity.categoryArray.size(); i++) {
				if (categorySpinner.getItemAtPosition(i).equals(currentItem.getItemCategory())) {
					categorySpinner.setSelection(i);
				}
			}

			DatabaseActivity.downloadImage(String.valueOf(currentItem.getTimestamp()), imgView);
			//downloads again after downloading in viewItemActivity
		} else {
			Log.d("delkat", "edit code 0");
			finish();
			//finishAndRemoveTask();
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