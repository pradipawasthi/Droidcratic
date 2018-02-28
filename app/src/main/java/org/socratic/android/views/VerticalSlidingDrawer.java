package org.socratic.android.views;

/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;

import org.socratic.android.R;

/**
 * VerticalSlidingDrawer is a custom implementation of SlidingDrawer.
 * The drawer hides content out of the screen and allows the user to swipe up and down
 * to reveal the content.
 *
 * Constructed as a special widget composed of two children views: the handle and the content
 *
 * VerticalSlidingDrawer should be used as an overlay inside layouts.
 * This means VerticalSlidingDrawer should only be used inside of a FrameLayout or a RelativeLayout.
 * The size of the VerticalSlidingDrawer defines how much space the content will occupy once slid
 * out so VerticalSlidingDrawer should usually use match_parent for both its dimensions.
 *
 * Inside an XML layout, VerticalSlidingDrawer must define the id of the handle and of the
 * content:
 *
 * <pre class="prettyprint">
 * &lt;SlidingDrawer
 *     android:id="@+id/drawer"
 *     android:layout_width="match_parent"
 *     android:layout_height="match_parent"
 *
 *     android:handle="@+id/handle"
 *     android:content="@+id/content"&gt;
 *
 *     &lt;ImageView
 *         android:id="@id/handle"
 *         android:layout_width="88dip"
 *         android:layout_height="44dip" /&gt;
 *
 *     &lt;GridView
 *         android:id="@id/content"
 *         android:layout_width="match_parent"
 *         android:layout_height="match_parent" /&gt;
 *
 * &lt;/SlidingDrawer&gt;
 * </pre>
 *
 */

public class VerticalSlidingDrawer extends ViewGroup {

    private static final int TAP_THRESHOLD = 6;
    private static final float MAXIMUM_TAP_VELOCITY = 100.0f;
    private static final float MAXIMUM_MINOR_VELOCITY = 150.0f;
    private static final float MAXIMUM_MAJOR_VELOCITY = 200.0f;
    private static final float MAXIMUM_ACCELERATION = 2000.0f;
    private static final int VELOCITY_UNITS = 1000;
    private static final int MSG_ANIMATE = 1000;
    private static final int ANIMATION_FRAME_DURATION = 1000 / 60;

    private static final int EXPANDED_FULL_OPEN = -10001;
    private static final int COLLAPSED_FULL_CLOSED = -10002;

    private final int mHandleId;
    private final int mContentId;

    private View mHandle;
    private View mContent;
    private View mNavControlView;
    private View mInputControlView;

    private final Rect mFrame = new Rect();
    private final Rect mTestFrame = new Rect();
    private final Rect mInvalidate = new Rect();
    private boolean mTracking;
    private boolean mLocked;

    private VelocityTracker mVelocityTracker;

    private boolean mVertical;
    private boolean mExpanded;
    private int mBottomOffset;
    private int mTopOffset;
    private int mHandleHeight;
    private int mHandleWidth;

    private OnDrawerOpenListener mOnDrawerOpenListener;
    private OnDrawerCloseListener mOnDrawerCloseListener;
    private OnDrawerScrollListener mOnDrawerScrollListener;
    private OnDrawerMoveListener mOnDrawerMoveListener;

    private final Handler mHandler = new SlidingHandler();
    private float mAnimatedAcceleration;
    private float mAnimatedVelocity;
    private float mAnimationPosition;
    private long mAnimationLastTime;
    private long mCurrentAnimationTime;
    private int mTouchDelta;
    private boolean mAnimating;
    private boolean mAllowSingleTap;
    private boolean mAnimateOnClick;

    private final int mTapThreshold;
    private final int mMaximumTapVelocity;
    private final int mMaximumMinorVelocity;
    private final int mMaximumMajorVelocity;
    private final int mMaximumAcceleration;
    private final int mVelocityUnits;

    private int mTouchSlop;
    private float downX, downY;

    /**
     * Callback invoked when the drawer is opened.
     */
    public static interface OnDrawerOpenListener {
        /**
         * Invoked when the drawer becomes fully open.
         */
        public void onDrawerOpened();
    }

    /**
     * Callback invoked when the drawer is closed.
     */
    public static interface OnDrawerCloseListener {
        /**
         * Invoked when the drawer becomes fully closed.
         */
        public void onDrawerClosed();
    }

    /**
     * Callback invoked when the drawer is scrolled.
     */
    public static interface OnDrawerScrollListener {
        /**
         * Invoked when the user starts dragging/flinging the drawer's handle.
         */
        public void onScrollStarted();

        /**
         * Invoked when the user stops dragging/flinging the drawer's handle.
         */
        public void onScrollEnded();
    }

    /**
     * Callback invoked when the drawer begins to expand or collapse
     */
    public static interface OnDrawerMoveListener {

        /**
         * Invoked when the drawer starts to expand
         */
        public void onDrawerExpand();

        /**
         * Invoked when the drawer starts to collapse
         */
        public void onDrawerCollapse();
    }

    /**
     * Creates a new SlidingDrawer from a specified set of attributes defined in XML.
     *
     * @param context The application's environment.
     */
    public VerticalSlidingDrawer(Context context) {
        this(context, null);
    }

    /**
     * Creates a new SlidingDrawer from a specified set of attributes defined in XML.
     *
     * @param context The application's environment.
     * @param attrs The attributes defined in XML.
     */
    public VerticalSlidingDrawer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Creates a new SlidingDrawer from a specified set of attributes defined in XML.
     *
     * @param context The application's environment.
     * @param attrs The attributes defined in XML.
     * @param defStyleAttr An attribute in the current theme that contains a
     *        reference to a style resource that supplies default values for
     *        the view. Can be 0 to not look for defaults.
     */
    public VerticalSlidingDrawer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.VerticalSlidingDrawer, defStyleAttr, 0);

        mVertical = true;
        mBottomOffset = (int) a.getDimension(R.styleable.VerticalSlidingDrawer_bottomOffset, 0.0f);
        mTopOffset = (int) a.getDimension(R.styleable.VerticalSlidingDrawer_topOffset, 0.0f);
        mAllowSingleTap = a.getBoolean(R.styleable.VerticalSlidingDrawer_allowSingleTap, true);
        mAnimateOnClick = a.getBoolean(R.styleable.VerticalSlidingDrawer_animateOnClick, true);

        int handleId = a.getResourceId(R.styleable.VerticalSlidingDrawer_handle, 0);
        if (handleId == 0) {
            throw new IllegalArgumentException("The handle attribute is required and must refer "
                    + "to a valid child.");
        }

        int contentId = a.getResourceId(R.styleable.VerticalSlidingDrawer_content, 0);
        if (contentId == 0) {
            throw new IllegalArgumentException("The content attribute is required and must refer "
                    + "to a valid child.");
        }

        if (handleId == contentId) {
            throw new IllegalArgumentException("The content and handle attributes must refer "
                    + "to different children.");
        }

        mHandleId = handleId;
        mContentId = contentId;

        ViewConfiguration vc = ViewConfiguration.get(context);
        mTouchSlop = vc.getScaledTouchSlop();

        final float density = getResources().getDisplayMetrics().density;
        mTapThreshold = (int) (TAP_THRESHOLD * density + 0.5f);
        mMaximumTapVelocity = (int) (MAXIMUM_TAP_VELOCITY * density + 0.5f);
        mMaximumMinorVelocity = (int) -(MAXIMUM_MINOR_VELOCITY * density + 0.5f);
        mMaximumMajorVelocity = (int) -(MAXIMUM_MAJOR_VELOCITY * density + 0.5f);
        mMaximumAcceleration = (int) -(MAXIMUM_ACCELERATION * density + 0.5f);
        mVelocityUnits = (int) (VELOCITY_UNITS * density + 0.5f);

        a.recycle();

        setAlwaysDrawnWithCacheEnabled(false);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mHandle = findViewById(mHandleId);
        if (mHandle == null) {
            throw new IllegalArgumentException("The handle attribute is must refer to an"
                    + " existing child.");
        }
        mHandle.setOnClickListener(new DrawerToggler());

        mContent = findViewById(mContentId);
        if (mContent == null) {
            throw new IllegalArgumentException("The content attribute is must refer to an"
                    + " existing child.");
        }
        mContent.setVisibility(View.GONE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize =  MeasureSpec.getSize(widthMeasureSpec);

        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize =  MeasureSpec.getSize(heightMeasureSpec);

        if (widthSpecMode == MeasureSpec.UNSPECIFIED || heightSpecMode == MeasureSpec.UNSPECIFIED) {
            throw new RuntimeException("SlidingDrawer cannot have UNSPECIFIED dimensions");
        }

        final View handle = mHandle;
        measureChild(handle, widthMeasureSpec, heightMeasureSpec);

        if (mVertical) {
            int height = heightSpecSize - handle.getMeasuredHeight() - mTopOffset;
            mContent.measure(MeasureSpec.makeMeasureSpec(widthSpecSize, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
        } else {
            int width = widthSpecSize - handle.getMeasuredWidth() - mTopOffset;
            mContent.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(heightSpecSize, MeasureSpec.EXACTLY));
        }

        setMeasuredDimension(widthSpecSize, heightSpecSize);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        final long drawingTime = getDrawingTime();
        final View handle = mHandle;
        final boolean isVertical = mVertical;

        drawChild(canvas, handle, drawingTime);

        if (mTracking || mAnimating) {
            final Bitmap cache = mContent.getDrawingCache();
            if (cache != null) {
                if (isVertical) {
                    canvas.drawBitmap(cache, 0, handle.getTop() - (getBottom() - getTop()) + mHandleHeight, null);
                } else {
                    canvas.drawBitmap(cache, handle.getLeft() - cache.getWidth(), 0, null);
                }
            } else {
                canvas.save();
                canvas.translate(isVertical ? 0 : handle.getLeft() - mTopOffset - mContent.getMeasuredWidth(), isVertical ? handle.getTop() - mTopOffset - mContent.getMeasuredHeight() : 0);
                drawChild(canvas, mContent, drawingTime);
                canvas.restore();
            }
            invalidate();
        } else if (mExpanded) {
            drawChild(canvas, mContent, drawingTime);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (mTracking) {
            return;
        }

        final int width = r - l;
        final int height = b - t;

        final View handle = mHandle;

        int childWidth = handle.getMeasuredWidth();
        int childHeight = handle.getMeasuredHeight();

        int childLeft;
        int childTop;

        final View content = mContent;

        if (mVertical) {
            childLeft = (width - childWidth) / 2;
            childTop = mExpanded ? height - mBottomOffset - childHeight : mTopOffset;
            content.layout( 0, mTopOffset, content.getMeasuredWidth(), mTopOffset + content.getMeasuredHeight() );
        } else {
            childTop = (height - childHeight) / 2;
            childLeft = mExpanded ? width - mBottomOffset - childWidth : mTopOffset;
            content.layout( mTopOffset, 0, mTopOffset + content.getMeasuredWidth(), content.getMeasuredHeight() );
        }

        handle.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
        mHandleHeight = handle.getHeight();
        mHandleWidth = handle.getWidth();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (mLocked) {
            return false;
        }

        final int action = event.getAction();

        float x = event.getX();
        float y = event.getY();

        //disable touch events if detected to be for nav page controller
        if (!checkIfTouchIsNavControl(x, y)) {
            return false;
        }

        //disable touch events if detected to be for input method controller
        if (!checkIfTouchIsInputControl(x, y)) {
            return false;
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN :
                downX = event.getX();
                downY = event.getY();

                mTracking = true;

                // Must be called before prepareTracking()
                prepareContent();

                if (mVertical) {
                    final int top = mHandle.getTop();
                    mTouchDelta = (int) downY - top;
                    prepareTracking(top);
                } else {
                    final int left = mHandle.getLeft();
                    mTouchDelta = (int) downX - left;
                    prepareTracking(left);
                }
                mVelocityTracker.addMovement(event);

                break;
        }

        return super.onInterceptTouchEvent(event);
    }

    public boolean checkIfTouchIsNavControl(float x, float y) {
        Rect frame = mFrame;
        boolean consumeTouchEvent = true;

        mNavControlView.getHitRect(frame);
        if (frame.contains((int) x, (int) y) && !mExpanded) {
            consumeTouchEvent = false;
        }

        return consumeTouchEvent;
    }

    public boolean checkIfTouchIsInputControl(float x, float y) {
        Rect frame = mTestFrame;
        boolean consumeTouchEvent = true;

        mInputControlView.getHitRect(frame);

        if (frame.contains((int) x, (int) y) && !mExpanded) {
            consumeTouchEvent = false;
        }

        return consumeTouchEvent;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mLocked) {
            return true;
        }

        if (mTracking) {
            mVelocityTracker.addMovement(event);
            final int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_MOVE:

                    //make sure that the scroll listener is only activated on vertical scroll movements
                    float moveY = event.getY();

                    float deltaY = Math.abs(downY - moveY);

                    if (deltaY > mTouchSlop) {
                        if (mOnDrawerScrollListener != null) {
                            mOnDrawerScrollListener.onScrollStarted();
                        }

                        moveHandle((int) (mVertical ? event.getY() : event.getX()) - mTouchDelta);
                    }

                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL: {

                    if (isHorizontalSwipe(event)) {
                        return false;
                    }

                    final VelocityTracker velocityTracker = mVelocityTracker;
                    velocityTracker.computeCurrentVelocity(mVelocityUnits);

                    float yVelocity = velocityTracker.getYVelocity();
                    float xVelocity = velocityTracker.getXVelocity();
                    boolean negative;

                    final boolean vertical = mVertical;
                    if (vertical) {
                        negative = yVelocity < 0;
                        if (xVelocity < 0) {
                            xVelocity = -xVelocity;
                        }
                        if (xVelocity < mMaximumMinorVelocity) {
                            xVelocity = mMaximumMinorVelocity;
                        }
                    } else {
                        negative = xVelocity < 0;
                        if (yVelocity < 0) {
                            yVelocity = -yVelocity;
                        }
                        if (yVelocity < mMaximumMinorVelocity) {
                            yVelocity = mMaximumMinorVelocity;
                        }
                    }

                    float velocity = (float) Math.hypot(xVelocity, yVelocity);
                    if (negative) {
                        velocity = -velocity;
                    }

                    final int top = mHandle.getTop();
                    final int left = mHandle.getLeft();
                    final int bottom = mHandle.getBottom();
                    final int right = mHandle.getRight();

                    if (Math.abs(velocity) < mMaximumTapVelocity) {
                        boolean c1;
                        boolean c2;
                        boolean c3;
                        boolean c4;

                        c1 = (mExpanded && (getBottom() - bottom) < mTapThreshold + mBottomOffset);
                        c2 = (!mExpanded && top < mTopOffset + mHandleHeight - mTapThreshold);
                        c3 = (mExpanded && (getRight() - right) < mTapThreshold + mBottomOffset);
                        c4 = (!mExpanded && left > mTopOffset + mHandleWidth + mTapThreshold);

                        if (vertical ? c1 || c2 : c3 || c4) {

                            if (mAllowSingleTap) {
                                playSoundEffect(SoundEffectConstants.CLICK);

                                if (mExpanded) {
                                    animateClose(vertical ? top : left);
                                } else {
                                    animateOpen( vertical ? top : left);
                                }
                            } else {
                                performFling(vertical ? top : left, velocity, false);
                            }
                        } else {
                            performFling(vertical ? top : left, velocity, false);
                        }
                    } else {
                        performFling(vertical ? top : left, velocity, false);
                    }
                }
                break;
            }
        }

        return mTracking || mAnimating || super.onTouchEvent(event);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    private boolean isHorizontalSwipe(MotionEvent event) {
        boolean isLeftSwipe = false;

        float upX = event.getX();
        float upY = event.getY();

        float deltaX = Math.abs(downX - upX);
        float changeY = Math.abs(downY - upY);

        if (changeY < (mTouchSlop * 5)) {
            if (deltaX > (mTouchSlop * 5)) {
                isLeftSwipe = true;
            }
        }

        return isLeftSwipe;
    }

    private void animateClose(int position) {
        prepareTracking(position);
        performFling(position, mMaximumAcceleration, true);
    }

    private void animateOpen(int position) {
        prepareTracking(position);
        performFling(position, -mMaximumAcceleration, true);
    }

    private void performFling(int position, float velocity, boolean always) {
        mAnimationPosition = position;
        mAnimatedVelocity = velocity;

        boolean c1;
        boolean c2;
        boolean c3;

        if (mExpanded) {

            int bottom = mVertical ? getBottom() : getRight();
            int handleHeight = mVertical ? mHandleHeight : mHandleWidth;

            c1 = velocity < mMaximumMajorVelocity;
            c2 = (bottom - (position + handleHeight)) + mBottomOffset > handleHeight;
            c3 = velocity < -mMaximumMajorVelocity;

            if (always || (c1 || (c2 && c3))) {
                // We are expanded and are now going to animate away.

                mAnimatedAcceleration = mMaximumAcceleration;
                if (velocity > 0) {
                    mAnimatedVelocity = 0;
                }

                if (mOnDrawerMoveListener != null) {
                    mOnDrawerMoveListener.onDrawerCollapse();
                }
            } else {
                // We are expanded, but they didn't move sufficiently to cause
                // us to retract.  Animate back to the expanded position.
                mAnimatedAcceleration = -mMaximumAcceleration;
                if (velocity < 0) {
                    mAnimatedVelocity = 0;
                }

                if (mOnDrawerMoveListener != null) {
                    mOnDrawerMoveListener.onDrawerExpand();
                }
            }
        } else {

            c1 = velocity < mMaximumMajorVelocity;
            c2 = (position < (mVertical ? getHeight() : getWidth()) / 2);
            c3 = velocity < -mMaximumMajorVelocity;

            if (!always && (c1 || (c2 && c3))) {
                // We are collapsed, and they moved enough to allow us to expand.
                mAnimatedAcceleration = mMaximumAcceleration;
                if (velocity > 0) {
                    mAnimatedVelocity = 0;
                }

                if (mOnDrawerMoveListener != null) {
                    mOnDrawerMoveListener.onDrawerExpand();
                }

            } else {
                // We are collapsed, but they didn't move sufficiently to cause
                // us to retract.  Animate back to the collapsed position.
                mAnimatedAcceleration = -mMaximumAcceleration;
                if (velocity < 0) {
                    mAnimatedVelocity = 0;
                }

                if (mOnDrawerMoveListener != null) {
                    mOnDrawerMoveListener.onDrawerCollapse();
                }
            }
        }

        long now = SystemClock.uptimeMillis();
        mAnimationLastTime = now;
        mCurrentAnimationTime = now + ANIMATION_FRAME_DURATION;
        mAnimating = true;
        mHandler.removeMessages(MSG_ANIMATE);
        mHandler.sendMessageAtTime(mHandler.obtainMessage(MSG_ANIMATE), mCurrentAnimationTime);
        stopTracking();
    }

    private void prepareTracking(int position) {
        mTracking = true;
        mVelocityTracker = VelocityTracker.obtain();
        boolean opening = !mExpanded;
        if (opening) {
            mAnimatedAcceleration = mMaximumAcceleration;
            mAnimatedVelocity = mMaximumMajorVelocity;
            mAnimationPosition = mTopOffset;
            moveHandle((int) mAnimationPosition);
            mAnimating = true;
            mHandler.removeMessages(MSG_ANIMATE);
            long now = SystemClock.uptimeMillis();
            mAnimationLastTime = now;
            mCurrentAnimationTime = now + ANIMATION_FRAME_DURATION;
            mAnimating = true;
        } else {
            if (mAnimating) {
                mAnimating = false;
                mHandler.removeMessages(MSG_ANIMATE);
            }
            moveHandle(position);
        }
    }

    private void moveHandle(int position) {
        final View handle = mHandle;

        if (mVertical) {
            if (position == EXPANDED_FULL_OPEN) {
                handle.offsetTopAndBottom(mBottomOffset + getBottom() - getTop() - mHandleHeight);
                handle.setVisibility(GONE);
                invalidate();
            } else if (position == COLLAPSED_FULL_CLOSED) {
                handle.offsetTopAndBottom(mTopOffset - handle.getTop());
                handle.setVisibility(VISIBLE);
                invalidate();
            } else {
                final int top = handle.getTop();
                int deltaY = position - top;
                if (position < mTopOffset) {
                    deltaY = mTopOffset - top;
                } else if (deltaY > mBottomOffset + getBottom() - getTop() - mHandleHeight - top) {
                    deltaY = mBottomOffset + getBottom() - getTop() - mHandleHeight - top;
                }
                handle.offsetTopAndBottom(deltaY);

                final Rect frame = mFrame;
                final Rect region = mInvalidate;

                handle.getHitRect(frame);
                region.set(frame);

                region.union(frame.left, frame.top - deltaY, frame.right, frame.bottom - deltaY);
                region.union(0, frame.bottom - deltaY, getWidth(),
                        frame.bottom - deltaY + mContent.getHeight());

                handle.setVisibility(GONE);

                invalidate(region);
            }
        } else {
            if (position == EXPANDED_FULL_OPEN) {
                handle.offsetLeftAndRight(mBottomOffset + getRight() - getLeft() - mHandleWidth);
                invalidate();
            } else if (position == COLLAPSED_FULL_CLOSED) {
                handle.offsetLeftAndRight(mTopOffset - handle.getLeft());
                invalidate();
            } else {
                final int left = handle.getLeft();
                int deltaX = position - left;
                if (position < mTopOffset) {
                    deltaX = mTopOffset - left;
                } else if (deltaX > mBottomOffset + getRight() - getLeft() - mHandleWidth - left) {
                    deltaX = mBottomOffset + getRight() - getLeft() - mHandleWidth - left;
                }
                handle.offsetLeftAndRight(deltaX);

                final Rect frame = mFrame;
                final Rect region = mInvalidate;

                handle.getHitRect(frame);
                region.set(frame);

                region.union(frame.left - deltaX, frame.top, frame.right - deltaX, frame.bottom);
                region.union(frame.right - deltaX, 0,
                        frame.right - deltaX + mContent.getWidth(), getHeight());

                invalidate(region);
            }
        }
    }

    private void prepareContent() {
        if (mAnimating) {
            return;
        }

        // Something changed in the content, we need to honor the layout request
        // before creating the cached bitmap
        final View content = mContent;
        if (content.isLayoutRequested()) {
            if (mVertical) {
                final int childHeight = mHandleHeight;
                int height = getBottom() - getTop() - childHeight - mTopOffset;
                content.measure(MeasureSpec.makeMeasureSpec(getRight() - getLeft(), MeasureSpec.EXACTLY),
                        MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
                content.layout(0, mTopOffset, content.getMeasuredWidth(), mTopOffset + content.getMeasuredHeight());
            } else {
                final int childWidth = mHandle.getWidth();
                int width = getRight() - getLeft() - childWidth - mTopOffset;
                content.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                        MeasureSpec.makeMeasureSpec(getBottom() - getTop(), MeasureSpec.EXACTLY));
                content.layout(mTopOffset, 0, mTopOffset + content.getMeasuredWidth(), content.getMeasuredHeight());
            }
        }
        // Try only once... we should really loop but it's not a big deal
        // if the draw was cancelled, it will only be temporary anyway
        content.getViewTreeObserver().dispatchOnPreDraw();
        if (!content.isHardwareAccelerated()) content.buildDrawingCache();

        content.setVisibility(View.GONE);
    }

    private void stopTracking() {
        mHandle.setPressed(false);
        mTracking = false;

        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    private void doAnimation() {
        if (mAnimating) {
            incrementAnimation();
            if (mAnimationPosition < mTopOffset) {
                mAnimating = false;
                closeDrawer();
            } else if (mAnimationPosition >= mTopOffset + (mVertical ? getHeight() : getWidth()) - 1) {
                mAnimating = false;
                openDrawer();
            } else {
                moveHandle((int)mAnimationPosition);
                mCurrentAnimationTime += ANIMATION_FRAME_DURATION;
                mHandler.sendMessageAtTime( mHandler.obtainMessage(MSG_ANIMATE), mCurrentAnimationTime);
            }
        }
    }

    private void incrementAnimation() {
        long now = SystemClock.uptimeMillis();
        float t = (now - mAnimationLastTime) / 1000.0f;                   // ms -> s
        final float position = mAnimationPosition;
        final float v = mAnimatedVelocity;                                // px/s
        final float a = mAnimatedAcceleration;                            // px/s/s
        mAnimationPosition = position + (v * t) + (0.5f * a * t * t);     // px
        mAnimatedVelocity = v + (a * t);                                  // px/s
        mAnimationLastTime = now;                                         // ms
    }

    /**
     * Toggles the drawer open and close. Takes effect immediately.
     *
     * @see #open()
     * @see #close()
     * @see #animateClose()
     * @see #animateOpen()
     * @see #animateToggle()
     */
    public void toggle() {
        if (!mExpanded) {
            openDrawer();
        } else {
            closeDrawer();
        }
        invalidate();
        requestLayout();
    }

    /**
     * Toggles the drawer open and close with an animation.
     *
     * @see #open()
     * @see #close()
     * @see #animateClose()
     * @see #animateOpen()
     * @see #toggle()
     */
    public void animateToggle() {
        if (!mExpanded) {
            animateOpen();
        } else {
            animateClose();
        }
    }

    /**
     * Opens the drawer immediately.
     *
     * @see #toggle()
     * @see #close()
     * @see #animateOpen()
     */
    public void open() {
        openDrawer();
        invalidate();
        requestLayout();

        sendAccessibilityEvent(AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED);
    }

    /**
     * Closes the drawer immediately.
     *
     * @see #toggle()
     * @see #open()
     * @see #animateClose()
     */
    public void close() {
        closeDrawer();
        invalidate();
        requestLayout();
    }

    /**
     * Closes the drawer with an animation.
     *
     * @see #close()
     * @see #open()
     * @see #animateOpen()
     * @see #animateToggle()
     * @see #toggle()
     */
    public void animateClose() {
        prepareContent();
        final OnDrawerScrollListener scrollListener = mOnDrawerScrollListener;
        if (scrollListener != null) {
            scrollListener.onScrollStarted();
        }
        animateClose(mVertical ? mHandle.getTop() : mHandle.getLeft());

        if (scrollListener != null) {
            scrollListener.onScrollEnded();
        }
    }

    /**
     * Opens the drawer with an animation.
     *
     * @see #close()
     * @see #open()
     * @see #animateClose()
     * @see #animateToggle()
     * @see #toggle()
     */
    public void animateOpen() {
        prepareContent();
        final OnDrawerScrollListener scrollListener = mOnDrawerScrollListener;
        if (scrollListener != null) {
            scrollListener.onScrollStarted();
        }
        animateOpen(mVertical ? mHandle.getTop() : mHandle.getLeft());

        sendAccessibilityEvent(AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED);

        if (scrollListener != null) {
            scrollListener.onScrollEnded();
        }
    }

    @Override
    public CharSequence getAccessibilityClassName() {
        return VerticalSlidingDrawer.class.getName();
    }

    private void closeDrawer() {
        moveHandle(COLLAPSED_FULL_CLOSED);
        mContent.setVisibility(View.GONE);
        mContent.destroyDrawingCache();

        if (!mExpanded) {
            return;
        }

        mExpanded = false;
        if (mOnDrawerCloseListener != null) {
            mOnDrawerCloseListener.onDrawerClosed();
        }
    }

    private void openDrawer() {
        moveHandle(EXPANDED_FULL_OPEN);
        mContent.setVisibility(View.VISIBLE);

        if (mExpanded) {
            return;
        }

        mExpanded = true;

        if (mOnDrawerOpenListener != null) {
            mOnDrawerOpenListener.onDrawerOpened();
        }
    }

    /**
     * Sets the nav control view that is excluded from SocraticSlidingDrawer's intercept touch events
     */
    public void setNavControlView(View view) {
        this.mNavControlView = view;
    }

    /**
     * Sets the input control view that is excluded from SocraticSlidingDrawer's intercept touch events
     */
    public void setInputControlView(View view) { this.mInputControlView = view; }

    /**
     * Sets the listener that receives a notification when the drawer becomes open.
     *
     * @param onDrawerOpenListener The listener to be notified when the drawer is opened.
     */
    public void setOnDrawerOpenListener(OnDrawerOpenListener onDrawerOpenListener) {
        mOnDrawerOpenListener = onDrawerOpenListener;
    }

    /**
     * Sets the listener that receives a notification when the drawer becomes close.
     *
     * @param onDrawerCloseListener The listener to be notified when the drawer is closed.
     */
    public void setOnDrawerCloseListener(OnDrawerCloseListener onDrawerCloseListener) {
        mOnDrawerCloseListener = onDrawerCloseListener;
    }

    /**
     * Sets the listener that receives a notification when the drawer starts or ends
     * a scroll. A fling is considered as a scroll. A fling will also trigger a
     * drawer opened or drawer closed event.
     *
     * @param onDrawerScrollListener The listener to be notified when scrolling
     *        starts or stops.
     */
    public void setOnDrawerScrollListener(OnDrawerScrollListener onDrawerScrollListener) {
        mOnDrawerScrollListener = onDrawerScrollListener;
    }

    /**
     * Sets the listener that receives a notification when the drawer starts to expand or collapse
     */
    public void setOnDrawerMoveListener(OnDrawerMoveListener onDrawerMoveListener) {
        mOnDrawerMoveListener = onDrawerMoveListener;
    }

    /**
     * Returns the handle of the drawer.
     *
     * @return The View representing the handle of the drawer, identified by
     *         the "handle" id in XML.
     */
    public View getHandle() {
        return mHandle;
    }

    /**
     * Returns the content of the drawer.
     *
     * @return The View representing the content of the drawer, identified by
     *         the "content" id in XML.
     */
    public View getContent() {
        return mContent;
    }

    /**
     * Unlocks the SlidingDrawer so that touch events are processed.
     *
     * @see #lock()
     */
    public void unlock() {
        mLocked = false;
    }

    /**
     * Locks the SlidingDrawer so that touch events are ignored.
     *
     * @see #unlock()
     */
    public void lock() {
        mLocked = true;
    }

    /**
     * Indicates whether the drawer is currently fully opened.
     *
     * @return True if the drawer is opened, false otherwise.
     */
    public boolean isOpened() {
        return mExpanded;
    }

    /**
     * Indicates whether the drawer is scrolling or flinging.
     *
     * @return True if the drawer is scroller or flinging, false otherwise.
     */
    public boolean isMoving() {
        return mTracking || mAnimating;
    }

    private class DrawerToggler implements OnClickListener {
        public void onClick(View v) {
            if (mLocked) {
                return;
            }
            // mAllowSingleTap isn't relevant here; you're *always*
            // allowed to open/close the drawer by clicking with the
            // trackball.

            if (mAnimateOnClick) {
                animateToggle();
            } else {
                toggle();
            }
        }
    }

    private class SlidingHandler extends Handler {

        public void handleMessage(Message m)
        {
            switch ( m.what ) {
                case MSG_ANIMATE:
                    doAnimation();
                    break;
            }
        }
    }
}
