package com.example.quickcommerceadmin.AuthViewModel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.quickcommerceadmin.models.CartOrder;
import com.example.quickcommerceadmin.models.MyOrderDisplay;
import com.example.quickcommerceadmin.models.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OrderViewModel extends ViewModel {

    private final MutableLiveData<List<MyOrderDisplay>> ordersLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Product>> productsLiveData = new MutableLiveData<>();

    public LiveData<List<MyOrderDisplay>> getOrdersLiveData() {
        return ordersLiveData;
    }

    public LiveData<List<Product>> getProductsLiveData() {
        return productsLiveData;
    }

    public void fetchOrders() {
        DatabaseReference ordersRef = FirebaseDatabase.getInstance()
                .getReference("Admins")
                .child("Orders");

        ordersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<MyOrderDisplay> displayList = new ArrayList<>();

                for (DataSnapshot orderSnap : snapshot.getChildren()) {
                    CartOrder.Order order = orderSnap.getValue(CartOrder.Order.class);

                    if (order != null) {
                        List<String> imageUrls = new ArrayList<>();
                        List<CartOrder.CartItem> cartItems = order.getCartItems();

                        if (cartItems != null) {
                            for (CartOrder.CartItem item : cartItems) {
                                if (item.getImageUrl() != null) {
                                    imageUrls.add(item.getImageUrl());
                                }
                            }
                        }

                        String statusText = getOrderStatusText(order.getOrderStatus());

                        MyOrderDisplay display = new MyOrderDisplay(
                                order.getOrderId(),
                                imageUrls,
                                statusText,
                                order.getOrderDate(),
                                "₹" + String.format("%.2f", order.getTotalAmount()),
                                order.getUserAddress()
                        );

                        displayList.add(display);
                    }
                }

                ordersLiveData.setValue(displayList);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("Firebase", "Database error: " + error.getMessage());
            }
        });
    }

    public String getOrderStatusText(long status) {
        if (status == 0) {
            return "Ordered";
        } else if (status == 1) {
            return "Shipped";
        } else if (status == 2) {
            return "Out for Delivery";
        } else if (status == 3) {
            return "Delivered";
        }
        return "Ordered"; // Default to "Ordered" if an invalid status is given
    }
}
