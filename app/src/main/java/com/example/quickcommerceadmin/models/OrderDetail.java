package com.example.quickcommerceadmin.models;

import java.util.List;

public class OrderDetail {
    private String orderId;
    private int orderStatus;
    private List<OrderItem> orderedItems;
    private double totalAmount;
    private String userAddress;  // ✅ Added user address field

    public OrderDetail() {
        // Required by Firebase
    }

    // ✅ Updated constructor to include `userAddress`
    public OrderDetail(String orderId, int orderStatus, List<OrderItem> orderedItems, double totalAmount, String userAddress) {
        this.orderId = orderId;
        this.orderStatus = orderStatus;
        this.orderedItems = orderedItems;
        this.totalAmount = totalAmount;
        this.userAddress = userAddress;
    }

    // Getters and Setters
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public int getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(int orderStatus) {
        this.orderStatus = orderStatus;
    }

    public List<OrderItem> getOrderedItems() {
        return orderedItems;
    }

    public void setOrderedItems(List<OrderItem> orderedItems) {
        this.orderedItems = orderedItems;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }
}
