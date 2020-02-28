package com.example.digitalesinventar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import java.util.ArrayList;

//custom ListAdapter to display an object of type "DataModelItemList" which holds a database entry
public class ItemListAdapter extends ArrayAdapter<DataModelItemList> implements Filterable {
    ArrayList<DataModelItemList> dataSet;
    FragmentActivity fragActivity;//functions as "context"
    ArrayList<DataModelItemList> filteredList;

    private static class ViewHolder {
        TextView txtItemName;
        TextView txtTimestamp;
    }

    public ItemListAdapter(ArrayList<DataModelItemList> data, FragmentActivity fragActivity) {
        super(fragActivity, R.layout.list_item_itemlist, data);
        this.dataSet = data;
        this.fragActivity = fragActivity;
        this.filteredList = data;
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
        viewHolder.txtItemName.setText(dataModel.getItemName());
        viewHolder.txtTimestamp.setText(InputChecker.formattedDate(dataModel).toString());

        // Return the completed view to render on screen
        return convertView;
    }
/*
    @Override
    public Filter getFilter(){
        Log.i("ItemListAdapter", "Filter");
        return new Filter(){

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                if (charString.isEmpty()){
                    filteredList = dataSet;
                } else {
                    ArrayList<DataModelItemList> filteredData = new ArrayList<>();
                    for (DataModelItemList row : dataSet) {
                        if (row.getItemName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredData.add(row);
                        }
                    }
                    filteredList = filteredData;
                }
                Log.i("Filter", "filteredList: " + filteredList);

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredList = (ArrayList<DataModelItemList>) results.values;
                Log.i("publishResult", "filteredList: " + filteredList);
                notifyDataSetChanged();
                clear();
                for(int i = 0, l = filteredList.size(); i < l; i++)
                    add(filteredList.get(i));
                notifyDataSetInvalidated();
            }
        };
    }
*/
}
