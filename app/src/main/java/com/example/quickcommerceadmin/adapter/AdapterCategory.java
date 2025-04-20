package com.example.quickcommerceadmin.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.quickcommerceadmin.OnCategoryClickListener;
import com.example.quickcommerceadmin.R;
import com.example.quickcommerceadmin.models.Category;

import java.util.ArrayList;
import java.util.List;

public class AdapterCategory extends RecyclerView.Adapter<AdapterCategory.categoryViewHolder> {

    public ArrayList<Category> categorylist;
    private OnCategoryClickListener listener;

    public AdapterCategory(ArrayList<Category> categorylist, OnCategoryClickListener listener) {
        this.categorylist = categorylist;
        this.listener = listener;
    }


    @NonNull
    @Override
    public categoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemview = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_product_categories, parent, false);
        return new categoryViewHolder(itemview);

    }

    @Override
    public void onBindViewHolder(@NonNull categoryViewHolder holder, int position) {
        Category currentItem = categorylist.get(position);

        Glide.with(holder.img.getContext())
                .load(currentItem.getImage())
                .override(800, 800)
                .into(holder.img);

        holder.title.setText(currentItem.getTitle());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCategoryClick(currentItem);
            }
        });

        Log.d("AdapterCategory", "Binding item: " + currentItem.getTitle());
    }


    @Override
    public int getItemCount() {
        return categorylist.size();
    }

    public static class categoryViewHolder extends RecyclerView.ViewHolder {

        public ImageView img;
        public TextView title;

        public categoryViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.ivCategoriesImage);
            title = itemView.findViewById(R.id.tvCategoriesTitle);
        }
    }
    public void updateData(List<Category> newList) {
        this.categorylist = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

}
