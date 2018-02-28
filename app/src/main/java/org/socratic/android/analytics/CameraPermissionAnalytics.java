package org.socratic.android.analytics;

/**
 * Created by williamxu on 8/14/17.
 */

public class CameraPermissionAnalytics {

    public static class CameraPermissionsNativeModalRequested extends Event {
        public String getEventName() { return "cameraPermissionsNativeModalRequested"; }
    }

    public static class CameraPermissionsNativeModalPermissionGranted extends Event {
        public String getEventName() { return "cameraPermissionsNativeModalPermissionGranted"; }
    }

    public static class CameraPermissionsNativeModalPermissionDenied extends Event {
        public String getEventName() { return "cameraPermissionsNativeModalPermissionDenied"; }
    }

    public static class CameraPermissionsDeniedModalShown extends Event {
        public String getEventName() { return "cameraPermissionsDeniedModalShown"; }
    }

    public static class CameraPermissionsDeniedModalGoToSettingsTapped extends Event {
        public String getEventName() { return "cameraPermissionsDeniedModalGoToSettingsTapped"; }
    }

    public static class CameraPermissionsChanged extends Event {
        public String fromStatus;
        private String toStatus;
        public String getEventName() { return "cameraPermissionsChanged"; }
    }
}
