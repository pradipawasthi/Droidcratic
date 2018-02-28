package org.socratic.android.util;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

/**
 * Created by williamxu on 6/5/17.
 */

public class DimenUtil {

    public static float convertSpToPx(float sp, Context context) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, sp, context.getResources().getDisplayMetrics());
    }

    public static float getDisplayScale(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        Activity activity = (Activity) context;
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        float scale = displayMetrics.densityDpi;

        return scale;
    }

    public static int convertDpToPx(Context context, float dpValue) {
        try {
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int) (dpValue * scale + 0.5f);
        } catch (Exception e) {
            return (int) (dpValue + 0.5f);
        }
    }
}
