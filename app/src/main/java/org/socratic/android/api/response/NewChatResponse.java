package org.socratic.android.api.response;

import com.google.gson.annotations.SerializedName;

import org.socratic.android.api.model.Channel;

/**
 * Created by jessicaweinberg on 10/3/17.
 */

public class NewChatResponse {
    @SerializedName("channel")
    private Channel channel;

    public Channel getChannel() { return channel; }
}
