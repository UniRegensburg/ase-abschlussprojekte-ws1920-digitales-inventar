package com.example.digitalesinventar;

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

import com.google.android.material.snackbar.Snackbar;

//Activity for adding a new entry to the inventar/database
public class NewItemActivity extends AppCompatActivity {
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
        Log.i("NewItemActivity", "initView called");
        //adjust item-layouts to fit 1/2 of the screen dynamically
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int width = displaymetrics.widthPixels;
        Log.i("displayMetrics", "width: " + width);
        int halfWidth = width/2;

        TextView textViewCategory = findViewById(R.id.textViewCategory);
        textViewCategory.setWidth(halfWidth);
        TextView textViewName = findViewById(R.id.textViewName);
        textViewName.setWidth(halfWidth);
        TextView textViewLocation = findViewById(R.id.textViewLocation);
        textViewLocation.setWidth(halfWidth);

        //Spinner categorySpinner = findViewById(R.id.spinnerCategory);
        //categorySpinner.setWidth(halfWidth);
        EditText editTextName = findViewById(R.id.itemName);
        editTextName.setWidth(halfWidth);
        EditText editTextLocation = findViewById(R.id.itemLocation);
        editTextLocation.setWidth(halfWidth);


        Button cancel = findViewById(R.id.addItemCancel);
        cancel.setWidth(halfWidth);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button save = findViewById(R.id.addItemSave);
        save.setWidth(halfWidth);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getNewItem();
                //DatabaseActivity.getDataFromDatabase(); //data loads itself after added entry
                Snackbar.make(v, "Item is saved", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                //NOTE: Snackbar does currently not work!
                finish();
            }
        });
    }

    public void setupView() {
        Log.i("NewItemActivity", "setupView called");
        setContentView(R.layout.add_item);
        Log.i("NewItemActivity", "xml file linked");
        EditText itemName = (EditText)findViewById(R.id.itemName);
        itemName.setFilters(new InputFilter[] { filter });
        initView();
    }

    //get new item from EditText to add new database entry
    public void getNewItem() {
        EditText itemName = (EditText)findViewById(R.id.itemName);
        if (checkEmptyInput(itemName.getText().toString())) {
            DatabaseActivity.addEntry(itemName.getText().toString());
        } else {
            //show toast
        }
    }

    private boolean checkEmptyInput(String input) {
        if (input.equals("")) {
            return false;
        } else {
            return true;
        }
    }


    //these inpputs are not allowed in editText for ItemName
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
