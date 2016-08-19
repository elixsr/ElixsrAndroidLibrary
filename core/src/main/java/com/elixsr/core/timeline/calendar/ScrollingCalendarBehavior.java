package com.elixsr.core.timeline.calendar;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.util.AttributeSet;

/**
 * Created by Niall McShane on 12/06/2016.
 */
public class ScrollingCalendarBehavior extends AppBarLayout.Behavior {

    private static final String TAG = "ScrollingCalendarBeh";
    private AppBarLayout.OnOffsetChangedListener onOffsetChangedListener;
    private float mLastTouchX;
    private float mLastTouchY;
    private float mPosY;
    private float mPosX;
    private float originalY;
    private int originalHeight;
    private int currentOffset = 200;

    private AppBarLayout mAppBarLayout;

    public ScrollingCalendarBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);

        onOffsetChangedListener = new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
//                Log.i(TAG, "onOffsetChanged: Calleddis " + verticalOffset);
                mAppBarLayout = appBarLayout;
                currentOffset = verticalOffset+appBarLayout.getTotalScrollRange()+200;
            }
        };


    }




    //    @Override
//    public boolean onInterceptTouchEvent(CoordinatorLayout parent, AppBarLayout child, MotionEvent ev) {
//
//        child.addOnOffsetChangedListener(onOffsetChangedListener);
//
//        Log.i(TAG, "onInterceptTouchEvent: raw " + Math.round(ev.getRawY()) + " currentOffset " +
//                currentOffset);
//
//        if (Math.round(ev.getRawY()) <= currentOffset) {
//            Log.i(TAG, "onInterceptTouchEvent: Got inside");
//            return true;
//        }
//
//
//
//
//
//        return false;
//    }

    /* http://neevek.net/posts/2013/10/13/implementing-onInterceptTouchEvent-and-onTouchEvent-for
        -ViewGroup.html - Read */
//    @Override
//    public boolean onTouchEvent(CoordinatorLayout parent, AppBarLayout child, MotionEvent ev) {
//
//        Log.i(TAG, "onInterceptTouchEvent: ev " + ev.toString());
////        Log.i(TAG, "onInterceptTouchEvent: currentOffset " + currentOffset);
//
//
//
//
////        if(ev.getAction() == MotionEvent.ACTION_MOVE){
////            return true;
////        }
//
//        return super.onTouchEvent();
//    }


}
