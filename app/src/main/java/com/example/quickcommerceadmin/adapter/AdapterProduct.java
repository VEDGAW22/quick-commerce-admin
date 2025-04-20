package com.example.quickcommerceadmin.adapter;

import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.quickcommerceadmin.R;
import com.example.quickcommerceadmin.databinding.ItemViewProductBinding;
import com.example.quickcommerceadmin.models.Product;

import java.util.ArrayList;
import java.util.List;

public class AdapterProduct extends ListAdapter<Product, AdapterProduct.ProductViewHolder> {

    // Listener interface
    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    private OnProductClickListener productClickListener;

    // Setter for the click listener
    public void setOnProductClickListener(OnProductClickListener listener) {
        this.productClickListener = listener;
    }

    public AdapterProduct() {
        super(DIFF_CALLBACK);
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

        // Handle click event
        holder.itemView.setOnClickListener(v -> {
            if (productClickListener != null) {
                productClickListener.onProductClick(product);
            }
        });
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
                for (String imageUrl : imageUrls) {
                    imageList.add(new SlideModel(imageUrl, ScaleTypes.FIT));
                }
            } else {
                imageList.add(new SlideModel("https://example.com/placeholder.jpg", ScaleTypes.FIT)); // Placeholder image
            }

            binding.ivImageSlider.setImageList(imageList);
            binding.tvProductTitle.setText(product.getTitle() != null ? product.getTitle() : "No title available");
            binding.tvProductPrice.setText(binding.getRoot().getContext().getString(R.string.currency_symbol) + product.getPrice());
            binding.tvProductDescripition.setText(product.getDescription() != null ? product.getDescription() : "No description available");
            // ✅ Enable scrolling on description
            binding.tvProductDescripition.setMovementMethod(new ScrollingMovementMethod());
        }
    }

    private static final DiffUtil.ItemCallback<Product> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Product>() {
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




