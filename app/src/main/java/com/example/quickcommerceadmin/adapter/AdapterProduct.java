package com.example.quickcommerceadmin.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Filter;
import android.widget.Filterable;

import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.quickcommerceadmin.AuthViewModel.AdminViewModel;
import com.example.quickcommerceadmin.R;
import com.example.quickcommerceadmin.databinding.ItemViewProductBinding;
import com.example.quickcommerceadmin.models.Product;

import java.util.ArrayList;
import java.util.List;

public class AdapterProduct extends ListAdapter<Product, AdapterProduct.ProductViewHolder> implements Filterable {

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    private OnProductClickListener productClickListener;
    private List<Product> fullList;
    private AdminViewModel adminViewModel; // Add ViewModel reference

    public AdapterProduct(AdminViewModel viewModel) {
        super(DIFF_CALLBACK);
        fullList = new ArrayList<>();
        this.adminViewModel = viewModel;
    }

    public void setOnProductClickListener(OnProductClickListener listener) {
        this.productClickListener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemViewProductBinding binding = ItemViewProductBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new ProductViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = getItem(position);
        holder.bind(product);

        holder.itemView.setOnClickListener(v -> {
            if (productClickListener != null) {
                productClickListener.onProductClick(product);
            }
        });
    }

    @Override
    public void submitList(List<Product> list) {
        if (list != null) {
            fullList = new ArrayList<>(list); // Store full list for filtering
        }
        super.submitList(list);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if (constraint == null || constraint.length() == 0) {
                    // No filtering, return full list
                    results.values = fullList;
                    results.count = fullList.size();
                } else {
                    List<Product> filteredList = new ArrayList<>();
                    String filterPattern = constraint.toString().trim().toLowerCase();

                    // Search across all categories by checking product titles
                    for (Product product : fullList) {
                        if (product.getTitle() != null && product.getTitle().toLowerCase().contains(filterPattern)) {
                            filteredList.add(product);
                        }
                    }

                    results.values = filteredList;
                    results.count = filteredList.size();
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results.values != null) {
                    // Update the list to show filtered results
                    submitList((List<Product>) results.values);
                }
            }
        };
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        private final ItemViewProductBinding binding;

        public ProductViewHolder(@NonNull ItemViewProductBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Product product) {
            List<String> imageUrls = product.getImageUrls();
            ArrayList<SlideModel> imageList = new ArrayList<>();

            if (imageUrls != null && !imageUrls.isEmpty()) {
                for (String url : imageUrls) {
                    imageList.add(new SlideModel(url, ScaleTypes.FIT));
                }
            } else {
                imageList.add(new SlideModel("https://example.com/placeholder.jpg", ScaleTypes.FIT)); // Placeholder
            }

            binding.ivImageSlider.setImageList(imageList);
            binding.tvProductTitle.setText(product.getTitle() != null ? product.getTitle() : "No title available");
            binding.tvProductPrice.setText(binding.getRoot().getContext().getString(R.string.currency_symbol) + product.getPrice());
            binding.tvProductDescripition.setText(product.getDescription() != null ? product.getDescription() : "No description available");

            // Enable scroll on long descriptions
            binding.tvProductDescripition.setMovementMethod(new android.text.method.ScrollingMovementMethod());
        }
    }

    private static final DiffUtil.ItemCallback<Product> DIFF_CALLBACK = new DiffUtil.ItemCallback<Product>() {
        @Override
        public boolean areItemsTheSame(@NonNull Product oldItem, @NonNull Product newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Product oldItem, @NonNull Product newItem) {
            return oldItem.equals(newItem);
        }
    };
}





