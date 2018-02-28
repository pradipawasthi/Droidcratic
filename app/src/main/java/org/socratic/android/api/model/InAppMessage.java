package org.socratic.android.api.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jessicaweinberg on 7/18/17.
 */

public class InAppMessage {
    private int id;

    @SerializedName("message_text")
    private String messageText;

    @SerializedName("button_text")
    private String buttonText;

    private String url;

    @SerializedName("seconds_delay")
    private int secondsDelay;

    @SerializedName("in_app_message_type")
    private String inAppMessageType;

    public int getID() { return id; }
    public String getMessageText() { return messageText; }
    public String getButtonText() { return buttonText; }
    public String getURL() { return url; }
    public int getSecondsDelay() { return secondsDelay; }
    public String getInAppMessageType() { return inAppMessageType; }
}
