package com.develop.sns.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.appcompat.widget.SearchView;


public class CustomSearchView extends SearchView {
    public CustomSearchView(Context context) {
        super(context);
        //setFont();
        setLayoutDirection();
    }

    public CustomSearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //setFont();
        setLayoutDirection();
    }

    public CustomSearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //setFont();
        setLayoutDirection();
    }


    private void setLayoutDirection() {
        setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
    }
}
