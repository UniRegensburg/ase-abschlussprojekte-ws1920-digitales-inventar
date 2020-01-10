package com.example.digitalesinventar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class ItemListAdapter extends ArrayAdapter<DataModelItemList> {
    ArrayList<DataModelItemList> dataSet;
    Context context;

    private static class ViewHolder {
        TextView txtItemName;
        TextView txtTimestamp;
    }

    public ItemListAdapter (ArrayList<DataModelItemList> data, Context context) {
        super(context, R.layout.list_item_itemlist, data);
        this.dataSet = data;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        DataModelItemList dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_item_itemlist, parent, false);
            viewHolder.txtItemName = (TextView) convertView.findViewById(R.id.item_name);
            viewHolder.txtTimestamp = (TextView) convertView.findViewById(R.id.item_ts);

            result=convertView;
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        viewHolder.txtItemName.setText(dataModel.getItemName());
        viewHolder.txtTimestamp.setText(dataModel.getTimestamp());

        // Return the completed view to render on screen
        return convertView;
    }

}
