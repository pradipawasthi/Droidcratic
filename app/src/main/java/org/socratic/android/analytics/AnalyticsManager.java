package org.socratic.android.analytics;

import android.content.SharedPreferences;
import android.util.Log;

import com.amplitude.api.Amplitude;
import com.amplitude.api.Identify;

import org.json.JSONException;
import org.json.JSONObject;
import org.socratic.android.BuildConfig;
import org.socratic.android.R;
import org.socratic.android.activities.SplashActivity;
import org.socratic.android.storage.DiskStorage;
import org.socratic.android.storage.InstallPref;
import org.socratic.android.util.Util;

import java.lang.reflect.Field;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

/**
 * @date 2017-03-20
 */
public class AnalyticsManager {

    private InstallPref installPref;
    private DiskStorage diskStorage;
    private SharedPreferences sharedPreferences;

    private static final String TAG = AnalyticsManager.class.getSimpleName();

    @Inject
    public AnalyticsManager(InstallPref installPref, DiskStorage diskStorage, SharedPreferences sharedPreferences) {
        this.installPref = installPref;
        this.diskStorage = diskStorage;
        this.sharedPreferences = sharedPreferences;
    }

    public void onSplashActivityCreated(SplashActivity activity) {

        Amplitude.getInstance().initialize(
                activity,
                activity.getString(R.string.amplitude),
                installPref.getUserId()).enableForegroundTracking(activity.getApplication());
        if (BuildConfig.DEBUG) {
            Amplitude.getInstance().setLogLevel(Log.VERBOSE);
        }

        try {
            Identify identify = new Identify().set("languageCode", Locale.getDefault().getLanguage());
            Amplitude.getInstance().identify(identify);
        } catch (Exception ex) {
            Log.e(TAG, "Error setting amplitude user properties.", ex);
        }
    }

    @SuppressWarnings("unchecked")
    public void track(Event event) {
        JSONObject props = new JSONObject();
        Map<Object, Object> metadataMap = null;

        try {
            Field[] fields = event.getClass().getDeclaredFields();
            for (Field field : fields) {
                Object val = field.get(event);
                if (val == null) {
                    continue;
                }
                if (val.getClass().isEnum()) {
                    val = val.toString();
                }
                if (field.getName().equals("metadata")) {
                    //extract metadata
                    if (val instanceof Map<?, ?>) {
                        metadataMap = (Map<Object, Object>) val;
                    }
                } else {
                    props.put(field.getName(), val);
                }
            }

        } catch (Exception ex) {
            Log.e(TAG, "Error constructing analytics event for: " + event, ex);
            return;
        }

        //add metadata values
        if (metadataMap != null) {
            for (Map.Entry<Object, Object> entry : metadataMap.entrySet()) {
                String key = (String) entry.getKey();
                try {
                    props.put(key, entry.getValue());
                } catch (JSONException e) {
                    Log.e(TAG, "Error inserting property: " + key, e);
                }
            }
        }

        Amplitude.getInstance().logEvent(event.getEventName(), props);
    }

    public void setUserProperties(boolean isSocialOn) {
        JSONObject userProperties = new JSONObject();

        try {
            userProperties.put("deviceId", installPref.getUserId());
            userProperties.put("sessionCount", diskStorage.getSessionCount());
            userProperties.put("languageCode", Util.getLanguage());
            userProperties.put("numConnectedContacts", sharedPreferences.getInt("numContacts", 0));
            userProperties.put("onboardingExperience", sharedPreferences.getString("experience", ""));
            userProperties.put("isSocialOn", isSocialOn);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Amplitude.getInstance().setUserProperties(userProperties);
    }

    public void setUserSessionCount(int sessionCount) {
        Identify identify = new Identify().set("sessionCount", sessionCount);
        Amplitude.getInstance().identify(identify);
    }

    public static class SearchEnded extends Event {
        public String queryText;
        public String searchType;
        public int longestViewedResult;
        public int lastViewedResult;
//        static let ResultFullURL = "result" + "%d" + "FullURL" // String
//        static let ResultIsReaderMode = "result" + "%d" + "IsReaderMode" // String
//        static let ResultIsPriorityLoading = "result" + "%d" + "IsPriorityLoading" // String
//        static let ResultViewTimeAfterRender = "result" + "%d" + "ViewTimeAfterRender" // Int
//        static let ResultSource = "result" + "%d" + "Source" // String
        public int numberOfResults;
        public double maxRankingScore;
        public String getEventName() { return "searchEnded"; }
    }

    public static class CustomWebViewJSLoaded extends Event {
        public boolean success;
        public String url;
        public int timeElapsed;
        public String getEventName() { return "customWebViewJSLoaded"; }
    }

    public static class ScrollToHighlightButtonSeen extends Event {
        public String url;
        public String getEventName() { return "scrollButtonSeen"; }
    }

    public static class ScrollToHighlightButtonTapped extends Event {
        public String url;
        public String getEventName() { return "scrollButtonTapped"; }
    }

    public static class FirstAppOpenAfterAppInstall extends Event {
        public String getEventName() { return "firstAppOpenAfterAppInstall"; }
    }

    public static class InvitationsChannelTapped extends Event {
        public String channel;
        public String shareURL;
        public String getEventName() { return "invitationsChannelTapped"; }
    }

    public static class InvitationsShareSheetMediumTapped extends Event {
        public String shareMedium;
        public String completed;
        public String shareURL;
        public String getEventName() { return "invitationsShareSheetMediumTapped"; }
    }

    public static class InvitationsSMSSent extends Event {
        public String shareURL;
        public String getEventName() { return "invitationsSMSSent"; }
    }

    public static class InvitationsFacebookMessageSent extends Event {
        public String shareURL;
        public String getEventName() { return "invitationsFacebookMessageSent"; }
    }

    public static class InvitationsPaneOpened extends Event {
        public String getEventName() { return "invitationsPaneOpened"; }
    }

    public static class EventPropertyOptions {

        public enum SearchType {
            userTyping,
            mathTyping,
            autoSuggestTap,
            exampleQueryTap,
            ocr,
            editQueryFromResults,
            editQueryAfterOCRWarning,
            searchFromNotification,
            demoModeOCR,
            randomSearch
        }

        public enum ShareCardSource {
            nativeShareButton,
            webviewShareButton
        }

        public enum ShareAppSource {
            inAppMessage
        }

        public enum DemoModeSource {
            cameraPermissions,
            cameraOnboardingTip,
            modalError,
            tocTip,
            devDemoButton
        }
    }
}