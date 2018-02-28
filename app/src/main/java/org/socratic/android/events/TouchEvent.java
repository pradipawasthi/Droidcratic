package org.socratic.android.events;

import android.view.MotionEvent;

/**
 * Created by williamxu on 6/29/17.
 */

public class TouchEvent {

    private MotionEvent event;

    public TouchEvent(MotionEvent event) {
        this.event = event;
    }

    public MotionEvent getMotionEvent() {return event;}
}
