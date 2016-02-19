package com.suntiago.mytest.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.suntiago.mytest.R;

/**
 * Created by yu.zai on 2016/2/19.
 */
public class ShakeLinearLayout extends LinearLayout {

    private Animation mAnimation;

    public ShakeLinearLayout(Context context) {
        super(context);
    }

    public ShakeLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ShakeLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ShakeLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void shake() {
        if (mAnimation == null) {
            mAnimation = AnimationUtils.loadAnimation(this.getContext(), R.anim.shake);
        }
        this.startAnimation(mAnimation);
    }
    public void setShake(Animation animation) {
        this.mAnimation = animation;
    }
}
