package org.socratic.android.api.response;

import com.google.gson.annotations.SerializedName;

import org.socratic.android.api.model.Channel;
import org.socratic.android.api.model.ChannelInfo;
import org.socratic.android.api.model.Contact;
import org.socratic.android.api.model.Person;

import java.util.ArrayList;

/**
 * Created by jessicaweinberg on 9/28/17.
 */

public class ChatListResponse {
    @SerializedName("contacts")
    private ArrayList<Contact> contacts;

    @SerializedName("persons")
    private ArrayList<Person> persons;

    @SerializedName("channels")
    private ArrayList<ChannelInfo> channels;

    @SerializedName("unread_message_count")
    private int unreadMessageCount;

    public int getUnreadMessageCount() { return unreadMessageCount; }
    public ArrayList<Contact> getContacts() { return contacts; }
    public ArrayList<Person> getPersons() { return persons; }
    public ArrayList<ChannelInfo> getChannels() { return channels; }
}

