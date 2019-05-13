package org.socratic.android.activities;

import android.graphics.Point;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.appsee.Appsee;



import org.socratic.android.R;
import org.socratic.android.contract.ViewAdapter;
import org.socratic.android.analytics.AnalyticsManager;
import org.socratic.android.analytics.OnboardingAnalytics;
import org.socratic.android.databinding.ActivityStartBinding;
import org.socratic.android.viewmodel.NoOpViewModel;

import java.util.ArrayList;

import javax.inject.Inject;

public class StartActivity extends BaseActivity<ActivityStartBinding, NoOpViewModel> implements ViewAdapter {

    @Inject
    AnalyticsManager analyticsManager;

    private static final int[] rainmojis = new int[]{
            R.drawable.banana_rain,
            R.drawable.broccoli_rain,
            R.drawable.carrot_rain,
            R.drawable.cherry_rain,
            R.drawable.eyes_rain,
            R.drawable.heart_rain,
            R.drawable.hundred_rain,
            R.drawable.message_rain,
            R.drawable.okhand_rain,
            R.drawable.pencil_rain
    };

    private static final String TAG = StartActivity.class.getSimpleName();
    public static int APP_REQUEST_CODE = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityComponent().inject(this);
        setAndBindContentView(savedInstanceState, R.layout.activity_start);

        binding.btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                analyticsManager.track(
//                        new OnboardingAnalytics.SignUpScreenButtonTapped());
//
//                Appsee.addEvent("signupScreenButtonTapped");
//
//                loginBySMS();
                Intent intent = new Intent(getApplicationContext(), IdentityActivity.class);
            startActivity(intent);
            }
        });

        ViewGroup container = (ViewGroup) findViewById(R.id.emoji_container);
        View contentView = findViewById(R.id.content_container);

        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        // want everything to be consistently in pixels, not dp
        int screenHeight = Math.round(outMetrics.heightPixels);
        int screenWidth = Math.round(outMetrics.widthPixels);

        float density = getResources().getDisplayMetrics().density;
        int spaceBetween = 150 * (int) density;
        int emojiSize = 50 * (int) density;

        float rowSpeedDifference = 2f / 3f;
        float durationMultiplier = 10000f;

        // set up the positions
        ArrayList<Point> positions = new ArrayList<Point>();
        for (int j = 0; j <= screenHeight / spaceBetween; j++) {
            for (int i = 0; i <= screenWidth / spaceBetween; i++) {
                int x = i * spaceBetween;
                int y = j * spaceBetween;
                positions.add(new Point(x, y));
            }
        }

        // show and animate an emoji at each position
        for (int i = 0; i < positions.size(); i++) {

            Point p = positions.get(i);

            // create the ImageView and add it to the screen
            ImageView e = new ImageView(this);
            int emoji = rainmojis[i % rainmojis.length];

            e.setImageResource(emoji);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(emojiSize, emojiSize);
            params.leftMargin = p.x;
            params.topMargin = p.y;
            container.addView(e, params);

            // Each emoji will have three animations/movements, two of which will be visible:
            // 1. (visible) From it's start position, `p`, to the left/bottom, until it's off screen
            //      The ending position of this animation is calculated below as `firstPoint`
            // 2. (invisible) From the left/bottom off-screen to right/top off-screen
            // 3. (visible) From the right/top off-screen position back to it's starting position, `p`
            //      The starting position of this animation is calculated below as `secondPoint`
            float firstDistance, secondDistance;
            Point firstPoint, secondPoint;
            if (-p.y >= p.x - screenWidth) {
                firstDistance = (float) Math.sqrt((p.x + emojiSize) * (p.x + emojiSize) * 2);
                secondDistance = (float) Math.sqrt((p.y + emojiSize) * (p.y + emojiSize) * 2);
                firstPoint = new Point(-emojiSize, (p.x + emojiSize) + p.y);
                secondPoint = new Point(p.x + p.y + emojiSize, -emojiSize);
            } else if (-p.y >= p.x - screenHeight) {
                firstDistance = (float) Math.sqrt((p.x + emojiSize) * (p.x + emojiSize) * 2);
                secondDistance = (float) Math.sqrt((screenWidth - p.x) * (screenWidth - p.x) * 2);
                firstPoint = new Point(-emojiSize, (p.x + emojiSize) + p.y);
                secondPoint = new Point(screenWidth, screenHeight - ((screenHeight - p.y) + (screenWidth - p.x)));
            } else {
                firstDistance = (float) Math.sqrt((screenHeight - p.y) * (screenHeight - p.y) * 2);
                secondDistance = (float) Math.sqrt((screenWidth - p.x) * (screenWidth - p.x) * 2);
                firstPoint = new Point(screenWidth - ((screenHeight - p.y) + (screenWidth - p.x)), screenHeight);
                secondPoint = new Point(screenWidth, screenHeight - ((screenHeight - p.y) + (screenWidth - p.x)));
            }

            // Given the points and distances between points, we can calculate how long
            // the animation should take so that each diagonal column of emojis
            // moves at the same speed.
            float totalDistance = firstDistance + secondDistance;
            float maxDistance = (float) Math.sqrt((screenWidth * screenWidth) + (screenHeight * screenHeight));
            float duration = totalDistance / maxDistance * durationMultiplier;

            // every 2nd diagonal column moves at a slightly different speed
            if ((((p.x + p.y) / spaceBetween) % 2) == 0) {
                duration = duration * rowSpeedDifference;
            }

            // Set up the animations using an AnimationSet of TranslateAnimations
            // We create three animations and combine them into an AnimationSet
            // All animations are scheduled to start at the same time, but
            // animations 2 and 3 have startOffsets to effectively delay them.
            final AnimationSet as = new AnimationSet(true);
            as.setInterpolator(new LinearInterpolator());

            TranslateAnimation anim1 = new TranslateAnimation(
                    TranslateAnimation.ABSOLUTE, 0,
                    TranslateAnimation.ABSOLUTE, (firstPoint.x - p.x),
                    TranslateAnimation.ABSOLUTE, 0,
                    TranslateAnimation.ABSOLUTE, (firstPoint.y - p.y)
            );
            TranslateAnimation anim2 = new TranslateAnimation(
                    TranslateAnimation.ABSOLUTE, 0,
                    TranslateAnimation.ABSOLUTE, (secondPoint.x - firstPoint.x),
                    TranslateAnimation.ABSOLUTE, 0,
                    TranslateAnimation.ABSOLUTE, (secondPoint.y - firstPoint.y)
            );
            TranslateAnimation anim3 = new TranslateAnimation(
                    TranslateAnimation.ABSOLUTE, 0,
                    TranslateAnimation.ABSOLUTE, (p.x - secondPoint.x),
                    TranslateAnimation.ABSOLUTE, 0,
                    TranslateAnimation.ABSOLUTE, (p.y - secondPoint.y)
            );

            long anim1duration = (long) (duration * (firstDistance / totalDistance));
            long anim3duration = (long) (duration * (secondDistance / totalDistance));

            anim1.setDuration(anim1duration);

            anim2.setDuration(1);
            anim2.setStartOffset(anim1duration);

            anim3.setDuration(anim3duration);
            anim3.setStartOffset(anim1duration + 1);

            as.addAnimation(anim1);
            as.addAnimation(anim2);
            as.addAnimation(anim3);

            as.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    animation.reset();
                    animation.start();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });

            e.startAnimation(as);
        }

        contentView.bringToFront();
    }

    @Override
    protected void onResume() {
        super.onResume();

        analyticsManager.track(
                new OnboardingAnalytics.SignUpScreenSeen());
    }

    private void loginBySMS() {
//        final Intent intent = new Intent(this, AccountKitActivity.class);
//        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
//                new AccountKitConfiguration.AccountKitConfigurationBuilder(
//                        LoginType.PHONE,
//                        AccountKitActivity.ResponseType.TOKEN);
//
//        intent.putExtra(
//                AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
//                configurationBuilder.build());
//        startActivityForResult(intent, APP_REQUEST_CODE);
//
//        analyticsManager.track(
//                new OnboardingAnalytics.PhoneNumberScreenVisited());
//
//        Appsee.addEvent("phoneNumberScreenVisited");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == APP_REQUEST_CODE) {
////            AccountKitLoginResult loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
//
//            //handle error state
//            if (loginResult.getError() != null) {
//                OnboardingAnalytics.accountKitPhoneLoginError event
//                        = new OnboardingAnalytics.accountKitPhoneLoginError();
//                event.message = loginResult.getError().getErrorType().getMessage();
//                analyticsManager.track(event);
//            //handle cancelled state
//            } else if (loginResult.wasCancelled()) {
//                analyticsManager.track(
//                        new OnboardingAnalytics.accountKitPhoneLoginCancel());
//            } else {
//                analyticsManager.track(
//                        new OnboardingAnalytics.PhoneAuthenticationDidComplete());
//
//                Appsee.addEvent("phoneAuthenticationDidComplete");
//
//                handleLoginSuccess(loginResult);
//            }
//        }
        }

//    private void handleLoginSuccess(AccountKitLoginResult loginResult) {
//        final AccessToken accessToken = loginResult.getAccessToken();
//        if (accessToken != null) {
//            Intent intent = new Intent(this, IdentityActivity.class);
//            startActivity(intent);
//        } else {
//            //handle unknown response type
//        }
//
//        finish();
//    }
    }
}
