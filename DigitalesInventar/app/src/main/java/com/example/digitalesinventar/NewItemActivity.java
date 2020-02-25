package com.example.digitalesinventar;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
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
    //ADAPTER
    static ArrayAdapter<String> adapter;
    //BUTTONS
    Button editCategories;
    Button save;
    Button cancel;
    //SCREEN WIDTH
    int screenWidth;

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
        Log.i("NewItemActivity", "setupView called");
        setContentView(R.layout.activity_add_item);
        Log.i("NewItemActivity", "xml file linked");
        initView();
        setWidths();
        setupButtons();
        setupSpinner();
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

    public void setupSpinner() {
        // Create an ArrayAdapter for the spinner using the string array and a default spinner layout
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, DatabaseActivity.categoryArray);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        categorySpinner.setAdapter(adapter);
    }

    public void setWidths() {
        screenWidth = UIhelper.screenWidth(getWindowManager());
        textViewCategory.setWidth(screenWidth/2);
        textViewName.setWidth(screenWidth/2);
        editTextName.setWidth(screenWidth/2);
        editTextLocation.setWidth(screenWidth/2);
        textViewLocation.setWidth(screenWidth/2);
        editCategories.setWidth(screenWidth/4);
        cancel.setWidth(screenWidth/2);
        save.setWidth(screenWidth/2);
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
