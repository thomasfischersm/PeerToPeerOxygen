package com.playposse.peertopeeroxygen.android.util;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.playposse.peertopeeroxygen.android.R;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Utility class for accessing the log for diagnostic purposes.
 */
public class LogUtil {

    private static final String LOG_CAT = LogUtil.class.getSimpleName();

    public static final int EXTERNAL_DRIVE_PERMISSION_REQUEST = 1;

    public static void emailLog(Activity activity) {
        if (!PermissionUtil.checkAndGetPermission(
                activity,
                "android.permission.WRITE_EXTERNAL_STORAGE",
                EXTERNAL_DRIVE_PERMISSION_REQUEST)) {
            return;
        }

        new EmailLogAsyncTask(activity).execute();
    }

    private static class EmailLogAsyncTask extends AsyncTask<Void, Void, File> {

        private final Activity activity;

        public EmailLogAsyncTask(Activity activity) {
            this.activity = activity;
        }

        @Override
        protected File doInBackground(Void... voids) {
            // Export log to file.
            File outputFile = new File(Environment.getExternalStorageDirectory(), "logcat.txt");
            try {
                Log.i(LOG_CAT, "About to start dumping log.");
                Process process =
                        Runtime.getRuntime().exec("logcat -f " + outputFile.getAbsolutePath());
                Log.i(LOG_CAT, "Finished dumping log.");
                InputStream errorStream = process.getErrorStream();
            } catch (Throwable ex) {
                Log.e(LOG_CAT, "Failed to export log to the file system", ex);
            }
            return outputFile;
        }

        @Override
        protected void onPostExecute(File outputFile) {
            // Create intent.
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("vnd.android.cursor.dir/email");
            String to[] = {activity.getString(R.string.publisher_email)};
            emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
            emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(outputFile));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, activity.getString(R.string.email_log_subject));

            // Start activity.
            String intentChooserTitle = activity.getString(R.string.email_log_dialog_title);
            activity.startActivity(Intent.createChooser(emailIntent , intentChooserTitle));
        }
    }
}
