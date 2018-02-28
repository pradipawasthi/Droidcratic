package org.socratic.android.api.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jessicaweinberg on 7/18/17.
 */



public class AskFriends {
    @SerializedName("is_on")
    boolean isAskFriendsOn;

    private String conversationsAskFriendsSMSExperiment;

    public boolean getIsAskFriendsOn() {
        return isAskFriendsOn;
    }

    public void setIsAskFriendsOn(boolean isAskFriendsOn) {
        this.isAskFriendsOn = isAskFriendsOn;
    }

    public String getConversationsAskFriendsSMSExperiment() {
        return conversationsAskFriendsSMSExperiment;
    }

    public void setConversationsAskFriendsSMSExperiment(String conversationsAskFriendsSMSExperiment) {
        this.conversationsAskFriendsSMSExperiment = conversationsAskFriendsSMSExperiment;
    }
}


