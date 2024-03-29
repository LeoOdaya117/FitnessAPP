package com.example.fitnessapplication;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NoInternetFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NoInternetFragment extends Fragment {
    private RetryListener retryListener;
    // Define keys for fragment arguments
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // Define an interface for the retry listener
    public interface RetryListener {
        void onRetry();
    }

    public NoInternetFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NoInternetFragment.
     */
    public static NoInternetFragment newInstance(String param1, String param2) {
        NoInternetFragment fragment = new NoInternetFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_no_internet, container, false);

        Button retryButton = rootView.findViewById(R.id.retryButton);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (retryListener != null) {
                    retryListener.onRetry();
                }
            }
        });

        return rootView;
    }

    public void setRetryListener(RetryListener listener) {
        this.retryListener = listener;
    }
}
