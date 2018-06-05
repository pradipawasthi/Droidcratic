package org.socratic.android.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import org.socratic.android.R;
import org.socratic.android.contract.SplashContract;
import org.socratic.android.databinding.ActivitySplashBinding;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.socratic.android.analytics.AnalyticsManager;
import org.socratic.android.events.InitEvent;
import org.socratic.android.globals.InitManager;
import org.socratic.android.storage.DiskStorage;
import org.socratic.android.util.PermissionsHelper;

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

    @Override
    public void navigateToNextScreen() {
        Intent intent = null;

        SharedPreferences.Editor editor = sharedPreferences.edit();
        long millis = System.currentTimeMillis();
        long currentTimeTS = millis / 1000;
        editor.putLong("app_opened_timestamp", currentTimeTS).apply();

        if (PermissionsHelper.hasCameraPermissions(this)) {
            intent = new Intent(this, CameraActivity.class);
        } else {
            intent = new Intent(this, DefaultPermissionActivity.class);
        }

        if (!diskStorage.getLoggedFirstOpen()) {
            analyticsManager.setUserProperties(false);
        } else {
            analyticsManager.setUserSessionCount(diskStorage.getSessionCount());
        }

        startActivity(intent);
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(InitEvent event) {
        switch (event.getContext()) {
            case InitEvent.CONTEXT_FINISHED:

                viewModel.callInit();
        }

    }

}
