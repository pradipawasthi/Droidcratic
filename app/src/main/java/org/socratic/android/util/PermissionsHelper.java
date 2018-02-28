package org.socratic.android.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * @date 2017-02-17
 */
public class PermissionsHelper {

    private static final String TAG = PermissionsHelper.class.getSimpleName();
    public static final int PERMISSION_REQUEST_CAMERA = 500;
    public static final int PERMISSION_REQUEST_READ_CONTACTS = 400;


    public static boolean hasCameraPermissions(Context context) {
        return ContextCompat.checkSelfPermission(context,
                Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean hasContactsPermissions(Context context) {
        return ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean allPermissionsGranted(Context context) {
        return hasCameraPermissions(context) && hasContactsPermissions(context);
    }

    public static boolean needsCameraPermissionExplanation(Activity activity) {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity,
                Manifest.permission.CAMERA);
    }

    public static boolean needsContactsPermissionExplanation(Activity activity) {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity,
                Manifest.permission.READ_CONTACTS);
    }

    public static void requestCameraPermission(Activity activity) {
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.CAMERA},
                PERMISSION_REQUEST_CAMERA);
    }

    public static void requestContactsPermission(Activity activity) {
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.READ_CONTACTS},
                PERMISSION_REQUEST_READ_CONTACTS);
    }

    public static boolean didGrantPermission(int requestCode,
                                                    int[] grantResults) {
        switch (requestCode) {
            case PermissionsHelper.PERMISSION_REQUEST_CAMERA: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    return true;
                }
            }
            case PermissionsHelper.PERMISSION_REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    return true;
                }
            }
        }

        return false;
    }

    public static void startSystemPermissionsIntent(Context context)
        throws Exception
    {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        context.startActivity(intent);
    }
}
