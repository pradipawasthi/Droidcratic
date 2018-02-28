package org.socratic.android.api.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jessicaweinberg on 7/18/17.
 */

public class AppSee {
    @SerializedName("turn_on")
    boolean turnOnAppSee;

    @SerializedName("force_turn_on")
    boolean forceTurnOnAppSee;

    public boolean getTurnOnAppSee() {
        return turnOnAppSee;
    }

    public void setTurnOnAppSee(boolean turnOnAppSee) {
        this.turnOnAppSee = turnOnAppSee;
    }

    public boolean getForceTurnOnAppSee() {
        return forceTurnOnAppSee;
    }

    public void setForceTurnOnAppSee(boolean forceTurnOnAppSee) {
        this.forceTurnOnAppSee = forceTurnOnAppSee;
    }
}
