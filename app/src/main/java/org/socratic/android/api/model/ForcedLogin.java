package org.socratic.android.api.model;

/**
 * Created by jessicaweinberg on 7/18/17.
 */

import com.google.gson.annotations.SerializedName;

public class ForcedLogin {
    @SerializedName("is_on")
    boolean forcedLoginIsOn;

    private String experience;

    private String onboardingAdvanceExperiment;

    private String onboardingSMSExperiment;

    public boolean getForcedLoginIsOn() {
        return forcedLoginIsOn;
    }

    public void setForcedLoginIsOn(boolean forcedLoginIsOn) {
        this.forcedLoginIsOn = forcedLoginIsOn;
    }

    public String getOnboardingAdvanceExperiment() {
        return onboardingAdvanceExperiment;
    }

    public void setOnboardingAdvanceExperiment(String onboardingAdvanceExperiment) {
        this.onboardingAdvanceExperiment = onboardingAdvanceExperiment;
    }

    public String getOnboardingSMSExperiment() {
        return onboardingSMSExperiment;
    }

    public void setOnboardingSMSExperiment(String onboardingSMSExperiment) {
        this.onboardingSMSExperiment = onboardingSMSExperiment;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }
}
