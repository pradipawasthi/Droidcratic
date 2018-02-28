package org.socratic.android.views;

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by williamxu on 10/10/17.
 *
 * Custom View Pager that has ability to lock paging
 */

public class AdvancedViewPager extends ViewPager {

    private boolean enabled = true;
    private View ignoreTouchView;
    private final Rect mFrame = new Rect();

    public AdvancedViewPager(Context context) {
        this(context, null);
    }

    public AdvancedViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (enabled) {
            if (shouldTouchBeIgnored(ev.getX(), ev.getY())) {
                return false;
            } else {
                return super.onInterceptTouchEvent(ev);
            }
        }

        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (enabled) {
            if (shouldTouchBeIgnored(ev.getX(), ev.getY())) {
                return false;
            } else {
                return super.onTouchEvent(ev);
            }
        }

        return false;
    }

    public void setPagingEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * method to add a view whose bounds are to be ignored in AdvancedViewPager's touch methods
     *
     * @param view
     */
    public void setIgnoreTouchView(View view) {
        this.ignoreTouchView = view;
    }

    private boolean shouldTouchBeIgnored(float x, float y) {
        boolean ignoreEvent = false;

        if (ignoreTouchView != null) {
            Rect frame = mFrame;

            ignoreTouchView.getHitRect(frame);

            if (frame.contains((int) x, (int) y)) {
                ignoreEvent = true;
            }
        }

        return ignoreEvent;
    }
}
