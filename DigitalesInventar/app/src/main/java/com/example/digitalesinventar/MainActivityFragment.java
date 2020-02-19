package com.example.digitalesinventar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.fragment.app.Fragment;


//Fragment for displaying database entries as list in layout of MainActivity
public class MainActivityFragment extends Fragment {

    public static ArrayAdapter itemArrayAdapter;
    static ListView itemListView;
    long timestamp;
    public MainActivityFragment()  {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //set content-view for fragment
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        Log.i("MainActivityFragment", "inflater called");
        rootView.findViewById(R.id.fragment);
        //set layout for ListView for data from db
        itemListView = (ListView) rootView.findViewById(R.id.fragment_list);
        Log.i("MainActivityFragment", "listView: ");
        itemListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent,
                View view, int position, long id) {
                DataModelItemList itemTs = (DataModelItemList) parent.getItemAtPosition(position);
                Log.i("MainActivityFraglong", "" + itemTs.getTimestamp());
                DatabaseActivity.deleteItemFromDatabase(String.valueOf(itemTs.getTimestamp()));
                return true;
            }
        });

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
        Intent intent = new Intent(getActivity(), ViewItem.class);
        Bundle extras = new Bundle();
        extras.putLong("itemTs",timestamp);
        intent.putExtras(extras);
        Log.i("MainActivity", "intent to start viewItem created");
        startActivity(intent);
    }
}
