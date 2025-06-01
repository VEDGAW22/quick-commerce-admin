package com.example.quickcommerceadmin.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.quickcommerceadmin.AuthViewModel.OrderViewModel;
import com.example.quickcommerceadmin.R;
import com.example.quickcommerceadmin.adapter.OrderAdapter;
import com.example.quickcommerceadmin.databinding.FragmentOrderBinding;
import com.example.quickcommerceadmin.models.MyOrderDisplay;

public class OrderFragment extends Fragment {
    private FragmentOrderBinding binding;
    private OrderAdapter orderAdapter;
    private OrderViewModel orderViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentOrderBinding.inflate(inflater, container, false);

        orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);

        setupRecyclerView();
        observeOrders();

        orderAdapter.setOnOrderClickListener(this::onOrderItemClicked);
        orderViewModel.fetchOrders(); // Start fetching
        onBackClicked();

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        orderAdapter = new OrderAdapter();
        binding.rvOrder.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvOrder.setAdapter(orderAdapter);
    }

    private void observeOrders() {
        orderViewModel.getOrdersLiveData().observe(getViewLifecycleOwner(), orders -> {
            if (orders != null && !orders.isEmpty()) {
                orderAdapter.submitList(orders);
                binding.rvOrder.setVisibility(View.VISIBLE);
                binding.tvEmptyOrders.setVisibility(View.GONE);
            } else {
                binding.rvOrder.setVisibility(View.GONE);
                binding.tvEmptyOrders.setVisibility(View.VISIBLE);
            }
        });
    }

    private void onOrderItemClicked(MyOrderDisplay order) {
        Bundle bundle = new Bundle();
        bundle.putString("status", order.getOrderStatus());
        bundle.putString("orderId", order.getOrderId());
        bundle.putString("userAddress", order.getUserAddress());
        Navigation.findNavController(requireView())
                .navigate(R.id.action_orderFragment_to_orderDetailFragment, bundle);
    }
    private void onBackClicked() {
        binding.btnBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
