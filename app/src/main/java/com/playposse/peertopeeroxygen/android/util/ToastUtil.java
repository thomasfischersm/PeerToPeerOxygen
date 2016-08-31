package com.playposse.peertopeeroxygen.android.util;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

/**
 * A little helper class to send toasts when outside of an activity.
 */
public class ToastUtil {

    public static void sendToast(final Context context, final String message) {
        Handler h = new Handler(context.getMainLooper());
        h.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            }
        });
    }
}
