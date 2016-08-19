package com.elixsr.core.common.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Animatable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.elixsr.core.R;


/**
 * Created by Niall on 29/07/2016.
 */
public class EmptyStateView extends FrameLayout {

    private final String mTitleText;
    private final String mMessageText;
    private final int mImageResourceId;
    private View emptyStateContainer;
    private ImageView imageView;

    public EmptyStateView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.EmptyStateView,
                0, 0);

        try {
            mImageResourceId = a.getResourceId(R.styleable.EmptyStateView_emptyStateViewImage, -1);
            mTitleText = a.getString(R.styleable.EmptyStateView_emptyStateViewTitle);
            mMessageText = a.getString(R.styleable.EmptyStateView_emptyStateViewMessage);
        } finally {
            a.recycle();
        }

        init();
    }

    private void init(){
        inflate(getContext(), R.layout.empty_state, this);

        imageView = (ImageView)findViewById(R.id.empty_state_image);
        TextView titleTextView = (TextView)findViewById(R.id.empty_state_title);
        TextView messageTextView = (TextView)findViewById(R.id.empty_state_message);
        emptyStateContainer = findViewById(R.id.empty_state_container);

//        if(mImageDrawable != null){
//            imageView.setImageDrawable(mImageDrawable);
//            imageView.setImageResource(mImageDrawable);
//        }

        //https://twittercommunity.com/t/android-theming-setting-logo-with-vector-drawable/67521
        if(mImageResourceId != -1){
            imageView.setImageResource(mImageResourceId);
        }



        if(mTitleText != null){
            titleTextView.setText(mTitleText);
        }

        if(mMessageText != null){
            messageTextView.setText(mMessageText);
        }

    }

    public void hideEmptyState(){
        emptyStateContainer.setVisibility(GONE);
    }

    public void showEmptyState(){
        emptyStateContainer.setVisibility(VISIBLE);
    }

    public void animateImage(){
        Animatable emptyStateAnimation = (Animatable) imageView.getDrawable();
        emptyStateAnimation.start();
    }
}
