package com.playposse.peertopeeroxygen.android.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.playposse.peertopeeroxygen.android.data.OxygenSharedPreferences;
import com.playposse.peertopeeroxygen.android.util.LogoutUtil;

/**
 * A {@link android.content.BroadcastReceiver} that waits for the screen to turn off to log out
 * users with a studio loaner device.
 */
public class ScreenOffBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            if (OxygenSharedPreferences.getLoanerDeviceId(context) != null) {
                LogoutUtil.logout(context);
            }
        }
    }
}
