package com.example.quickcommerceadmin.AuthViewModel;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.quickcommerceadmin.models.OrderDetail;
import com.example.quickcommerceadmin.models.OrderItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class OrderDetailViewModel extends ViewModel {

    private final MutableLiveData<OrderDetail> orderDetailLiveData = new MutableLiveData<>();
    private final DatabaseReference databaseReference;
    private static final String TAG = "OrderDetailViewModel";

    public OrderDetailViewModel() {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Admins").child("Orders");
    }

    public LiveData<OrderDetail> getOrderDetail() {
        return orderDetailLiveData;
    }

    public void fetchOrderDetails(String orderId) {
        if (orderId == null || orderId.isEmpty()) {
            Log.e(TAG, "Invalid orderId.");
            orderDetailLiveData.postValue(null);
            return;
        }

        databaseReference.child(orderId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Log.w(TAG, "No order found for ID: " + orderId);
                    orderDetailLiveData.postValue(null);
                    return;
                }

                Log.d(TAG, "Order data retrieved: " + snapshot.getValue());

                Integer orderStatus = snapshot.child("orderStatus").getValue(Integer.class);
                if (orderStatus == null) orderStatus = 0;

                Double totalAmount = snapshot.child("totalAmount").getValue(Double.class);
                if (totalAmount == null) totalAmount = 0.0;

                String userId = snapshot.child("userId").getValue(String.class);

                fetchUserAddress(userId, orderId, orderStatus, totalAmount, snapshot.child("cartItems"));
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(TAG, "Database error: " + error.getMessage());
                orderDetailLiveData.postValue(null);
            }
        });
    }

    private void fetchUserAddress(String userId, String orderId, Integer orderStatus, Double totalAmount, DataSnapshot cartItemsSnapshot) {
        if (userId == null || userId.isEmpty()) {
            Log.e(TAG, "User ID is missing, cannot fetch address.");
            orderDetailLiveData.postValue(null);
            return;
        }

        // ✅ Correct path to user address
        DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReference("AllUser")
                .child("Users")
                .child(userId)
                .child("SavedAddresses");

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String fullAddress = "No address available";  // Default fallback
                String addressId = null;

                if (snapshot.exists()) {
                    for (DataSnapshot addressSnapshot : snapshot.getChildren()) {
                        if (addressSnapshot.hasChild("address")) {
                            addressId = addressSnapshot.getKey();
                            fullAddress = addressSnapshot.child("address").getValue(String.class);
                            if (fullAddress != null && !fullAddress.isEmpty()) {
                                break;
                            }
                        }
                    }
                }

                Log.d(TAG, "User address retrieved: " + fullAddress + " (Address ID: " + addressId + ")");

                List<OrderItem> items = new ArrayList<>();
                for (DataSnapshot itemSnap : cartItemsSnapshot.getChildren()) {
                    String productId = getStringValue(itemSnap.child("productId"));
                    String title = getStringValue(itemSnap.child("title"));
                    String size = getStringValueOrNull(itemSnap.child("size"));
                    String imageUrl = getStringValue(itemSnap.child("imageUrl"));
                    Integer quantity = getIntValue(itemSnap.child("quantity"));
                    Double price = getDoubleValue(itemSnap.child("price"));

                    items.add(new OrderItem(productId, title, size, quantity, price, imageUrl));
                }

                OrderDetail orderDetail = new OrderDetail(orderId, orderStatus, items, totalAmount, fullAddress);
                orderDetailLiveData.postValue(orderDetail);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(TAG, "Database error fetching user address: " + error.getMessage());
                orderDetailLiveData.postValue(null);
            }
        });
    }

    public void updateOrderStatus(String orderId, int statusCode) {
        if (orderId == null || orderId.isEmpty()) {
            Log.e(TAG, "Cannot update status. Order ID is null or empty.");
            return;
        }

        databaseReference.child(orderId).child("orderStatus").setValue(statusCode)
                .addOnSuccessListener(unused -> Log.d(TAG, "Order status updated to: " + statusCode))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to update order status", e));
    }

    private Integer getIntValue(DataSnapshot snapshot) {
        try {
            Object value = snapshot.getValue();
            if (value instanceof Long) {
                return ((Long) value).intValue();
            } else if (value instanceof Integer) {
                return (Integer) value;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error converting value to int: " + snapshot.getKey(), e);
        }
        return 0;
    }

    private String getStringValue(DataSnapshot snapshot) {
        Object value = snapshot.getValue();
        return (value != null) ? value.toString() : "";
    }

    private String getStringValueOrNull(DataSnapshot snapshot) {
        Object value = snapshot.getValue();
        return (value != null) ? value.toString() : "null";
    }

    private Double getDoubleValue(DataSnapshot snapshot) {
        try {
            Object value = snapshot.getValue();
            if (value instanceof Long) {
                return ((Long) value).doubleValue();
            } else if (value instanceof Double) {
                return (Double) value;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error converting value to double: " + snapshot.getKey(), e);
        }
        return 0.0;
    }
}

