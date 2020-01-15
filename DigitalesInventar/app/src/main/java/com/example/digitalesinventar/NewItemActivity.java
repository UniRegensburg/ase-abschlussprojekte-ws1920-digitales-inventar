package com.example.digitalesinventar;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.snackbar.Snackbar;

import java.io.Console;

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
                DatabaseActivity.getDataFromDatabase();
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
        initView();
    }

    //get new item from EditText to add new database entry
    public void getNewItem() {
        EditText itemName = (EditText)findViewById(R.id.itemName);
        DatabaseActivity.addEntry(itemName.getText().toString());
    }

}
