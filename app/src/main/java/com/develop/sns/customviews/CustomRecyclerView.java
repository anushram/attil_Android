package com.develop.sns.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class CustomRecyclerView extends RecyclerView {
    public CustomRecyclerView(Context context) {
        super(context);
        //setFont();
        setLayoutDirection();
    }

    public CustomRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //setFont();
        setLayoutDirection();
    }

    public CustomRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //setFont();
        setLayoutDirection();
    }

    private void setLayoutDirection() {
        setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
    }
}
