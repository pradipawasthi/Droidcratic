package org.socratic.android.util;

import android.view.MotionEvent;
import android.view.View;

/**
 * Created by williamxu on 11/30/17.
 *
 * Utility class for useful gesture and touch methods
 */

public class TouchUtil {

    public static void disableTouchTheft(View view) {
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                view.getParent().requestDisallowInterceptTouchEvent(true);
                switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_UP:
                        view.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                return false;
            }
        });
    }
}
