package com.playposse.peertopeeroxygen.android.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import com.playposse.peertopeeroxygen.android.R;

import java.util.List;

/**
 * A utility for sending e-mails.
 */
public class EmailUtil {

    public static void sendEmail(
            Context context,
            String recipient,
            int subject,
            String bodyString) {

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{recipient});
        intent.putExtra(Intent.EXTRA_SUBJECT, context.getResources().getString(subject));
        intent.putExtra(Intent.EXTRA_TEXT, bodyString);

        if (canHandleIntent(context, intent)) {
            context.startActivity(intent);
        } else {
            ToastUtil.sendToast(context, R.string.no_email_client_found_toast);
        }
    }

    private static boolean canHandleIntent(Context context, Intent intent) {
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> infoList = packageManager.queryIntentActivities(intent, 0);
        return infoList.size() > 0;
    }
}
