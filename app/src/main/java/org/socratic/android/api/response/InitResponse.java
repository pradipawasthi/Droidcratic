package org.socratic.android.api.response;

/**
 * Created by jessicaweinberg on 7/18/17.
 */

import com.google.gson.annotations.SerializedName;

import org.socratic.android.api.model.InAppMessage;

public class InitResponse {
    @SerializedName("in_app_message")
    private InAppMessage inAppMessage;

    public InAppMessage getInAppMessage() { return inAppMessage; }
}
