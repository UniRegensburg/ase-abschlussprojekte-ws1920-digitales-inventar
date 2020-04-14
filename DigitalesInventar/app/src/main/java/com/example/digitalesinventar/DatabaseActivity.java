package com.example.digitalesinventar;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
    public static boolean currentlyLoading = false;

    /*private static String removeSpaces(String inputStr) {
      String returnStr = inputStr;
      if (inputStr.charAt(0) == ' ') {
        returnStr = returnStr.substring(1, returnStr.length());
      }
      if (inputStr.charAt(inputStr.length()-1) == ' ') {
        returnStr = returnStr.substring(0, returnStr.length() - 1);
      }
      return returnStr;
    }*/

    //ADD ITEM TO DB
    public static void addEntry(String name, String category , String location, String buyDate, double value, final boolean newImage) {
        long tsLong = System.currentTimeMillis();
        final String ts = Long.toString(tsLong);
        Map<String, Object> entry = new HashMap<>();
        entry.put("name", name);//removeSpaces(name));
        entry.put("category", category);//removeSpaces(category));
        entry.put("location", location);//removeSpaces(location));
        entry.put("buydate", buyDate);
        entry.put("value", value);
        entry.put("ts", ts);

        db.collection("users").document(MainActivity.userID).collection("items").document(ts)
                .set(entry)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void avoid) {
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

                    }
                });
    }

    //UPDATE EDITED ITEM IN DB
    public static void updateEntry(String name, String category, String location, String buyDate, String value, final Long timestamp, final boolean newImage) {
      Double valueWip = Double.valueOf(0);
      if (value.length() > 0) { //catch for parsing error
        valueWip = Double.parseDouble(value);
      }
      //final DataModelItemList wipItem = new DataModelItemList(removeSpaces(name), removeSpaces(category), removeSpaces(location), buyDate, valueWip, false, timestamp);
      final DataModelItemList wipItem = new DataModelItemList(name, category, location, buyDate, valueWip, false, timestamp);
      db.collection("users").document(MainActivity.userID).collection("items").document(String.valueOf(timestamp))
        .update("name", name, "category", category, "location", location, "buydate", buyDate, "value", valueWip,"ts", timestamp)
          .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void avoid) {
              EditItemActivity.showToast(true);
              if (newImage) {
                deleteImage(String.valueOf(timestamp));
                if (cachedBitmap != null) {
                  uploadImage(cachedBitmap, String.valueOf(timestamp));
                }
              } else {
                getDataFromDatabase();
              }
              ViewItemActivity.updateDataAfterEdit(wipItem, newImage, cachedBitmap);
            }
          })
          .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
              EditItemActivity.showToast(false);
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
          db.collection("users").document(MainActivity.userID).collection("items")
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
              @Override
              public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                  for (QueryDocumentSnapshot document : task.getResult()) {
                    //add entry as DataModelItemList object
                    //to be able to reference different attributes of the object later on
                    DataModelItemList newItem = new DataModelItemList(document.get("name").toString(), document.get("category").toString(), document.get("location").toString(), document.get("buydate").toString(), Double.parseDouble(document.get("value").toString()), false, Long.parseLong(document.get("ts").toString()));
                    itemArray.add(0, newItem); //add item on top of the list
                    itemArrayBackup.add(0, newItem);
                  }
                  currentlyLoading = false;
                  MainActivityFragment.updateList(); //update view in fragment
                } else {
                  currentlyLoading = false;
                }
              }
            });
        }
    }

    //DELETE ITEM FROM DB
    public static void deleteItemFromDatabase(final String id) {
        db.collection("users").document(MainActivity.userID).collection("items").document(id)
        .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void avoid) {
                        deleteImage(id);
                        getDataFromDatabase();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
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
     Map<String, Object> catEntry = new HashMap<>();
     catEntry.put("categoryName", catName);//removeSpaces(catName));
     db.collection("users").document(MainActivity.userID).collection("categories").document(catName)
       .set(catEntry)
       .addOnSuccessListener(new OnSuccessListener<Void>() {
         @Override
         public void onSuccess(Void avoid) {
           getCategoriesFromDatabase();
         }
       })
       .addOnFailureListener(new OnFailureListener() {
         @Override
         public void onFailure(@NonNull Exception e) {
         }
       });
     }

    //DELETE CATEGORY FROM DB
    public static void deleteCategoryFromDatabase(String catName) {
      db.collection("users").document(MainActivity.userID).collection("categories").document(catName)
        .delete()
        .addOnSuccessListener(new OnSuccessListener<Void>() {
          @Override
          public void onSuccess(Void avoid) {
            getCategoriesFromDatabase();
          }
        })
        .addOnFailureListener(new OnFailureListener() {
          @Override
          public void onFailure(@NonNull Exception e) {
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
    db.collection("users").document(MainActivity.userID).collection("categories")
      .get()
      .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
        @Override
        public void onComplete(@NonNull Task<QuerySnapshot> task) {
          if (task.isSuccessful()) {
            //Set default values for categories
            categoryArray.add("Einrichtung");
            categoryArray.add("Hobby");
            categoryArray.add("Kleidung");
            //add user's categories
            for (QueryDocumentSnapshot document : task.getResult()) {
              String newItem = document.get("categoryName").toString();
              categoryArray.add(newItem);
            }
            try {
              CategoryFragment.updateList();
            } catch (Exception e) {
              e.printStackTrace();
            }
          } else {
          }
        }
      });
  }

  //Update Category
  public static void updateCategoryInDatabase(String oldCat, final String newCat){
    db.collection("users").document(MainActivity.userID).collection("categories").document(oldCat)
      .delete()
      .addOnSuccessListener(new OnSuccessListener<Void>() {
        @Override
        public void onSuccess(Void avoid) {
          addCategory(newCat);
        }
      })
      .addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
        }
      });
  }

  //Media
  public static void setCachedBitmap(Bitmap bitmap) {
      cachedBitmap = bitmap;
  }

  public static void uploadImage(Bitmap bitmap, final String itemID) {
    String pathStr = MainActivity.userID + "/images/" + itemID + ".jpg";
    StorageReference imagesRef = storageRef.child(pathStr);

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
    byte[] data = baos.toByteArray();

    UploadTask uploadTask = imagesRef.putBytes(data);
    uploadTask.addOnFailureListener(new OnFailureListener() {
      @Override
      public void onFailure(@NonNull Exception exception) {
        // Handle unsuccessful uploads
      }
    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
      @Override
      public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
        // ...
        getDataFromDatabase();
      }
    });
  }

  public static void downloadImage(String itemID, final ImageView view, final Bitmap defaultBitmap) {
    String imgPath = MainActivity.userID + "/images/" + itemID + ".jpg";
    StorageReference islandRef = storageRef.child(imgPath);

    final long ONE_MEGABYTE = 1024 * 1024;
    islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
      @Override
      public void onSuccess(byte[] bytes) {
        // Data for "images/island.jpg" is returns, use this as needed
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        downloadedBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
        view.setImageBitmap(downloadedBitmap);
      }
    }).addOnFailureListener(new OnFailureListener() {
      @Override
      public void onFailure(@NonNull Exception exception) {
        // Handle any errors
        view.setImageBitmap(defaultBitmap);
      }
    });

  }

  public static void deleteImage(String itemID) {
    String pathStr = MainActivity.userID + "/images/" + itemID + ".jpg";
    StorageReference deleteRef = storageRef.child(pathStr);
      // Delete the file
      deleteRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
        @Override
        public void onSuccess(Void aVoid) {
          // File deleted successfully
            MainActivityFragment.updateList();
        }
      }).addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception exception) {
        }
      });
  }

}
