package org.socratic.android.analytics;

/**
 * Created by williamxu on 8/14/17.
 */

public class ContactsPermissionAnalytics {

    public static class ContactsPermissionsNativeModalPermissionGranted extends Event {
        public String getEventName() { return "contactsPermissionsNativeModalPermissionGranted"; }
    }

    public static class ContactsPermissionsNativeModalPermissionDenied extends Event {
        public String getEventName() { return "contactsPermissionsNativeModalPermissionDenied"; }
    }

    public static class ContactsPermissionsDeniedGoToSettingsTapped extends Event {
        public String getEventName() { return "contactsPermissionsDeniedGoToSettingsTapped"; }
    }

    public static class ContactsPermissionsChanged extends Event {
        public String getEventName() { return "contactsPermissionsChanged"; }
    }
}
