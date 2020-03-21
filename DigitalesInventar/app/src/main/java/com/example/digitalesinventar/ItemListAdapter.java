package com.example.digitalesinventar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.util.ArrayList;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

//custom ListAdapter to display an object of type "DataModelItemList" which holds a database entry
public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.MultiViewHolder> implements Filterable {
    ArrayList<DataModelItemList> dataSet;
    FragmentActivity fragActivity;//functions as "context"
    Context context;
    ArrayList<DataModelItemList> filteredList;
    boolean multiselect;
    @SuppressLint("RestrictedApi") DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());

public ItemListAdapter(Context context, ArrayList<DataModelItemList> data) {
    this.context = context;
    this.dataSet = data;
    this.filteredList = data;
}

public void setData(ArrayList<DataModelItemList> data) {
    this.dataSet = new ArrayList<>();
    this.dataSet = data;
    notifyDataSetChanged();
}

    @Override
    public Filter getFilter() {
        return null;
    }

    class MultiViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewName;
        private TextView textViewCategory;
        private TextView textViewDate;
        private ImageView imageView;
        private long timestamp;

        MultiViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.item_name);
            textViewCategory = itemView.findViewById(R.id.item_category);
            textViewDate = itemView.findViewById(R.id.item_ts);
            imageView = itemView.findViewById(R.id.item_img);
        }

        void bind(final DataModelItemList item) {
            textViewName.setText(item.getItemName());
            textViewCategory.setText(item.getItemCategory());
            textViewDate.setText(dateFormat.format(InputChecker.formattedDate(item)));
            timestamp = item.getTimestamp();
            DatabaseActivity.downloadImage(Long.toString(item.getTimestamp()), imageView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!multiselect) {
                        Log.i("ItemListAdapter", "launchNewItemActivity called");
                        Intent intent = new Intent(context, ViewItemActivity.class);
                        Bundle extras = new Bundle();
                        extras.putLong("itemTs",timestamp);
                        extras.putBoolean("fromMain", true);
                        intent.putExtras(extras);
                        Log.i("MainActivity", "intent to start viewItem created");
                        context.startActivity(intent);
                    }else {
                        if (item.getChecked()) {
                            item.setChecked(false);
														MainActivityFragment.setItemCounter(getSelected().size());
														view.setBackgroundColor(Color.WHITE);
                        }else{
                            item.setChecked(true);
														MainActivityFragment.setItemCounter(getSelected().size());
                            view.setBackgroundColor(0x996200EE);
                        }
                    }
                }
            });


            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    MainActivityFragment.setMultiChoiceMode(true);
                    multiselect = true;
                    //first Item selected with LongClick
                    MainActivityFragment.setItemCounter(1);
                    item.setChecked(true);
                    view.setBackgroundColor(0x996200EE);
                    return true;
                }
            });
        }
    }


    public ArrayList<DataModelItemList> getAll() {
        return dataSet;
    }

    public ArrayList<DataModelItemList> getSelected() {
        ArrayList<DataModelItemList> selected = new ArrayList<>();
        for (int i = 0; i < dataSet.size(); i++) {
            if (dataSet.get(i).getChecked()) {
                selected.add(dataSet.get(i));
            }
        }
        return selected;
    }


		public void unselectAll(RecyclerView viewHolder) {
	Log.i("selectedItems", "selected: " + getSelected().toString());
			for (int i = 0; i < dataSet.size(); i++) {
				dataSet.get(i).setChecked(false);
			}
			notifyDataSetChanged();
			//reset colors
			for (int i = 0; i < viewHolder.getChildCount(); i++) {
			    viewHolder.getChildAt(i).setBackgroundColor(Color.WHITE);
      }
		}

    @NonNull
    @Override
    public MultiViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_itemlist, viewGroup, false);
        return new MultiViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MultiViewHolder multiViewHolder, int position) {
        multiViewHolder.bind(dataSet.get(position));
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public void setMultiselect(boolean active) {
			multiselect = active;
		}

}
