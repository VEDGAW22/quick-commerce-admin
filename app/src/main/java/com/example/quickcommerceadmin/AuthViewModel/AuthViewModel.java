package com.example.quickcommerceadmin.AuthViewModel;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
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
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.concurrent.TimeUnit;

public class AuthViewModel extends AndroidViewModel {
    private static final String TAG = "AuthViewModel";
    private static final String PREF_NAME = "user_prefs";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    private final FirebaseAuth mAuth;
    private final SharedPreferences sharedPreferences;

    private String verificationId;
    private PhoneAuthProvider.ForceResendingToken resendToken;

    private final MutableLiveData<Boolean> isUserLoggedIn = new MutableLiveData<>(false);

    public AuthViewModel(@NonNull Application application) {
        super(application);
        mAuth = FirebaseAuth.getInstance();
        sharedPreferences = application.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        checkLoginState();
    }

    public MutableLiveData<Boolean> getUiState() {
        return isUserLoggedIn;
    }

    private void checkLoginState() {
        FirebaseUser user = mAuth.getCurrentUser();
        boolean loggedIn = user != null || sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
        isUserLoggedIn.setValue(loggedIn);
    }

    private void saveLoginState(boolean loggedIn) {
        sharedPreferences.edit().putBoolean(KEY_IS_LOGGED_IN, loggedIn).apply();
        isUserLoggedIn.setValue(loggedIn);
    }

    public interface OtpVerificationCallback {
        void onVerificationSuccess();
        void onVerificationFailed(String errorMessage);
    }

    public void sendOTP(String phoneNumber, Activity activity) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber("+91" + phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(getVerificationCallbacks(phoneNumber, activity))
                .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    public void resendOTP(String phoneNumber, Activity activity) {
        if (resendToken == null) {
            Log.e(TAG, "Resend token is null. Cannot resend OTP.");
            return;
        }

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber("+91" + phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(getVerificationCallbacks(phoneNumber, activity))
                .setForceResendingToken(resendToken)
                .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks getVerificationCallbacks(String phoneNumber, Activity activity) {
        return new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                Log.d(TAG, "Auto verification completed.");
                signInWithCredential(credential, phoneNumber, activity, null);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Log.e(TAG, "OTP Verification failed: " + e.getMessage());
            }

            @Override
            public void onCodeSent(@NonNull String vId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                verificationId = vId;
                resendToken = token;
                Log.d(TAG, "OTP Code sent: " + vId);
            }
        };
    }

    public void verifyOtp(String otp, String phoneNumber, Activity activity, OtpVerificationCallback callback) {
        if (verificationId == null) {
            Log.e(TAG, "Verification ID is null.");
            if (callback != null) callback.onVerificationFailed("OTP session expired. Please resend OTP.");
            return;
        }

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);
        signInWithCredential(credential, phoneNumber, activity, callback);
    }

    private void signInWithCredential(PhoneAuthCredential credential, String phoneNumber, Activity activity, OtpVerificationCallback callback) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(activity, (Task<AuthResult> task) -> {
            if (task.isSuccessful()) {
                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                if (firebaseUser != null) {
                    String uid = firebaseUser.getUid();
                    Admin admin = new Admin(uid, phoneNumber, null);

                    FirebaseDatabase.getInstance().getReference("Admins/AdminInfo")
                            .child(uid)
                            .setValue(admin)
                            .addOnCompleteListener(dbTask -> {
                                if (dbTask.isSuccessful()) {
                                    saveLoginState(true);

                                    // Get FCM Token
                                    FirebaseMessaging.getInstance().getToken()
                                            .addOnCompleteListener(tokenTask -> {
                                                if (tokenTask.isSuccessful()) {
                                                    String token = tokenTask.getResult();
                                                    FirebaseDatabase.getInstance().getReference("Admins/AdminInfo")
                                                            .child(uid)
                                                            .child("fcmToken")
                                                            .setValue(token)
                                                            .addOnSuccessListener(unused -> Log.d(TAG, "FCM Token saved"))
                                                            .addOnFailureListener(e -> Log.e(TAG, "FCM Token save failed: " + e.getMessage()));
                                                } else {
                                                    Log.e(TAG, "FCM Token retrieval failed: " + tokenTask.getException());
                                                }
                                            });

                                    if (callback != null) callback.onVerificationSuccess();
                                } else {
                                    Log.e(TAG, "Admin save failed: ", dbTask.getException());
                                    if (callback != null) callback.onVerificationFailed("Database error. Please try again.");
                                }
                            });
                }
            } else {
                Log.e(TAG, "Sign-in failed: ", task.getException());
                if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                    if (callback != null) callback.onVerificationFailed("Invalid OTP. Please try again.");
                } else {
                    if (callback != null) callback.onVerificationFailed("Authentication failed.");
                }
            }
        });
    }
}
