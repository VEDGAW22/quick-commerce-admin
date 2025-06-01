package com.example.quickcommerceadmin.AuthViewModel;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.quickcommerceadmin.models.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class AdminViewModel extends ViewModel {

    private final MutableLiveData<List<Product>> productListLiveData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<Product>> filteredProductListLiveData = new MutableLiveData<>(new ArrayList<>());
    private List<Product> cachedProducts = new ArrayList<>();

    public LiveData<List<Product>> getProductListLiveData() {
        return productListLiveData;
    }

    public LiveData<List<Product>> getFilteredProductListLiveData() {
        return filteredProductListLiveData;
    }

    // Interface for image upload callback
    public interface OnImageUploadListener {
        void onAllImagesUploaded(ArrayList<String> imageUrls);
        void onUploadFailed(Exception e);
    }

    // Method to upload images to Firebase Storage
    public void saveImagesInFirebase(ArrayList<Uri> imageUris, OnImageUploadListener listener) {
        if (imageUris == null || imageUris.isEmpty()) {
            listener.onUploadFailed(new Exception("No images selected for upload"));
            return;
        }

        ArrayList<String> downloadUrls = new ArrayList<>();
        final int[] uploadCount = {0};

        for (Uri imageUri : imageUris) {
            String fileName = imageUri.getLastPathSegment();
            if (fileName == null) continue;

            StorageReference storageRef = FirebaseStorage.getInstance()
                    .getReference("products/" + fileName);

            storageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> taskSnapshot.getStorage().getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                downloadUrls.add(uri.toString());
                                uploadCount[0]++;
                                if (uploadCount[0] == imageUris.size()) {
                                    listener.onAllImagesUploaded(downloadUrls);
                                }
                            }))
                    .addOnFailureListener(exception -> {
                        Log.e("AdminViewModel", "Image upload failed: " + exception.getMessage());
                        listener.onUploadFailed(exception);
                    });
        }
    }

    // Method to upload product to Firebase under AllProducts, ProductCategory, and ProductType
    public void uploadProduct(Product product, ArrayList<String> imageUrls) {
        product.setImageUrls(imageUrls);
        String category = product.getCategory();
        String productType = product.getType(); // Assuming productType is a field in your model

        DatabaseReference allProductsRef = FirebaseDatabase.getInstance()
                .getReference("Admins")
                .child("AllProducts");

        DatabaseReference categoryRef = FirebaseDatabase.getInstance()
                .getReference("Admins")
                .child("ProductCategory")
                .child(category);

        DatabaseReference productTypeRef = FirebaseDatabase.getInstance()
                .getReference("Admins")
                .child("ProductType")
                .child(productType);

        String productId = allProductsRef.push().getKey();
        if (productId != null) {
            product.setId(productId);

            // Upload to AllProducts
            allProductsRef.child(productId).setValue(product)
                    .addOnSuccessListener(unused -> Log.d("AdminViewModel", "Product uploaded to AllProducts"))
                    .addOnFailureListener(e -> Log.e("AdminViewModel", "Failed to upload product to AllProducts: " + e.getMessage()));

            // Upload to ProductCategory
            categoryRef.child(productId).setValue(product)
                    .addOnSuccessListener(unused -> Log.d("AdminViewModel", "Product uploaded to ProductCategory"))
                    .addOnFailureListener(e -> Log.e("AdminViewModel", "Failed to upload product to ProductCategory: " + e.getMessage()));

            // Upload to ProductType
            productTypeRef.child(productId).setValue(product)
                    .addOnSuccessListener(unused -> Log.d("AdminViewModel", "Product uploaded to ProductType"))
                    .addOnFailureListener(e -> Log.e("AdminViewModel", "Failed to upload product to ProductType: " + e.getMessage()));
        } else {
            Log.e("AdminViewModel", "Failed to generate product ID");
        }
    }

    // Fetch all products
    public void fetchAllProducts() {
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference("Admins")
                .child("AllProducts");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Product> allProducts = new ArrayList<>();
                if (snapshot.exists()) {
                    for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                        Product product = productSnapshot.getValue(Product.class);
                        if (product != null) {
                            allProducts.add(product);
                        }
                    }
                }
                cachedProducts = allProducts;
                productListLiveData.setValue(allProducts);
                filteredProductListLiveData.setValue(allProducts); // So filter works too
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("AdminViewModel", "Error fetching products from AllProducts", error.toException());
                productListLiveData.setValue(null);
            }
        });
    }

    // Fetch products by category
    public void fetchProductsByCategory(String category) {
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference("Admins")
                .child("ProductCategory")
                .child(category);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Product> categoryProducts = new ArrayList<>();
                if (snapshot.exists()) {
                    for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                        Product product = productSnapshot.getValue(Product.class);
                        if (product != null) {
                            categoryProducts.add(product);
                        }
                    }
                }
                productListLiveData.setValue(categoryProducts);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("AdminViewModel", "Error fetching products from category " + category, error.toException());
                productListLiveData.setValue(null);
            }
        });
    }


    // Method to update a product in Firebase (handles all relevant paths)
    public void updateProduct(Product product) {
        if (product == null || product.getId() == null || product.getCategory() == null) {
            Log.e("AdminViewModel", "updateProduct: Product is invalid or missing required fields.");
            return;
        }

        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference("Admins")
                .child("ProductCategory")
                .child(product.getCategory())
                .child(product.getId());

        reference.setValue(product)
                .addOnSuccessListener(aVoid -> {
                    Log.d("AdminViewModel", "Product updated successfully: " + product.getTitle());
                    fetchProductsByCategory(product.getCategory());  // Fetch updated products
                })
                .addOnFailureListener(e -> {
                    Log.e("AdminViewModel", "Failed to update product: " + e.getMessage());
                });
    }

    // Method to filter products globally based on a search query
    public void filterProducts(String query) {
        if (query == null || query.isEmpty()) {
            filteredProductListLiveData.setValue(cachedProducts); // Show all products if no search query
            return;
        }

        if (cachedProducts == null || cachedProducts.isEmpty()) {
            filteredProductListLiveData.setValue(new ArrayList<>());
            return;
        }

        List<Product> filteredList = new ArrayList<>();
        for (Product product : cachedProducts) {
            if (product.getTitle().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(product);
            }
        }

        filteredProductListLiveData.setValue(filteredList);
    }

}








