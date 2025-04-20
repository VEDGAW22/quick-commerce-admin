package com.example.quickcommerceadmin.models;

import java.util.ArrayList;

public class Product {
    private String id;
    private String title;
    private String gender;
    private String size;
    private int price;
    private int stock;
    private String category;
    private String type;
    private String description;
    private ArrayList<String> imageUrls = new ArrayList<>(); // Initialized here as well

    // No-argument constructor (required by Firebase)
    public Product() {
        // Firebase uses this to deserialize the object
    }

    // Full constructor
    public Product(String id, String title, String gender, String size, int price, int stock,
                   String category, String type, String description) {
        this.id = id;
        this.title = title;
        this.gender = gender;
        this.size = size;
        this.price = price;
        this.stock = stock;
        this.category = category;
        this.type = type;
        this.description = description;
        this.imageUrls = new ArrayList<>();  // Ensuring imageUrls is initialized
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(ArrayList<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Optional: For DiffUtil or equality comparison
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Product)) return false;

        Product other = (Product) obj;
        return id != null && id.equals(other.id);
    }
}

