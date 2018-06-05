package org.socratic.android.api.request;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by pcnofelt on 4/18/17.
 */

public class RatingPostRequest extends SocraticBaseRequest {

    private int rating;

    public RatingPostRequest(int rating) {
        this.rating = rating;
    }

    @Override
    public String getHttpMethod() {
        return BaseRequest.POST;
    }

    @Override
    public String getPath() {
        return "/ratings";
    }

    @Override
    public RequestBody buildRequestBody() {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("rating", String.valueOf(rating))
                .build();
        return requestBody;
    }
}
