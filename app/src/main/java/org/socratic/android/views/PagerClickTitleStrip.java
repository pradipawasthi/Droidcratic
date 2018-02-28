package org.socratic.android.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewParent;
import android.widget.TextView;

/**
 * Created by williamxu on 9/05/17.
 */

public class PagerClickTitleStrip extends PagerTitleStrip {

    private TextView textPrev, textNext, textCurrent;
    private ViewPager pager;

    public PagerClickTitleStrip(Context arg0, AttributeSet arg1) {
        super(arg0, arg1);

        textPrev = (TextView) getChildAt(0);
        textCurrent = (TextView) getChildAt(1);
        textNext = (TextView) getChildAt(2);

        textNext.setTextColor(Color.WHITE);
        textPrev.setTextColor(Color.WHITE);
        textCurrent.setTextColor(Color.WHITE);

        Typeface ceraproFont = Typeface.createFromAsset(getContext().getAssets(), "fonts/cerapro-regular.otf");
        textCurrent.setTypeface(ceraproFont);
        textPrev.setTypeface(ceraproFont);
        textNext.setTypeface(ceraproFont);

        textPrev.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (pager != null && pager.getCurrentItem() != 0)
                    pager.setCurrentItem(pager.getCurrentItem() - 1);
            }
        });
        textNext.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (pager != null
                        && pager.getCurrentItem() != pager
                        .getAdapter().getCount() - 1)
                    pager.setCurrentItem(pager.getCurrentItem() + 1);
            }

        });

        invalidate();
    }

    public TextView getCurrentTitle() {
        return textCurrent;
    }

    public TextView getPreviousTitle() {
        return textPrev;
    }

    public TextView getNextTitle() {
        return textNext;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ViewParent parent = getParent();
        if (!(parent instanceof ViewPager)) {
            throw new IllegalStateException("PagerTitleStrip must be a direct child of a ViewPager.");
        }
        pager =(ViewPager)parent;
    }
}
