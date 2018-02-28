package org.socratic.android.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.text.TextUtils;
import android.util.Log;

import org.socratic.android.BuildConfig;

import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;


/**
 * Utility class with network-related methods.
 */
public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    public static boolean getConnectivityStatus(Context context) {
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            return connectivity.getActiveNetworkInfo() != null
                    && connectivity.getActiveNetworkInfo().isAvailable()
                    && connectivity.getActiveNetworkInfo().isConnected();
        } catch (Exception e) {
            return false;
        }
    }

    public static void logRequest(String tag, String url) {
        if (BuildConfig.DEBUG && BuildConfig.LOG_HTTP_REQUESTS) {
            Log.d(tag, "Starting request to: " + url);
        }
    }

    public static void logResponse(final String tag, final Call call, final Response response,
                                   final String body, final Throwable th) {
        if (BuildConfig.DEBUG && BuildConfig.LOG_HTTP_REQUESTS) {
            Log.d(tag, call.request().url().toString());

            Headers requestHeaders = call.request().headers();
            Log.d(tag, "Request headers:");
            for (int i = 0; i < requestHeaders.size(); i++) {
                Log.d(tag, "  " + requestHeaders.name(i) + " => " + requestHeaders.value(i));
            }

            if (call.isCanceled()) {
                Log.e(TAG, "This call has been canceled.");
                return;
            }

            if (response != null) {
                Log.d(tag, "Response code: " + response.code());
                Log.d(tag, "Response headers:");
                Headers headers = response.headers();
                for (int i = 0; i < headers.size(); i++) {
                    Log.d(tag, "  " + headers.name(i) + " => " + headers.value(i));
                }

                ResponseBody responseBody = response.body();
                if (responseBody != null) {
                    Log.d(tag, "Response body:");

                    try {
                        Log.d(tag, responseBody.string());
                    } catch (Exception ex) {

                    }
                }
            }

            if (!TextUtils.isEmpty(body)) {
                Log.d(tag, "Response body:\n" + body);
            }

            if (th != null) {
                Log.e(tag, "Error.", th);
            }
        }
    }
}
