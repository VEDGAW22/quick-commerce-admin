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
    private List<Product> cachedProducts = new ArrayList<>();

    public LiveData<List<Product>> getProductListLiveData() {
        return productListLiveData;
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

    // Method to upload product to Firebase
    public void uploadProduct(Product product, ArrayList<String> imageUrls) {
        product.setImageUrls(imageUrls);
        String category = product.getCategory();

        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference("Admins")
                .child("ProductCategory")
                .child(category);

        String productId = reference.push().getKey();
        if (productId != null) {
            product.setId(productId);
            reference.child(productId).setValue(product)
                    .addOnSuccessListener(unused -> Log.d("AdminViewModel", "Product uploaded: " + product.getTitle()))
                    .addOnFailureListener(e -> Log.e("AdminViewModel", "Failed to upload product: " + e.getMessage()));
        } else {
            Log.e("AdminViewModel", "Failed to generate product ID");
        }
    }

    // Method to fetch products from Firebase
    public void fetchProductsFromFirebase(String category) {
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference("Admins")
                .child("ProductCategory")
                .child(category);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Product> productList = new ArrayList<>();
                if (snapshot.exists()) {
                    for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                        Product product = productSnapshot.getValue(Product.class);
                        if (product != null) {
                            productList.add(product);
                        }
                    }
                }

                cachedProducts = productList;
                productListLiveData.setValue(productList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("AdminViewModel", "Error fetching products from category " + category, error.toException());
                productListLiveData.setValue(null);
            }
        });
    }

    // Method to update a product in Firebase
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
                    fetchProductsFromFirebase(product.getCategory());
                })
                .addOnFailureListener(e -> {
                    Log.e("AdminViewModel", "Failed to update product: " + e.getMessage());
                });
    }

    // Method to fetch all products in the cache (for offline usage)
    public List<Product> getCachedProducts() {
        return cachedProducts;
    }
}





