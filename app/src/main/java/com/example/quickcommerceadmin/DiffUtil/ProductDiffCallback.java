package com.example.quickcommerceadmin.DiffUtil;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import com.example.quickcommerceadmin.models.Product;

public class ProductDiffCallback extends DiffUtil.ItemCallback<Product> {
    @Override
    public boolean areItemsTheSame(@NonNull Product oldItem, @NonNull Product newItem) {
        return oldItem.getId().equals(newItem.getId());
    }

    @Override
    public boolean areContentsTheSame(@NonNull Product oldItem, @NonNull Product newItem) {
        return oldItem.equals(newItem);
    }
}
