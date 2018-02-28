package org.socratic.android.api;

import org.json.JSONArray;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by williamxu on 8/11/17.
 */

public class ContactsPostRequest extends SocraticBaseRequest {

    private JSONArray contactsArray;
    private String deviceName;
    private String userId;

    public ContactsPostRequest(JSONArray contactsArray, String deviceName, String userId) {
        this.contactsArray = contactsArray;
        this.deviceName = deviceName;
        this.userId = userId;
    }

    @Override
    public String getPath() {
        return "/contact/upload";
    }

    @Override
    public String getHttpMethod() {
        return BaseRequest.POST;
    }

    @Override
    public RequestBody buildRequestBody() {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("contacts", contactsArray.toString())
                .addFormDataPart("device_name", deviceName)
                .addFormDataPart("person_id", userId)
                .build();
        return requestBody;
    }
}
