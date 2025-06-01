package com.example.quickcommerceadmin.adapter;


import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.quickcommerceadmin.R;
import com.example.quickcommerceadmin.models.MyOrderDisplay;


import java.util.List;

public class OrderAdapter extends ListAdapter<MyOrderDisplay, OrderAdapter.OrderViewHolder> {

    private OnOrderClickListener onOrderClickListener;

    public interface OnOrderClickListener {
        void onOrderClick(MyOrderDisplay order);
    }

    public void setOnOrderClickListener(OnOrderClickListener listener) {
        this.onOrderClickListener = listener;
    }

    public OrderAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<MyOrderDisplay> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<MyOrderDisplay>() {
                @Override
                public boolean areItemsTheSame(@NonNull MyOrderDisplay oldItem, @NonNull MyOrderDisplay newItem) {
                    return oldItem.getOrderId().equals(newItem.getOrderId());
                }

                @SuppressLint("DiffUtilEquals")
                @Override
                public boolean areContentsTheSame(@NonNull MyOrderDisplay oldItem, @NonNull MyOrderDisplay newItem) {
                    return oldItem.equals(newItem);
                }
            };

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_view_myorder, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        MyOrderDisplay order = getItem(position);
        holder.orderAmount.setText(order.getTotalAmount());
        holder.orderDate.setText(order.getOrderDate());

        // Map status
        String status = mapStatus(order.getOrderStatus(), holder);
        holder.statusText.setText(status);

        // Load product images
        List<String> images = order.getImageUrls();
        loadImage(holder.productImage1, images, 0);
        loadImage(holder.productImage2, images, 1);
        loadImage(holder.productImage3, images, 2);
        loadImage(holder.productImage4, images, 3);

        holder.itemView.setOnClickListener(v -> {
            if (onOrderClickListener != null) {
                onOrderClickListener.onOrderClick(order);
            }
        });
    }

    private void loadImage(ImageView imageView, List<String> images, int index) {
        if (imageView != null && images != null && index < images.size()) {
            Glide.with(imageView.getContext())
                    .load(images.get(index))
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .into(imageView);
        } else {
            imageView.setImageResource(R.drawable.placeholder_image);
        }
    }

    private String mapStatus(String rawStatus, OrderViewHolder holder) {
        if (rawStatus == null) return "Unknown";

        String status = rawStatus.trim().toLowerCase();
        int colorRes = R.color.lightbluegray;
        String statusText = "Unknown";

        switch (status) {
            case "0":
            case "ordered":
                statusText = "Ordered";
                colorRes = R.color.orange;
                break;
            case "1":
            case "shipped":
                statusText = "Shipped";
                colorRes = R.color.blue;
                break;
            case "2":
            case "out for delivery":
                statusText = "Out for Delivery";
                colorRes = R.color.red;
                break;
            case "3":
            case "delivered":
                statusText = "Delivered";
                colorRes = R.color.green;
                break;
        }

        holder.statusText.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), colorRes));
        return statusText;
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView statusText, orderAmount, orderDate;
        ImageView productImage1, productImage2, productImage3, productImage4;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            statusText = itemView.findViewById(R.id.orderStatusText);
            orderAmount = itemView.findViewById(R.id.orderAmount);
            orderDate = itemView.findViewById(R.id.orderdates);
            productImage1 = itemView.findViewById(R.id.productImage1);
            productImage2 = itemView.findViewById(R.id.productImage2);
            productImage3 = itemView.findViewById(R.id.productImage3);
            productImage4 = itemView.findViewById(R.id.productImage4);
        }
    }
}
