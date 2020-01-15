package com.example.digitalesinventar;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.fragment.app.Fragment;


//Fragment for displaying database entries as list in layout of MainActivity
public class MainActivityFragment extends Fragment {

    public static ArrayAdapter itemArrayAdapter;
    static ListView itemListView;

    public MainActivityFragment()  {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //set content-view for fragment
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        Log.i("MainActivityFragment", "inflater called");
        rootView.findViewById(R.id.fragment);
        //set layout for ListView for data from db
        itemListView = (ListView) rootView.findViewById(R.id.fragment_list);
        Log.i("MainActivityFragment", "listView: ");
        setupList();
        Log.i("MainActivityFragment", "setupList called");
        return rootView;

    }

    //link custom adapter with ListView for db entries
    public void setupList() {
        Log.i("MainActivityFragment", "setupList called");
        DatabaseActivity.getDataFromDatabase();
        itemArrayAdapter = new ItemListAdapter(DatabaseActivity.itemArray,getActivity());
        itemListView.setAdapter(itemArrayAdapter);
        Log.i("MainActivityFragment", "listAdapter created");
        Log.i("MainActivityFragment", "listAdapter set");
    }

    public static void updateList() {
        Log.i("MainActivityFragment", "adapter datset changed");
        itemArrayAdapter.notifyDataSetChanged();
    }
}
