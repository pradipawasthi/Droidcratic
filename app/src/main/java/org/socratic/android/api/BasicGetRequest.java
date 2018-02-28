package org.socratic.android.api;

import org.socratic.android.globals.HttpManager;

import okhttp3.Request;

/**
 * Created by pcnofelt on 3/14/17.
 */

public class BasicGetRequest extends BaseRequest {

    String mUrl;

    public BasicGetRequest(String url) {
        mUrl = url;
    }

    @Override
    public String getHttpMethod() {
        return BaseRequest.GET;
    }

    @Override
    public String getUrl() {
        return mUrl;
    }

    @Override
    public void setHeaderInfo(Request.Builder builder, HttpManager.HeaderInfos header) {
        // do nothing
    }
}
