package com.example.digitalesinventar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.MultiViewHolder> implements Filterable {
	ArrayList<String> dataSet;
	Context context;
	String catName;

	CategoryListAdapter(Context context, ArrayList<String> data) {
		this.context = context;
		this.dataSet = data;
	}

	public void setData(ArrayList<String> data) {
		this.dataSet = new ArrayList<>();
		this.dataSet = data;
		notifyDataSetChanged();
	}

	@Override
	public Filter getFilter() {
		return null;
	}

	class MultiViewHolder extends RecyclerView.ViewHolder {
		private TextView textViewCategory;

		MultiViewHolder(@NonNull View itemView) {
			super(itemView);
			textViewCategory = itemView.findViewById(R.id.item_name1);
		}

		void bind(final String item) {
			textViewCategory.setText(item);
			catName = item;

			itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					Log.i("CatListAdapter", "launchCatActivity called for " + catName);
					Intent intent = new Intent(context, SearchActivity.class);
					Bundle extras = new Bundle();
					extras.putString("searchQuery", catName);
					extras.putBoolean("fromMain", true);
					intent.putExtras(extras);
					Log.i("catctivity", "intent to start search created");
					context.startActivity(intent);
				}
			});
		}
	}

		public ArrayList<String> getAll() {
			return dataSet;
		}

		@NonNull
		@Override
		public MultiViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
			View view = LayoutInflater.from(context).inflate(R.layout.list_item_categorylist, viewGroup, false);
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
}

