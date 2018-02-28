package org.socratic.android.api.model;

import com.google.gson.annotations.SerializedName;

import java.sql.Date;
import java.util.HashMap;

/**
 * Created by jessicaweinberg on 10/3/17.
 */

public class Channel implements Chat {
    @SerializedName("name")
    private String name;

    @SerializedName("code")
    private String code;

    @SerializedName("is_two_person")
    private boolean isTwoPerson;

    @SerializedName("school_id")
    private int schoolID;

    @SerializedName("property_map")
    private HashMap propertyMap;

    @SerializedName("id")
    private int id;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    public String getName() { return name; }
    public String getCode() { return code; }
    public boolean getIsTwoPerson() { return isTwoPerson; }
    public int getSchoolID() { return schoolID; }
    public HashMap getPropertyMap() { return propertyMap; }
    public int getChannelID() { return id; }
    public int getPersonID() { return 0; }
    public String getPhoneNumberNational() { return ""; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }
    public String getState() {return ""; }
    public Date getLastMessageSentAt() {return null; }

    @Override
    public String getIsGroupChat() {
        return isTwoPerson ? "false" : "true";
    }
}
