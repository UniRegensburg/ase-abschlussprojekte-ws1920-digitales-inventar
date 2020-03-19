package com.example.digitalesinventar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


//Fragment for displaying database entries as list in layout of MainActivity
public class MainActivityFragment extends Fragment {

    //public static ArrayAdapter itemArrayAdapter;
    //static SwipeMenuListView itemListView;
    static ItemListAdapter itemArrayAdapter;
    static RecyclerView itemListView;
    static RelativeLayout heading;
    static TextView itemCounter;
    Button cancelMultiSelect;
    Button deleteMultipleItems;
    long timestamp;
    int count;
    ArrayList<String> selected_items = new ArrayList<>();
    static ArrayList<DataModelItemList> filteredList;// = new ArrayList<>();
    static ArrayList<String> nameList = new ArrayList<>();
    static ArrayList<String> tsList = new ArrayList<>();

    public MainActivityFragment()  {
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


    public static void sortByNameUp() {
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

    public static void sortByNameDown() {
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

    public static void sortByOldest() {
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

    public static void sortByNewest() {
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
        /*
        // Multiple Items can be selected - works with longClick
        itemListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        launchMultipleItemSelection();

        itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent,
                                           View view, int position, long id) {
                DataModelItemList itemTs = (DataModelItemList) parent.getItemAtPosition(position);
                timestamp = itemTs.getTimestamp();
                Log.i("MainActivityItemOnClick", "" + timestamp);
                launchViewItem();
            }
        });

        launchSwipeMenu();
        itemListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        // edit
                        Log.i("onMenuItemClicked", "Edit");
                        DataModelItemList item = (DataModelItemList) itemListView.getItemAtPosition(position);
                        timestamp = item.getTimestamp();

                        Intent intent = new Intent(getActivity(),EditItemActivity.class);
                        Bundle extras = new Bundle();
                        extras.putLong("itemTs",timestamp);
                        extras.putBoolean("fromMain", true);
                        intent.putExtras(extras);
                        startActivityForResult(intent, 666);
                        break;
                    case 1:
                        // delete
                        Log.i("onMenuItemClicked", "Delete");
                        DataModelItemList itemDelete = (DataModelItemList) itemListView.getItemAtPosition(position);
                        String itemTimestamp = String.valueOf(itemDelete.getTimestamp());

                        ArrayList<String> delete_list = new ArrayList<>();
                        delete_list.add(itemTimestamp);
                        showConfirmDialog(delete_list,"1");
                        break;
                }
                return false;
            }
        });
*/
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
        itemArrayAdapter = new ItemListAdapter(getActivity(), filteredList);
        itemListView.setAdapter(itemArrayAdapter);
        Log.i("MainActivityFragment", "listAdapter set");
    }

    static void updateList() {
        Log.i("MainActivityFragment", "adapter dataset changed");
        itemArrayAdapter.notifyDataSetChanged();
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
				heading.setBackgroundColor(0x99999999);
				heading.setVisibility(View.VISIBLE);
			} else {
    		itemArrayAdapter.setMultiselect(false);
				itemListView.setBackgroundColor(0x00999999);
				heading.setBackgroundColor(0x00999999);
				heading.setVisibility(View.INVISIBLE);
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

 		void setupSearchListener(SearchView searchView){
        Log.i("MainActivityFragment", "setupSearchListener");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.i("SetupSearchListener", "onQueryTextChange");
                //itemArrayAdapter.getFilter().filter(newText);
                doLiveUpdates(newText);
                return true;
            }
        });
    }

    static void setItemCounter(int count) {
    	itemCounter.setText(String.valueOf(count));
		}
    /*private void launchSwipeMenu(){
        Log.i("MainActivityFragment", "launchSwipeMenu called");

        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                // create "edit" item
                SwipeMenuItem editItem = new SwipeMenuItem(getActivity());
                // set item background
                editItem.setBackground(R.color.primaryVariant);
                // set item width
                editItem.setWidth(250);
                // set item title
                editItem.setIcon(R.drawable.edit_white30px);
                // add to menu
                menu.addMenuItem(editItem);

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity());
                // set item background
                deleteItem.setBackground(R.color.error);
                // set item width
                deleteItem.setWidth(250);
                // set a icon
                deleteItem.setIcon(R.drawable.delete_white30px);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };

        //set creator
        itemListView.setMenuCreator(creator);
    }*/

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
