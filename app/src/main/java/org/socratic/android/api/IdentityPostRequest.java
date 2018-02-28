package org.socratic.android.api;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by williamxu on 8/17/17.
 */

public class IdentityPostRequest extends SocraticBaseRequest {

    private String name;
    private String phoneNumber;

    public IdentityPostRequest(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String getPath() {
        return "/person";
    }

    @Override
    public String getHttpMethod() {
        return BaseRequest.POST;
    }

    @Override
    public RequestBody buildRequestBody() {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("name", name)
                .addFormDataPart("phone_number", phoneNumber)
                .build();
        return requestBody;
    }
}
