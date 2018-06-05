package org.socratic.android.globals;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.util.DisplayMetrics;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import org.socratic.android.BuildConfig;
import org.socratic.android.api.request.BaseRequest;
import org.socratic.android.api.response.NoSerializeResponse;
import org.socratic.android.api.response.BingPredictionsResponse;
import org.socratic.android.storage.InstallPref;
import org.socratic.android.util.MultiLog;
import org.socratic.android.util.NetworkUtils;
import org.socratic.android.util.Util;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Locale;

import javax.inject.Inject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;


/**
 * @author Mark Wyszomierski (markww@gmail.com)
 * @date 2015-10-05
 */
public class HttpManager {

    private OkHttpClient okHttpClient;
    private InstallPref installPref;

    private static final String TAG = HttpManager.class.getSimpleName();

    private Handler mHandler;
    private HeaderInfos headerInfoDefaults;
    private Context appContext;

    @Inject
    public HttpManager(Context appContext, OkHttpClient okHttpClient, InstallPref installPref) {
        this.appContext = appContext;
        this.okHttpClient = okHttpClient;
        this.installPref = installPref;
        mHandler = new Handler();
    }

    public void initHeaderDefaults() {
        PackageInfo pi = Util.getPackageInfo(appContext);

        DisplayMetrics dm = appContext.getResources().getDisplayMetrics();

        String deviceName = Util.removeNonAscii(Util.getDeviceName());

        // iOS example: "Socratic/3.0.1 (iPad; iOS 9.3.5; Scale/2.00)"
        StringBuilder userAgent = new StringBuilder(128);
        userAgent.append("Socratic/");
        userAgent.append(pi.versionCode).append(" ");
        userAgent.append("(");
        userAgent.append(deviceName).append("; ");
        userAgent.append("Android ").append(Build.VERSION.RELEASE).append("; ");
        userAgent.append("Scale/").append(dm.density);

        //only devices with telephony capabilities (phones) classified as Mobile
        if (appContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY)) {
            userAgent.append("; ").append("Mobile;");
        }
        userAgent.append(")");

        // iOS example: "en-US;q=1". We can leave quality value off.
        StringBuilder acceptLanaguage = new StringBuilder(64);
        acceptLanaguage.append(Util.getLanguage());

        headerInfoDefaults = new HeaderInfos();
        headerInfoDefaults.acceptLanguage = acceptLanaguage.toString();
        headerInfoDefaults.bundleId = appContext.getPackageName();
        headerInfoDefaults.appBuild = String.valueOf(pi.versionCode);
        headerInfoDefaults.appVersion = pi.versionName;
        headerInfoDefaults.deviceId = installPref.getUserId();
        headerInfoDefaults.languageCode = Locale.getDefault().getLanguage();
        headerInfoDefaults.environment = getEnvironmentHeader();
        headerInfoDefaults.userAgent = userAgent.toString();
        headerInfoDefaults.os = "Android";
        headerInfoDefaults.osVersion = Build.VERSION.RELEASE;
    }

    public Call request(final BaseRequest request,
                        final HttpCallback callback)
    {

        final Request requestInternal = getPreparedRequest(request);

        callback.onStart();

        Call call = okHttpClient.newCall(requestInternal);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(final Call call, final IOException e) {

                NetworkUtils.logResponse(TAG, call, null, null, e);

                if (call.isCanceled()) {
                    // Don't notify.
                } else {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (e instanceof UnknownHostException) {
                                callback.onFailureNoConnection();
                            }

                            callback.onFailure(call.request(), null, 0, e);
                            callback.onFinished();
                        }
                    });
                }
            }

            @Override
            public void onResponse(final Call call, final Response response) throws IOException {

                if (call.isCanceled()) {
                    // Don't notify.
                    NetworkUtils.logResponse(TAG, call, response, null, null);
                    return;
                }

                ResponseBody responseBody = null;
                String responseBodyAsString = null;
                try {
                    responseBody = response.body();
                    responseBodyAsString = responseBody.string();
                } catch (final Exception ex) {

                    // Request failed; Couldn't parse a response.
                    ex.printStackTrace();
                    MultiLog.d(TAG, "Request failed, couldn't parse a response -> " + ex.getMessage());
                    NetworkUtils.logResponse(TAG, call, response, null, ex);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (call.isCanceled()) {
                                // Don't notify.
                            } else {
                                callback.onFailure(response.request(), null, response.code(), ex);
                                callback.onFinished();
                            }
                        }
                    });
                    return;
                }

                final String responseBodyAsStringFinal = responseBodyAsString;

                NetworkUtils.logResponse(TAG, call, response, responseBodyAsStringFinal, null);

                // Parse response, method will catch parse errors and cleanup for us.
                final Object responseParsed = parseResponse(callback, response, responseBodyAsStringFinal);
                if (responseParsed == null) {
                    return;
                }

                if (response.isSuccessful()) {
                    // Success!
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onSuccess(response, responseParsed);
                            callback.onSuccess(response, responseBodyAsStringFinal, responseParsed);
                            callback.onFinished();
                        }
                    });
                } else {
                    // Response parsed okay, but request failed.
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailure(response.request(), responseParsed, response.code(), null);
                            callback.onFinished();
                        }
                    });
                }
            }
        });

        return call;
    }

    private Request getPreparedRequest(BaseRequest request) {
        Request.Builder builder = new Request.Builder();

        String requestUrl = request.getUrl();
        builder.url(requestUrl);
        builder.tag(request.getTag());

        NetworkUtils.logRequest(TAG, requestUrl);

        if (BaseRequest.GET.equalsIgnoreCase(request.getHttpMethod())) {
            builder.get();

        } else if (BaseRequest.POST.equalsIgnoreCase(request.getHttpMethod())) {
            RequestBody requestBody = request.buildRequestBody();
            if (requestBody != null) {
                builder.post(requestBody);
            } else {
                requestBody = RequestBody.create(null, new byte[0]);
                builder.post(requestBody);
            }

        } else if (BaseRequest.DELETE.equalsIgnoreCase(request.getHttpMethod())) {
            builder.delete();
        } else if (BaseRequest.PUT.equalsIgnoreCase(request.getHttpMethod())) {
            RequestBody requestBody = request.buildRequestBody();
            if (requestBody != null) {
                builder.put(requestBody);
            } else {
                requestBody = RequestBody.create(null, new byte[0]);
                builder.put(requestBody);
            }
        }

        request.setHeaderInfo(builder, headerInfoDefaults);

        return builder.build();
    }

    private Object parseResponse(final HttpCallback callback, final Response response,
                                 String responseBodyAsString) {
        if (callback.getResponseType() == NoSerializeResponse.class) {
            return null;
        }

        //parse difficult json response from bing api manually as Gson cannot handle it
        if (callback.getResponseType() == BingPredictionsResponse.class) {
            try {
                JsonParser parser = new JsonParser();
                JsonArray jsonArray = (JsonArray) parser.parse(responseBodyAsString);
                JsonArray predictionArray = (JsonArray) jsonArray.get(1);

                BingPredictionsResponse predictions = new BingPredictionsResponse();
                for (int i = 0; i < predictionArray.size(); i++) {
                    predictions.add(predictionArray.get(i).getAsString());
                }

                return predictions;
            } catch (Exception ex) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFailure(response.request(), null, 0, null);
                        callback.onFinished();
                    }
                });
            }
        }

        Gson gson = new Gson();
        try {
            return gson.fromJson(responseBodyAsString, callback.getResponseType());
        } catch (Exception ex) {
            ex.printStackTrace();
            MultiLog.d(TAG, "Couldn't parse a proper response -> " + ex.getMessage());
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onFailure(response.request(), null, 0, null);
                    callback.onFinished();
                }
            });
        }

        return null;
    }

    private String getEnvironmentHeader() {
        if (BuildConfig.DEBUG) {
            return "Alpha";
        } else {
            return "GooglePlay";
        }
    }

    public void cancelRequest(String tag) {
        if (tag != null) {
            for (Call call : okHttpClient.dispatcher().queuedCalls()) {
                if (call.request().tag().equals(tag)) {
                    call.cancel();
                }
            }
            for (Call call : okHttpClient.dispatcher().runningCalls()) {
                if (call.request().tag().equals(tag)) {
                    call.cancel();
                }
            }
        }
    }

    public static abstract class HttpCallback<T> {
        private final Class<T> type;

        public HttpCallback(Class<T> type) {
            this.type = type;
        }

        public abstract void onStart();

        public void onFailureNoConnection() {
        }

        public abstract void onFailure(Request request, T responseParsed, int statusCode, Exception e);

        public abstract void onCancel(Request request);

        public abstract void onSuccess(Response response, T responseParsed);

        public void onSuccess(Response response, String responseBodyAsString, T responseParsed) {
        }

        public abstract void onFinished();

        public Class<T> getResponseType() {
            return this.type;
        }
    }

    public static class HeaderInfos {
        private String acceptLanguage;
        private String bundleId;
        private String appBuild;
        private String appVersion;
        private String deviceId;
        private String languageCode;
        private String environment;
        private String userAgent;
        private String os;
        private String osVersion;

        public String getAcceptLanguage() {
            return acceptLanguage;
        }

        public String getBundleId() {
            return bundleId;
        }

        public String getAppBuild() {
            return appBuild;
        }

        public String getAppVersion() {
            return appVersion;
        }

        public String getDeviceId() {
            return deviceId;
        }

        public String getLanguageCode() { return languageCode; }

        public String getEnvironment() {
            return environment;
        }

        public String getUserAgent() {
            return userAgent;
        }

        public String getOs() {
            return os;
        }

        public String getOsVersion() {
            return osVersion;
        }
    }
}