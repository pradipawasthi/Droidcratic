package org.socratic.android.api.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jessicaweinberg on 7/18/17.
 */

public class Drawer {
    @SerializedName("is_on")
    boolean isDrawerOn;

    private String conversationsFriendsPaneSMSExperiment;

    public boolean getIsDrawerOn() {
        return isDrawerOn;
    }

    public void setIsDrawerOn(boolean isDrawerOn) {
        this.isDrawerOn = isDrawerOn;
    }

    public String getConversationsFriendsPaneSMSExperiment() {
        return conversationsFriendsPaneSMSExperiment;
    }

    public void setConversationsFriendsPaneSMSExperiment(String conversationsFriendsPaneSMSExperiment) {
        this.conversationsFriendsPaneSMSExperiment = conversationsFriendsPaneSMSExperiment;
    }
}