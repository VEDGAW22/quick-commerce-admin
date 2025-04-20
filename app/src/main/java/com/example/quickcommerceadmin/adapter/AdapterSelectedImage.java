package com.example.quickcommerceadmin.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.quickcommerceadmin.R;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;

public class AdapterSelectedImage extends RecyclerView.Adapter<AdapterSelectedImage.SelectedImageViewHolder> {

    private final List<Uri> imageUris;

    public AdapterSelectedImage() {
        this.imageUris = new ArrayList<>();
    }

    @NonNull
    @Override
    public SelectedImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_view_image_selection, parent, false);
        return new SelectedImageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectedImageViewHolder holder, int position) {
        Uri image = imageUris.get(position);

        // ✅ Load image using Glide
        Glide.with(holder.imageView.getContext())
                .load(image)
                .override(800, 800)
                .into(holder.imageView);

        // Remove image when close button is clicked
        holder.closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPosition = holder.getBindingAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION && currentPosition < imageUris.size()) {
                    imageUris.remove(currentPosition);
                    notifyItemRemoved(currentPosition);
                    notifyItemRangeChanged(currentPosition, imageUris.size()); // Update indices
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageUris.size();
    }

    public void setImages(List<Uri> newImages) {
        imageUris.clear();
        imageUris.addAll(newImages);
        notifyDataSetChanged();
    }

    public static class SelectedImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageButton closeButton;

        public SelectedImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.ivImage);
            closeButton = itemView.findViewById(R.id.closeButton);
        }
    }
}


