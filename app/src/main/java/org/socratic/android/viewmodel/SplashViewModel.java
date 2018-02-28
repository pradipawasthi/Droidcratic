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
    private InstallPref installPref;

    @Inject
    public SplashViewModel(InitManager initManager, SharedPreferences sharedPreferences, InstallPref installPref) {
        this.initManager = initManager;
        this.sharedPreferences = sharedPreferences;
        this.installPref = installPref;
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

            boolean forcedLoginIsOn = response.getForcedLogin().getForcedLoginIsOn();
            editor.putBoolean("forced_login_is_on", forcedLoginIsOn);

            String experience = response.getForcedLogin().getExperience();
            editor.putString("experience", experience);

            int unreadMessageCount = response.getUnreadMessageCount();
            editor.putInt("unread_message_count", unreadMessageCount);

            String onboardingAdvanceExperiment = response.getForcedLogin().getOnboardingAdvanceExperiment();
            editor.putString("onboarding_advance_experiment", onboardingAdvanceExperiment);

            String onboardingSMSExperiment = response.getForcedLogin().getOnboardingSMSExperiment();
            editor.putString("onboarding_sms_experiment", onboardingSMSExperiment);

            boolean isAskFriendsOn = response.getConversations().getAskFriends().getIsAskFriendsOn();
            editor.putBoolean("is_ask_friends_on", isAskFriendsOn);

            String conversationsAskFriendsSMSExperiment = response.getConversations().getAskFriends().getConversationsAskFriendsSMSExperiment();
            editor.putString("converations_ask_friends_sms_experiment", conversationsAskFriendsSMSExperiment);

            boolean isDrawerOn = response.getConversations().getDrawer().getIsDrawerOn();
            editor.putBoolean("is_drawer_on", isDrawerOn);

            String conversationsFriendsPaneSMSExperiment = response.getConversations().getDrawer().getConversationsFriendsPaneSMSExperiment();
            editor.putString("conversations_friends_pane_sms_experiment", conversationsFriendsPaneSMSExperiment);

            boolean turnOnAppSee = response.getAppSee().getTurnOnAppSee();
            editor.putBoolean("turn_on_app_see", turnOnAppSee);

            boolean forceTurnOnAppSee = response.getAppSee().getForceTurnOnAppSee();
            editor.putBoolean("force_turn_on_app_see", forceTurnOnAppSee);

            if (response.getPerson() != null) {
                int personID = response.getPerson().getID();
                editor.putInt("person_id", personID);

                if (personID == 0) {
                    Crashlytics.log("User saved with Person ID 0 in Splash");
                    Crashlytics.log("User device id:" + installPref.getUserId());
                    Crashlytics.log("User experience setting:" + experience);
                    Crashlytics.logException(new Exception("UserZeroIdException"));
                }
            }

            editor.apply();
        }

        getView().navigateToNextScreen();
    }
}
