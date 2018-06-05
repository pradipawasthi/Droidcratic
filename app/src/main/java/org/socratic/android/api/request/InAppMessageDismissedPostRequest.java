package org.socratic.android.api.request;

/**
 * Created by jessicaweinberg on 7/18/17.
 */

public class InAppMessageDismissedPostRequest extends SocraticBaseRequest {
    private String id;

    public InAppMessageDismissedPostRequest(String id) {
        this.id = id;
    }

    @Override
    public String getHttpMethod() {
        return BaseRequest.POST;
    }

    @Override
    public String getPath() {
        return String.format("/in_app_message/%s/dismissed", this.id);
    }
}
