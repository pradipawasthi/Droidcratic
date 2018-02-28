package org.socratic.android.api.response;

import com.google.gson.annotations.SerializedName;

import org.socratic.android.api.model.Channel;
import org.socratic.android.api.model.Message;
import org.socratic.android.api.model.PendingPerson;
import org.socratic.android.api.model.Person;

import java.util.ArrayList;

/**
 * Created by jessicaweinberg on 10/3/17.
 */

public class AllMessagesResponse {
    @SerializedName("messages")
    private ArrayList<Message> messages;

    @SerializedName("persons")
    private ArrayList<Person> persons;

    @SerializedName("pending_persons")
    private ArrayList<PendingPerson> pendingPersons;

    @SerializedName("channel")
    private Channel channel;

    @SerializedName("has_earlier_messages")
    private boolean hasEarlierMessages;

    @SerializedName("unread_message_count")
    private int unreadMessageCount;


    public ArrayList<Message> getMessages() { return messages; }
    public ArrayList<Person> getPersons() { return persons; }
    public ArrayList<PendingPerson> getPendingPersons() { return pendingPersons; }
    public Channel getChannel() { return channel; }
    public boolean getHasEarlierMessages() { return hasEarlierMessages; }
    public int getUnreadMessageCount() { return unreadMessageCount; }
}