package org.socratic.android.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.socratic.android.R;
import org.socratic.android.contract.DefaultPermissionContract;
import org.socratic.android.analytics.AnalyticsManager;
import org.socratic.android.analytics.CameraPermissionAnalytics;
import org.socratic.android.databinding.ActivityDefaultPermissionBinding;
import org.socratic.android.util.MultiLog;
import org.socratic.android.util.PermissionsHelper;

import javax.inject.Inject;

public class DefaultPermissionActivity extends BaseActivity<ActivityDefaultPermissionBinding, DefaultPermissionContract.ViewModel>
    implements DefaultPermissionContract.View {

    @Inject AnalyticsManager analyticsManager;
    @Inject SharedPreferences sharedPreferences;

    private static final String TAG = DefaultPermissionActivity.class.getSimpleName();

    boolean mPermissionStateTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityComponent().inject(this);
        setAndBindContentView(savedInstanceState, R.layout.activity_default_permission);

        binding.btnEnable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPermissions();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        MultiLog.d(TAG, "Permissions screen resumed.");

        if (PermissionsHelper.hasCameraPermissions(this)) {
            gotoNextActivity();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {

        Log.e(TAG, "onRequestPermissionsResult");

        if (PermissionsHelper.didGrantPermission(requestCode, grantResults)) {

            Log.e(TAG, "  Camera permission was granted.");

            analyticsManager.track(
                    new CameraPermissionAnalytics.CameraPermissionsNativeModalPermissionGranted());

            gotoNextActivity();

        } else {

            Log.e(TAG, "  Camera permission was denied.");

            analyticsManager.track(
                    new CameraPermissionAnalytics.CameraPermissionsNativeModalPermissionDenied());

            boolean permissionStateTestPost = PermissionsHelper.needsCameraPermissionExplanation(this);

            Log.e(TAG, "  needsCameraPermissionExplanation: " + permissionStateTestPost);

            if (mPermissionStateTest == false && !permissionStateTestPost) {

                analyticsManager.track(
                        new CameraPermissionAnalytics.CameraPermissionsDeniedModalGoToSettingsTapped());

                try {
                    PermissionsHelper.startSystemPermissionsIntent(this);
                } catch (Exception ex) {
                    Log.e(TAG, "Error starting app permissions system screen.", ex);
                }
            }
        }
    }

    private void requestPermissions() {

        Log.e(TAG, "Camera permission missing, requesting.");

        mPermissionStateTest = PermissionsHelper.needsCameraPermissionExplanation(this);

        Log.e(TAG, "  needsCameraPermissionExplanation: " + mPermissionStateTest);

        analyticsManager.track(
                new CameraPermissionAnalytics.CameraPermissionsNativeModalRequested());

        PermissionsHelper.requestCameraPermission(this);
    }

    private void gotoNextActivity() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        long millis = System.currentTimeMillis();
        long currentTimeTS = millis / 1000;
        editor.putLong("app_opened_timestamp", currentTimeTS).apply();
        Intent intent = new Intent(this, CameraActivity.class);
        startActivity(intent);
        finish();
    }
}
