package com.example.quickcommerceadmin.models;

import java.util.List;

public class MyOrderDisplay {
    private String orderId;
    private List<String> imageUrls; // List of image URLs
    private String orderStatus; // String status (e.g., "Processing")
    private String orderDate;
    private String totalAmount;
    private String userAddress;
    private List<CartOrder.CartItem> cartItems; // List of CartItem objects

    // Constructor for creating MyOrderDisplay from Firebase Order data
    public MyOrderDisplay(String orderId, List<String> imageUrls, String orderStatus,
                          String orderDate, String totalAmount, String userAddress) {
        this.orderId = orderId;
        this.imageUrls = imageUrls;
        this.orderStatus = orderStatus;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.userAddress = userAddress;
    }

    // Constructor for creating MyOrderDisplay with CartItem details
    public MyOrderDisplay(String orderId, List<String> imageUrls, String orderStatus,
                          String orderDate, String totalAmount, List<CartOrder.CartItem> cartItems) {
        this.orderId = orderId;
        this.imageUrls = imageUrls;
        this.orderStatus = orderStatus;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.cartItems = cartItems;
    }

    // Getters and Setters
    public String getOrderId() {
        return orderId;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public List<CartOrder.CartItem> getCartItems() {
        return cartItems;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public void setCartItems(List<CartOrder.CartItem> cartItems) {
        this.cartItems = cartItems;
    }
}
