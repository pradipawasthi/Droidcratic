package org.socratic.android.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;


import org.socratic.android.BuildConfig;
import org.socratic.android.R;
import org.socratic.android.contract.SplashContract;
import org.socratic.android.databinding.ActivitySplashBinding;
import android.util.Log;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.socratic.android.analytics.AnalyticsManager;
import org.socratic.android.events.InitEvent;
import org.socratic.android.globals.InitManager;
import org.socratic.android.storage.DiskStorage;
import org.socratic.android.util.PermissionsHelper;

import java.util.Random;
import java.util.Locale;

import com.appsee.Appsee;

import javax.inject.Inject;

/**
 * @date 2017-02-17
 */
public class SplashActivity extends BaseActivity<ActivitySplashBinding, SplashContract.ViewModel>
        implements SplashContract.View {

    @Inject AnalyticsManager analyticsManager;
    @Inject DiskStorage diskStorage;
    @Inject InitManager initManager;
    @Inject SharedPreferences sharedPreferences;

    private static final int TARGET_INT = 22;
    private static final String TAG = SplashActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityComponent().inject(this);
        setAndBindContentView(savedInstanceState, R.layout.activity_splash);
        setRegisterEventBus();

        //init call
        int searchCount = diskStorage.getSearchCount();
        String searchCountStr = Integer.toString(searchCount);
        initManager.startInit(searchCountStr);

        analyticsManager.onSplashActivityCreated(this);

        startAppseeIfEnabled();

        if (!diskStorage.getLoggedFirstOpen()) {
            diskStorage.setLoggedFirstOpen();
            analyticsManager.track(
                    new AnalyticsManager.FirstAppOpenAfterAppInstall());
        } else {
            //session is defined as - if it has been more than 5 minutes since user last opened the app
            long lastAppTimeStamp = sharedPreferences.getLong("last_app_opened", System.currentTimeMillis() / 1000);
            long currentTimeTS = System.currentTimeMillis() / 1000;

            if (currentTimeTS - lastAppTimeStamp > 300) {
                diskStorage.incrementSessionCount();
                sharedPreferences.edit().putLong("last_app_opened", currentTimeTS).apply();
            } else {
                sharedPreferences.edit().putLong("last_app_opened", lastAppTimeStamp).apply();
            }

        }
    }

    private void startAppseeIfEnabled() {
        boolean appseeDisabled = sharedPreferences.getBoolean("appsee_disabled", false);
        boolean appseeOn = sharedPreferences.getBoolean("appsee_on", false);
        String country = Locale.getDefault().getCountry();

        // Record all sessions for a user if appsee has already been on for that user.
        if (appseeOn) {
            Log.d(TAG, "Appsee is enabled for this user and currently on.");
//            Appsee.start(BuildConfig.APPSEE_API_KEY);
            return;
        }

        // Return if the user is not in the US or appsee is disabled for user.
        if (!country.equals("US")) {
            Log.d(TAG, "User is not in the US.");
            return;
        }
        if (appseeDisabled) {
            Log.d(TAG, "Appsee is not enabled for this user.");
            return;
        }

        Log.d(TAG, "User is in the US and appsee is enabled.");

        // Generate a random number between 1 and 100
        Random r = new Random();
        int randomInt = r.nextInt(100) + 1;

        Log.d(TAG, "Checking to see if appsee should be turned on for user...");

        // If the random number is the target number then turn appsee on
        // so that we only turn on appsee 1 percent of the time
        if (randomInt == TARGET_INT) {

            Log.d(TAG, "Target number has been hit. Turning on and enabling appsee for user.");

//            Appsee.start(BuildConfig.APPSEE_API_KEY);
            sharedPreferences.edit().putBoolean("appsee_on", true).apply();
            sharedPreferences.edit().putBoolean("appsee_disabled", false).apply();
            return;
        }

        Log.d(TAG, "Target number has not been hit.");
        Log.d(TAG, "Turning off and disabling appsee for user.");

        // disable appsee for user if target number was not hit
        sharedPreferences.edit().putBoolean("appsee_on", false).apply();
        sharedPreferences.edit().putBoolean("appsee_disabled", true).apply();
    }

    @Override
    public void navigateToNextScreen() {
        Intent intent = null;

        //check if user needs to go through onboarding
        String experience = sharedPreferences.getString("experience", "forced");

        if (experience.equals("forced")) {
            intent = checkReturningUserState();
        } else {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            long millis = System.currentTimeMillis();
            long currentTimeTS = millis / 1000;
            editor.putLong("app_opened_timestamp", currentTimeTS).apply();
            intent = new Intent(this, CameraActivity.class);
        }

        if (!diskStorage.getLoggedFirstOpen()) {
            analyticsManager.setUserProperties(false);
        } else {
            analyticsManager.setUserSessionCount(diskStorage.getSessionCount());
        }

        startActivity(intent);
        finish();
    }

    private Intent checkReturningUserState() {
        Intent intent = null;

        //check if returning user is still in onboarding flow
        boolean atStart = sharedPreferences.getBoolean("atStart", true);
        boolean atIdentity = sharedPreferences.getBoolean("atIdentity", true);

        if (atStart) {
            intent = new Intent(this, StartActivity.class);
        } else if (atIdentity) {
            intent = new Intent(this, IdentityActivity.class);
        } else {
//            AccessToken accessToken = AccountKit.getCurrentAccessToken();
//            if (accessToken != null) {
//                //handle returning user
//                intent = checkPermissions();
//            }
        }

        return intent;
    }

    private Intent checkPermissions() {
        Intent intent = null;
        if (PermissionsHelper.allPermissionsGranted(this)) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            long millis = System.currentTimeMillis();
            long currentTimeTS = millis / 1000;
            editor.putLong("app_opened_timestamp", currentTimeTS).apply();
            intent = new Intent(this, CameraActivity.class);
        } else {
            intent = new Intent(this, SplashPermissionsActivity.class);
        }

        return intent;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(InitEvent event) {
        switch (event.getContext()) {
            case InitEvent.CONTEXT_FINISHED:

                viewModel.callInit();
        }

    }

}
