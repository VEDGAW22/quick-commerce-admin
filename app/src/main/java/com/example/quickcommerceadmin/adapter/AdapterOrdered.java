package com.example.quickcommerceadmin.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.quickcommerceadmin.R;
import com.example.quickcommerceadmin.models.OrderItem;


import java.util.ArrayList;
import java.util.List;

public class AdapterOrdered extends RecyclerView.Adapter<AdapterOrdered.OrderItemViewHolder> {

    private List<OrderItem> orderItemList;

    public AdapterOrdered(List<OrderItem> orderItemList) {
        this.orderItemList = orderItemList != null ? orderItemList : new ArrayList<>();
    }

    @NonNull
    @Override
    public OrderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ordered_product, parent, false);
        return new OrderItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemViewHolder holder, int position) {
        OrderItem orderItem = orderItemList.get(position);

        // Bind product info
        holder.productName.setText(orderItem.getItemName());
        holder.productSize.setText("Size: " + orderItem.getSize());    // Make sure OrderItem has this field
        holder.productPrice.setText("₹" + orderItem.getPrice());

        // Load image
        Glide.with(holder.productImage.getContext())
                .load(orderItem.getImageUrl())
                .placeholder(com.denzcoskun.imageslider.R.drawable.placeholder)
                .error(R.drawable.error_image)
                .into(holder.productImage);
    }

    @Override
    public int getItemCount() {
        return orderItemList.size();
    }

    public void updateOrderItems(List<OrderItem> newOrderItemList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return orderItemList.size();
            }

            @Override
            public int getNewListSize() {
                return newOrderItemList.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return orderItemList.get(oldItemPosition).getId().equals(newOrderItemList.get(newItemPosition).getId());
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return orderItemList.get(oldItemPosition).equals(newOrderItemList.get(newItemPosition));
            }
        });

        orderItemList = newOrderItemList;
        diffResult.dispatchUpdatesTo(this);
    }

    public static class OrderItemViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName, productSize, productPrice;

        public OrderItemViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.ivProductImage);
            productName = itemView.findViewById(R.id.productNameTextView);
            productSize = itemView.findViewById(R.id.tvSize);
            productPrice = itemView.findViewById(R.id.productPriceTextView);
        }
    }
}
