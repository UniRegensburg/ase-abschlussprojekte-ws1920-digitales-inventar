package com.example.digitalesinventar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.fragment.app.FragmentActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

//custom ListAdapter to display an object of type "DataModelItemList" which holds a database entry
public class ItemListAdapter extends ArrayAdapter<DataModelItemList> {
    ArrayList<DataModelItemList> dataSet;
    FragmentActivity fragActivity;//functions as "context"

    private static class ViewHolder {
        TextView txtItemName;
        TextView txtTimestamp;
    }

    public ItemListAdapter(ArrayList<DataModelItemList> data, FragmentActivity fragActivity) {
        super(fragActivity, R.layout.list_item_itemlist, data);
        this.dataSet = data;
        this.fragActivity = fragActivity;
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
            convertView.setTag(dataModel.getTimestamp());
            Log.i("itemListAdapter", "" + dataModel.getTimestamp());
            viewHolder.txtItemName = (TextView) convertView.findViewById(R.id.item_name);
            viewHolder.txtTimestamp = (TextView) convertView.findViewById(R.id.item_ts);

            result = convertView;
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }
        //format date
        long itemTs = Long.parseLong(String.valueOf(dataModel.getTimestamp()));
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
        Date resultdate = new Date(itemTs);

        viewHolder.txtItemName.setText(dataModel.getItemName());
        viewHolder.txtTimestamp.setText(sdf.format(resultdate));

        // Return the completed view to render on screen
        return convertView;
    }

}
