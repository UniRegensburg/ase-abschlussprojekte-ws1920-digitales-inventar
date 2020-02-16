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
    Spinner categorySpinner;
    //BUTTONS
    Button save;
    Button cancel;

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

    public void initView() {
        //UI-ELEMENTS
        //TEXT-VIEWS
        textViewCategory = findViewById(R.id.textViewCategory);
        textViewName = findViewById(R.id.textViewName);
        textViewLocation = findViewById(R.id.textViewLocation);
        //EDIT-TEXTS
        editTextName = findViewById(R.id.itemName);
        editTextLocation = findViewById(R.id.itemLocation);
        //SPINNER
        categorySpinner = (Spinner) findViewById(R.id.spinnerCategory);
        //BUTTONS
        save = findViewById(R.id.addItemSave);
        cancel = findViewById(R.id.addItemCancel);

        Log.i("NewItemActivity", "initView called");
        //adjust item-layouts to fit 1/2 of the screen dynamically
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int width = displaymetrics.widthPixels;
        Log.i("displayMetrics", "width: " + width);
        int halfWidth = width/2;

        textViewCategory.setWidth(halfWidth);
        textViewName.setWidth(halfWidth);
        textViewLocation.setWidth(halfWidth);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
          R.array.categories_spinner, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        categorySpinner.setAdapter(adapter);

        editTextName.setWidth(halfWidth);
        editTextLocation.setWidth(halfWidth);

        cancel.setWidth(halfWidth);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        save.setWidth(halfWidth);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getNewItem()) {
                    Toast.makeText(getApplicationContext(), "new Item added!", Toast.LENGTH_SHORT).show();
                    finish();
                }else{
                    //show toast
                    Toast.makeText(getApplicationContext(),"Please name your item!",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void setupView() {
        Log.i("NewItemActivity", "setupView called");
        setContentView(R.layout.add_item);
        Log.i("NewItemActivity", "xml file linked");
        initView();
        //setting input filters
        editTextName.setFilters(new InputFilter[] { filter });
        editTextLocation.setFilters(new InputFilter[] { filter });
    }

    //get new item from EditText to add new database entry
    public boolean getNewItem() {

        if (checkEmptyInput(editTextName.getText().toString())) {
            //get spinner input
            TextView selectedItem = (TextView)categorySpinner.getSelectedView();
            //add item to database
            DatabaseActivity.addEntry(editTextName.getText().toString(), selectedItem.getText().toString(), editTextLocation.getText().toString());
            return true;
        } else {
            return false;
        }
    }

    private boolean checkEmptyInput(String input) {
        if (input.equals("")) {
            return false;
        } else {
            return true;
        }
    }


    //these inputs are not allowed in editText for ItemName
    String blockCharacterSet = "\n";

    //input filter to avoid userinput problems
    private InputFilter filter = new InputFilter() {

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
