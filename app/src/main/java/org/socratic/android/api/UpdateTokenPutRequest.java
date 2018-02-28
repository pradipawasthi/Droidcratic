package org.socratic.android.api;

/**
 * Created by jessicaweinberg on 10/3/17.
 */

public class UpdateTokenPutRequest extends SocraticBaseRequest {


    public UpdateTokenPutRequest(String firebaseRegistrationToken) {
        putParam("firebase_registration_token", firebaseRegistrationToken);
    }

    @Override
    public String getHttpMethod() {
        return BaseRequest.PUT;
    }

    @Override
    public String getPath() {
        return "/device";
    }
}
