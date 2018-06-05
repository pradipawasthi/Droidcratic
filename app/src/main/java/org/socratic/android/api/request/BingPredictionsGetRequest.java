package org.socratic.android.api.request;

import org.socratic.android.globals.HttpManager;

import okhttp3.Request;

/**
 * Created by williamxu on 6/27/17.
 */

public class BingPredictionsGetRequest extends BaseRequest {

    private String mHost = "http://api.bing.com/osjson.aspx";

    public BingPredictionsGetRequest(String query) {
        putParam("query", query);
    }

    @Override
    public String getHttpMethod() {
        return BaseRequest.GET;
    }

    @Override
    public String getUrl() {
        StringBuilder builder = new StringBuilder();
        builder.append(mHost);

        String urlParams = getParamsAsUrlString();
        if (urlParams != null) {
            builder.append(urlParams);
        }

        return builder.toString();
    }

    @Override
    public void setHeaderInfo(Request.Builder builder, HttpManager.HeaderInfos header) {
        //no headers
    }
}
