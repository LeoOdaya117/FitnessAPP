package com.example.fitnessapplication;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.recyclerview.widget.RecyclerView;

public class CustomRecyclerView extends RecyclerView {

    private boolean isScrollEnabled = true;

    public CustomRecyclerView(Context context) {
        super(context);
    }

    public CustomRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        return isScrollEnabled && super.onTouchEvent(e);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        return isScrollEnabled && super.onInterceptTouchEvent(e);
    }

    public void setScrollEnabled(boolean scrollEnabled) {
        isScrollEnabled = scrollEnabled;
    }
}
