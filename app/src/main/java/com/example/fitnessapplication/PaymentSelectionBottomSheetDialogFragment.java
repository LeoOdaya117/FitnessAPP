package com.example.fitnessapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class PaymentSelectionBottomSheetDialogFragment extends BottomSheetDialogFragment {

    private PaymentMethodListener mListener;

    public interface PaymentMethodListener {
        void onPaymentMethodSelected(String paymentMethod);
    }

    public PaymentSelectionBottomSheetDialogFragment() {
        // Required empty public constructor
    }

    public static PaymentSelectionBottomSheetDialogFragment newInstance() {
        return new PaymentSelectionBottomSheetDialogFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.payment_selection_bottom_sheet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView textViewCash = view.findViewById(R.id.textViewCash);
        TextView textViewGcash = view.findViewById(R.id.textViewGcash);

        textViewCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onPaymentMethodSelected("Cash");
                dismiss();
            }
        });

        textViewGcash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onPaymentMethodSelected("GCash");
                dismiss();
            }
        });
    }

    public void setPaymentMethodListener(PaymentMethodListener listener) {
        mListener = listener;
    }
}
