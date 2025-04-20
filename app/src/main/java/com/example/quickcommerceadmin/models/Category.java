package com.example.quickcommerceadmin.models;

public class Category {
    private String title; // ✅ Follow Java naming conventions
    private int image;

    public Category(String title, int image) {
        this.title = title;
        this.image = image;
    }

    public String getTitle() {
        return title; // ✅ Now matches the field name
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
