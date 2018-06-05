package org.socratic.android.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import org.socratic.android.BuildConfig;
import org.socratic.android.R;
import org.socratic.android.SocraticApp;
import org.socratic.android.analytics.AppReviewAnalytics;
import org.socratic.android.api.request.RatingPostRequest;
import org.socratic.android.api.response.BaseResponse;
import org.socratic.android.databinding.ViewSocraticRatingBinding;
import org.socratic.android.analytics.AnalyticsManager;
import org.socratic.android.globals.HttpManager;
import org.socratic.android.storage.DiskStorage;
import org.socratic.android.util.AppReviewSwipeListener;
import org.socratic.android.util.Util;

import javax.inject.Inject;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by pcnofelt on 4/17/17.
 * Manages the logic and flow of different views to ask the user for
 * rating, feedback and App Store review.
 * <p>
 * First, user is shown the 5 star rating view. If they click 4 stars
 * and higher, they are shown the "Rate us on app store" view. If they
 * click 3 or less stars, they are shown the feedback view.
 * <p>
 * User is only shown the stars banner if they haven't already
 * clicked a star rating.
 * <p>
 * In the case if user keeps dismissing the stars view, user is shown
 * the banner a maximum of 3 times, each after increasing amounts
 * of results seen.
 **/
public class SocraticRatingView extends LinearLayout {

    @Inject DiskStorage diskStorage;
    @Inject AnalyticsManager analyticsManager;
    @Inject HttpManager httpManager;

    private static final String TAG = SocraticRatingView.class.getSimpleName();
    private static final long POPUP_DELAY_MS = 7000;
    private static final boolean ALWAYS_SHOW_DEBUG_MODE = BuildConfig.DEBUG && true;

    private final int STAR_RATING_FOUR = 4;

    private final int SHOW_COUNT_FIRST_TIME = BuildConfig.DEBUG ? 1 : 10;
    private final int SHOW_COUNT_SECOND_TIME = BuildConfig.DEBUG ? 3 : 30;
    private final int SHOW_COUNT_THIRD_TIME = BuildConfig.DEBUG ? 5 : 100;

    private int[] SHOW_ATTEMPTS_INDEX = new int[]{SHOW_COUNT_FIRST_TIME, SHOW_COUNT_SECOND_TIME, SHOW_COUNT_THIRD_TIME};

    Handler mHandler;
    int mUserProvidedRating = -1;

    TextView tvRatingStarMessage;
    KeyboardEditText etRatingFeedback;
    ViewGroup viewRatingParent;

    ViewSocraticRatingBinding binding;

    public SocraticRatingView(Context context) {
        super(context);
        SocraticApp.getViewComponent(this, context).inject(this);
        init(context);
    }

    public SocraticRatingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        SocraticApp.getViewComponent(this, context).inject(this);
        init(context);
    }

    public SocraticRatingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        SocraticApp.getViewComponent(this, context).inject(this);
        init(context);
    }

    private void init(Context context) {

        Log.d(TAG, "Init.");

        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        binding = ViewSocraticRatingBinding.inflate(inflater, this, true);
        mHandler = new Handler();

        tvRatingStarMessage = binding.tvRatingStar;
        etRatingFeedback = binding.etRatingFeedback;
        viewRatingParent = binding.vgRatingParent;

        setVisibility(GONE);

        if (ALWAYS_SHOW_DEBUG_MODE || !diskStorage.hasCompletedRatingRequest()) {

            Log.d(TAG, "User has not completed rating request.");

            boolean isOkToShow = false;
            if (ALWAYS_SHOW_DEBUG_MODE) {
                // Always ok to show for debug builds.
                Log.d(TAG, "In debug mode, ok to show.");
                isOkToShow = true;
            } else if (Util.isNowPastMayThird()) {
                Log.d(TAG, "In release mode, and after May 3, 2017, ok to show.");
                isOkToShow = true;
            } else {
                Log.d(TAG, "It is not ok to show.");
            }

            if (isOkToShow) {
                mHandler.postDelayed(mRunnableDelayShow, POPUP_DELAY_MS);
            }

        } else {
            Log.d(TAG, "User has completed rating request, will not show again.");
        }

        binding.btnRatingNoThanks.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                tvRatingStarMessage.setText(SocraticRatingView.this.getContext().getString(R.string.thanks));
                animateHideEntireView();
            }
        });

        binding.btnRatingSure.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                animateHideEntireView();

                String appPackageName = SocraticRatingView.this.getContext().getPackageName();
                if (!TextUtils.isEmpty(appPackageName)) {
                    if (appPackageName.endsWith(".alpha") || appPackageName.endsWith(".beta") || appPackageName.endsWith(".gamma")) {
                        appPackageName = appPackageName.substring(0, appPackageName.lastIndexOf('.'));
                    }
                }

                Log.d(TAG, "Using package name '" + appPackageName + "' for store intent.");

                try {
                    SocraticRatingView.this.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException ex) {
                    Log.e(TAG, "Error starting store intent.", ex);
                    SocraticRatingView.this.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }

                analyticsManager.track(new AppReviewAnalytics.AppReviewAppStoreTapped());
            }
        });


        etRatingFeedback.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    final InputMethodManager inputMethodManager = (InputMethodManager)
                            getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    animateHideEntireView(0);

                    AppReviewAnalytics.AppReviewFeedbackSent feedback = new AppReviewAnalytics.AppReviewFeedbackSent();
                    feedback.starRating = mUserProvidedRating;
                    feedback.feedback = v.getText().toString();

                    analyticsManager.track(feedback);
                }
                return true;
            }
        });

        // add event if user backs out of feedback
        etRatingFeedback.setOnKeyboardListener(new KeyboardEditText.KeyboardListener() {
            @Override
            public void onStateChanged(KeyboardEditText keyboardEditText, boolean showing) {
                if (showing == false) {
                    animateHideEntireView(0);
                }
            }
        });

        etRatingFeedback.setImeOptions(EditorInfo.IME_ACTION_SEND);
        etRatingFeedback.setRawInputType(InputType.TYPE_CLASS_TEXT);

        setOnTouchListener(
                new AppReviewSwipeListener(
                        this,
                        null,
                        new AppReviewSwipeListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(Object token) {
                                return true;
                            }

                            @Override
                            public void onDismiss(View view, Object token) {
                                SocraticRatingView.this.setVisibility(View.GONE);
                            }
                        }
                ));
    }

    private void showRatingIfNeeded() {

        // if activity is getting destroyed just skip show attempt
        Activity parentActivity = (Activity) getContext();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (parentActivity.isFinishing() || parentActivity.isDestroyed()) {
                return;
            }
        } else if (parentActivity.isFinishing()) {
            return;
        }

        int showAttempts = diskStorage.getRatingRequestShowAttempts();

        boolean okToShow = false;
        if (ALWAYS_SHOW_DEBUG_MODE) {
            okToShow = true;
        } else if (showAttempts < SHOW_ATTEMPTS_INDEX.length
                && diskStorage.getSearchCount() >= SHOW_ATTEMPTS_INDEX[showAttempts])
        {
            okToShow = true;
        }

        if (okToShow) {

            // Going to show rating bar, so increment attempts
            diskStorage.incrementRatingRequestShowAttempts();

            animateShowEntireView();

            binding.ratingbarReview.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    diskStorage.setCompletedRatingRequest();

                    evaluateRating(Math.round(rating));

                    AppReviewAnalytics.AppReviewStarsTapped event = new AppReviewAnalytics.AppReviewStarsTapped();
                    event.starRating = mUserProvidedRating;
                    analyticsManager.track(event);
                }
            });

            // report as shown
            analyticsManager.track(new AppReviewAnalytics.AppReviewStarsShown());
        }
    }

    private void evaluateRating(int rating) {
        mUserProvidedRating = rating;

        animateShowPostRating();
        sendRating();
    }

    private void sendRating() {
        RatingPostRequest request = new RatingPostRequest(mUserProvidedRating);
        httpManager.request(request,
                new HttpManager.HttpCallback<BaseResponse>(BaseResponse.class) {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onFailure(Request request, BaseResponse responseParsed, int statusCode, Exception e) {
                    }

                    @Override
                    public void onCancel(Request request) {

                    }

                    @Override
                    public void onSuccess(Response response, BaseResponse responseParsed) {
                    }

                    @Override
                    public void onFinished() {
                    }
                });
    }

    private void animateShowPostRating() {
        // 1. Fade in votes
        Animation slideDownAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_down);
        slideDownAnimation.setInterpolator(AnimationUtils.loadInterpolator(getContext(), android.R.anim.accelerate_interpolator));

        final Animation slideUpAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_up);
        slideUpAnimation.setInterpolator(AnimationUtils.loadInterpolator(getContext(), android.R.anim.decelerate_interpolator));

        slideDownAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                binding.vgRatingStars.setVisibility(GONE);

                if (mUserProvidedRating < STAR_RATING_FOUR) {
                    analyticsManager.track(new AppReviewAnalytics.AppReviewAppStoreShown());
                    binding.vgProvideFeedback.setVisibility(VISIBLE);
                    requestFocus(etRatingFeedback);
                } else {
                    analyticsManager.track(new AppReviewAnalytics.AppReviewFeedbackShown());
                    binding.vgRatingButton.setVisibility(VISIBLE);
                }
                SocraticRatingView.this.startAnimation(slideUpAnimation);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        tvRatingStarMessage.setText(SocraticRatingView.this.getContext().getString(R.string.thanks));
        slideDownAnimation.setStartOffset(450);
        startAnimation(slideDownAnimation);
    }

    private void animateShowEntireView() {
        // set up rating to show delayed. On show increment show count.
        setTranslationY(0f);
        setVisibility(VISIBLE);
        Animation slideUpAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_up);
        slideUpAnimation.setInterpolator(AnimationUtils.loadInterpolator(getContext(), android.R.anim.decelerate_interpolator));
        startAnimation(slideUpAnimation);
    }

    private void animateHideEntireView(int startOffset) {

        Animation slideDownAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_down);
        slideDownAnimation.setInterpolator(AnimationUtils.loadInterpolator(getContext(), android.R.anim.accelerate_interpolator));
        slideDownAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                SocraticRatingView.this.setVisibility(GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        binding.tvRatingReviewRequest.setText(SocraticRatingView.this.getContext().getString(R.string.thanks));
        slideDownAnimation.setStartOffset(startOffset);
        SocraticRatingView.this.startAnimation(slideDownAnimation);
    }

    private void animateHideEntireView() {
        animateHideEntireView(450);
    }

    private void requestFocus(View view) {
        view.setFocusableInTouchMode(true);
        view.requestFocus();

        final InputMethodManager inputMethodManager = (InputMethodManager)
                getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }

    Runnable mRunnableDelayShow = new Runnable() {
        @Override
        public void run() {
            showRatingIfNeeded();
        }
    };

}

