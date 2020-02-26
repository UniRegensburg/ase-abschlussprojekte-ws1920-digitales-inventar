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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *Handles all background activity from the firebase db.
 *entries can be added and removed.
 *provides access for information to be displayed.
 */

public class DatabaseActivity {
    // Access a Cloud Firestore instance from your Activity
    static FirebaseFirestore db = FirebaseFirestore.getInstance();
    //ArrayList to store firebase data for displaying later
    public static ArrayList<DataModelItemList> itemArray = new ArrayList<DataModelItemList>();
    public static ArrayList<String> categoryArray = new ArrayList<>();

    //ITEMS

    //ADD ITEM TO DB
    public static void addEntry(String name, String category ,String location) {
        Log.d("DB addEntry", "item added");
        long tsLong = System.currentTimeMillis();
        String ts = Long.toString(tsLong);
        Map<String, Object> entry = new HashMap<>();
        entry.put("name", name);
        entry.put("category", category);
        entry.put("location", location);
        entry.put("ts", ts);

        //db.collection("items").document(ts)
        db.collection("users").document(MainActivity.userID).collection("items").document(ts)
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
                        Log.d("DB addEntry", "item NOT added to database");
                    }
                });
    }

    //UPDATE EDITED ITEM IN DB
  //TODO UPDATE ITEM
    public static void updateEntry(String id, String name, String category, String location, Long timestamp) {
      db.collection("users").document(MainActivity.userID).collection("items").document(id)
        .update("name", name, "category", category, "location", location, "ts", timestamp)
          .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void avoid) {
              EditItemActivity.showToast(true);
              Log.d("DB updateEntry", "item updated");
              getDataFromDatabase(); //or add manually and call updateList
              //Log.i("current db at 0: " ,"" + itemArray.get(0).itemToString()); //crashed app
            }
          })
          .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
              EditItemActivity.showToast(false);
              Log.d("DB updateEntry", "item NOT updated");
            }
          });
    }

    //GET ITEM-DATA FROM DB
    public static void getDataFromDatabase() {
        itemArray.clear(); //clear array first to avoid multiple entries of single entry
        //db.collection("items")
        db.collection("users").document(MainActivity.userID).collection("items")
        .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //add entry as DataModelItemList object
                                //to be able to reference different attributes of the object later on
                                DataModelItemList newItem = new DataModelItemList(document.get("name").toString(), document.get("category").toString(), document.get("location").toString(), Long.parseLong(document.get("ts").toString()));
                                itemArray.add(newItem);
																Collections.reverse(itemArray);
                            }
                            Log.d("DB loadEntry", "items loaded from db");
                            //Log.i("current db at 0: " ,"" + itemArray.get(0).itemToString());
                            MainActivityFragment.updateList(); //update view in fragment
                        } else {
                            Log.d("DB loadEntry", "item not loaded from db");
                        }
                    }
                });
    }

    //DELETE ITEM FROM DB
    public static void deleteItemFromDatabase(String id) {
        Log.d("DB del Entry", "id" + id);
        db.collection("users").document(MainActivity.userID).collection("items").document(id)
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

  //GET ITEM FROM DB
    public static DataModelItemList getItemFromDatabase(Long id) {
      for (int i=0; i < itemArray.size(); i++) {
        if(itemArray.get(i).getTimestamp() == id) {
          return itemArray.get(i);
        }
      }
      return null;
    }

    //CATEGORIES

    //ADD CATEGORY TO DB
    public static void addCategory(String catName) {
     Log.d("DB addCategory", "category added");
     Map<String, Object> catEntry = new HashMap<>();
     catEntry.put("categoryName", catName);
     db.collection("users").document(MainActivity.userID).collection("categories").document(catName)
       .set(catEntry)
       .addOnSuccessListener(new OnSuccessListener<Void>() {
         @Override
         public void onSuccess(Void avoid) {
           Log.d("DB addCategory", "category added to database");
           getCategoriesFromDatabase();
         }
       })
       .addOnFailureListener(new OnFailureListener() {
         @Override
         public void onFailure(@NonNull Exception e) {
           Log.d("DB addCategory", "Category NOT added to database");
         }
       });
     }

    //DELETE CATEGORY FROM DB
    public static void deleteCategoryFromDatabase(String catName) {
      Log.d("DB del Category", "Catname" + catName);
      db.collection("users").document(MainActivity.userID).collection("categories").document(catName)
        .delete()
        .addOnSuccessListener(new OnSuccessListener<Void>() {
          @Override
          public void onSuccess(Void avoid) {
            Log.d("DB del category", "DocumentSnapshot successfully deleted!");
            getCategoriesFromDatabase();
          }
        })
        .addOnFailureListener(new OnFailureListener() {
          @Override
          public void onFailure(@NonNull Exception e) {
            Log.d("DB del category", "Error deleting document", e);
          }
        });
    }

    //GET CATEGORY FROM DB
    public static String getCategoryFromDatabase(String catName) {
      for (int i=0; i < categoryArray.size(); i++) {
        if(categoryArray.get(i).equals(catName)) {
          return categoryArray.get(i);
        }
      }
      return null;
    }

  //GET CATEGORY-DATA FROM DB
  public static void getCategoriesFromDatabase() {
    categoryArray.clear(); //clear array first to avoid multiple entries of single entry
    Log.d("dbCollection" , "current: " + db.collection("users").document(MainActivity.userID).collection("categories")
      .get());
    db.collection("users").document(MainActivity.userID).collection("categories")
      .get()
      .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
        @Override
        public void onComplete(@NonNull Task<QuerySnapshot> task) {
          if (task.isSuccessful()) {
            //Set default values for categories
            categoryArray.add("Unterhaltungselektronik");
            categoryArray.add("Haushaltsgegenstände");
            categoryArray.add("Einrichtung");
            categoryArray.add("Hobby");
            categoryArray.add("Werkzeug");
            //add user's categories
            for (QueryDocumentSnapshot document : task.getResult()) {
              String newItem = document.get("categoryName").toString();
              categoryArray.add(newItem);
            }
            Log.d("DB loadCategories", "categories loaded from db");
          } else {
            Log.d("DB loadCategories", "categories not loaded from db");
          }
        }
      });
  }
}
