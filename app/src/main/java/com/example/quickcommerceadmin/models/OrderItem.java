package com.example.quickcommerceadmin.models;

import java.util.Objects;

public class OrderItem {
    private String id;
    private String itemName;
    private String description;
    private String size;
    private double price;
    private int quantity;
    private String imageUrl;

    // Required default constructor for Firebase
    public OrderItem() {}

    // Full constructor
    public OrderItem(String id, String itemName, String size,
                     int quantity, double price, String imageUrl) {
        this.id = id;
        this.itemName = itemName;
        this.size = size;
        this.quantity = quantity;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getItemName() {
        return itemName;
    }

    public String getDescription() {
        return description;
    }

    public String getSize() {
        return size;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    // Optional: For DiffUtil comparison
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof OrderItem)) return false;
        OrderItem other = (OrderItem) obj;
        return Double.compare(price, other.price) == 0 &&
                quantity == other.quantity &&
                Objects.equals(id, other.id) &&
                Objects.equals(itemName, other.itemName) &&
                Objects.equals(description, other.description) &&
                Objects.equals(size, other.size) &&
                Objects.equals(imageUrl, other.imageUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, itemName, description, size, price, quantity, imageUrl);
    }
}
