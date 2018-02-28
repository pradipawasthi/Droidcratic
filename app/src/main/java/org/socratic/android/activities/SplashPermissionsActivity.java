package org.socratic.android.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.socratic.android.R;
import org.socratic.android.analytics.CameraPermissionAnalytics;
import org.socratic.android.analytics.ContactsPermissionAnalytics;
import org.socratic.android.analytics.OnboardingAnalytics;
import org.socratic.android.api.model.UserContact;
import org.socratic.android.databinding.ActivitySplashPermissionsBinding;
import org.socratic.android.analytics.AnalyticsManager;
import org.socratic.android.contract.SplashPermissionsContract;
import org.socratic.android.util.ContactsUtil;
import org.socratic.android.util.MultiLog;
import org.socratic.android.util.PermissionsHelper;
import org.socratic.android.util.Util;

import java.util.List;

import javax.inject.Inject;

/**
 * @date 2017-03-06
 */
public class SplashPermissionsActivity extends BaseActivity<ActivitySplashPermissionsBinding, SplashPermissionsContract.ViewModel>
    implements SplashPermissionsContract.View {

    @Inject AnalyticsManager analyticsManager;
    @Inject SharedPreferences sharedPreferences;

    boolean cameraExplainState;
    boolean contactsExplainState;

    private static final String TAG = SplashPermissionsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityComponent().inject(this);
        setAndBindContentView(savedInstanceState, R.layout.activity_splash_permissions);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("atIdentity", false);
        editor.apply();

        binding.btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestCamera();
            }
        });

        binding.btnContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestContacts();
            }
        });

        binding.btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (cameraExplainState) {
                    requestCamera();
                } else if (contactsExplainState) {
                    requestContacts();
                } else {
                    //detect which permission the user is addressing in settings
                    if (binding.bodyText.getText().toString().contains("classmates")) {
                        analyticsManager.track(
                                new ContactsPermissionAnalytics.ContactsPermissionsDeniedGoToSettingsTapped());
                    } else {
                        analyticsManager.track(
                                new CameraPermissionAnalytics.CameraPermissionsDeniedModalGoToSettingsTapped());
                    }

                    try {
                        PermissionsHelper.startSystemPermissionsIntent(SplashPermissionsActivity.this);
                    } catch (Exception ex) {
                        Log.e(TAG, "Error starting app permissions system screen.", ex);
                    }

                    sharedPreferences.edit().putBoolean("isOnExplainScreen", false).apply();
                }
            }
        });
    }

    private void requestContacts() {
        Log.e(TAG, "Contacts permission missing, requesting.");

        analyticsManager.track(
                new CameraPermissionAnalytics.CameraPermissionsNativeModalRequested());

        PermissionsHelper.requestContactsPermission(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        MultiLog.d(TAG, "Permissions screen resumed.");

        setupCurrentState();

        if (sharedPreferences.getBoolean("arrivePermissionsScreenFirst", true)) {

            analyticsManager.track(
                    new OnboardingAnalytics.PermissionsScreenViewed());

            sharedPreferences.edit().putBoolean("arrivePermissionsScreenFirst", false).apply();
        }
    }

    private void setupCurrentState() {
        boolean isOnExplainScreen = sharedPreferences.getBoolean("isOnExplainScreen", false);
        boolean lastContactsPermissionState = sharedPreferences.getBoolean("contactsPermissionGranted", false);

        if (PermissionsHelper.allPermissionsGranted(this)) {

            //track user who granted contacts permission in settings
            if (!lastContactsPermissionState) {
                analyticsManager.track(
                        new ContactsPermissionAnalytics.ContactsPermissionsChanged());
            }

            gotoNextActivity();
        } else if (PermissionsHelper.hasCameraPermissions(this)) {

            if (!isOnExplainScreen) {
                setCameraGranted();
            } else {
                binding.permissionScreen.setVisibility(View.GONE);
            }
        } else if (PermissionsHelper.hasContactsPermissions(this)) {

            //track user who granted contacts permission in settings
            if (!lastContactsPermissionState) {
                analyticsManager.track(
                        new ContactsPermissionAnalytics.ContactsPermissionsChanged());
            }

            if (!isOnExplainScreen) {
                setContactsGranted();
            } else {
                binding.permissionScreen.setVisibility(View.GONE);
            }
        }

        if (isOnExplainScreen) {
            binding.permissionScreen.setVisibility(View.GONE);
        }

        if (binding.permissionScreen.getVisibility() == View.GONE) {
            binding.btnSettings.setEnabled(true);
        } else {
            binding.btnSettings.setEnabled(false);
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

            if (requestCode == PermissionsHelper.PERMISSION_REQUEST_CAMERA) {

                Log.e(TAG, "  Camera permission was granted.");

                analyticsManager.track(
                        new CameraPermissionAnalytics.CameraPermissionsNativeModalPermissionGranted());

                setCameraGranted();
            } else if (requestCode == PermissionsHelper.PERMISSION_REQUEST_READ_CONTACTS) {

                Log.e(TAG, "  Contacts permission was granted.");

                boolean lastContactsPermissionState = sharedPreferences.getBoolean("contactsPermissionGranted", false);

                if (!lastContactsPermissionState) {
                    analyticsManager.track(
                            new ContactsPermissionAnalytics.ContactsPermissionsChanged());
                }

                sharedPreferences.edit().putBoolean("contactsPermissionGranted", true).apply();

                analyticsManager.track(
                        new ContactsPermissionAnalytics.ContactsPermissionsNativeModalPermissionGranted());

                setContactsGranted();

                uploadContacts();
            }

            if (PermissionsHelper.allPermissionsGranted(this)) {

                gotoNextActivity();
            }
        } else {

            if (requestCode == PermissionsHelper.PERMISSION_REQUEST_CAMERA) {

                Log.e(TAG, "  Camera permission was denied.");

                analyticsManager.track(
                        new CameraPermissionAnalytics.CameraPermissionsNativeModalPermissionDenied());

                cameraExplainState = PermissionsHelper.needsCameraPermissionExplanation(this);

                if (cameraExplainState) {
                    binding.settingsBtnText.setText(getString(R.string.enable_camera));
                } else {
                    binding.settingsBtnText.setText(getString(R.string.go_to_settings));
                }
            } else if (requestCode == PermissionsHelper.PERMISSION_REQUEST_READ_CONTACTS) {
                Log.e(TAG, "  Contacts permission was denied.");
                sharedPreferences.edit().putBoolean("contactsPermissionGranted", false).apply();

                analyticsManager.track(
                        new ContactsPermissionAnalytics.ContactsPermissionsNativeModalPermissionDenied());

                binding.bodyText.setText(getText(R.string.contacts_permission_denied));

                contactsExplainState = PermissionsHelper.needsContactsPermissionExplanation(this);

                if (contactsExplainState) {
                    binding.settingsBtnText.setText(getString(R.string.enable_contacts));
                } else {
                    binding.settingsBtnText.setText(getString(R.string.go_to_settings));
                }
            }

            binding.btnSettings.setEnabled(true);
            binding.permissionScreen.setVisibility(View.GONE);
            sharedPreferences.edit().putBoolean("isOnExplainScreen", true).apply();
        }
    }

    private void uploadContacts() {
        List<UserContact> contacts = ContactsUtil.fetchContacts(this);

        sharedPreferences.edit().putInt("numContacts", contacts.size()).apply();

        JSONArray contactsJSON = new JSONArray();
        for (UserContact contact : contacts) {
            contactsJSON.put(contact.toJSON());
        }

        String deviceName = Util.getUserDeviceName(this);

        if (deviceName == null) {
            deviceName = "";
        }

        viewModel.sendContacts(contactsJSON, deviceName);

        sharedPreferences.edit().putBoolean("contacts_loaded_onboarding", true).apply();
    }

    private void setContactsGranted() {
        binding.permissionScreen.setVisibility(View.VISIBLE);
        binding.btnContacts.setBackground(ContextCompat.getDrawable(this, R.drawable.blue_btn_disabled));
        binding.contactsCheck.setVisibility(View.VISIBLE);
        contactsExplainState = false;
    }

    private void setCameraGranted() {
        binding.permissionScreen.setVisibility(View.VISIBLE);
        binding.btnCamera.setBackground(ContextCompat.getDrawable(this, R.drawable.blue_btn_disabled));
        binding.cameraCheck.setVisibility(View.VISIBLE);
        cameraExplainState = false;
    }

    private void requestCamera() {

        Log.e(TAG, "Camera permission missing, requesting.");

        analyticsManager.track(
                    new CameraPermissionAnalytics.CameraPermissionsNativeModalRequested());

        PermissionsHelper.requestCameraPermission(this);
    }

    private void gotoNextActivity() {
        analyticsManager.track(
                new OnboardingAnalytics.PermissionsComplete());

        SharedPreferences.Editor editor = sharedPreferences.edit();
        long millis = System.currentTimeMillis();
        long currentTimeTS = millis / 1000;
        editor.putLong("app_opened_timestamp", currentTimeTS).apply();

        analyticsManager.setUserProperties(true);

        Intent intent = new Intent(this, CameraActivity.class);
        startActivity(intent);
        finish();
    }
}
