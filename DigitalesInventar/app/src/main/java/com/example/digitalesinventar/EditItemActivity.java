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

//Activity for editing existing entries in the database
public class EditItemActivity extends AppCompatActivity {

	//UI-ELEMENTS
	//TEXT-VIEWS
	TextView textViewName;
	TextView textViewTime;
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
	ImageButton editImage;
	Button editBuyDate;
	Button save;
	Button cancel;
	//IMAGE VIEW
	ImageView imgView;
	//IMAGE
	Bitmap defaultImage;
	//ITEM
	DataModelItemList currentItem;
	//CONTESXT
	public static Context context;
	//ACTIVITY
	static Activity activity;
	//SEARCH
	String searchquery;
	//CATEGORY ARRAY
	ArrayList<String> catArray = new ArrayList<>();


	boolean newImage = false;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = EditItemActivity.this;
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
		editImage = findViewById(R.id.imageButton);
		editBuyDate = findViewById(R.id.addBuyDateButton);
		save = findViewById(R.id.addItemSave);
		cancel = findViewById(R.id.addItemCancel);
		//setting input filters
		editTextName.setFilters(new InputFilter[] { InputChecker.filter });
		editTextLocation.setFilters(new InputFilter[] { InputChecker.filter });
		//numbers only for value
		editTextValue.setInputType(InputType.TYPE_CLASS_NUMBER);

		final EditText [] editTexts = new EditText[]{editTextName, editTextLocation, editTextName};

		for (EditText editText : editTexts) {
			editText.setOnKeyListener(new View.OnKeyListener() {
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					// If the event is a key-down event on the "enter" button
					if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
						(keyCode == KeyEvent.KEYCODE_ENTER)) {
						UIhelper.hideKeyboard(EditItemActivity.this);
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
				if (currentItem != null) {
					context = getApplicationContext();
					DatabaseActivity.updateEntry(editTextName.getText().toString(), categorySpinner.getSelectedItem().toString(), editTextLocation.getText().toString(), textViewBuyDate.getText().toString(), editTextValue.getText().toString(), currentItem.getTimestamp(), newImage);
					newImage = false;
					Intent returnIntent = new Intent(context, ViewItemActivity.class);
					Bundle extras = new Bundle();
					extras.putLong("itemTs", currentItem.getTimestamp());
					extras.putString("searchQuery", searchquery);
					extras.putBoolean("fromMain", false);
					returnIntent.putExtras(extras);
					setResult(Activity.RESULT_OK, returnIntent);
					//set query back to def
					searchquery = "";
					finish();
				}
			}
		});

		editImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				LayoutInflater layoutInflater = LayoutInflater.from(EditItemActivity.this);
				View imgView = layoutInflater.inflate(R.layout.add_image, null);

				AlertDialog.Builder builder = new AlertDialog.Builder(EditItemActivity.this);
				final AlertDialog alertDialog = builder.create();
				alertDialog.setTitle("Bild auswählen");
				ImageButton delete = imgView.findViewById(R.id.deleteButton);
				delete.setVisibility(View.VISIBLE);
				TextView deleteText = imgView.findViewById(R.id.deleteButtonText);
				deleteText.setVisibility(View.VISIBLE);
				ImageButton camera = imgView.findViewById(R.id.cameraButton);
				ImageButton gallery = imgView.findViewById(R.id.galleryButton);

				delete.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (currentItem != null) {
							DatabaseActivity.deleteImage(String.valueOf(currentItem.getTimestamp()));
							//set imgView back to default
							DatabaseActivity.setCachedBitmap(null);
							newImage = true;
							resetShownImage();
							alertDialog.dismiss();
						}
					}
				});

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

				alertDialog.setView(imgView);
				alertDialog.show();
			}
		});
		//not working
		//if (newImage){ //why?
		//	imgView.setImageBitmap(defaultImage);
		//}
	}

	private void resetShownImage() {
		imgView.setImageBitmap(defaultImage);
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
				Bitmap bitmap = (Bitmap) extras.get("data");
				bitmap = Bitmap.createScaledBitmap(bitmap, 200, 200, false);
				DatabaseActivity.setCachedBitmap(bitmap);
				imgView.setImageBitmap(bitmap);
				newImage = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (requestCode == 333){ //code 333
			finishAndRemoveTask();
		}
	}


	private void setupAddCat() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(EditItemActivity.this);
		alertDialog.setTitle("Kategorie hinzufügen");
		alertDialog.setMessage("Gebe einen Namen ein");

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
						categorySpinner.setSelection(catArray.size()); //letzter index der spinners
						//clear input
						input.setText("");
						//hide keyboard
						UIhelper.hideKeyboard(EditItemActivity.this);
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

	public static void showToast(boolean success) {
		if (success) {
			Toast.makeText(context, "Item erfolgreich geändert!", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(context, "Hoppla, hier funktioniert etwas nicht", Toast.LENGTH_SHORT).show();
		}
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
			if (currentItem != null) {
				editTextName.setText(currentItem.getItemName()); //currentitem sometimes null, when db slower than expected
				editTextLocation.setText(currentItem.getItemLocation());
				//format and set date
				textViewTime.setText(InputChecker.formattedDate(currentItem).toString());
				textViewBuyDate.setText(currentItem.getItemBuyDate());
				editTextValue.setText(Double.toString(currentItem.getItemValue()));
				//set spinner item
				for (int i = 0; i < DatabaseActivity.categoryArray.size(); i++) {
					if (categorySpinner.getItemAtPosition(i+1).equals(currentItem.getItemCategory())) {
						categorySpinner.setSelection(i+1);
					}
				}

				DatabaseActivity.downloadImage(String.valueOf(currentItem.getTimestamp()), imgView, defaultImage);
				//downloads again after downloading in viewItemActivity
			}
		} else {
			finish();
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
