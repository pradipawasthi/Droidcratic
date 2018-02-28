package org.socratic.android.api.model;

import com.google.gson.annotations.SerializedName;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by jessicaweinberg on 10/3/17.
 */

public class ChannelInfo implements Chat {
    @SerializedName("channel")
    private Channel channel;

    @SerializedName("channel_state")
    private String channelState;

    @SerializedName("contact_identifier")
    private String contactIdentifier;

    @SerializedName("person_id")
    private int personID;

    @SerializedName("num_members")
    private int numMembers;

    @SerializedName("background_color")
    private String backgroundColor;

    @SerializedName("last_message_sent_at")
    private String lastMessageSentAt;

    public Channel getChannel() { return channel; }
    public int getChannelID() { return channel.getChannelID(); }
    public String getState() { return channelState; }
    public String getContactIdentifier() { return contactIdentifier; }
    public int getPersonID() { return personID; }
    public String getPhoneNumberNational() { return ""; }
    public int getNumMembers() { return numMembers; }
    public String getBackgroundColor() { return backgroundColor; }
    public String getName() { return channel.getName(); }
    public Date getLastMessageSentAt() {
        if (this.lastMessageSentAt == null) {
            return null;
        }

        if (this.lastMessageSentAt == "") {
            return null;
        }

        if (this.lastMessageSentAt.isEmpty()) {
            return null;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        java.util.Date parsed = null;
        try {
            parsed = sdf.parse(this.lastMessageSentAt);
        } catch (ParseException e1) {
            e1.printStackTrace();
        }

        if (parsed == null) {
            return null;
        }

        Date data = new java.sql.Date(parsed.getTime());
        return data;
    }

    @Override
    public String getIsGroupChat() {
        return channel.getIsTwoPerson() ? "false" : "true";
    }
}