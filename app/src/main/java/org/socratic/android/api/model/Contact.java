package org.socratic.android.api.model;

import com.google.gson.annotations.SerializedName;

import java.sql.Date;

/**
 * Created by jessicaweinberg on 9/28/17.
 */

public class Contact implements Chat {

    @SerializedName("contact_identifier")
    private String contactIdentifier;

    @SerializedName("person_id")
    private int personID;

    @SerializedName("status")
    private String status;

    private String name;

    public String getContactIdentifier() { return contactIdentifier; }
    public int getPersonID() { return personID; }
    public String getStatus() { return status; }
    public String getPhoneNumberNational() { return ""; }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getChannelID() {
        return 0;
    }

    public String getState() { return ""; }

    public Date getLastMessageSentAt() {return null; }

    @Override
    public String getIsGroupChat() {
        return "";
    }
}
