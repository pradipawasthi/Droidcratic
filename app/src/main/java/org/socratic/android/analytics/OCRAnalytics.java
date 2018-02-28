package org.socratic.android.analytics;

import java.util.Map;

/**
 * Created by williamxu on 8/14/17.
 */

public class OCRAnalytics {

    public static class OCRImageUploadError extends Event {
        public String errorDescription;
        public int fileSizeInBytes;
        public int afterNumberOfRetries;
        public String getEventName() { return "ocrImageUploadError"; }
    }

    public static class OCRRequestError extends Event {
        public String errorDescription;
        public AnalyticsManager.EventPropertyOptions.SearchType searchType;
        public String query;
        public String imageId;
        public String getEventName() { return "ocrRequestError"; }
    }

    public static class QuerySearchError extends Event {
        public String errorDescription;
        public AnalyticsManager.EventPropertyOptions.SearchType searchType;
        public String query;
        public String imageId;
        public String getEventName() { return "querySearchError"; }
    }

    public static class OCRRequested extends Event {
        public String queryText;
        public String searchType;
        public String imageId;
        public String getEventName() { return "ocrRequested"; }
    }

    public static class QuerySearched extends Event {
        public String queryText;
        public String resourceURL;
        public AnalyticsManager.EventPropertyOptions.SearchType searchType;
        public Map<Object, Object> metadata;
        public String getEventName() { return "querySearched"; }
    }

    public static class DemoQuerySearched extends Event {
        public String queryText;
        public String resourceURL;
        public AnalyticsManager.EventPropertyOptions.SearchType searchType;
        public String getEventName() { return "demoQuerySearched"; }
    }

    public static class EditQueryClicked extends Event {
        public String queryText;
        public String previousSearchType;
        public String getEventName() { return "editQueryClicked"; }
    }

    public static class EditQueryCancelled extends Event {
        public String getEventName() { return "editQueryCancelled"; }
    }

    public static class OCRImageUploaded extends Event {
        public int fileSizeInBytes;
        public String imageId;
        public int afterNumberOfRetries;
        public int facesDetectedCount;
        public double skewAngle;
        public String getEventName() { return "ocrImageUploaded"; }
    }

    public static class OCRPhotoCropped extends Event {
        public boolean croppingToolUsed;
        public boolean paragraphDetected;
        public String getEventName() { return "ocrPhotoCropped"; }
    }
}
