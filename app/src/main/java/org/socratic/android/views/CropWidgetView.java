package org.socratic.android.views;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.StaticLayout;
import android.text.Layout;
import android.text.TextPaint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import org.socratic.android.R;
import org.socratic.android.SocraticApp;
import org.socratic.android.util.PhotoCropHelper;
import org.socratic.android.util.DimenUtil;

import javax.inject.Inject;

import static android.graphics.Color.WHITE;

/**
 * @date 2017-02-02
 */
public class CropWidgetView extends LinearLayout {

    @Inject SharedPreferences sharedPreferences;

    private static final String TAG = CropWidgetView.class.getSimpleName();
    public static final int MODE_CAPTURE = 0;
    public static final int MODE_EDITING = 1;
    public static final int MODE_HIDE_FTUE = 1;
    public static final int MODE_SHOW_FTUE = 0;
    public static final float CROP_OFFSET = 5;
    public static final int ANIMATION_SPEED = 200;
    public static final int QUERIES_BEFORE_SUBJECT_TIP = 3;

    private int mode;
    private int ftueMode;
    private RectF rcLeft;
    private RectF rcTop;
    private RectF rcRight;
    private RectF rcBottom;
    private Paint paint;
    private Rect rcMaxCrop;
    private AnimatableRectF rcCrop;
    private int minCropRectSize;
    private Paint paintCropOutside;
    private Paint paintCropInside;
    private DragState dragState;
    private TextPaint smallFTUETextPaint;
    private TextPaint largeFTUETextPaint;
    private Paint strokePaint;
    private Paint cameraOnboardingTipBackground;

    private int handleSizeWidth;
    private int handleSizeHeight;
    private float scale;

    private DrawRect bmpCrosshair;
    private DrawRect bmpCornerTopLeft;
    private DrawRect bmpCornerTopRight;
    private DrawRect bmpCornerBottomLeft;
    private DrawRect bmpCornerBottomRight;
    private DrawRect bmpCircleBottomRightCorner;
    private DrawRect bmpCircleTopRightCorner;
    private DrawRect bmpCircleBottomLeftCorner;
    private DrawRect bmpCircleTopLeftCorner;

    private PhotoCropHelper photoCropHelper;

    public CropWidgetView(Context context) {
        super(context);
        init();
        SocraticApp.getViewComponent(this, context).inject(this);
    }

    public CropWidgetView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        SocraticApp.getViewComponent(this, context).inject(this);
    }

    public CropWidgetView(Context context, AttributeSet attrs,
                          int defStyle)
    {
        super(context, attrs, defStyle);
        init();
        SocraticApp.getViewComponent(this, context).inject(this);
    }

    private void init() {
        setWillNotDraw(false);

        DisplayMetrics dm = getResources().getDisplayMetrics();

        mode = MODE_CAPTURE;

        rcLeft = new RectF();
        rcTop = new RectF();
        rcRight = new RectF();
        rcBottom = new RectF();

        rcMaxCrop = new Rect();
        rcCrop = new AnimatableRectF();

        dragState = new DragState();

        paint = new Paint();
        paint.setAntiAlias(true);

        paintCropOutside = new Paint();
        paintCropOutside.setColor(getResources().getColor(R.color.crop_widget_alpha_outside));
        paintCropOutside.setStyle(Paint.Style.FILL);

        paintCropInside =  new Paint();
        paintCropInside.setColor(getResources().getColor(R.color.crop_widget_alpha_inside));
        paintCropInside.setStyle(Paint.Style.FILL);

        Typeface ceraproFont = Typeface.createFromAsset(getContext().getAssets(), "fonts/cerapro-regular.otf");

        smallFTUETextPaint = new TextPaint();
        int cameraOnboardingTextSizeInSp = (int) getResources().getDimension(R.dimen.camera_onboarding_text_size);
        smallFTUETextPaint.setTypeface(ceraproFont);
        smallFTUETextPaint.setColor(Color.WHITE);
        smallFTUETextPaint.setStyle(Paint.Style.FILL);
        smallFTUETextPaint.setTextSize(DimenUtil.convertSpToPx(cameraOnboardingTextSizeInSp, getContext()));

        largeFTUETextPaint = new TextPaint();
        int cropOnboardTextSize = (int) getResources().getDimension(R.dimen.onboarding_text_size);
        largeFTUETextPaint.setTypeface(ceraproFont);
        largeFTUETextPaint.setColor(Color.WHITE);
        largeFTUETextPaint.setShadowLayer(1.0f, 0, 5.0f, getResources().getColor(R.color.dropShadow));
        largeFTUETextPaint.setStyle(Paint.Style.FILL);
        largeFTUETextPaint.setTextSize(DimenUtil.convertSpToPx(cropOnboardTextSize, getContext()));

        cameraOnboardingTipBackground = new Paint();
        cameraOnboardingTipBackground.setColor(getResources().getColor(R.color.cameraOnboardingTip));
        cameraOnboardingTipBackground.setStyle(Paint.Style.FILL);

        strokePaint = new Paint();
        strokePaint.setColor(WHITE);
        strokePaint.setStyle(Paint.Style.STROKE);

        minCropRectSize = (int)getResources().getDimension(R.dimen.min_crop_rect_height);

        handleSizeWidth = (int)(getResources().getDimension(R.dimen.crop_handlebar_bitmap_width));
        handleSizeHeight = (int)(getResources().getDimension(R.dimen.crop_handlebar_bitmap_height));
        int handleOffset = (int)(getResources().getDimension(R.dimen.crop_handlebar_bitmap_offset));

        int cornerSizeWidth = (int)(getResources().getDimension(R.dimen.capture_corner_bitmap_width));
        int cornerSizeHeight = (int)(getResources().getDimension(R.dimen.capture_corner_bitmap_height));
        int crosshairSizeWidth = (int)(getResources().getDimension(R.dimen.capture_crosshair_bitmap_width));
        int crosshairSizeHeight = (int)(getResources().getDimension(R.dimen.capture_crosshair_bitmap_height));

        bmpCrosshair = new DrawRect(getContext(),
                R.drawable.camera_crosshair, crosshairSizeWidth, crosshairSizeHeight,
                DrawRect.DRAW_CENTERED);
        bmpCornerTopLeft = new DrawRect(getContext(),
                R.drawable.camera_corner_top_left, cornerSizeWidth, cornerSizeHeight,
                DrawRect.DRAW_UPPER_LEFT);
        bmpCornerTopRight = new DrawRect(getContext(),
                R.drawable.camera_corner_top_right, cornerSizeWidth, cornerSizeHeight,
                DrawRect.DRAW_UPPER_RIGHT);
        bmpCornerBottomLeft = new DrawRect(getContext(),
                R.drawable.camera_corner_bottom_left, cornerSizeWidth, cornerSizeHeight,
                DrawRect.DRAW_LOWER_LEFT);
        bmpCornerBottomRight = new DrawRect(getContext(),
                R.drawable.camera_corner_bottom_right, cornerSizeWidth, cornerSizeHeight,
                DrawRect.DRAW_LOWER_RIGHT);
        bmpCircleTopLeftCorner = new DrawRect(getContext(),
                R.drawable.crop_handle, handleSizeHeight, handleSizeWidth,
                DrawRect.DRAW_CENTERED);
        bmpCircleTopRightCorner = new DrawRect(getContext(),
                R.drawable.crop_handle, handleSizeHeight, handleSizeWidth,
                DrawRect.DRAW_CENTERED);
        bmpCircleBottomLeftCorner = new DrawRect(getContext(),
                R.drawable.crop_handle, handleSizeWidth, handleSizeHeight,
                DrawRect.DRAW_CENTERED);
        bmpCircleBottomRightCorner = new DrawRect(getContext(),
                R.drawable.crop_handle, handleSizeWidth, handleSizeHeight,
                DrawRect.DRAW_CENTERED);

        bmpCircleTopLeftCorner.setOffsetHorizontal(handleOffset);
        bmpCircleTopRightCorner.setOffsetVertical(handleOffset);
        bmpCircleBottomLeftCorner.setOffsetHorizontal(-handleOffset);
        bmpCircleBottomRightCorner.setOffsetVertical(-handleOffset);

        photoCropHelper = new PhotoCropHelper();

        scale = DimenUtil.getDisplayScale(getContext());

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
    }

    public RectF getCropRectangle() {
        return rcCrop;
    }

    public void setCropRectangle(int l, int t, int r, int b) {
        rcCrop = new AnimatableRectF(l, t, r, b);
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        Log.d(TAG, "onSizeChanged() to: " + w + " x " + h);

        photoCropHelper.initScreenSize(getContext(), w, h);
        rcMaxCrop = photoCropHelper.getMaxCropRect();

        rcCrop = new AnimatableRectF(rcMaxCrop);

        if (mode == MODE_EDITING) {
            animateCropRect();
        }
        resetDrawingRects();
    }

    private void animateCropRect() {
        ObjectAnimator animateLeft = ObjectAnimator.ofFloat(rcCrop, "left", rcCrop.left, rcCrop.left + scale / CROP_OFFSET);
        ObjectAnimator animateTop = ObjectAnimator.ofFloat(rcCrop, "top", rcCrop.top, rcCrop.top + scale / CROP_OFFSET);
        ObjectAnimator animateRight = ObjectAnimator.ofFloat(rcCrop, "right", rcCrop.right, rcCrop.right - scale / CROP_OFFSET);
        ObjectAnimator animateBottom = ObjectAnimator.ofFloat(rcCrop, "bottom", rcCrop.bottom, rcCrop.bottom - scale / CROP_OFFSET);
        animateBottom.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                //update the crop handle positions and refresh the view
                updateCropPositions();
                postInvalidate();
            }
        });

        AnimatorSet animatorSet = new AnimatorSet();
        if (!animatorSet.isStarted()) {
            animatorSet.playTogether(animateLeft, animateTop, animateRight, animateBottom);
            animatorSet.setDuration(ANIMATION_SPEED).start();
        }
    }

    private void resetDrawingRects() {
        DisplayMetrics dm = getResources().getDisplayMetrics();

        rcLeft.set(0, 0, rcMaxCrop.left, getBottom());
        rcTop.set(rcMaxCrop.left, 0, rcMaxCrop.right, rcMaxCrop.top);
        rcRight.set(rcMaxCrop.right, 0, getRight(), getBottom());
        rcBottom.set(rcMaxCrop.left, rcMaxCrop.bottom, rcMaxCrop.right, getBottom());

        updateCropPositions();

        bmpCrosshair.scaleAndMove(dm, rcMaxCrop.centerX(), rcMaxCrop.centerY());
        bmpCornerTopLeft.scaleAndMove(dm, rcMaxCrop.left, rcMaxCrop.top);
        bmpCornerTopRight.scaleAndMove(dm, rcMaxCrop.right, rcMaxCrop.top);
        bmpCornerBottomLeft.scaleAndMove(dm, rcMaxCrop.left, rcMaxCrop.bottom);
        bmpCornerBottomRight.scaleAndMove(dm, rcMaxCrop.right, rcMaxCrop.bottom);
    }

    private void updateCropPositions() {
        DisplayMetrics dm = getResources().getDisplayMetrics();

        bmpCircleTopLeftCorner.scaleAndMove(dm, Math.round(rcCrop.left), Math.round(rcCrop.top));
        bmpCircleTopRightCorner.scaleAndMove(dm, Math.round(rcCrop.right), Math.round(rcCrop.top));
        bmpCircleBottomLeftCorner.scaleAndMove(dm, Math.round(rcCrop.left), Math.round(rcCrop.bottom));
        bmpCircleBottomRightCorner.scaleAndMove(dm, Math.round(rcCrop.right), Math.round(rcCrop.bottom));
    }

    @Override
    public void onDraw(Canvas canvas) {
        boolean isFirstTime = sharedPreferences.getBoolean("firstTime", true);

        if (mode == MODE_EDITING) {
            rcLeft.set(0, 0, rcCrop.left, getBottom());
            rcTop.set(rcCrop.left, 0, rcCrop.right, rcCrop.top);
            rcRight.set(rcCrop.right, 0, getRight(), getBottom());
            rcBottom.set(rcCrop.left, rcCrop.bottom, rcCrop.right, getBottom());

            canvas.drawRect(rcLeft, paintCropOutside);
            canvas.drawRect(rcTop, paintCropOutside);
            canvas.drawRect(rcRight, paintCropOutside);
            canvas.drawRect(rcBottom, paintCropOutside);

            canvas.drawRoundRect(rcCrop, 15, 15, paintCropInside);
            canvas.drawRoundRect(rcCrop, 15, 15, strokePaint);

            bmpCircleTopLeftCorner.draw(canvas, paint);
            bmpCircleTopRightCorner.draw(canvas, paint);
            bmpCircleBottomLeftCorner.draw(canvas, paint);
            bmpCircleBottomRightCorner.draw(canvas, paint);

            showCropOnboardTip(canvas);
        } else {
            canvas.drawRect(rcLeft, paintCropOutside);
            canvas.drawRect(rcTop, paintCropOutside);
            canvas.drawRect(rcRight, paintCropOutside);
            canvas.drawRect(rcBottom, paintCropOutside);

            bmpCornerTopLeft.draw(canvas, paint);
            bmpCornerTopRight.draw(canvas, paint);
            bmpCornerBottomLeft.draw(canvas, paint);
            bmpCornerBottomRight.draw(canvas, paint);

            // We do not want to show the cross hair image when the
            // first time camera onboarding tip is shown
            if (!isFirstTime) {
                bmpCrosshair.draw(canvas, paint);
            }

            if (ftueMode != MODE_HIDE_FTUE) {
                showCameraOnboardTip(canvas);
                showSubjectsOnboardTip(canvas);
            }
        }
    }

    private void showCropOnboardTip(Canvas canvas) {
        Rect rect = new Rect();
        rect.set(canvas.getWidth()/8, rcMaxCrop.top/4,canvas.getWidth()-(canvas.getWidth()/8),rcMaxCrop.top);
        StaticLayout sl = new StaticLayout(getResources().getString(R.string.crop_msg), largeFTUETextPaint, rect.width(),
                Layout.Alignment.ALIGN_CENTER, 1, 1, true);
        canvas.save();

        float textHeight = getTextHeight(getResources().getString(R.string.crop_msg), largeFTUETextPaint);
        int numberOfTextLines = sl.getLineCount();
        float textYCoordinate = rect.exactCenterY() -
                ((numberOfTextLines * textHeight) / 2);
        float textXCoordinate = rect.left;

        canvas.translate(textXCoordinate, textYCoordinate);

        //draws static layout on canvas
        sl.draw(canvas);
        canvas.restore();
    }

    private void showCameraOnboardTip(Canvas canvas) {
        //determine whether to show camera onboarding tip (only the first time user uses camera)
        boolean isFirstTime = sharedPreferences.getBoolean("firstTime", true);

        if (isFirstTime) {
            //draw onboard tip background
            RectF rect = new RectF();
            rect.set(rcMaxCrop.left + (rcMaxCrop.left + rcMaxCrop.right) / 11, (rcMaxCrop.top + rcMaxCrop.bottom) / 2 - (rcMaxCrop.bottom - rcMaxCrop.top) / 7, rcMaxCrop.right - (rcMaxCrop.left + rcMaxCrop.right) / 11, (rcMaxCrop.top + rcMaxCrop.bottom) / 2 + (rcMaxCrop.bottom - rcMaxCrop.top) / 5);
            canvas.drawRoundRect(rect, 140, 140, cameraOnboardingTipBackground);

            Rect rectText = new Rect();
            rectText.set(rcMaxCrop.left + (rcMaxCrop.left + rcMaxCrop.right) / 5, (rcMaxCrop.top + rcMaxCrop.bottom) / 2 - (rcMaxCrop.bottom - rcMaxCrop.top) / 4, rcMaxCrop.right - (rcMaxCrop.left + rcMaxCrop.right) / 5, (rcMaxCrop.top + rcMaxCrop.bottom) / 2 + (rcMaxCrop.bottom - rcMaxCrop.top) / 4);
            StaticLayout sl = new StaticLayout(getResources().getString(R.string.take_picture_with_question), smallFTUETextPaint, rectText.width(),
                    Layout.Alignment.ALIGN_CENTER, 1, 1, true);
            canvas.save();

            float textHeight = getTextHeight(getResources().getString(R.string.take_picture_with_question), smallFTUETextPaint);
            int numberOfTextLines = sl.getLineCount();
            float textYCoordinate = rectText.exactCenterY() -
                    ((numberOfTextLines * textHeight) / 2);
            float textXCoordinate = rectText.left;

            canvas.translate(textXCoordinate, textYCoordinate);

            //draws static layout on canvas
            sl.draw(canvas);
            canvas.restore();
        }

    }

    private void showSubjectsOnboardTip(Canvas canvas) {
        //determine whether to show subjects onboarding tip (only after 3 successful uses)
        int successfulQueries = sharedPreferences.getInt("successfulQueries", 0);
        boolean subjectOnboardShown = sharedPreferences.getBoolean("subjectOnboardShown", false);

        if (successfulQueries >= QUERIES_BEFORE_SUBJECT_TIP && !subjectOnboardShown) {
            Rect rect = new Rect();
            rect.set(canvas.getWidth()/4, rcMaxCrop.top/2, canvas.getWidth()-(canvas.getWidth()/4),rcMaxCrop.top/2);
            //draw onboard tip text
            StaticLayout sl = new StaticLayout(getResources().getString(R.string.try_subjects), largeFTUETextPaint, rect.width(),
                    Layout.Alignment.ALIGN_CENTER, 1, 1, true);
            canvas.save();
            float textHeight = getTextHeight(getResources().getString(R.string.try_subjects), largeFTUETextPaint);
            int numberOfTextLines = sl.getLineCount();
            float textYCoordinate = rect.exactCenterY() -
                    ((numberOfTextLines * textHeight) / 2);
            float textXCoordinate = rect.left;

            canvas.translate(textXCoordinate, textYCoordinate);
            //draws static layout on canvas
            sl.draw(canvas);
            canvas.restore();
        }
    }

    private float getTextHeight(String text, Paint paint) {

        Rect rect = new Rect();
        paint.getTextBounds(text, 0, text.length(), rect);
        return rect.height();
    }


    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (mode == MODE_CAPTURE) {
            return super.onTouchEvent(e);
        }

        int x = (int)e.getX();
        int y = (int)e.getY();

        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                dragState.reset();
                dragState.ptDrag.set(x, y);

                if (x < rcCrop.left) {
                    dragState.mode = DragState.DRAG_STATE_LEFT;
                } else if (x > rcCrop.right) {
                    dragState.mode = DragState.DRAG_STATE_RIGHT;
                } else if (y < rcCrop.top + rcCrop.height() / 2) {
                    dragState.mode = DragState.DRAG_STATE_TOP;
                } else {
                    dragState.mode = DragState.DRAG_STATE_BOTTOM;
                }

                break;

            case MotionEvent.ACTION_MOVE:

                int dx = x - dragState.ptDrag.x;
                int dy = y - dragState.ptDrag.y;
                float cx = rcCrop.left + rcCrop.width() / 2;
                float cy = rcCrop.top + rcCrop.height() / 2;

                    if (x < cx) {
                        // update left edge.
                        rcCrop.left = clampRect(
                                rcCrop.left + dx,
                                rcMaxCrop.left,
                                rcCrop.right - minCropRectSize);
                    } else {
                        // update right edge.
                        rcCrop.right = clampRect(
                                rcCrop.right + dx,
                                rcCrop.left + minCropRectSize,
                                rcMaxCrop.right);
                    }

                    if (y < cy) {
                        // update top edge.
                        rcCrop.top = clampRect(
                                rcCrop.top + dy,
                                rcMaxCrop.top,
                                rcCrop.bottom - minCropRectSize);
                    } else {
                        // update bottom edge.
                        rcCrop.bottom = clampRect(
                                rcCrop.bottom + dy,
                                rcCrop.top + minCropRectSize,
                                rcMaxCrop.bottom);
                    }

                dragState.ptDrag.set(x, y);

                break;

            case MotionEvent.ACTION_UP:
                dragState.reset();
                break;
        }

        updateCropPositions();
        invalidate();
        return true;
    }

    private float clampRect(float newPos, float min, float max) {
        if (newPos < min) {
            return min;
        } else if (newPos > max) {
            return max;
        }

        return newPos;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public void setFTUEMode(int mode) {
        this.ftueMode = mode;
    }

    private static class DragState {

        static final int DRAG_STATE_NONE = 0;
        static final int DRAG_STATE_TOP = 1;
        static final int DRAG_STATE_BOTTOM = 2;
        static final int DRAG_STATE_LEFT = 3;
        static final int DRAG_STATE_RIGHT = 4;
        Point ptDrag;
        int mode;

        public DragState() {
            ptDrag = new Point();
        }

        public void reset() {
            ptDrag.set(0, 0);
            mode = DRAG_STATE_NONE;
        }
    }

    private static class DrawRect {
        // draw methods for handling y,x position.
        static final int DRAW_CENTERED = 0;
        static final int DRAW_UPPER_LEFT = 1;
        static final int DRAW_LOWER_LEFT = 2;
        static final int DRAW_UPPER_RIGHT = 3;
        static final int DRAW_LOWER_RIGHT = 4;
        static final int DRAW_MIDDLE_LEFT = 5;
        static final int DRAW_MIDDLE_RIGHT = 6;
        static final int DRAW_TOP_MIDDLE = 7;
        static final int DRAW_BOTTOM_MIDDLE = 8;

        int drawMode;
        Bitmap bitmap;
        Rect rcSrc;
        Rect rcDst;
        int width;
        int height;
        int offsetHorizontal;
        int offsetVertical;

        public DrawRect(Context context, int resId, int width, int height, int drawMode) {
            this.bitmap = BitmapFactory.decodeResource(context.getResources(), resId);
            this.rcSrc = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            this.rcDst = new Rect();
            this.width = width;
            this.height = height;
            this.drawMode = drawMode;
        }

        public void setOffsetHorizontal(int offsetHorizontal) {
            this.offsetHorizontal = offsetHorizontal;
        }

        public void setOffsetVertical(int offsetVertical) {
            this.offsetVertical = offsetVertical;
        }

        public void scaleAndMove(DisplayMetrics dm, int x, int y) {
            if (drawMode == DRAW_CENTERED) {
                rcDst.left = (x - width / 2);
                rcDst.top = (y - height / 2);
            } else if (drawMode == DRAW_UPPER_LEFT) {
                rcDst.left = x;
                rcDst.top = y;
            } else if (drawMode == DRAW_LOWER_LEFT) {
                rcDst.left = x;
                rcDst.top = y - height;
            } else if (drawMode == DRAW_UPPER_RIGHT) {
                rcDst.left = x - width;
                rcDst.top = y;
            } else if (drawMode == DRAW_LOWER_RIGHT) {
                rcDst.left = x - width;
                rcDst.top = y - height;
            } else if (drawMode == DRAW_MIDDLE_LEFT) {
                rcDst.left = x;
                rcDst.top = y - height / 2;
            } else if (drawMode == DRAW_MIDDLE_RIGHT ) {
                rcDst.left = x - width;
                rcDst.top = y - height / 2;
            } else if (drawMode == DRAW_TOP_MIDDLE) {
                rcDst.left = x - width / 2;
                rcDst.top = y;
            } else if (drawMode == DRAW_BOTTOM_MIDDLE) {
                rcDst.left = x - width / 2;
                rcDst.top = y - height;
            }
            rcDst.right = rcDst.left + width;
            rcDst.bottom = rcDst.top + height;

            rcDst.offset(offsetHorizontal, offsetVertical);
        }

        public void draw(Canvas canvas, Paint paint) {
            canvas.drawBitmap(bitmap, rcSrc, rcDst, paint);
        }
    }
}
