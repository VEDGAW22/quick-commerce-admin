package com.example.quickcommerceadmin;
import android.content.Context;

import android.view.LayoutInflater;

import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.quickcommerceadmin.databinding.ProgressDialogBinding;


public class utils {
    private static AlertDialog dialog;

    public static void showDialog(Context context, String message) {
        ProgressDialogBinding progressBinding = ProgressDialogBinding.inflate(LayoutInflater.from(context));
        progressBinding.tvMessage.setText(message); // Ensure `tvMessage` exists in XML

        dialog = new AlertDialog.Builder(context)
                .setView(progressBinding.getRoot())
                .setCancelable(false)
                .create();

        if (dialog != null) {
            dialog.show();
        }
    }

    public static void hideDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }
    public static void showToast(Context context,String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}

