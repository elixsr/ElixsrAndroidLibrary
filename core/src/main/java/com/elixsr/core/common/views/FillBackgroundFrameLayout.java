package com.elixsr.core.common.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.elixsr.core.R;


/**
 * Created by Niall McShane on 08/07/2016.
 */
public class FillBackgroundFrameLayout extends android.support.percent.PercentRelativeLayout {

    private static final String TAG = "FillBackgroundFrameLay";
    private final int imageColour;
    private Rect fillRectangle;
    private Paint paint;
    private int maxHeightFound;

    public FillBackgroundFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        //get attributes
        TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable
                .RepeatingImageLayout, 0, 0);

        try {
            imageColour = attributes.getColor(R.styleable.RepeatingImageLayout_repeatingImageLayoutImageColor,
                    getResources().getColor(android.R.color.white));
        }finally{
            attributes.recycle();
        }

        paint = new Paint();
        fillRectangle = new Rect(0, 0, getWidth(), getHeight());
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {

        super.dispatchDraw(canvas);

        paint.setColor(imageColour);
        canvas.drawRect(fillRectangle, paint);
        Log.d(TAG, "dispatchDraw: canvas rect " + fillRectangle.toShortString() + "height ");

    }

    /*
    Work out the top of the child item.

    Work out the height of the drawable in the child item

    We will add these, and this will give us the location of the bottom of that item.

    We then draw a big rectangle to fill the rest of the screen.
     */
    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        calculateFillRectangleSize();
        super.onWindowFocusChanged(hasWindowFocus);
    }

    private void calculateFillRectangleSize(){
        calculateFillRectangleSize(getWidth(), getHeight());
    }

    private void calculateFillRectangleSize(int width, int height) {
        maxHeightFound = 0;
        int maxTopFound = 0;
        int newTop = 0;
        FillableView view;
        for (int i=0; i<getChildCount(); i++){

            view = (FillableView) getChildAt(i);

            //max height found
            if(view.getMaxChildHeight() > maxHeightFound && view.getMaxChildHeight() != 0){
                maxHeightFound = view.getMaxChildHeight();
            }

            //max top found
            if(((View)view).getTop() > maxTopFound && ((View)view).getTop() != 0){
                maxTopFound = ((View)view).getTop();
            }
        }

        newTop = maxTopFound + maxHeightFound;

        fillRectangle.set(0, newTop, width, height); //getWidth()

        Log.d(TAG, "onWindowFocusChanged: children - " + getChildCount());
        Log.d(TAG, "onWindowFocusChanged: maxtop - " + maxTopFound);
        Log.d(TAG, "onWindowFocusChanged: maxheight - " + maxHeightFound);
        Log.d(TAG, "onWindowFocusChanged: width - " + width + " height " + height);
        Log.d(TAG, "onWindowFocusChanged: rect stuff - " + fillRectangle.toShortString());

        invalidate();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        calculateFillRectangleSize();
    }

    public interface FillableView {
        public int getMaxChildHeight();
    }
}
