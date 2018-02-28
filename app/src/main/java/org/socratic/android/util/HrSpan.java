package org.socratic.android.util;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.style.ReplacementSpan;

/**
 * Created by byfieldj on 10/6/17.
 */

public class HrSpan extends ReplacementSpan {

    private Paint paint;

    public HrSpan(){
        paint = new Paint();
        paint.setColor(Color.parseColor("#33000000"));
    }
    @Override
    public int getSize(@NonNull Paint paint, CharSequence text, @IntRange(from = 0) int start,
                       @IntRange(from = 0) int end, @Nullable Paint.FontMetricsInt fm) {
        return 0;
    }

    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text, @IntRange(from = 0) int start,
                     @IntRange(from = 0) int end, float x, int top, int y, int bottom,
                     @NonNull Paint paint) {
        // Draws an 4px tall rectangle the same
        // width as the TextView
        canvas.drawRect(x, top, canvas.getWidth(), top + 4, this.paint);
    }
}