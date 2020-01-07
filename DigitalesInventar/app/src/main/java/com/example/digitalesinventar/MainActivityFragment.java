package com.example.digitalesinventar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import static com.example.digitalesinventar.MainActivity.getItemArray;

public class MainActivityFragment extends Fragment {

    public MainActivityFragment()  {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        List<List<String>> itemArray = getItemArray();

        ArrayAdapter<List<String>> itemListAdapter = new ArrayAdapter<>(getActivity(),R.layout.list_item_itemlist, R.id.list_item_itemlist, itemArray);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        ListView itemListView = (ListView) rootView.findViewById(R.id.listview_items);
        itemListView.setAdapter(itemListAdapter);

        return rootView;
    }
}
