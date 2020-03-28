package com.example.digitalesinventar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


//Fragment for displaying database entries as list in layout of MainActivity
public class MainActivityFragment extends Fragment {

    private static ItemListAdapter itemArrayAdapter;
    private SwipeController swipeController = null;
    private static RecyclerView itemListView;
    private static RelativeLayout heading;
    private static TextView itemCounter;
    private Button cancelMultiSelect;
    private Button deleteMultipleItems;
    private String[] searchArray;
    private long timestamp;
    int count;
    ArrayList<String> selected_items = new ArrayList<>();
    private static ArrayList<DataModelItemList> filteredList;// = new ArrayList<>();
    private static ArrayList<String> nameList = new ArrayList<>();
    private static ArrayList<String> tsList = new ArrayList<>();
    static Bitmap defaultBitmap;

    public MainActivityFragment(Bitmap defaultBitmap)  {
        this.defaultBitmap = defaultBitmap;
    }

    public MainActivityFragment() {

    }

    private static void extractNames() {
        nameList.clear();
        for (DataModelItemList item : DatabaseActivity.itemArray) {
            nameList.add(item.getItemName());
        }
    }

    private static void extractTimestamps() {
        tsList.clear();
        for (DataModelItemList item : DatabaseActivity.itemArray) {
            tsList.add(String.valueOf(item.getTimestamp()));
        }
    }

    private static void sortItemArrayBySortedNames() {
        Log.d("mainSort", "sortName");
        DatabaseActivity.loadBackup();
        ArrayList<DataModelItemList> wipItemArray = new ArrayList<>();
        wipItemArray.addAll(DatabaseActivity.itemArray);
        filteredList.clear();
        for (String sortName : nameList) {
            for (DataModelItemList sortingItem : wipItemArray) {
                if (sortName == sortingItem.getItemName()) {
                    filteredList.add(sortingItem);
                }
            }
        }
        Log.d("mainSort", "" + filteredList);
        itemArrayAdapter.notifyDataSetChanged();
    }

    private static void sortItemArrayBySortedTs() {
        Log.d("mainSort", "sortTs");
        DatabaseActivity.loadBackup();
        ArrayList<DataModelItemList> wipItemArray = new ArrayList<>();
        wipItemArray.addAll(DatabaseActivity.itemArray);
        filteredList.clear();
        for (String sortTs : tsList) {
            Log.d("mainSort", "outsideFor" + sortTs);
            for (DataModelItemList sortingItem : wipItemArray) {
                Log.d("mainSort", "insideFor" + sortingItem.getTimestamp());
                if (Long.valueOf(sortTs) == sortingItem.getTimestamp()) {
                    Log.d("mainSort", "hit");
                    filteredList.add(sortingItem);
                }
            }
        }
        Log.d("mainSort", "" + filteredList);
        itemArrayAdapter.notifyDataSetChanged();
    }


    static void sortByNameDown() {
        Log.d("mainSort", "nameUp");
        extractNames();
        Collections.sort(nameList, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s2.compareToIgnoreCase(s1);
            }
        });
        sortItemArrayBySortedNames();
    }

    static void sortByNameUp() {
        Log.d("mainSort", "nameDown");
        extractNames();
        Collections.sort(nameList, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.compareToIgnoreCase(s2);
            }
        });
        sortItemArrayBySortedNames();
    }

    static void sortByOldest() {
        Log.d("mainSort", "alt");
        extractTimestamps();
        Collections.sort(tsList, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.compareToIgnoreCase(s2);
            }
        });
        sortItemArrayBySortedTs();
    }

    static void sortByNewest() {
        Log.d("mainSort", "neu");
        extractTimestamps();
        Collections.sort(tsList, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s2.compareToIgnoreCase(s1);
            }
        });
        sortItemArrayBySortedTs();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        //set content-view for fragment
        View view= inflater.inflate(R.layout.fragment_main, container, false);
        Log.i("MainActivityFragment", "inflater called");
        //rootView.findViewById(R.id.fragment_container);
        //set layout for ListView for data from db
        heading = view.findViewById(R.id.itemsHeading);
        itemCounter = view.findViewById(R.id.itemCounter);
        deleteMultipleItems = view.findViewById(R.id.deleteMultipleItemsButton);
        cancelMultiSelect = view.findViewById(R.id.cancelMultiSelect);
        itemListView = view.findViewById(R.id.fragment_list);
        itemListView.setLayoutManager(new LinearLayoutManager(getContext()));
        Log.i("MainActivityFragment", "listView: ");
	      setupSwipeController();
				setupButtons();
        setupList();
        Log.i("MainActivityFragment", "setupList called");
        return view;
    }

    private void setupButtons() {
		cancelMultiSelect.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
				 setMultiChoiceMode(false);
				}
			});

			deleteMultipleItems.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					deleteMultipleItems();
				}
			});
		}

    //link custom adapter with ListView for db entries
		private void setupList() {
        Log.i("MainActivityFragment", "setupList called");
        filteredList = DatabaseActivity.itemArray;
        itemArrayAdapter = new ItemListAdapter(getActivity(), filteredList, defaultBitmap);
        itemListView.setAdapter(itemArrayAdapter);
        Log.i("MainActivityFragment", "listAdapter set");
    }

    static void updateList() {
        Log.i("MainActivityFragment", "adapter dataset changed");
        itemArrayAdapter.notifyDataSetChanged();
        MainActivity.sortBySpinner.setSelection(MainActivity.spinnerPos); //also sorts
        switch (MainActivity.spinnerPos) {
            case 0:
                sortByNewest();
                break;
            case 1:
                sortByOldest();
                break;
            case 2:
                sortByNameUp();
                break;
            case 3:
                sortByNameDown();
                break;
        }
    }


    private void launchViewItem() {
        Log.i("MainActivity", "launchNewItemActivity called");
        Intent intent = new Intent(getActivity(), ViewItemActivity.class);
        Bundle extras = new Bundle();
        extras.putLong("itemTs",timestamp);
        extras.putBoolean("fromMain", true);
        intent.putExtras(extras);
        Log.i("MainActivity", "intent to start viewItem created");
        startActivity(intent);
    }

    private void showConfirmDialog(ArrayList timestamps, String itemCount){
        //Create Dialog
        Bundle args = new Bundle();
        args.putStringArrayList(DeleteItemsConfirmationDialogFragment.ARG_TIMESTAMP, timestamps);
        args.putString(DeleteItemsConfirmationDialogFragment.ARG_COUNT, itemCount);
        DialogFragment dialog = new DeleteItemsConfirmationDialogFragment();
        dialog.setArguments(args);
        dialog.show(getFragmentManager(),"tag");
    }

    static void setMultiChoiceMode(boolean on) {
    	if (on) {
				itemListView.setBackgroundColor(0x99999999);
				//deactivate LongClicks in childViews
				for (int i = 0; i < itemListView.getChildCount(); i++) {
				    itemListView.getChildAt(i).setLongClickable(false);
        }
				heading.setBackgroundColor(0x99999999);
				heading.setVisibility(View.VISIBLE);
			} else {
    		itemArrayAdapter.setMultiselect(false);
				itemListView.setBackgroundColor(0x00999999);
				heading.setBackgroundColor(0x00999999);
				heading.setVisibility(View.GONE);
				itemArrayAdapter.unselectAll(itemListView);
			}
    }

    private void deleteMultipleItems() {
    	ArrayList<String> timestamps = new ArrayList<>();
    	for(int i = 0; i < itemArrayAdapter.getSelected().size(); i++) {
    		timestamps.add(String.valueOf(itemArrayAdapter.getSelected().get(i).getTimestamp()));
			}
			this.showConfirmDialog(timestamps, String.valueOf(timestamps.size()));
    	setMultiChoiceMode(false);
		}

		//method to extract itemNames - not working
		private String[] getSearchArray(){
        ArrayList<String> itemList = new ArrayList<>();
        for (int i = 0; i < DatabaseActivity.itemArray.size(); i++) {
            if (!itemList.contains(DatabaseActivity.itemArray.get(i).itemName) &&
              DatabaseActivity.itemArray.get(i).itemName != "") {
                itemList.add(DatabaseActivity.itemArray.get(i).itemName);
            }
        }
        searchArray = itemList.toArray(new String[0]);
        /*DatabaseActivity.loadBackup();
        ArrayList<DataModelItemList> itemList = new ArrayList<>();
        itemList.addAll(DatabaseActivity.itemArray);
        Log.i("getSearchArray", "itemList: " + itemList);
        for (int i = 0; i < itemList.size(); i++){
            Log.i("getSearchArray", "for: " + i + " - itemName: " + itemList.get(i).itemName );
            searchArray[i] = itemList.get(i).itemName;
        }*/
        Log.i("getSearchArray", "array: " + searchArray);
        return searchArray;
    }

 		void setupSearchListener(final MaterialSearchView searchView){
        Log.i("MainActivityFragment", "setupSearchListener");
        searchArray = getSearchArray();
        //searchArray = new String[]{"Baseball", "Baseballschläger", "Klopapier", "Trompete", "Volleyball", "dietrich"}; //noch durch allgmeine Funktion tauschen

        searchView.closeSearch();
        searchView.setSuggestions(searchArray);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                Bundle extras = new Bundle();
                extras.putString("searchQuery",query);
                intent.putExtras(extras);
                Log.i("MainActivity", "intent to start search created");
                startActivity(intent);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.i("SetupSearchListener", "onQueryTextChange");
                //doLiveUpdates(newText);
                if (searchArray.length == 0 && !DatabaseActivity.currentlyLoading) {
                    searchArray = getSearchArray();
                    //searchView.setSuggestions(searchArray);
                    setupSearchListener(searchView);
                }
                return false;
            }
        });
        searchView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                intent.putExtra("searchQuery", parent.getItemAtPosition(position).toString());
                Log.i("MainActivityFrag", "intent to start SearchActivity created w/ " + parent.getItemAtPosition(position).toString());
                startActivity(intent);
            }
        });
    }

    static void setItemCounter(int count) {
    	itemCounter.setText(String.valueOf(count));
		}

		private void setupSwipeController() {
        swipeController = new SwipeController(getContext(), new SwipeControllerActions() {
            @Override
            public void onRightClicked(int position) {
                Log.i("onMenuItemClicked", "Delete");
                DataModelItemList itemDelete = itemArrayAdapter.dataSet.get(position);
                String itemTimestamp = String.valueOf(itemDelete.getTimestamp());
                ArrayList<String> delete_list = new ArrayList<>();
                delete_list.add(itemTimestamp);
                showConfirmDialog(delete_list, "1");
            }

            @Override
            public void onLeftClicked(int position) {
                Log.i("onMenuItemClicked", "Edit");
                DataModelItemList itemDelete = itemArrayAdapter.dataSet.get(position);
                String itemTimestamp = String.valueOf(itemDelete.getTimestamp());
                Intent intent = new Intent(getActivity(),EditItemActivity.class);
                Bundle extras = new Bundle();
                extras.putLong("itemTs", Long.parseLong(itemTimestamp));
                extras.putBoolean("fromMain", true);
                intent.putExtras(extras);
                startActivityForResult(intent, 666);
            }
        });

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
        itemTouchhelper.attachToRecyclerView(itemListView);

        itemListView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                swipeController.onDraw(c);
            }
        });
    }

    private void doLiveUpdates(String query) {
        DatabaseActivity.loadBackup();
        ArrayList<DataModelItemList> dataSet = new ArrayList<>();
        dataSet.addAll(DatabaseActivity.itemArray);
        Log.i("DoMyFilter", "dataset: " + dataSet);
        filteredList.clear();
        if (query.isEmpty()){
            filteredList.addAll(dataSet); //search doesn't get called on empty input
        } else {
            for (DataModelItemList row : dataSet) {
                if (row.getItemName().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(row);
                }// else if (row.getItemCategory().toLowerCase().contains(query.toLowerCase())) {
                //    filteredList.add(row);
                //} else if (row.getItemLocation().toLowerCase().contains(query.toLowerCase())) {
                //    filteredList.add(row);
                //}
            }
        }

        Log.i("DoMyFilter", "filteredList: " + filteredList);
        itemArrayAdapter.notifyDataSetChanged();
    }
}
