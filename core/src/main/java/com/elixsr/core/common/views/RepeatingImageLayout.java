package com.elixsr.core.common.views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.elixsr.core.R;


/**
 * Created by Niall McShane on 08/07/2016.
 */
public class RepeatingImageLayout extends FrameLayout implements FillBackgroundFrameLayout.FillableView {

    private static final String TAG = "RepeatingImageLayout";
    private final ValueAnimator animator;
    private final int image;
    private final int imageColour;
    private final ImageView backgroundOne;
    private final ImageView backgroundTwo;
    private final int duration;
    private final boolean tile;
    private boolean drawVectors = true;
    private int maxChildHeight = 0;

    public RepeatingImageLayout(Context context, AttributeSet attrs) {
        super(context, attrs, 0);

        //get attributes
        TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable
                .RepeatingImageLayout, 0, 0);

        try{
            image = attributes.getResourceId(R.styleable.RepeatingImageLayout_repeatingImageLayoutImage, -1);
            imageColour = attributes.getColor(R.styleable.RepeatingImageLayout_repeatingImageLayoutImageColor,
                    getResources().getColor(android.R.color.white));
            duration = attributes.getInteger(R.styleable.RepeatingImageLayout_repeatingImageLayoutDuration, 10000);
            tile = attributes.getBoolean(R.styleable.RepeatingImageLayout_repeatingImageLayoutTile, false);

            //ensure we have a valid resource id
            if(image == -1){
                throw new RuntimeException("Could not locate ID for image Resource.");
            }
        }finally{
            attributes.recycle();
        }


        //set up animator
        animator = ValueAnimator.ofFloat(0.0f, 1.0f);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(duration);

        ValueAnimator.AnimatorUpdateListener dualImageAnimation = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float progress = (float) animation.getAnimatedValue();
                final float width = backgroundOne
                        .getDrawable().getIntrinsicWidth();
                final float translationX = width * progress;
                backgroundOne.setTranslationX(translationX);
                backgroundTwo.setTranslationX(translationX - width);
            }
        };

        ValueAnimator.AnimatorUpdateListener singleImageAnimation = new ValueAnimator
                .AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float progress = (float) animation.getAnimatedValue();
                final float width = backgroundOne
                        .getDrawable().getIntrinsicWidth();
                final float translationX = ((width+width) * progress)-width;
                backgroundOne.setTranslationX(translationX);
            }
        };

        //set up images
        backgroundOne = new TopCropImageView(context);
        backgroundOne.setImageResource(image);

        //we need to ensure the drawable has been created - otherwise lets ensure they are not drawn!
        if(backgroundOne.getDrawable() == null){
            drawVectors = false;
        }

        if(drawVectors) {
            backgroundOne.getDrawable().setColorFilter(imageColour, PorterDuff.Mode.MULTIPLY );
            backgroundOne.setAdjustViewBounds(true);
            backgroundOne.setLayoutParams(new LayoutParams(backgroundOne.getDrawable().getIntrinsicWidth(),
                    backgroundOne.getDrawable().getIntrinsicHeight()));
            addView(backgroundOne);
            maxChildHeight = backgroundOne
                    .getDrawable().getIntrinsicHeight();
        }

        backgroundTwo = new TopCropImageView(context);
        if(tile) {
            backgroundTwo.setImageResource(image);

            if(drawVectors) {
                backgroundTwo.getDrawable().setColorFilter(imageColour, PorterDuff.Mode.MULTIPLY);
                backgroundTwo.setAdjustViewBounds(true);
                backgroundTwo.setLayoutParams(new LayoutParams(backgroundOne.getDrawable().getIntrinsicWidth(),
                        backgroundOne.getDrawable().getIntrinsicHeight()));
                addView(backgroundTwo);
                animator.addUpdateListener(dualImageAnimation);
            }
        }else{
            animator.addUpdateListener(singleImageAnimation);
        }

        if(drawVectors) {
            animator.start();
        }



    }


    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
    }

    @Override
    public int getMaxChildHeight() {
        return maxChildHeight;
    }

    public ValueAnimator getAnimator() {
        return animator;
    }
}
