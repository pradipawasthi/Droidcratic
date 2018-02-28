package org.socratic.android.analytics;

/**
 * Created by williamxu on 8/14/17.
 */

public class OnboardingAnalytics {

    public static class SignUpScreenSeen extends Event {
        public String getEventName() { return "signupScreenSeen"; }
    }

    public static class SignUpScreenButtonTapped extends Event {
        public String getEventName() { return "signupScreenButtonTapped"; }
    }

    public static class PhoneNumberScreenVisited extends Event {
        public String getEventName() { return "phoneNumberScreenVisited"; }
    }

    public static class accountKitPhoneLoginError extends Event {
        public String message;
        public String getEventName() { return "accountKitPhoneLoginError"; }
    }

    public static class accountKitPhoneLoginCancel  extends Event {
        public String getEventName() { return "accountKitPhoneLoginCancel "; }
    }

    public static class PhoneNumberSubmitted extends Event {
        public String getEventName() { return "phoneNumberSubmitted"; }
    }

    public static class PhoneNumberSubmissionDidSucceed extends Event {
        public String getEventName() { return "phoneNumberSubmissionDidSucceed"; }
    }

    public static class PhoneConfirmationCodeEntryScreenVisited extends Event {
        public String getEventName() { return "phoneConfirmationCodeEntryScreenVisited"; }
    }

    public static class PhoneConfirmationCodeSubmitted extends Event {
        public String getEventName() { return "phoneConfirmationCodeSubmitted"; }
    }

    public static class PhoneAuthenticationDidComplete extends Event {
        public String getEventName() { return "phoneAuthenticationDidComplete"; }
    }

    public static class ProfileCreateContinue extends Event {
        public String getEventName() { return "profileCreateContinue"; }
    }

    public static class PermissionsScreenViewed extends Event {
        public String getEventName() { return "permissionsScreenViewed"; }
    }

    public static class PermissionsComplete extends Event {
        public String getEventName() { return "permissionsComplete"; }
    }

    public static class SocialOnboardingComplete extends Event {
        public String getEventName() { return "socialOnboardingComplete"; }
    }
}
