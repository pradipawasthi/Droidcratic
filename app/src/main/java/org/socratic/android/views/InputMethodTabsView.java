package org.socratic.android.views;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import org.socratic.android.SocraticApp;
import org.socratic.android.databinding.ViewInputMethodTabsBinding;

/**
 * Created by williamxu on 9/01/17.
 */

public class InputMethodTabsView extends LinearLayout implements ViewPager.OnPageChangeListener {

    ViewInputMethodTabsBinding binding;

    public InputMethodTabsView(Context context) {
        this(context, null);
    }

    public InputMethodTabsView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InputMethodTabsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        SocraticApp.getViewComponent(this, context).inject(this);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        binding = ViewInputMethodTabsBinding.inflate(inflater, this, true);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
