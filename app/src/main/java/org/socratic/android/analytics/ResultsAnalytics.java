package org.socratic.android.analytics;

/**
 * Created by williamxu on 8/14/17.
 */

public class ResultsAnalytics {

    public static class ResultWebViewLoadError extends Event {
        public String errorDescription;
        public String url;
        public String getEventName() { return "resultWebViewLoadError"; }
    }

    public static class ResultNativeLoadError extends Event {
        public String errorDescription;
        public String cardType;
        public String query;
        public String resultSource;
        public String resultFullURL;
        public String resultHostURL;
        public int pageNumberInScrollView;
        public String getEventName() { return "resultNativeLoadError"; }
    }

    public static class ResultsSwipedAway extends Event {
        public String swipeType;
        public String queryText;
        public String getEventName() { return "resultsSwipedAway"; }
    }

    public static class ResultNativeLoaded extends Event {
        public String cardType;
        public String query;
        public String resultSource;
        public String resultFullURL;
        public String resultHostURL;
        public int pageNumberInScrollView;
        public String getEventName() { return "resultNativeLoaded"; }
    }

    public static class ResultPageViewed extends Event {
        public String queryText;
        public int viewCount;
        public boolean isLastResultViewed;
        public boolean isReaderMode;
        public boolean isPriorityLoading;
        public String resultFullURL;
        public String resultHostURL;
        public float maximumVerticalDistanceScrolled;
        public int pageNumberInScrollView;
        public String resultSource;
        public String cardType;
        public boolean isNativeCard;
        public String getEventName() { return "resultPageViewed"; }
    }

    public static class ResultWebViewLoaded extends Event {
        public String resultFullURL;
        public String resultHostURL;
        public boolean isReaderMode;
        public boolean isPriorityLoading;
        public int pageNumberInScrollView;
        public boolean isMobileOptimized;
        public String cardType;
        public String getEventName() { return "resultWebViewLoaded"; }
    }

    public static class ResultWebViewStartedLoading extends Event {
        public String resultFullURL;
        public String resultHostURL;
        public boolean isReaderMode;
        public boolean isPriorityLoading;
        public int pageNumberInScrollView;
        public String getEventName() { return "resultWebViewStartedLoading"; }
    }

    public static class ResultsTipShown extends Event {
        public String queryText;
        public String tipType;
        public String getEventName() { return "resultsTipShown"; }
    }

    public static class ResultQuality extends Event {
        public String queryText;
        public String searchType;
        public double maxRankingScore;
        public boolean has_explainer;
        public boolean has_math_steps;
        public boolean stepsShown;
        public boolean tipShown;
        public String tipType;
        public String getEventName() { return "resultQuality"; }
    }
}
