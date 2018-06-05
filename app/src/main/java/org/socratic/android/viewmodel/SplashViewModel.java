package org.socratic.android.viewmodel;

import android.content.SharedPreferences;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

import org.socratic.android.contract.SplashContract;
import org.socratic.android.api.response.InitResponse;
import org.socratic.android.globals.InitManager;
import org.socratic.android.storage.InstallPref;

import javax.inject.Inject;

/**
 * Created by jessicaweinberg on 7/21/17.
 */

public class SplashViewModel extends BaseViewModel<SplashContract.View>
        implements SplashContract.ViewModel {

    private InitManager initManager;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Inject
    public SplashViewModel(InitManager initManager, SharedPreferences sharedPreferences) {
        this.initManager = initManager;
        this.sharedPreferences = sharedPreferences;
    }

    @Override
    public void callInit() {
        editor = this.sharedPreferences.edit();

        InitResponse response = initManager.getResponse();
        if (response != null) {

            Log.d("splash view model", String.valueOf(response));

            if (response.getInAppMessage() != null) {
                editor.putBoolean("in_app_message_presented", false);
                editor.putBoolean("in_app_message_exists_for_user", true);

                int id = response.getInAppMessage().getID();
                editor.putInt("message_id", id);

                String messageText = response.getInAppMessage().getMessageText();
                editor.putString("message_text", messageText);

                String buttonText = response.getInAppMessage().getButtonText();
                editor.putString("button_text", buttonText);

                String url = response.getInAppMessage().getURL();
                editor.putString("message_url", url);

                int secondsDelay = response.getInAppMessage().getSecondsDelay();
                editor.putInt("message_seconds_delay", secondsDelay);

                String inAppMessageType = response.getInAppMessage().getInAppMessageType();
                editor.putString("message_type", inAppMessageType);
            }

            editor.apply();
        }

        getView().navigateToNextScreen();
    }
}
