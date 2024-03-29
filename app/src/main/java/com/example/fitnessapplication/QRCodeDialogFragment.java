package com.example.fitnessapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.squareup.picasso.Picasso;

public class QRCodeDialogFragment extends DialogFragment {

    private ImageView qrCodeImageView;
    private String qrCodeImageUrl;

    // Define a method to create a new instance of the fragment with arguments
    public static QRCodeDialogFragment newInstance(String qrCodeImageUrl) {
        QRCodeDialogFragment fragment = new QRCodeDialogFragment();
        Bundle args = new Bundle();
        args.putString("qrCodeImageUrl", qrCodeImageUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            qrCodeImageUrl = getArguments().getString("qrCodeImageUrl");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_qr_code, null);
        qrCodeImageView = view.findViewById(R.id.imageView_qrCode);
        // Load QR code image using Picasso
        Picasso.get().load(qrCodeImageUrl).into(qrCodeImageView);
        builder.setView(view);
        return builder.create();
    }
}

