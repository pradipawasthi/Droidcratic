package org.socratic.android.api;

import org.socratic.android.api.model.Contact;
import org.socratic.android.api.model.UserContact;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by jessicaweinberg on 10/3/17.
 */

public class NewChatPostRequest extends SocraticBaseRequest {
    private int personID;
    private UserContact contact;


    public NewChatPostRequest(int personID, UserContact contact) {
        this.personID = personID;
        this.contact = contact;
    }

    @Override
    public String getHttpMethod() {
        return BaseRequest.POST;
    }

    @Override
    public String getPath() {
        return String.format("/channel", this.personID);
    }

    @Override
    public RequestBody buildRequestBody() {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("contact", contact.toJSON().toString())
                .addFormDataPart("person_id", String.valueOf(personID))
                .build();
        return requestBody;
    }
}
