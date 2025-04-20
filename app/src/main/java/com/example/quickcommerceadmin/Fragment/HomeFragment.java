package com.example.quickcommerceadmin.Fragment;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.quickcommerceadmin.AuthViewModel.AdminViewModel;
import com.example.quickcommerceadmin.Constants;
import com.example.quickcommerceadmin.R;
import com.example.quickcommerceadmin.adapter.AdapterCategory;
import com.example.quickcommerceadmin.adapter.AdapterProduct;
import com.example.quickcommerceadmin.databinding.FragmentHomeBinding;
import com.example.quickcommerceadmin.models.Category;
import com.example.quickcommerceadmin.models.Product;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private AdminViewModel viewModel;
    private AdapterProduct adapterProduct;
    private AdapterCategory adapterCategory;
    private ArrayList<Category> allCategories;
    private static final String TAG = "HomeFragment";

    private String currentCategory = "Fashion"; // default category

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(AdminViewModel.class);

        setupProductRecycler();
        prepareCategories();

        // Observe LiveData once
        observeProductList();

        // Fetch default category
        fetchProducts(currentCategory);

        adapterProduct.setOnProductClickListener(product -> showEditProductDialog(product));
        changeBar();
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchProducts(currentCategory); // always fetch current category when returning
    }

    private void fetchProducts(String category) {
        currentCategory = category;
        Log.d(TAG, "Fetching products for: " + category);
        binding.shimmerViewConntainer.setVisibility(View.VISIBLE);
        binding.rvProduct.setVisibility(View.GONE);
        binding.tvText.setVisibility(View.GONE);

        viewModel.fetchProductsFromFirebase(category);
    }

    private void observeProductList() {
        viewModel.getProductListLiveData().observe(getViewLifecycleOwner(), products -> {
            binding.shimmerViewConntainer.setVisibility(View.GONE);
            if (products != null && !products.isEmpty()) {
                binding.rvProduct.setVisibility(View.VISIBLE);
                binding.tvText.setVisibility(View.GONE);
                adapterProduct.submitList(new ArrayList<>(products)); // defensive copy
            } else {
                binding.rvProduct.setVisibility(View.GONE);
                binding.tvText.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setupProductRecycler() {
        adapterProduct = new AdapterProduct();
        binding.rvProduct.setLayoutManager(new GridLayoutManager(getContext(), 2));
        binding.rvProduct.setAdapter(adapterProduct);
    }

    private void prepareCategories() {
        allCategories = new ArrayList<>();
        if (Constants.allProductCategory != null && Constants.allProductsCategoryIcon != null) {
            for (int i = 0; i < Constants.allProductCategory.length; i++) {
                allCategories.add(new Category(
                        Constants.allProductCategory[i],
                        Constants.allProductsCategoryIcon[i]
                ));
            }
        }

        adapterCategory = new AdapterCategory(allCategories, category -> {
            Log.d(TAG, "Selected Category: " + category.getTitle());
            fetchProducts(category.getTitle());
        });

        binding.rvCato.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvCato.setAdapter(adapterCategory);
    }

    private void showEditProductDialog(Product product) {
        Log.d(TAG, "Showing edit dialog for product ID: " + product.getId());

        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.edit_product_layout, null);

        AutoCompleteTextView etGender = dialogView.findViewById(R.id.etProductGender);
        AutoCompleteTextView etUnit = dialogView.findViewById(R.id.etProductUnit);
        AutoCompleteTextView etCategory = dialogView.findViewById(R.id.etProductCategory);
        AutoCompleteTextView etType = dialogView.findViewById(R.id.etProductType);
        EditText etTitle = dialogView.findViewById(R.id.etProductTitle);
        EditText etPrice = dialogView.findViewById(R.id.etProductPrice);
        EditText etDescription = dialogView.findViewById(R.id.etProductDescription);
        EditText etStock = dialogView.findViewById(R.id.etProductStock);
        MaterialButton btnEdit = dialogView.findViewById(R.id.btnEdit);
        MaterialButton btnSave = dialogView.findViewById(R.id.btnSave);

        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, Constants.allGender);
        ArrayAdapter<String> sizeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, Constants.allSizeOfProduct);
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, Constants.allProductCategory);
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, Constants.allProductType);

        etGender.setAdapter(genderAdapter);
        etUnit.setAdapter(sizeAdapter);
        etCategory.setAdapter(categoryAdapter);
        etType.setAdapter(typeAdapter);

        // Set product data to the fields
        etTitle.setText(product.getTitle());
        etPrice.setText(String.valueOf(product.getPrice()));
        etDescription.setText(product.getDescription());
        etStock.setText(String.valueOf(product.getStock()));
        etGender.setText(product.getGender(), false);
        etUnit.setText(product.getSize(), false);
        etCategory.setText(product.getCategory(), false);
        etType.setText(product.getType(), false);

        setFieldsEnabled(false, etTitle, etPrice, etDescription, etStock, etGender, etUnit, etCategory, etType);
        btnSave.setEnabled(false);

        // Show the dialog
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle("Edit Product")
                .setView(dialogView)
                .setNegativeButton("Close", (d, w) -> d.dismiss())
                .create();

        btnEdit.setOnClickListener(v -> {
            setFieldsEnabled(true, etTitle, etPrice, etDescription, etStock, etGender, etUnit, etCategory, etType);
            btnSave.setEnabled(true);
        });

        btnSave.setOnClickListener(v -> {
            if (!validateFields(etTitle, etPrice, etDescription, etStock, etGender, etUnit, etCategory, etType)) {
                Toast.makeText(requireContext(), "Please fill out all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Save updated product details
            product.setPrice(Integer.parseInt(etPrice.getText().toString().trim()));
            product.setTitle(etTitle.getText().toString().trim());
            product.setDescription(etDescription.getText().toString().trim());
            product.setStock(Integer.parseInt(etStock.getText().toString().trim()));
            product.setGender(etGender.getText().toString().trim());
            product.setSize(etUnit.getText().toString().trim());
            product.setCategory(etCategory.getText().toString().trim());
            product.setType(etType.getText().toString().trim());

            // Update the product in the ViewModel
            viewModel.updateProduct(product);
            Toast.makeText(requireContext(), "Product updated", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
        dialog.show();
    }

    private boolean validateFields(EditText... fields) {
        for (EditText field : fields) {
            if (field.getText().toString().trim().isEmpty()) return false;
        }
        return true;
    }

    private void setFieldsEnabled(boolean enabled,
                                  EditText etTitle,
                                  EditText etPrice,
                                  EditText etDescription,
                                  EditText etStock,
                                  AutoCompleteTextView etGender,
                                  AutoCompleteTextView etUnit,
                                  AutoCompleteTextView etCategory,
                                  AutoCompleteTextView etType) {
        etTitle.setEnabled(enabled);
        etPrice.setEnabled(enabled);
        etDescription.setEnabled(enabled);
        etStock.setEnabled(enabled);
        etGender.setEnabled(enabled);
        etUnit.setEnabled(enabled);
        etCategory.setEnabled(enabled);
        etType.setEnabled(enabled);
    }

    private void changeBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = requireActivity().getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.lightyellow));
        }
    }
}





