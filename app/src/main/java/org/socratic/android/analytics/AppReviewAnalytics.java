package org.socratic.android.analytics;

/**
 * Created by williamxu on 8/14/17.
 */

public class AppReviewAnalytics {

    public static class AppReviewStarsShown extends Event {
        public String getEventName() { return "appReviewStarsShown"; }
    }

    public static class AppReviewStarsTapped extends Event {
        public int starRating;
        public String getEventName() { return "appReviewStarsTapped"; }
    }

    public static class AppReviewAppStoreShown extends Event {
        public String getEventName() { return "appReviewAppStoreShown"; }
    }

    public static class AppReviewAppStoreTapped extends Event {
        public String getEventName() { return "appReviewAppStoreTapped"; }
    }

    public static class AppReviewFeedbackShown extends Event {
        public String getEventName() { return "appReviewFeedbackShown"; }
    }

    public static class AppReviewFeedbackSent extends Event {
        public String feedback;
        public int starRating;
        public String getEventName() { return "appReviewFeedbackSent"; }
    }
}
