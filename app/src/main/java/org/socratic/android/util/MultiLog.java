package org.socratic.android.util;

import android.util.Log;

import com.crashlytics.android.Crashlytics;

/**
 * @date 2017-04-04
 */
public class MultiLog {

    private MultiLog() {
    }

    public static void d(String tag, String msg) {
        Log.d(tag, msg);
        Crashlytics.log(msg);
    }

    public static void e(String tag, String msg) {
        Log.e(tag, msg);
        Crashlytics.log(msg);
    }

    public static void e(String tag, String msg, Throwable th) {
        Log.e(tag, msg, th);
        Crashlytics.log(msg);
        Crashlytics.logException(th);
    }

    public static void e(String tag, Throwable th) {
        Log.e(tag, "", th);
        Crashlytics.logException(th);
    }
}
