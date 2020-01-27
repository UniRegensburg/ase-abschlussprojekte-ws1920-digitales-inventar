package com.example.digitalesinventar;

import android.util.Log;
import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *Handles all background activity from the firebase db.
 *entrys can be added and removed.
 *provides access for information to be displayed.
 */

public class DatabaseActivity {
    // Access a Cloud Firestore instance from your Activity
    static FirebaseFirestore db = FirebaseFirestore.getInstance();
    //ArrayList to store firebase data for displaying later
    public static ArrayList<DataModelItemList> itemArray = new ArrayList<DataModelItemList>();

    //add an entry to database
    public static void addEntry(String name) {
        Log.d("DB addEntry", "item added");
        long tsLong = System.currentTimeMillis();
        String ts = Long.toString(tsLong);
        Map<String, Object> entry = new HashMap<>();
        entry.put("name", name);
        entry.put("ts", ts);

        db.collection("items").document(ts)
                .set(entry)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void avoid) {
                        Log.d("DB addEntry", "item added to database");
                        getDataFromDatabase(); //or add manually and call updateList
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("DB addEntry", "item not added to database");
                    }
                });
    }

    public static void getDataFromDatabase() {
        itemArray.clear(); //clear array first to avoid multiple entries of single entry
        db.collection("items")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //add entry as DataModelItemList object
                                //to be able to reference different attribute of the object later on
                                itemArray.add(new DataModelItemList(document.get("name").toString(),
                                  Long.parseLong(document.get("ts").toString())));
                            }
                            Log.d("DB loadEntry", "items loaded from db");
                            MainActivityFragment.updateList(); //update view in fragment
                        } else {
                            Log.d("DB loadEntry", "item not loaded from db");
                        }
                    }
                });
    }

    public static void deleteItemFromDatabase(String id) {
        Log.d("DB del Entry", "id" + id);
        db.collection("items").document(id)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void avoid) {
                        Log.d("DB del Entry", "DocumentSnapshot successfully deleted!");
                        getDataFromDatabase();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("DB del Entry", "Error deleting document", e);
                    }
                });
    }

}
