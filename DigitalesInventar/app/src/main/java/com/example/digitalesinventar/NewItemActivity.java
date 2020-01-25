package com.example.digitalesinventar;

import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

    public void initView(){
        Log.i("NewItemActivity", "initView called");
        Button cancel = findViewById(R.id.addItemCancel);
        cancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button save = findViewById(R.id.addItemSave);
        save.setOnClickListener(new View.OnClickListener(){
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

    public void setupView(){
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
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            if (source != null && blockCharacterSet.contains(("" + source))) {
                return "";
            }
            return null;
        }
    };

}
