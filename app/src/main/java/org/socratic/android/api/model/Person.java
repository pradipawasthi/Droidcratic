package org.socratic.android.api.model;

import com.google.gson.annotations.SerializedName;

import java.sql.Date;

/**
 * Created by jessicaweinberg on 9/28/17.
 */

public class Person implements Chat {
    @SerializedName("display_name")
    private String displayName;

    @SerializedName("name")
    private String name;

    @SerializedName("picture")
    private String picture;

    @SerializedName("picture_small")
    private String pictureSmall;

    @SerializedName("username")
    private String username;

    @SerializedName("phone_number_raw")
    private String phoneNumberRaw;

    @SerializedName("phone_number_international")
    private String phoneNumberInternational;

    @SerializedName("phone_number_national")
    private String phoneNumberNational;

    @SerializedName("phone_number_country_code")
    private String phoneNumberCountryCode;

    @SerializedName("id")
    private int id;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    public String getDisplayName() { return displayName; }
    public String getName() { return name; }
    public String getPicture() { return picture; }
    public String getPictureSmall() { return pictureSmall; }
    public String getUsername() { return username; }
    public String getPhoneNumberRaw() { return phoneNumberRaw; }
    public String getPhoneNumberInternational() { return phoneNumberInternational; }
    public String getPhoneNumberNational() { return phoneNumberNational; }
    public String getPhoneNumberCountryCode() { return phoneNumberCountryCode; }
    public int getID() { return id; }
    public int getPersonID() { return id; }
    public int getChannelID() { return 0; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }
    public String getState() {return ""; }
    public Date getLastMessageSentAt() {return null; }

    @Override
    public String getIsGroupChat() {
        return "";
    }
}
