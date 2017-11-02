package com.techsignage.techsignmeetings.Layouts;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;

/**
 * Created by heat on 9/18/2017.
 */

public class SwipeLinearLayout extends LinearLayout {
    private GestureDetector gestureDetector;
    private OnSwipeListener listener;
    private int lastMotionX;
    private int lastMotionY;

    public interface OnSwipeListener {
        void onSwipeLeft();
    }

    public SwipeLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        gestureDetector = new GestureDetector(getContext(), new LeftSwipeListener());
    }

    public void setOnSwipeListener(OnSwipeListener listener) {
        this.listener = listener;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            lastMotionX = (int) event.getX();
            lastMotionY = (int) event.getY();
        }

        final int scaledTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        final int x = (int) event.getX();
        final int y = (int) event.getY();
        final int diffX = Math.abs(x - lastMotionX);
        final int diffY = Math.abs(y - lastMotionY);
        boolean isSwipingSideways = diffX > scaledTouchSlop && diffX > diffY;

        // Start sending all events to our onTouchEvent from this point
        if (action == MotionEvent.ACTION_MOVE && isSwipingSideways) {
            return true;
        }

        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    private class LeftSwipeListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_MIN_DISTANCE = 120;
        private static final int SWIPE_THRESHOLD_VELOCITY = 1200; // Toy with this value to adjust how hard you have to swipe

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            // right to left swipe
            if(lastMotionX - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                if (listener != null) {
                    listener.onSwipeLeft();
                    return true;
                }
            }

            return false;
        }
    }
}