package org.socratic.android.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.socratic.android.BuildConfig;
import org.socratic.android.R;
import org.socratic.android.contract.ResultsContract;
import org.socratic.android.analytics.AnalyticsManager;
import org.socratic.android.api.model.CardResponse;
import org.socratic.android.api.response.OcrTextResponse;
import org.socratic.android.api.response.SearchResponse;
import org.socratic.android.databinding.ActivityResultsBinding;
import org.socratic.android.fragments.MathCardFragment;
import org.socratic.android.fragments.NativeCardQAFragment;
import org.socratic.android.fragments.NativeDefintionCardFragment;
import org.socratic.android.fragments.NativeCardVideoFragment;
import org.socratic.android.fragments.WebCardFragment;
import org.socratic.android.globals.BaseSearchManager;
import org.socratic.android.globals.OcrSearchManager;
import org.socratic.android.globals.TextSearchManager;
import org.socratic.android.util.DimenUtil;
import org.socratic.android.util.MultiLog;
import org.socratic.android.views.JLatexView;

import javax.inject.Inject;

import static org.socratic.android.views.CropWidgetView.QUERIES_BEFORE_SUBJECT_TIP;

/**
 * @date 2017-02-02
 */
public class ResultsActivity extends BaseActivity<ActivityResultsBinding, ResultsContract.ViewModel> implements ResultsContract.View {

    @Inject
    OcrSearchManager ocrSearchManager;
    @Inject
    TextSearchManager textSearchManager;
    @Inject
    SharedPreferences sharedPreferences;
    @Inject
    AnalyticsManager analyticsManager;

    private boolean firstCardSwipedAway;

    private static final String TAG = ResultsActivity.class.getSimpleName();
    private static final String EXTRA_IS_OCR = "@eio";

    ViewPager viewPager;

    JLatexView latexImageView;

    TextView textViewQuestion;

    LinearLayout viewDots;

    CollectionPagerAdapter mViewPagerAdapter;
    boolean isOCR;
    BaseSearchManager searchManager;

    private static final String CARD_TYPE_MATH_STEP = "math-steps";
    private static final String CARD_TYPE_EXPLAINER = "explainer";
    private static final String CARD_TYPE_DEFINITIONS = "definitions";
    private static final String CARD_TYPE_VIDEO = "video";

    boolean viewPagerScrolled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityComponent().inject(this);
        setAndBindContentView(savedInstanceState, R.layout.activity_results);

        MultiLog.d(TAG, "Results screen created.");

        viewPager = binding.viewPager;
        latexImageView = binding.ivQuestionLatex;
        textViewQuestion = binding.tvQuestion;
        viewDots = binding.viewPagerCountDots;

        isOCR = getIntent().getExtras().getBoolean(EXTRA_IS_OCR, true);
        if (isOCR) {

            if (ocrSearchManager.getResponse() == null) {
                exitWithNoResults();
            }
            searchManager = ocrSearchManager;


        } else {
            if (textSearchManager.getResponse() == null) {
                exitWithNoResults();
            }

            searchManager = textSearchManager;
        }

        viewPager.setPageMargin((int) getResources().getDimension(R.dimen.results_page_margin));

        mViewPagerAdapter = new CollectionPagerAdapter(
                getSupportFragmentManager());

        viewPager.setAdapter(mViewPagerAdapter);

        if (BuildConfig.DEBUG && isOCR) {

            OcrTextResponse response = (OcrTextResponse) ocrSearchManager.getResponse();
            if (response != null &&
                    response.getText() != null) {
                MultiLog.d(TAG, response.getText());
            }
        }

        // setup dots
        drawPageSelectionIndicators(0);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (!viewPagerScrolled) {
                    // Only load one off screen webview at first, in order to speed up
                    // webview loading time.
                    viewPager.setOffscreenPageLimit(1);
                    viewPagerScrolled = true;
                } else {
                    // After user starts scrolling, as a speed up ensure that pages on either
                    // side are retained, this keeps them from being recreated on revisit.
                    viewPager.setOffscreenPageLimit(7);
                }
            }

            @Override
            public void onPageSelected(int position) {
                drawPageSelectionIndicators(position);

                // If position is greater than 0
                // We know that the user has swiped passed the math card, notify MathCardFragment to
                // stop its timer
                if (!firstCardSwipedAway) {
                    if (position == 1) {
                        MathCardFragment.stopTimer(analyticsManager);
                        firstCardSwipedAway = true;
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        binding.btnStartover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoCameraActivity();
            }
        });

        setQuestionText();
        updateSuccessfulQueryCount();
        updateSubjectOnboardingTip();

        binding.inAppMessageView.setup(TAG);
    }

    //increment successful query count
    private void updateSuccessfulQueryCount() {
        int successfulQueries = sharedPreferences.getInt("successfulQueries", 0);
        successfulQueries++;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("successfulQueries", successfulQueries);
        editor.apply();
    }

    //update state of subjects onboarding tip from camera screen
    private void updateSubjectOnboardingTip() {
        int successfulQueries = sharedPreferences.getInt("successfulQueries", 0);
        boolean subjectOnboardShown = sharedPreferences.getBoolean("subjectOnboardShown", false);

        if (successfulQueries > QUERIES_BEFORE_SUBJECT_TIP && !subjectOnboardShown) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("subjectOnboardShown", true);
            editor.apply();
        }
    }

    private void exitWithNoResults() {
        MultiLog.d(TAG, "Search results screen finds no results, exiting.");
        finish();
        return;
    }

    @Override
    public void onResume() {
        super.onResume();

        MultiLog.d(TAG, "Results screen resumed.");
    }

    @Override
    public void onPause() {
        super.onPause();

        MultiLog.d(TAG, "Results screen paused.");
    }

    private void setQuestionText() {
        SearchResponse response = searchManager.getResponse();

        if (response == null
                || TextUtils.isEmpty(response.getQuestionText())) {
            return;
        }

        if (response.getQuestionQueryType().equalsIgnoreCase(OcrTextResponse.QUERY_TYPE_LATEX)) {
            textViewQuestion.setVisibility(View.GONE);
            latexImageView.setVisibility(View.VISIBLE);
            latexImageView.setTextColor(Color.WHITE);
            latexImageView.setTextSize((int) DimenUtil.convertSpToPx(20, ResultsActivity.this));
            latexImageView.setLatex(response.getQuestionText(), false);
        } else {
            textViewQuestion.setVisibility(View.VISIBLE);
            latexImageView.setVisibility(View.GONE);
            textViewQuestion.setText(response.getQuestionText());
        }
    }

    private void drawPageSelectionIndicators(int mPosition) {

        // Set up dots if viewDots is empty
        if (viewDots.getChildCount() == 0) {
            for (int i = 0; i < mViewPagerAdapter.getCount(); i++) {
                ImageView dot = new ImageView(getApplicationContext());

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

                int margin = (int) getResources().getDimension(R.dimen.padding_5dp);
                params.setMargins(margin, 0, margin, 0);
                viewDots.addView(dot, params);
            }
        }

        // Update dot style
        for (int i = 0; i < viewDots.getChildCount(); i++) {
            ImageView dot = (ImageView) viewDots.getChildAt(i);

            Drawable dotDrawable = getResources().getDrawable(i == mPosition ? R.drawable.item_selected : R.drawable.item_unselected);
            dot.setImageDrawable(dotDrawable);
        }
    }

    @Override
    public void onBackPressed() {
        // Always jump to camera activity.
        gotoCameraActivity();
    }

    private void gotoCameraActivity() {
        Intent intent = new Intent(this, CameraActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private class CollectionPagerAdapter extends FragmentStatePagerAdapter {
        public CollectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int index) {
            Fragment fragment = null;

            CardResponse card
                    = searchManager.getResponseCard(index);

            if (NativeCardQAFragment.isValidCardData(card)) {
                fragment = NativeCardQAFragment.newInstance(index, isOCR);
            } else if (card.getType().equals(CARD_TYPE_MATH_STEP)) {
                fragment = MathCardFragment.newInstance(searchManager.getResponse().getQuestionText());
            } else if(card.getType().equals(CARD_TYPE_DEFINITIONS)){
                fragment = NativeDefintionCardFragment.newInstance(index, isOCR);
            } else if (card.getType().equals(CARD_TYPE_VIDEO)) {
                fragment = NativeCardVideoFragment.newInstance(index, isOCR);
            } else {
                fragment = WebCardFragment.newInstance(index, isOCR);
            }

            return fragment;
        }

        @Override
        public float getPageWidth(int position) {
            // Changing the viewport width of the page as a % of the screen width
            return 1.0f;
        }

        @Override
        public int getCount() {

            SearchResponse response = searchManager.getResponse();

            if (response == null || response.getCardResults() == null) {
                return 0;
            }

            return response.getCardResults().size();


        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Result " + (position + 1);
        }
    }
}