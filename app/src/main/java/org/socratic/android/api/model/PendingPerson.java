package org.socratic.android.api.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jessicaweinberg on 10/3/17.
 */

public class PendingPerson {
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

    public String getPhoneNumberRaw() { return phoneNumberRaw; }
    public String getPhoneNumberInternational() { return phoneNumberInternational; }
    public String getPhoneNumberNational() { return phoneNumberNational; }
    public String getPhoneNumberCountryCode() { return phoneNumberCountryCode; }
    public int getID() { return id; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }
}