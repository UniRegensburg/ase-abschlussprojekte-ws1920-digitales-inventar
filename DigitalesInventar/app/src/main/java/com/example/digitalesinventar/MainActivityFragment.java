package com.example.digitalesinventar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


//Fragment for displaying database entries as list in layout of MainActivity
public class MainActivityFragment extends Fragment {

    public static ArrayAdapter itemArrayAdapter;
    static SwipeMenuListView itemListView;
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
        Log.d("mainSort", ""+filteredList);
        itemArrayAdapter.notifyDataSetChanged();
    }

    private static void sortItemArrayBySortedTs() {
        Log.d("mainSort", "sortTs");
        DatabaseActivity.loadBackup();
        ArrayList<DataModelItemList> wipItemArray = new ArrayList<>();
        wipItemArray.addAll(DatabaseActivity.itemArray);
        filteredList.clear();
        for (String sortTs : tsList) {
            Log.d("mainSort", "outsideFor"+sortTs);
            for (DataModelItemList sortingItem : wipItemArray) {
                Log.d("mainSort", "insideFor"+sortingItem.getTimestamp());
                if (Long.valueOf(sortTs) == sortingItem.getTimestamp()) {
                    Log.d("mainSort", "hit");
                    filteredList.add(sortingItem);
                }
            }
        }
        Log.d("mainSort", ""+filteredList);
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

    public static void sortByÄlteste() {
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

    public static void sortByNeueste() {
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
        itemListView = view.findViewById(R.id.fragment_list);
        Log.i("MainActivityFragment", "listView: ");
        
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

        setupList();
        Log.i("MainActivityFragment", "setupList called");
        return view;

    }

    //link custom adapter with ListView for db entries
    public void setupList() {
        Log.i("MainActivityFragment", "setupList called");
        filteredList = DatabaseActivity.itemArray;
        itemArrayAdapter = new ItemListAdapter(filteredList,getActivity());
        itemListView.setAdapter(itemArrayAdapter);
        Log.i("MainActivityFragment", "listAdapter set");
    }

    public static void updateList() {
        Log.i("MainActivityFragment", "adapter dataset changed");
        itemArrayAdapter.notifyDataSetChanged();
    }

    //onClick action for plusButton --> launches newItemActivity
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

    private void launchMultipleItemSelection(){
        Log.i("MainActivityFragment", "launchMultipleItemSelected called");
        count = 0;

        itemListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                DataModelItemList item = (DataModelItemList) itemListView.getItemAtPosition(position);
                String itemTimestamp = String.valueOf(item.getTimestamp());
                if (!checked){
                    count = count -1;
                    View child = itemListView.getChildAt(position);
                    child.setBackgroundColor(0x00999999);
                    selected_items.remove(itemTimestamp);
                } else {
                    count = count +1;
                    View child = itemListView.getChildAt(position);
                    child.setBackgroundColor(0x99990000);
                    selected_items.add(itemTimestamp);
                }
                if (count > 1){
                    mode.setTitle(count + " items are selected");
                } else {
                    mode.setTitle(count + " item is selected");
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater menuInflater = mode.getMenuInflater();
                menuInflater.inflate(R.menu.context_menu, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

                for (int i = 0; i < itemListView.getCount(); i++){
                    View child = itemListView.getChildAt(i);
                    child.setBackgroundColor(0x00999999);
                }
                String itemCount = String.valueOf(count);
                showConfirmDialog(selected_items, itemCount);

                mode.finish();
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                for (int i = 0; i < itemListView.getCount(); i++){
                    View child = itemListView.getChildAt(i);
                    child.setBackgroundColor(0x00999999);
                }
                count = 0;
            }
        });
    }

    public void setupSearchListener(SearchView searchView){
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

    private void launchSwipeMenu(){
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
                editItem.setIcon(R.drawable.ic_edit);
                // add to menu
                menu.addMenuItem(editItem);

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity());
                // set item background
                deleteItem.setBackground(R.color.error);
                // set item width
                deleteItem.setWidth(250);
                // set a icon
                deleteItem.setIcon(R.drawable.ic_delete);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };

        //set creator
        itemListView.setMenuCreator(creator);
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
