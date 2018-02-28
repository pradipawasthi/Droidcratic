package org.socratic.android.util;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.Nullable;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * General utils.
 *
 * @date 2016-03-06
 */
public class Util {

    private Util() {
    }

    public static PackageInfo getPackageInfo(Context appContext) {
        try {
            return appContext.getPackageManager().getPackageInfo(
                    appContext.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            return new PackageInfo();
        }
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return model;
        }
        return manufacturer + " " + model;
    }

    public static String getLanguage() {
        StringBuilder sb = new StringBuilder(32);
        sb.append(Locale.getDefault().getLanguage());
        sb.append("-");
        sb.append(Locale.getDefault().getCountry());
        return sb.toString();
    }

    public static void makeStatusBarTransparent(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = activity.getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    public static void hideStatusBar(Activity activity) {
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public static String removeNonAscii(String in) {
        return in.replaceAll("[^\\p{ASCII}]", "");
    }

    public static boolean floatEquals(double a, double b, double epsilon) {
        return Math.abs(a - b) < epsilon;
    }

    public static int parseApiColor(String text) {
        if (text == null) {
            return 0;
        }

        if (!text.startsWith("#")) {
            text = "#" + text;
        }

        try {
            return Color.parseColor(text);
        } catch (Exception ex) {
            return 0;
        }
    }

    public static boolean isNowPastMayThird() {

        DateTime may3rd = new DateTime(
                2017, 5, 4, 3, 0, DateTimeZone.forID("America/New_York"));
        DateTime now = new DateTime().withZone(DateTimeZone.forID("America/New_York"));

        return now.isAfter(may3rd);
    }

    public static String getUserDeviceName(Context context) {
        return Settings.System.getString(context.getContentResolver(), "device_name");
    }

    // Tries to cast an Activity Context to another type
    @SuppressWarnings("unchecked")
    @Nullable
    public static <T> T castActivityFromContext(Context context, Class<T> castClass) {
        if(castClass.isInstance(context)) {
            return (T) context;
        }

        while(context instanceof ContextWrapper) {
            context = ((ContextWrapper) context).getBaseContext();

            if(castClass.isInstance(context)) {
                return (T) context;
            }
        }

        return null;
    }

}
