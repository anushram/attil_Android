package com.develop.sns.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;


public class CustomRelativeLayout extends RelativeLayout {
    public CustomRelativeLayout(Context context) {
        super(context);
        //setFont();
        setLayoutDirection();
    }

    public CustomRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        //setFont();
        setLayoutDirection();
    }

    public CustomRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //setFont();
        setLayoutDirection();
    }

    private void setLayoutDirection() {
        //setLayoutDirection(AppConstant.LANGUAGE_ID == AppConstant.LANGUAGE_TYPE_ARABIC ? View.LAYOUT_DIRECTION_RTL : View.LAYOUT_DIRECTION_LTR);
    }
}
