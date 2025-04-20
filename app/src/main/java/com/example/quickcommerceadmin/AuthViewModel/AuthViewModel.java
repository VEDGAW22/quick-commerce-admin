package com.example.quickcommerceadmin.AuthViewModel;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import com.example.quickcommerceadmin.models.Admin;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import android.content.SharedPreferences;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class AuthViewModel extends AndroidViewModel {
    private static final String TAG = "AuthViewModel";
    private String _verificationId;
    private FirebaseAuth mAuth;
    private final SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "user_prefs";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    private MutableLiveData<Boolean> _isCurrentUser = new MutableLiveData<>(false);

    public MutableLiveData<Boolean> getUiState() {
        return _isCurrentUser;
    }

    // ✅ FIX: Constructor must call super(application) and correctly initialize SharedPreferences
    public AuthViewModel(@NonNull Application application) {
        super(application);
        mAuth = FirebaseAuth.getInstance();
        sharedPreferences = application.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        checkLoginState();
    }

    private void checkLoginState() {
        FirebaseUser user = mAuth.getCurrentUser();
        boolean isLoggedIn = user != null || sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
        _isCurrentUser.setValue(isLoggedIn);
    }

    public void saveLoginState(boolean isLoggedIn) {
        sharedPreferences.edit().putBoolean(KEY_IS_LOGGED_IN, isLoggedIn).apply();
        _isCurrentUser.setValue(isLoggedIn);
    }

    public interface OtpVerificationCallback {
        void onVerificationSuccess();
        void onVerificationFailed(String errorMessage);
    }

    public void sendOTP(String userNumber, Activity activity) {
        PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                        Log.d(TAG, "Verification auto-completed");
                        signInWithPhoneAuthCredential(credential, userNumber, activity, null);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Log.e(TAG, "Verification failed: " + e.getMessage());
                        // Inform the user about the failure
                    }

                    @Override
                    public void onCodeSent(@NonNull String verificationId,
                                           @NonNull PhoneAuthProvider.ForceResendingToken token) {
                        _verificationId = verificationId;
                        mResendToken = token;
                        Log.d(TAG, "OTP sent successfully: " + verificationId);
                    }
                };

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber("+91" + userNumber)  // Include the country code
                .setTimeout(60L, TimeUnit.SECONDS)    // Adjust timeout as needed
                .setActivity(activity)
                .setCallbacks(mCallbacks)
                .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }


    public void verifyOtp(String otp, String userNumber, Activity activity, OtpVerificationCallback callback) {
        if (_verificationId == null) {
            Log.e(TAG, "Verification ID is null. Cannot verify OTP.");
            if (callback != null)
                callback.onVerificationFailed("Verification ID is missing. Request OTP again.");
            return;
        }

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(_verificationId, otp);
        signInWithPhoneAuthCredential(credential, userNumber, activity, callback);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential, String phoneNumber,
                                               Activity activity, OtpVerificationCallback callback) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, (Task<AuthResult> task) -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithCredential: success");
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            String userId = firebaseUser.getUid();
                            Admin user = new Admin(userId, phoneNumber, null);
                            FirebaseDatabase.getInstance().getReference("Admins")
                                    .child("AdminInfo").child(userId)
                                    .setValue(user)
                                    .addOnCompleteListener(dbTask -> {
                                        if (dbTask.isSuccessful()) {
                                            Log.d(TAG, "User saved in database successfully");
                                            saveLoginState(true); // ✅ Save login state on success
                                            if (callback != null)
                                                callback.onVerificationSuccess();
                                        } else {
                                            Log.e(TAG, "Failed to save user in database", dbTask.getException());
                                            if (callback != null)
                                                callback.onVerificationFailed("Failed to save user in database.");
                                        }
                                    });
                        }
                    } else {
                        Log.e(TAG, "signInWithCredential: failure", task.getException());
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            Log.e(TAG, "Invalid verification code.");
                        }
                        if (callback != null)
                            callback.onVerificationFailed("Invalid OTP. Please try again.");
                    }
                });
    }
    public void resendOTP(String userNumber, Activity activity) {
        if (mResendToken == null) {
            Log.e(TAG, "Resend token is null. Cannot resend OTP.");
            return;
        }

        PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                        Log.d(TAG, "Resend: Verification auto-completed");
                        signInWithPhoneAuthCredential(credential, userNumber, activity, null);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Log.e(TAG, "Resend: Verification failed: " + e.getMessage());
                    }

                    @Override
                    public void onCodeSent(@NonNull String verificationId,
                                           @NonNull PhoneAuthProvider.ForceResendingToken token) {
                        _verificationId = verificationId;
                        mResendToken = token;
                        Log.d(TAG, "Resend: OTP sent again: " + verificationId);
                    }
                };

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber("+91" + userNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(mCallbacks)
                .setForceResendingToken(mResendToken)  // 👈 Key line
                .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }
}

