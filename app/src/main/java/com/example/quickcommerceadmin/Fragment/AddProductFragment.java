package com.example.quickcommerceadmin.Fragment;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.quickcommerceadmin.AuthViewModel.AdminViewModel;
import com.example.quickcommerceadmin.Constants;
import com.example.quickcommerceadmin.R;
import com.example.quickcommerceadmin.adapter.AdapterSelectedImage;
import com.example.quickcommerceadmin.databinding.FragmentAddProductBinding;
import com.example.quickcommerceadmin.models.Product;
import com.example.quickcommerceadmin.utils;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AddProductFragment extends Fragment {
    private AdminViewModel viewModel;
    private List<Uri> imageUri = new ArrayList<>();
    private FragmentAddProductBinding binding;
    private AdapterSelectedImage adapter;
    private ActivityResultLauncher<String> imagePickerLauncher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize ActivityResultLauncher for image selection
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetMultipleContents(),
                uris -> {
                    if (uris != null && !uris.isEmpty()) {
                        imageUri.clear();
                        imageUri.addAll(uris.subList(0, Math.min(5, uris.size())));
                        adapter.setImages(imageUri);
                    }
                }
        );
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAddProductBinding.inflate(inflater, container, false);
        changeBar();
        setAutoCompleteTextViews();
        setupImageSelection();
        setupRecyclerView();

        // Initialize the ViewModel
        viewModel = new ViewModelProvider(this).get(AdminViewModel.class);

        onAddButtonClicked();
        return binding.getRoot();
    }

    private void setupRecyclerView() {
        adapter = new AdapterSelectedImage();
        binding.rvProductImage.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvProductImage.setAdapter(adapter);
    }

    private void setupImageSelection() {
        binding.btnSelectImage.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));
    }

    private void setAutoCompleteTextViews() {
        ArrayAdapter<String> sizeAdapter = new ArrayAdapter<>(requireContext(), R.layout.show_list, Constants.allSizeOfProduct);
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(requireContext(), R.layout.show_list, Constants.allProductCategory);
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(requireContext(), R.layout.show_list, Constants.allProductType);
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(requireContext(), R.layout.show_list, Constants.allGender);

        binding.etProductUnit.setAdapter(sizeAdapter);
        binding.etProductCategory.setAdapter(categoryAdapter);
        binding.etProductType.setAdapter(typeAdapter);
        binding.etProductGender.setAdapter(genderAdapter);
    }

    private void onAddButtonClicked() {
        binding.btnAddProduct.setOnClickListener(v -> {
            utils.showDialog(requireContext(), "Uploading Images...");

            String productTitle = binding.etProductTitle.getText().toString();
            String productGender = binding.etProductGender.getText().toString();
            String productUnit = binding.etProductUnit.getText().toString();
            String productPrice = binding.etProductPrice.getText().toString();
            String productStock = binding.etProductStock.getText().toString();
            String productCategory = binding.etProductCategory.getText().toString();
            String productType = binding.etProductType.getText().toString();
            String productDescription = binding.etProductDescription.getText().toString();

            if (productTitle.isEmpty() || productGender.isEmpty() || productUnit.isEmpty() || productPrice.isEmpty() || productStock.isEmpty() || productCategory.isEmpty() || productType.isEmpty() || productDescription.isEmpty()) {
                utils.hideDialog();
                utils.showToast(requireContext(), "Please fill all the fields");
            } else if (imageUri.isEmpty()) {
                utils.hideDialog();
                utils.showToast(requireContext(), "Please select at least one image");
            } else {
                // Generate a unique ID for the product (you can also use Firebase push ID or UUID)
                String productId = UUID.randomUUID().toString();

                Product product = new Product(
                        productId,                  // 🟡 Add the ID
                        productTitle,
                        productGender,
                        productUnit,
                        Integer.parseInt(productPrice),
                        Integer.parseInt(productStock),
                        productCategory,
                        productType,
                        productDescription
                );

                saveImageToFirebase(product);
            }
        });
    }


    private void saveImageToFirebase(Product product) {
        ArrayList<Uri> uris = new ArrayList<>(imageUri);

        viewModel.saveImagesInFirebase(uris, new AdminViewModel.OnImageUploadListener() {
            @Override
            public void onAllImagesUploaded(ArrayList<String> imageUrls) {
                viewModel.uploadProduct(product, imageUrls); // save to Realtime DB
                utils.hideDialog();
                utils.showToast(requireContext(), "Product Added Successfully");
            }

            @Override
            public void onUploadFailed(Exception e) {
                utils.hideDialog();
                utils.showToast(requireContext(), "Image upload failed: " + e.getMessage());
            }
        });
    }

    private void changeBar() {
        if (getActivity() == null) return;
        Window window = getActivity().getWindow();

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.lightyellow));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}


