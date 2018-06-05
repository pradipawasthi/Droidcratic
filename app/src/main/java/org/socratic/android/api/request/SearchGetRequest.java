package org.socratic.android.api.request;

/**
 * Created by williamxu on 6/21/17.
 */

public class SearchGetRequest extends SocraticBaseRequest {

    public static final String NATIVE_CARD_TYPE_QA = "qa";

    public SearchGetRequest(String query) {
        putParam("q", query);
    }

    public SearchGetRequest(String query, String ... nativeCardTypes) {
        putParam("q", query);

        if (nativeCardTypes != null && nativeCardTypes.length > 0) {
            StringBuilder nativeCardTypesFormatted = new StringBuilder();
            for (int i = 0; i < nativeCardTypes.length; i++) {
                nativeCardTypesFormatted.append(nativeCardTypes[i]);
                if (i < nativeCardTypes.length-1) {
                    nativeCardTypesFormatted.append(",");
                }
            }
            putParam("request_native", nativeCardTypesFormatted.toString());
        }
    }

    @Override
    public String getPath() {
        return "/search";
    }

    @Override
    public String getHttpMethod() {
        return BaseRequest.GET;
    }
}
