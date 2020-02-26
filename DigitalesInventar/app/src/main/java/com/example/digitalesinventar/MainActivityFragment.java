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

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;


//Fragment for displaying database entries as list in layout of MainActivity
public class MainActivityFragment extends Fragment {

    public static ArrayAdapter itemArrayAdapter;
    static ListView itemListView;
    long timestamp;
    int count;
    ArrayList<String> selected_items = new ArrayList<>();

    public MainActivityFragment()  {
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        //set content-view for fragment
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        Log.i("MainActivityFragment", "inflater called");
        rootView.findViewById(R.id.fragment);
        //set layout for ListView for data from db
        itemListView = (ListView) rootView.findViewById(R.id.fragment_list);
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
        setupList();
        Log.i("MainActivityFragment", "setupList called");
        return rootView;

    }

    //link custom adapter with ListView for db entries
    public void setupList() {
        Log.i("MainActivityFragment", "setupList called");
        itemArrayAdapter = new ItemListAdapter(DatabaseActivity.itemArray,getActivity());
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
        intent.putExtras(extras);
        Log.i("MainActivity", "intent to start viewItem created");
        startActivity(intent);
    }

    private void showConfirmDialog(String timestamp){
        //Create Dialog
        Bundle args = new Bundle();
        args.putString(ConfirmationDialogFragment.ARG_TIMESTAMP, timestamp);
        DialogFragment dialog = new ConfirmationDialogFragment();
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

                for (String timestamp: selected_items){
                    showConfirmDialog(timestamp);
                    count = count -1;
                }
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
}
