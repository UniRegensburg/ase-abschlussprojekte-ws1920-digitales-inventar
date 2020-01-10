package com.example.digitalesinventar;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;


public class MainActivityFragment extends Fragment {

    ArrayList<DataModelItemList> dataModels;
    ItemListAdapter adapter;
    ListView listView;

    public MainActivityFragment()  {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        Log.i("MainActivityFragment", "inflater called");
        rootView.findViewById(R.id.fragment);

        listView = (ListView) rootView.findViewById(R.id.fragment_list);
        Log.i("MainActivityFragment", "listView: ");

        setupList();

        Log.i("MainActivityFragment", "setupList called");
        return rootView;

    }


    public void setupList() {
        dataModels = new ArrayList<>();
        Log.i("MainActivityFragment", "dataModels created");
        //Log.i("MainActivityFragment", "itemArray: " + DatabaseActivity.getItemArray().toString());

        if (DatabaseActivity.itemArray.size() > 0) {
            Log.i("MainActivityFragment", "itemArray is not null");
            for (int i = 0; i < DatabaseActivity.itemArray.size(); i++) {
                Log.i("MainActivityFragment", "itemArray.get(i).get(0): " + DatabaseActivity.itemArray.get(0).get(0));
                dataModels.add(new DataModelItemList(DatabaseActivity.itemArray.get(i).get(0), DatabaseActivity.itemArray.get(i).get(1)));
            }

        }else{
            dataModels.add(new DataModelItemList("Dummy", "TEST"));
            Log.i("MainActivityFragment", "dataModels: " + dataModels.toString());
        }

        adapter = new ItemListAdapter(dataModels, this.getContext());
        Log.i("MainActivityFragment", "listAdapter created");
        listView.setAdapter(adapter);
        Log.i("MainActivityFragment", "listAdapter set");
    }
}
