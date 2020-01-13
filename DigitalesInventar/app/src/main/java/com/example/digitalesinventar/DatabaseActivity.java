package com.example.digitalesinventar;

import android.content.Intent;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseActivity {
    // Access a Cloud Firestore instance from your Activity
    static FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static List<List<String>> itemArray = new ArrayList<List<String>>();

    public static List<List<String>> getItemArray() {
        return itemArray;
    }

    //add an entry to database
    public static void addEntry(String name) {
        Log.i("addEntry", "item added");
        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();
        Map<String, Object> entry = new HashMap<>();
        entry.put("name", name);
        entry.put("ts", ts);

        db.collection("items").document(ts)
                .set(entry)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i("addEntry", "item added to database");
                        Log.i("addEntry", "current database: " + itemArray.toString());
                        //HIER MainActiviyFragent aktualisieren
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("addEntry", "item not added to database");
                    }
                });
    }

    public static void getDataFromDatabase() {
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
                                MainActivityFragment.updateList();
                                Log.i("loadEntry", "item loaded from db");
                                //Log.i("getDatafromDatabase()", "currentDatabase: " + itemArray.toString());
                            }
                        } else {
                            Log.i("loadEntry", "item not loaded from db");
                        }
                    }
                });
    }

}
