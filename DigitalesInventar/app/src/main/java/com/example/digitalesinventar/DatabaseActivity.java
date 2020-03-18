package com.example.digitalesinventar;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
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
    static FirebaseStorage storage = FirebaseStorage.getInstance();
    static StorageReference storageRef = storage.getReference();
    //ArrayList to store firebase data for displaying later
    public static ArrayList<DataModelItemList> itemArray = new ArrayList<DataModelItemList>();
    private static ArrayList<DataModelItemList> itemArrayBackup = new ArrayList<DataModelItemList>();
    public static ArrayList<String> categoryArray = new ArrayList<>();
    //Bitmap to be cached while Item is created
    private static Bitmap cachedBitmap;
    private static Bitmap downloadedBitmap;
    //loading bool to prevent duplicating data locally
    private static boolean currentlyLoading = false;

    //ADD ITEM TO DB
    public static void addEntry(String name, String category , String location, String buyDate, double value, final boolean newImage) {
        Log.d("DB addEntry", "item added");
        long tsLong = System.currentTimeMillis();
        final String ts = Long.toString(tsLong);
        Map<String, Object> entry = new HashMap<>();
        entry.put("name", name);
        entry.put("category", category);
        entry.put("location", location);
        entry.put("buydate", buyDate);
        entry.put("value", value);
        entry.put("ts", ts);

        //db.collection("items").document(ts)
        db.collection("users").document(MainActivity.userID).collection("items").document(ts)
                .set(entry)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void avoid) {
                      Log.d("DB addEntry", "item added to database");
                      //getDataFromDatabase(); //  contained in uploadImage
                      if (newImage) {
                          uploadImage(cachedBitmap, ts);
                      } else {
                        getDataFromDatabase();
                      }
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
    public static void updateEntry(final String id, String name, String category, String location, String buyDate, String value, final Long timestamp, final boolean newImage) {
      //TODO remove timestamp as it's the same as id
      Double valueWip = Double.valueOf(0);
      if (value.length() > 0) { //catch for parsing error
        valueWip = Double.parseDouble(value);
      }
      final DataModelItemList wipItem = new DataModelItemList(name, category, location, buyDate, valueWip, timestamp);
      //Log.d("DB updateEntry", "data:"+id+" ;"+name+" ;"+category+" ;"+location+" ;"+timestamp);
      db.collection("users").document(MainActivity.userID).collection("items").document(id)
        .update("name", name, "category", category, "location", location, "buydate", buyDate, "value", valueWip, "ts", timestamp)
          .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void avoid) {
              EditItemActivity.showToast(true);
              Log.d("DB updateEntry", "item updated");
              if (newImage) {
                deleteImage(id);
                if (cachedBitmap != null) {
                  uploadImage(cachedBitmap, String.valueOf(timestamp));
                }
              } else {
                getDataFromDatabase();
              }
              ViewItemActivity.updateDataAfterEdit(wipItem, newImage, cachedBitmap);
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

    public static void loadBackup() {
      itemArray.clear();
      itemArray.addAll(itemArrayBackup);
    }

    //GET ITEM-DATA FROM DB
    public static void getDataFromDatabase() {
        if(!currentlyLoading) {
          currentlyLoading = true;
          itemArray.clear(); //clear array first to avoid multiple entries of single entry
          itemArrayBackup.clear();
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
                    DataModelItemList newItem = new DataModelItemList(document.get("name").toString(), document.get("category").toString(), document.get("location").toString(), document.get("buydate").toString(), Double.parseDouble(document.get("value").toString()) ,Long.parseLong(document.get("ts").toString()));
                    itemArray.add(0, newItem); //add item on top of the list
                    itemArrayBackup.add(0, newItem);
                  }
                  Log.d("DB loadEntry", "items loaded from db");
                  //Log.i("current db at 0: " ,"" + itemArray.get(0).itemToString());
                  currentlyLoading = false;
                  MainActivityFragment.updateList(); //update view in fragment
                } else {
                  Log.d("DB loadEntry", "item not loaded from db");
                  currentlyLoading = false;
                }
              }
            });
        }
    }

    //DELETE ITEM FROM DB
    public static void deleteItemFromDatabase(final String id) {
        Log.d("DB del Entry", "id" + id);
        db.collection("users").document(MainActivity.userID).collection("items").document(id)
        .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void avoid) {
                        Log.d("DB del Entry", "DocumentSnapshot successfully deleted!");
                        deleteImage(id);
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

    public static void deleteItemsByCategory(final String category) {
      deleteCategoryFromDatabase(category);
      currentlyLoading = true; //prevent loading while deleting
      for (int i = 0; i < itemArray.size(); i++) {
          if (itemArray.get(i).getItemCategory().equals(category)) {
            deleteItemFromDatabase((Long.toString(itemArray.get(i).getTimestamp())));
          }
        }
      currentlyLoading = false;
      getDataFromDatabase();
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
            Log.d("DB loadCategories", "categories loaded from db" + categoryArray);
            try {
              CategoryFragment.updateList();
            } catch (Exception e) {
              e.printStackTrace();
            }
          } else {
            Log.d("DB loadCategories", "categories not loaded from db");
          }
        }
      });
  }

  //Update Category
  public static void updateCategoryInDatabase(String oldCat, final String newCat){
    Log.d("DB updateCategory", "category updated");
    db.collection("users").document(MainActivity.userID).collection("categories").document(oldCat)
      .delete()
      .addOnSuccessListener(new OnSuccessListener<Void>() {
        @Override
        public void onSuccess(Void avoid) {
          Log.d("DB del category", "DocumentSnapshot successfully deleted!");
          //getCategoriesFromDatabase();
          addCategory(newCat);
        }
      })
      .addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
          Log.d("DB del category", "Error deleting document", e);
        }
      });
  }

  //Media
  public static void setCachedBitmap(Bitmap bitmap) {
      Log.d("updateItemView", "0.5 db bmp cached");
      cachedBitmap = bitmap;
  }

  public static void uploadImage(Bitmap bitmap, final String itemID) {
    String pathStr = MainActivity.userID + "/images/" + itemID + ".jpg";
    StorageReference imagesRef = storageRef.child(pathStr);
    //Log.d("uploadImg", "1.5: "+ pathStr);

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
    byte[] data = baos.toByteArray();

    UploadTask uploadTask = imagesRef.putBytes(data);
    uploadTask.addOnFailureListener(new OnFailureListener() {
      @Override
      public void onFailure(@NonNull Exception exception) {
        // Handle unsuccessful uploads
        Log.d("uploadImg", "fail");

      }
    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
      @Override
      public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
        // ...
        Log.d("uploadImg", "success");
        getDataFromDatabase();
      }
    });
  }

  public static void downloadImage(String itemID, final ImageView view) {
    String imgPath = MainActivity.userID + "/images/" + itemID + ".jpg";
    StorageReference islandRef = storageRef.child(imgPath);

    final long ONE_MEGABYTE = 1024 * 1024;
    islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
      @Override
      public void onSuccess(byte[] bytes) {
        // Data for "images/island.jpg" is returns, use this as needed
        Log.d("loadImg", "2 success from db");
        Log.d("currentIMG", ": " + view.getImageMatrix().toShortString());
        //ImageView imageView = findViewById(R.id.image_View);
        //Bitmap bitmap = MediaStore.Images.Media.
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        downloadedBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
        view.setImageBitmap(downloadedBitmap);
        ///imageView.setImageBitmap(bitmap);
      }
    }).addOnFailureListener(new OnFailureListener() {
      @Override
      public void onFailure(@NonNull Exception exception) {
        // Handle any errors
        Log.d("loadImg", "2 fail");
      }
    });

  }

  public static void deleteImage(String itemID) {
    Log.d("delItem", "1");
    String pathStr = MainActivity.userID + "/images/" + itemID + ".jpg";
    StorageReference deleteRef = storageRef.child(pathStr);
      // Delete the file
      deleteRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
        @Override
        public void onSuccess(Void aVoid) {
          // File deleted successfully
          Log.d("delItem", "2 success");
        }
      }).addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception exception) {
          // Uh-oh, an error occurred!
          Log.d("delItem", "2, fail");
        }
      });
  }

}
