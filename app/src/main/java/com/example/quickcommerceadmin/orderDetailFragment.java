package com.example.quickcommerceadmin;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.quickcommerceadmin.AuthViewModel.OrderDetailViewModel;
import com.example.quickcommerceadmin.adapter.AdapterOrdered;
import com.example.quickcommerceadmin.databinding.FragmentOrderDetailBinding;
import com.example.quickcommerceadmin.models.OrderDetail;
import com.example.quickcommerceadmin.models.OrderItem;

import java.util.ArrayList;
import java.util.List;

public class orderDetailFragment extends Fragment {

    private FragmentOrderDetailBinding binding;
    private OrderDetailViewModel viewModel;
    private AdapterOrdered productAdapter;
    private String orderId;
    private String orderStatus;
    private static final String TAG = "OrderDetailFragment";
    private TextView emptyMessage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        retrieveArguments();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentOrderDetailBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(OrderDetailViewModel.class);

        setupRecyclerView();
        setupEmptyMessage();
        observeOrderDetails();
        fetchOrderDetails();
        setupChangeStatusButton();
        onBackClick();

        return binding.getRoot();
    }

    private void retrieveArguments() {
        if (getArguments() != null) {
            orderId = getArguments().getString("orderId");
            orderStatus = getArguments().getString("status");
        } else {
            Log.w(TAG, "No arguments passed to fragment.");
        }
    }

    private void setupRecyclerView() {
        binding.rvorderedItems.setLayoutManager(new LinearLayoutManager(requireContext()));
        productAdapter = new AdapterOrdered(new ArrayList<>());
        binding.rvorderedItems.setAdapter(productAdapter);
    }

    private void setupEmptyMessage() {
        emptyMessage = new TextView(requireContext());
        emptyMessage.setText("No order items found");
        emptyMessage.setVisibility(View.GONE);
        binding.getRoot().addView(emptyMessage);
    }

    private void observeOrderDetails() {
        viewModel.getOrderDetail().observe(getViewLifecycleOwner(), orderDetail -> {
            if (orderDetail != null) {
                // 🔹 Update Ordered Items
                List<OrderItem> items = orderDetail.getOrderedItems();
                if (items != null && !items.isEmpty()) {
                    emptyMessage.setVisibility(View.GONE);
                    productAdapter.updateOrderItems(items);
                } else {
                    emptyMessage.setVisibility(View.VISIBLE);
                }

                // 🔹 Set Address Text from OrderDetail
                if (orderDetail.getUserAddress() != null && !orderDetail.getUserAddress().isEmpty()) {
                    binding.tvUserAddress.setText(orderDetail.getUserAddress());
                } else {
                    binding.tvUserAddress.setText("No address available");
                }

                // 🔹 Update Status Indicators
                updateStatusIndicators(orderDetail.getOrderStatus());
            } else {
                emptyMessage.setVisibility(View.VISIBLE);
            }
        });
    }


    private void fetchOrderDetails() {
        if (orderId != null) {
            viewModel.fetchOrderDetails(orderId);
        } else {
            Log.e(TAG, "Order ID is null. Cannot fetch order details.");
        }
    }

    private void updateStatusIndicators(int statusCode) {
        int green = getResources().getColor(R.color.green);

        if (statusCode >= 0) binding.iv1.setColorFilter(green);
        if (statusCode >= 1) {
            binding.view1.setBackgroundColor(green);
            binding.iv2.setColorFilter(green);
        }
        if (statusCode >= 2) {
            binding.view2.setBackgroundColor(green);
            binding.iv3.setColorFilter(green);
        }
        if (statusCode >= 3) {
            binding.view3.setBackgroundColor(green);
            binding.iv4.setColorFilter(green);
        }
    }

    private void updateOrderStatusInFirebase(int newStatusCode) {
        if (orderId != null) {
            viewModel.updateOrderStatus(orderId, newStatusCode);
            updateStatusIndicators(newStatusCode);
            Toast.makeText(requireContext(), "Status updated", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), "Order ID is missing", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupChangeStatusButton() {
        binding.btnChangeStatus.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(requireContext(), v);
            popup.getMenuInflater().inflate(R.menu.menu_popup, popup.getMenu());

            popup.setOnMenuItemClickListener(item -> {
                int code = getStatusCodeFromMenuItem(item);
                if (code != -1) {
                    updateOrderStatusInFirebase(code);
                    return true;
                }
                return false;
            });

            popup.show();
        });
    }

    private int getStatusCodeFromMenuItem(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menuOrdered) return 0;
        else if (itemId == R.id.menuShipped) return 1;
        else if (itemId == R.id.menuOutForDelivery) return 2;
        else if (itemId == R.id.menuDelivered) return 3;
        else return -1;
    }

    private int getStatusCodeFromStatus(String status) {
        if (status == null) return -1;
        switch (status) {
            case "Ordered": return 0;
            case "Shipped": return 1;
            case "Out for Delivery": return 2;
            case "Delivered": return 3;
            default: return -1;
        }
    }

    private void onBackClick() {
        binding.ivBackArrow.setOnClickListener(v -> requireActivity().onBackPressed());
    }
}
