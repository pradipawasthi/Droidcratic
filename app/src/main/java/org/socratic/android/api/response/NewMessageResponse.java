package org.socratic.android.api.response;

import com.google.gson.annotations.SerializedName;

import org.socratic.android.api.model.Message;

/**
 * Created by jessicaweinberg on 10/3/17.
 */

public class NewMessageResponse {
    @SerializedName("message")
    private Message message;

    public Message getMessage() { return message; }
}