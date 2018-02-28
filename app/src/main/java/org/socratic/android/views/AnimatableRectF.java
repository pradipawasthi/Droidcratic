package org.socratic.android.views;

import android.graphics.Rect;
import android.graphics.RectF;

/**
 * Created by williamxu on 6/2/17.
 *
 * Subclass of RectF (Rect is a final class) with setters for the left, top, right, bottom properties
 * Compatible with ObjectAnimator
 */

public class AnimatableRectF extends RectF {

    public AnimatableRectF() {
        super();
    }

    public AnimatableRectF(float left, float top, float right, float bottom) {
        super(left, top, right, bottom);
    }

    public AnimatableRectF(RectF rectF) {
        super(rectF);
    }

    public AnimatableRectF(Rect rect) {
        super(rect);
    }

    public void setTop(float top) {
        this.top = top;
    }

    public void setBottom(float bottom) {
        this.bottom = bottom;
    }

    public void setRight(float right) {
        this.right = right;
    }

    public void setLeft(float left) {
        this.left = left;
    }
}
