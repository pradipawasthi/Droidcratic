package org.socratic.android.api.response;

import com.google.gson.annotations.SerializedName;
import org.socratic.android.api.model.InAppMessage;

/**
 * Created by jessicaweinberg on 7/18/17.
 */

public class InAppMessageDismissedResponse {
    @SerializedName("in_app_message")
    private InAppMessage inAppMessage;

    @SerializedName("message")
    private InAppMessage message;

    public InAppMessage getInAppMessage() { return inAppMessage; }
    public InAppMessage getMessage() { return message; }
}
