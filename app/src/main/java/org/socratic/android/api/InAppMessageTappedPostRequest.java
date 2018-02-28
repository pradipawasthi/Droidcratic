package org.socratic.android.api;

/**
 * Created by jessicaweinberg on 7/18/17.
 */

public class InAppMessageTappedPostRequest extends SocraticBaseRequest {
    private String id;

    public InAppMessageTappedPostRequest(String id) {
        this.id = id;
    }

    @Override
    public String getHttpMethod() {
        return BaseRequest.POST;
    }

    @Override
    public String getPath() {
        return String.format("/in_app_message/%s/tapped", this.id);
    }
}
