package org.socratic.android.views;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import maximsblog.blogspot.com.jlatexmath.core.Insets;
import maximsblog.blogspot.com.jlatexmath.core.TeXConstants;
import maximsblog.blogspot.com.jlatexmath.core.TeXFormula;
import maximsblog.blogspot.com.jlatexmath.core.TeXIcon;

/**
 * Renders on to canvas using jlatex android lib.
 */
public class JLatexView extends View {
    String latex;
    int textSize;
    TeXFormula formula = null;
    TeXFormula.TeXIconBuilder cachedIconBuilder;
    TeXIcon cachedIcon;
    int currentWidthMeasureSpec;
    int currentHeightMeasureSpec;
    int currentMeasuredWidth;
    int currentMeasuredHeight;
    Integer textColor = null;
    private boolean isMultiLine;

    public JLatexView(Context context) {
        super(context);
    }

    public JLatexView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        textSize = 12;
    }

    public void setTextColor(int color) {
        textColor = color;
        invalidate();
    }

    public void setLatex(String latex, boolean multiline) {

        this.isMultiLine = multiline;

        if (this.latex != null && this.latex.equals(latex)) {
            return;
        }
        this.latex = latex;
        if (latex != null) {
            try {
                // New source latex means we throw everything away and start over.
                formula = new TeXFormula(latex);
                cachedIcon = null;
                // We reuse the same builder until the formula changes.
                cachedIconBuilder = formula.new TeXIconBuilder().setStyle(TeXConstants.STYLE_DISPLAY);


            } catch (Throwable t) {
                Log.w(JLatexView.class.getName(), "Couldn't compile formula", t);
            }
        }
        // We need to ask for a layout pass so we can re-measure since the content has changed.
        requestLayout();
    }

    public String getLatex() {
        return this.latex;
    }

    public void setTextSize(int textSize) {
        if (this.textSize == textSize) {
            return;
        }
        this.textSize = textSize;
        // If the text size changes, we need to throw away our cached icon since
        // text size of icon is fixed.
        cachedIcon = null;
        // We need to ask for a layout pass so we can re-measure with the new text size.
        requestLayout();
    }

    private void setMeasuredDimensionInternal(int measuredWidth, int measuredHeight) {
        setMeasuredDimension(measuredWidth, measuredHeight);
        // Preserve measured width and measured height in the case another measure pass occurs
        // and the content (latex) and measure specs (from the layout parent)
        // are all the same. We don't need to re-measure, we can just re-use these values.
        currentMeasuredWidth = measuredWidth;
        currentMeasuredHeight = measuredHeight;
    }

    public int getCurrentMeasuredWidth() {
        return this.currentMeasuredWidth;
    }

    public int getCurrentMeasuredHeight() {
        return this.currentMeasuredHeight;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        if (formula != null) {
            // If the layout is measuring us again with the same content and same measure
            // spec constraints, we just return our previous measurements and skip the step.
            if (widthMeasureSpec == currentWidthMeasureSpec
                    && currentHeightMeasureSpec == heightMeasureSpec
                    && cachedIcon != null) {

                setMeasuredDimension(currentMeasuredWidth, currentMeasuredHeight);
                return;
            }

            // Save these for the next pass.
            currentWidthMeasureSpec = widthMeasureSpec;
            currentHeightMeasureSpec = heightMeasureSpec;

            // (Re)set the text size on the icon builder.
            cachedIconBuilder.setSize(textSize);

            int exactWidth;
            switch (MeasureSpec.getMode(widthMeasureSpec)) {
                case MeasureSpec.EXACTLY:
                    // The parent layout is telling us that it wants us to be a specific size,
                    // making width measurement trivial. Just take the size in pixels, and subtract
                    // the left and right padding which will be added back in #setInsets()
                    exactWidth = MeasureSpec.getSize(widthMeasureSpec);
                    cachedIconBuilder.setWidth(TeXConstants.UNIT_PIXEL, exactWidth - getPaddingLeft() - getPaddingRight(), TeXConstants.ALIGN_LEFT)
                            .setIsMaxWidth(false);
                    break;
                case MeasureSpec.UNSPECIFIED:
                    // The parent layout is telling us that we can be any width we want. So let's
                    // set an arbitrarily large maximum size for the formula and take up as much space
                    // as the actual formula wants. This is an unlikely layout, that I believe will only occur when
                    // wrap_content is used for the JLatexView inside a horizontal scroll or
                    // recycler view.
                    cachedIconBuilder.setWidth(TeXConstants.UNIT_PIXEL,
                            Integer.MAX_VALUE, TeXConstants.ALIGN_LEFT)
                            .setIsMaxWidth(true);
                    break;
                case MeasureSpec.AT_MOST:
                    // The parent layout is telling us that we can be at most X pixels. This is the
                    // "wrap_content" case with a bounded width container, which is much more common.
                    // We set the maximum width to be the specified pixel size, less left and right padding,
                    // We'll set the actual measured width to be only as much room as the formula takes up.
                    cachedIconBuilder.setWidth(TeXConstants.UNIT_PIXEL, MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight(), TeXConstants.ALIGN_LEFT)
                            .setIsMaxWidth(true);
            }

            // If this flag is true, this latex can be displayed on more than one line
            if(isMultiLine) {
                cachedIconBuilder.setInterLineSpacing(TeXConstants.UNIT_PIXEL, 5);
            }

            // From the reusable builder, create an icon that will be used in onDraw to paint the canvas with
            // the formula.
            cachedIcon = cachedIconBuilder.build();

            // Get the width of the actual formula, and add back padding to set the real width in pixels of the view.
            int measuredWidth = cachedIcon.getIconWidth() + getPaddingLeft() + getPaddingRight();
            // Implement padding by using jlatex insets.
            cachedIcon.setInsets(new Insets(getPaddingLeft(), getPaddingTop(), getPaddingRight(), getPaddingBottom()));

            // Now that we have set the formula and the width,
            // The library should be able to tell us how tall the icon will be.
            switch (MeasureSpec.getMode(heightMeasureSpec)) {
                case MeasureSpec.EXACTLY:
                    // The parent is telling us that the height is fixed. So we respect that regardless of
                    // whether the formula will fit or not. This may result in draw overflow if the
                    // specified height truncates the content. However, this is an unlikely case.
                    setMeasuredDimensionInternal(measuredWidth, MeasureSpec.getSize(heightMeasureSpec));
                    break;
                case MeasureSpec.UNSPECIFIED:
                    // The parent is telling us that we can grow to be as tall as we want to be. This is probably
                    // the most normal case, wrap_content in a vertical list view or scroll view. We ask the icon what
                    // its height is, based on the width constraints we've already imposed earlier in this method.
                    // Remember that getIconHeight() only tells us how tall the actual text is, so we need to add in
                    // the top and bottom padding.
                    setMeasuredDimensionInternal(measuredWidth, cachedIcon.getIconHeight() + getPaddingBottom() + getPaddingTop());
                    break;
                case MeasureSpec.AT_MOST:
                    // The parent is telling us that we can grow to be as tall as we want to up until a fixed
                    // value. So we just give the shortest of the measured height or the fixed maximum value.
                    setMeasuredDimensionInternal(measuredWidth, Math.min(cachedIcon.getIconHeight() + getPaddingBottom() + getPaddingTop(),
                            MeasureSpec.getSize(heightMeasureSpec)));

            }
        } else {
            // If there's no formula to display, just act like a normal empty view.
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // Let android's base View class draw whatever background resource has been specified.
        super.onDraw(canvas);

        if (formula != null) {
            // On top, draw the math using the cached icon object.
            if (cachedIcon != null) {
                // Due to some jankyness in jlatex android, setting the foreground color in the
                // icon builder object doesn't work because by the time the builder calls setForeground()
                // the Box has already been initialized. So I added a setForeground color on
                // the icon that we can call after it has been built. It just passes it through to the
                // underlying box.
                cachedIcon.setForeground(textColor);
                cachedIcon.paintIcon(canvas, 0, 0);
            } else {
                Log.w(JLatexView.class.getName(), "No cached icon to paint? Why not?");
            }
        }
    }
}
