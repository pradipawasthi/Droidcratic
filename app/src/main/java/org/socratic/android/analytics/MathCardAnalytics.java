package org.socratic.android.analytics;

/**
 * Created by byfieldj on 9/18/17.
 */

public class MathCardAnalytics {

    public static class MathStepExpanded extends Event{

        public String stepType;

        public String query;

        @Override
        public String getEventName() {
            return "mathStepExpanded";
        }
    }

    public static class MathStepCollapsed extends Event{

        public String stepType;
        public String query;

        @Override
        public String getEventName() {
            return "mathStepCollapsed";
        }
    }

    public static class TimeOnStepper extends Event{

        public String query;
        public float eventDurationInSeconds;

        @Override
        public String getEventName() {
            return "timeOnStepper";
        }
    }

    public static class SwipeOffStepper extends Event{

        public String query;

        @Override
        public String getEventName() {
            return "swipeOffStepper";
        }
    }
}
