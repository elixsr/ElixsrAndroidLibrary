package com.elixsr.elixsrcore.timeline.calendar;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.support.annotation.NonNull;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;

/**
 * Created by Niall McShane on 15/06/2016.
 */
class TimelineAnimationHandler {

    public static final int HEIGHT_ANIM_DURATION_MILLIS = 650;
    public static final int INDICATOR_ANIM_DURATION_MILLIS = 600;
    private TimelineCalendarController timelineCalendarController;
    private TimelineCalendarView timelineCalendarView;

    TimelineAnimationHandler(TimelineCalendarController timelineCalendarController, TimelineCalendarView timelineCalendarView) {
        this.timelineCalendarController = timelineCalendarController;
        this.timelineCalendarView = timelineCalendarView;
    }

    void openCalendar() {
        Animation heightAnim = getCollapsingAnimation(true);
        heightAnim.setDuration(HEIGHT_ANIM_DURATION_MILLIS);
        heightAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        timelineCalendarController.setAnimationStatus(timelineCalendarController.EXPAND_COLLAPSE_CALENDAR);
        timelineCalendarView.getLayoutParams().height = 0;
        timelineCalendarView.requestLayout();
        timelineCalendarView.startAnimation(heightAnim);
    }

    void closeCalendar() {
        Animation heightAnim = getCollapsingAnimation(false);
        heightAnim.setDuration(HEIGHT_ANIM_DURATION_MILLIS);
        heightAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        timelineCalendarController.setAnimationStatus(TimelineCalendarController.EXPAND_COLLAPSE_CALENDAR);
        timelineCalendarView.getLayoutParams().height = timelineCalendarView.getHeight();
        timelineCalendarView.requestLayout();
        timelineCalendarView.startAnimation(heightAnim);
    }

    void openCalendarWithAnimation() {
        final Animator indicatorAnim = getIndicatorAnimator(1f, timelineCalendarController.getDayIndicatorRadius());
        final Animation heightAnim = getExposeCollapsingAnimation(true);
        timelineCalendarView.getLayoutParams().height = 0;
        timelineCalendarView.requestLayout();
        setUpAnimationLisForExposeOpen(indicatorAnim, heightAnim);
        timelineCalendarView.startAnimation(heightAnim);
    }

    private void setUpAnimationLisForExposeOpen(final Animator indicatorAnim, Animation heightAnim) {
        heightAnim.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                timelineCalendarController.setAnimationStatus(TimelineCalendarController.EXPOSE_CALENDAR_ANIMATION);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                indicatorAnim.start();
            }
        });
        indicatorAnim.addListener(new AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                timelineCalendarController.setAnimationStatus(TimelineCalendarController.ANIMATE_INDICATORS);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                timelineCalendarController.setAnimationStatus(TimelineCalendarController.IDLE);
            }
        });
    }

    void closeCalendarWithAnimation() {
        final Animator indicatorAnim = getIndicatorAnimator(timelineCalendarController.getDayIndicatorRadius(), 1f);
        final Animation heightAnim = getExposeCollapsingAnimation(false);
        timelineCalendarView.getLayoutParams().height = timelineCalendarView.getHeight();
        timelineCalendarView.requestLayout();
        setUpAnimationLisForExposeClose(indicatorAnim, heightAnim);
        timelineCalendarView.startAnimation(heightAnim);
    }

    private void setUpAnimationLisForExposeClose(final Animator indicatorAnim, Animation heightAnim) {
        heightAnim.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                timelineCalendarController.setAnimationStatus(TimelineCalendarController.EXPOSE_CALENDAR_ANIMATION);
                indicatorAnim.start();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                timelineCalendarController.setAnimationStatus(TimelineCalendarController.IDLE);
            }
        });
        indicatorAnim.addListener(new AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                timelineCalendarController.setAnimationStatus(TimelineCalendarController.ANIMATE_INDICATORS);
            }
        });
    }

    @NonNull
    private Animation getExposeCollapsingAnimation(final boolean isCollapsing) {
        Animation heightAnim = getCollapsingAnimation(isCollapsing);
        heightAnim.setDuration(HEIGHT_ANIM_DURATION_MILLIS);
        heightAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        return heightAnim;
    }

    @NonNull
    private Animation getCollapsingAnimation(boolean isCollapsing) {
        return new CollapsingAnimation(timelineCalendarView, timelineCalendarController, timelineCalendarController.getTargetHeight(), getTargetGrowRadius(), isCollapsing);
    }

    @NonNull
    private Animator getIndicatorAnimator(float from, float to) {
        ValueAnimator animIndicator = ValueAnimator.ofFloat(from, to);
        animIndicator.setDuration(INDICATOR_ANIM_DURATION_MILLIS);
        animIndicator.setInterpolator(new OvershootInterpolator());
        animIndicator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                timelineCalendarController.setGrowFactorIndicator((Float) animation.getAnimatedValue());
                timelineCalendarView.invalidate();
            }
        });
        return animIndicator;
    }

    private int getTargetGrowRadius() {
        int heightSq = timelineCalendarController.getTargetHeight() * timelineCalendarController.getTargetHeight();
        int widthSq = timelineCalendarController.getWidth() * timelineCalendarController.getWidth();
        return (int) (0.5 * Math.sqrt(heightSq + widthSq));
    }
}



