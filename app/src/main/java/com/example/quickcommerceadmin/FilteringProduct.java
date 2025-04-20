// FilteringProduct.java
package com.example.quickcommerceadmin;

import android.widget.Filter;

import com.example.quickcommerceadmin.adapter.AdapterProduct;
import com.example.quickcommerceadmin.models.Product;

import java.util.ArrayList;
import java.util.List;

public class FilteringProduct extends Filter {

    private List<Product> originalList;
    private AdapterProduct adapter;

    public FilteringProduct(List<Product> originalList, AdapterProduct adapter) {
        this.originalList = new ArrayList<>(originalList);
        this.adapter = adapter;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        List<Product> filteredList = new ArrayList<>();

        if (constraint == null || constraint.length() == 0) {
            filteredList.addAll(originalList);
        } else {
            String filterPattern = constraint.toString().toLowerCase().trim();
            for (Product product : originalList) {
                if (product.getTitle().toLowerCase().contains(filterPattern)) {
                    filteredList.add(product);
                }
            }
        }

        results.values = filteredList;
        results.count = filteredList.size();
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapter.submitList((List<Product>) results.values);
    }
}

