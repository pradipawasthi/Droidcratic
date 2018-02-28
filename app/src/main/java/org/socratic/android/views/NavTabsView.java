package org.socratic.android.views;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import org.socratic.android.SocraticApp;
import org.socratic.android.databinding.ViewNavTabsBinding;

/**
 * Created by williamxu on 8/4/17.
 */

public class NavTabsView extends RelativeLayout implements ViewPager.OnPageChangeListener {

    ViewNavTabsBinding binding;

    private static final String TAG = NavTabsView.class.getSimpleName();

    private int centerColor;
    private int sideColor;
    private ArgbEvaluator colorEvaluator;

    private float centerButtonTranslationY;
    private float sideButtonsTranslation;

    public NavTabsView(Context context) {
        this(context, null);
    }

    public NavTabsView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NavTabsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        SocraticApp.getViewComponent(this, context).inject(this);
        init(context);
    }

    private void init(Context context) {

        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        binding = ViewNavTabsBinding.inflate(inflater, this, true);

        centerColor = ContextCompat.getColor(getContext(), android.R.color.white);
        sideColor = ContextCompat.getColor(getContext(), android.R.color.darker_gray);

        colorEvaluator = new ArgbEvaluator();

        binding.circleBtn.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                centerButtonTranslationY = (getHeight() / 2) - (getHeight() / 16);
                sideButtonsTranslation = getHeight() / 8;

                binding.circleBtn.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        //make sure only touches on the nav icons are consumed

        float x = ev.getX();
        float y = ev.getY();

        final Rect circleFrame = new Rect();
        final Rect chatFrame = new Rect();
        final Rect groupFrame = new Rect();

        binding.circleBtn.getHitRect(circleFrame);
        binding.chatBtn.getHitRect(chatFrame);
        binding.groupsBtn.getHitRect(groupFrame);

        return !(circleFrame.contains((int) x, (int) y)
                || chatFrame.contains((int) x, (int) y)
                || groupFrame.contains((int) x, (int) y)) && super.onInterceptTouchEvent(ev);

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (position == 0) {
            setUpViews(1 - positionOffset,
                        .7f + (positionOffset * .3f),
                        (1 - positionOffset) * centerButtonTranslationY);
        }
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    private void setUpViews(float fractionFromCenter, float centerScale, float centerTransY) {
        binding.chatBtn.setTranslationX(-sideButtonsTranslation * fractionFromCenter);
        binding.chatBtn.setTranslationY(centerTransY);

        binding.circleBtn.setScaleX(centerScale);
        binding.circleBtn.setScaleY(centerScale);
        binding.circleBtn.setTranslationY(centerTransY);

        int color = (int) colorEvaluator.evaluate(fractionFromCenter, centerColor, sideColor);
        binding.circleBtn.setColorFilter(color);
        binding.chatBtn.setColorFilter(color);
    }

    public void setViewPager(ViewPager viewPager) {
        if (viewPager != null) {
            viewPager.addOnPageChangeListener(this);
        }
    }

    public void hideSocialButtons() {
        binding.chatBtn.setVisibility(GONE);
        binding.groupsBtn.setVisibility(GONE);
    }
}
