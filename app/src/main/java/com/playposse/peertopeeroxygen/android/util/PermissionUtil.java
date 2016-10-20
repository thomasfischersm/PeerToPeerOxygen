package com.playposse.peertopeeroxygen.android.util;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

/**
 * Utility class to make it less verbose to deal with Android permissions.
 */
public class PermissionUtil {

    public static boolean checkAndGetPermission(
            Activity activity,
            String permission,
            int permissionRequestCode) {

        int permissionStatus = ActivityCompat.checkSelfPermission(activity, permission);
        if (permissionStatus != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    new String[]{permission},
                    permissionRequestCode);
            return false;
        }

        return true;
    }
}
