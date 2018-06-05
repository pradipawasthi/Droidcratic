package org.socratic.android.api.request;

/**
 * Created by jessicaweinberg on 8/16/17.
 */

public class ShareURLPostRequest extends SocraticBaseRequest {

    public ShareURLPostRequest(String code, String source) {
        putParam("code", code);
        putParam("source", source);
    }

    @Override
    public String getHttpMethod() {
        return BaseRequest.POST;
    }

    @Override
    public String getPath() {
        return "/invite/app_link";
    }
}
