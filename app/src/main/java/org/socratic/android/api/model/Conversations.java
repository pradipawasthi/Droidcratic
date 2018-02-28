package org.socratic.android.api.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jessicaweinberg on 7/18/17.
 */

public class Conversations {
    @SerializedName("ask_friends")
    private AskFriends askFriends;

    private Drawer drawer;

    public AskFriends getAskFriends() { return askFriends; }
    public Drawer getDrawer() { return drawer; }
}
