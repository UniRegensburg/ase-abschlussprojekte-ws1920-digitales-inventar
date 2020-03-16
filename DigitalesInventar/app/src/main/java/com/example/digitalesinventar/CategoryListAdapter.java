package com.example.digitalesinventar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import java.util.ArrayList;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class CategoryListAdapter extends ArrayAdapter<String> implements Filterable {

	ArrayList<String> dataSet;
	FragmentActivity fragActivity;//functions as "context"
	//ArrayList<String> filteredList;

	public CategoryListAdapter(@NonNull Context context, int resource) {
		super(context, resource);
	}

	@SuppressLint("RestrictedApi")
	public CategoryListAdapter() {
		super(getApplicationContext(), 0);
	}

	public CategoryListAdapter(ArrayList<String> data, FragmentActivity fragActivity) {
		super(fragActivity, R.layout.list_item_categorylist, data);
		this.dataSet = data;
		this.fragActivity = fragActivity;
		//this.filteredList = data;
	}

	private static class ViewHolder {
		TextView txtItemName;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// Get the data item for this position
		String catName = getItem(position);
		// Check if an existing view is being reused, otherwise inflate the view
		ViewHolder viewHolder; // view lookup cache stored in tag

		final View result;

		if (convertView == null) {

			viewHolder = new ViewHolder();
			LayoutInflater inflater = LayoutInflater.from(getContext());
			convertView = inflater.inflate(R.layout.list_item_categorylist, parent, false);
			convertView.setTag(catName);
			Log.i("catListAdapter", "" + catName);
			viewHolder.txtItemName = (TextView) convertView.findViewById(R.id.item_name1); //eigene id?

			result = convertView;
			convertView.setTag(viewHolder);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
			result = convertView;
		}

		viewHolder.txtItemName.setText(catName);
		//@SuppressLint("RestrictedApi") DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());
		// Return the completed view to render on screen
		return convertView;
	}

}
