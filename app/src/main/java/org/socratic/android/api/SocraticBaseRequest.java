package org.socratic.android.api;

import org.socratic.android.BuildConfig;
import org.socratic.android.globals.HttpManager;

import okhttp3.Request;

/**
 * Created by pcnofelt on 3/14/17.
 */

public abstract class SocraticBaseRequest extends BaseRequest {

    private String mHost = "https://socratic.org";

    public String getUrl() {
        StringBuilder builder = new StringBuilder();
        builder.append(mHost)
                .append(getPath());

        String urlParams = getParamsAsUrlString();
        if (urlParams != null) {
            builder.append(urlParams);
        }
        
        return builder.toString();
    }

    public abstract String getPath();

    @Override
    public void setHeaderInfo(Request.Builder builder, HttpManager.HeaderInfos header) {
        builder.header("Accept", "application/json")
                .header("Accept-Language", header.getAcceptLanguage())
                .header("App-Bundle-Id", header.getBundleId())
                .header("App-Build", header.getAppBuild())
                .header("App-Version", header.getAppVersion())
                .header("Device-Id", header.getDeviceId())
                .header("LanguageCode", header.getLanguageCode())
                .header("Environment", header.getEnvironment())
                .header("User-Agent", header.getUserAgent())
                .header("OS", header.getOs())
                .header("OS-Version", header.getOsVersion());
    }
}
