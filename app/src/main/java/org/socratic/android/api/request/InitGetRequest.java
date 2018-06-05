package org.socratic.android.api.request;

/**
 * Created by jessicaweinberg on 7/18/17.
 */

public class InitGetRequest extends SocraticBaseRequest {
    public InitGetRequest(String numSessions) {
        putParam("num_sessions", numSessions);
    }

    @Override
    public String getPath() { return "/init"; }

    @Override
    public String getHttpMethod() { return BaseRequest.GET; }
}
