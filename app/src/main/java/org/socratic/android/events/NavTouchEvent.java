package org.socratic.android.events;

import android.view.MotionEvent;

/**
 * Created by williamxu on 6/29/17.
 */

public class NavTouchEvent {

    private MotionEvent event;

    public NavTouchEvent(MotionEvent event) {
        this.event = event;
    }

    public MotionEvent getMotionEvent() {return event;}
}
