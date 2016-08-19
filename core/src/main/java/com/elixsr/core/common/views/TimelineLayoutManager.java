package com.elixsr.core.common.views;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.tonicartos.superslim.LayoutManager;

/**
 * Created by Niall McShane on 14/06/2016.
 */
public class TimelineLayoutManager extends LayoutManager {

    private static final int DEFAULT_EXTRA_LAYOUT_SPACE = 600;
    private int extraLayoutSpace = -1;

    private boolean isVerticalScrollEnabled = true;
    private boolean isUpVerticalScrollEnabled = true;

    public TimelineLayoutManager(Context context) {
        super(context);
    }

    public void setScrollEnabled(boolean flag) {
        this.isVerticalScrollEnabled = flag;
    }


    @Override
    public boolean canScrollVertically() {
        return isVerticalScrollEnabled && super.canScrollVertically();
    }


    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        if(!isUpVerticalScrollEnabled){
            return 0;
        }

        return super.scrollVerticallyBy(dy, recycler, state);
    }

    public void setUpVerticalScrollEnabled(boolean upVerticalScrollEnabled) {
        isUpVerticalScrollEnabled = upVerticalScrollEnabled;
    }

    public void setExtraLayoutSpace(int extraLayoutSpace) {
        this.extraLayoutSpace = extraLayoutSpace;
    }

    protected int getExtraLayoutSpace(RecyclerView.State state) {
        if (extraLayoutSpace > 0) {
            return extraLayoutSpace;
        }
        return DEFAULT_EXTRA_LAYOUT_SPACE;
    }
}
