package org.socratic.android.api.response;

import com.google.gson.annotations.SerializedName;

import org.socratic.android.api.model.Person;

/**
 * Created by jessicaweinberg on 9/29/17.
 */

public class IdentityResponse {
    @SerializedName("person")
    private Person person;

    public Person getPerson() { return person; }
}
