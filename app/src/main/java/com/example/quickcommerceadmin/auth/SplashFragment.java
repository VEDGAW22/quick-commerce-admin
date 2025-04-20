package com.example.quickcommerceadmin.auth;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.example.quickcommerceadmin.AdminMainActivity;
import com.example.quickcommerceadmin.R;
import com.example.quickcommerceadmin.databinding.FragmentSplashBinding;
import com.example.quickcommerceadmin.AuthViewModel.AuthViewModel;


public class SplashFragment extends Fragment {

    private FragmentSplashBinding binding;
    private Handler handler = new Handler(Looper.getMainLooper());
    private AuthViewModel authViewModel; // ViewModel instance

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSplashBinding.inflate(inflater, container, false);
        changeBar();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NavController navController = Navigation.findNavController(view);
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        // Observe LiveData properly
        authViewModel.getUiState().observe(getViewLifecycleOwner(), isLoggedIn -> {
            new Handler().postDelayed(() -> {
                if (Boolean.TRUE.equals(isLoggedIn)) {
                    Intent intent = new Intent(requireActivity(), AdminMainActivity.class);
                    startActivity(intent);
                    requireActivity().finish(); // Close Splash screen
                } else {
                    navController.navigate(R.id.action_splashFragment_to_signinFragment);
                }
            }, 3000); // 2-second delay
        });
    }
    private void changeBar() {
        Window window = requireActivity().getWindow();

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        window.setStatusBarColor(getResources().getColor(R.color.lightyellow));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacksAndMessages(null);
        binding = null;
    }
}