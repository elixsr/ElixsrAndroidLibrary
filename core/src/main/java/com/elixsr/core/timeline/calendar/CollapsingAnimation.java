package com.elixsr.core.timeline.calendar;

import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by Niall McShane on 15/06/2016.
 */
class CollapsingAnimation extends Animation {
    private final int targetHeight;
    private final TimelineCalendarView view;
    private int targetGrowRadius;
    private final boolean down;
    private TimelineCalendarController timelineCalendarController;

    public CollapsingAnimation(TimelineCalendarView view, TimelineCalendarController timelineCalendarController, int targetHeight, int targetGrowRadius, boolean down) {
        this.view = view;
        this.timelineCalendarController = timelineCalendarController;
        this.targetHeight = targetHeight;
        this.targetGrowRadius = targetGrowRadius;
        this.down = down;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        float grow = 0;
        int newHeight;
        if (down) {
            newHeight = (int) (targetHeight * interpolatedTime);
            grow = (interpolatedTime * (targetGrowRadius * 2));
        } else {
            float progress = 1 - interpolatedTime;
            newHeight = (int) (targetHeight * progress);
            grow = (progress * (targetGrowRadius * 2));
        }
        timelineCalendarController.setGrowProgress(grow);
        view.getLayoutParams().height = newHeight;
        view.requestLayout();

    }

    @Override
    public void initialize(int width, int height, int parentWidth,
                           int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }
}