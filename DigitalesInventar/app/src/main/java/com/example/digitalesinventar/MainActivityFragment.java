package com.example.digitalesinventar;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;


public class MainActivityFragment extends Fragment {

    ArrayList<DataModelItemList> dataModels;
    //ItemListAdapter adapter;
    public static ArrayAdapter itemArrayAdapter;
    public static ArrayAdapter timeArrayAdapter;
    static ListView itemListView;
    static ListView timeListView;

    public MainActivityFragment()  {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        Log.i("MainActivityFragment", "inflater called");
        rootView.findViewById(R.id.fragment);

        itemListView = (ListView) rootView.findViewById(R.id.fragment_list);
        timeListView = (ListView) rootView.findViewById(R.id.fragment_list);
        Log.i("MainActivityFragment", "listView: ");

        setupList();

        Log.i("MainActivityFragment", "setupList called");
        return rootView;

    }


    public void setupList() {
        Log.i("MainActivityFragment", "setupList called");
        DatabaseActivity.getDataFromDatabase();
        //Log.i("MainActivityFragment", "itemArray: " + DatabaseActivity.getItemArray().toString());

        itemArrayAdapter = new ArrayAdapter(getActivity(), R.layout.list_item_itemlist, R.id.item_name, DatabaseActivity.itemArray);
        itemListView.setAdapter(itemArrayAdapter);
        Log.i("MainActivityFragment", "listAdapter created");
        //timeArrayAdapter = new ArrayAdapter(getActivity(), R.layout.list_item_itemlist, R.id.item_ts, DatabaseActivity.getTimeArray());
        //timeListView.setAdapter(timeArrayAdapter);

        Log.i("MainActivityFragment", "listAdapter set");
    }

    public static void updateList() {
        Log.i("MainActivityFragment", "adapter datset changed");
        itemArrayAdapter.notifyDataSetChanged();
        //timeArrayAdapter.notifyDataSetChanged();
    }
}
