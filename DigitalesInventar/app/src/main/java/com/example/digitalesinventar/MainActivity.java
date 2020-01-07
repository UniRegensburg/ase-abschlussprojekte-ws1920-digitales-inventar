package com.example.digitalesinventar;

import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    // Access a Cloud Firestore instance from your Activity
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static List<List<String>> itemArray = new ArrayList<List<String>>();

    public static List<List<String>> getItemArray() {
        return itemArray;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupMainMenu();
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
        Button cancel = findViewById(R.id.addItemCancel);
        cancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                setupMainMenu();
            }
        });

        Button save = findViewById(R.id.addItemSave);
        save.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
               Snackbar.make(v, "Item is saved", Snackbar.LENGTH_LONG)
                       .setAction("Action", null).show();
               //create Database entry();
                getNewItem();
                getDataFromDatabase();
                //setupMainMenu();
            }
        });
    }

    private void getDataFromDatabase() {
        db.collection("items")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ArrayList<String> entry = new ArrayList<String>();
                                entry.add(document.get("name").toString());
                                entry.add(document.get("ts").toString());
                                itemArray.add(entry);
                                setupMainMenu();
                                Log.i("loadEntry", "item loaded from db");
                            }
                        } else {
                            Log.i("loadEntry", "item not loaded from db");
                        }
                    }
                });
    }


    public void setupMainMenu(){

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setContentView(R.layout.add_item);
                initView();
            }
        });

    }

    //get new item from EditText to add new database entry
    public void getNewItem() {
        EditText itemName = (EditText)findViewById(R.id.itemName);
        DatabaseActivity.addEntry(itemName.getText().toString());
    }
}
