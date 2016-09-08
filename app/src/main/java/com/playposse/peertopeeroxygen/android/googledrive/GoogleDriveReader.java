package com.playposse.peertopeeroxygen.android.googledrive;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.OpenFileActivityBuilder;
import com.playposse.peertopeeroxygen.android.util.StreamUtil;

import java.io.IOException;

/**
 * A utility to help reading a text file from Google Drive.
 */
public class GoogleDriveReader {

    private static final String LOG_TAG = GoogleDriveReader.class.getSimpleName();

    private static final int REQUEST_CODE_OPENER = 2;

    public static void initiate(GoogleApiClient googleApiClient, Activity activity) {
        if (googleApiClient != null) {
            IntentSender intentSender = Drive.DriveApi
                    .newOpenFileActivityBuilder()
                    .setMimeType(new String[]{"text/plain"})
                    .build(googleApiClient);
            try {
                activity.startIntentSenderForResult(
                        intentSender, REQUEST_CODE_OPENER, null, 0, 0, 0);
            } catch (IntentSender.SendIntentException ex) {
                Log.w(LOG_TAG, "Unable to send intent", ex);
            }
        }
    }

    public static void onActivityResult(
            int requestCode,
            Intent data,
            final GoogleApiClient googleApiClient,
            final GoogleDriveReaderCallback callback) {

        if (requestCode == REQUEST_CODE_OPENER) {
            DriveId driveId = (DriveId) data.getParcelableExtra(
                    OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);
            DriveFile driveFile = driveId.asDriveFile();
            driveFile.open(googleApiClient, DriveFile.MODE_READ_ONLY, null)
                    .setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
                        @Override
                        public void onResult(
                                @NonNull DriveApi.DriveContentsResult driveContentsResult) {

                            if (!driveContentsResult.getStatus().isSuccess()) {
                                Log.e(LOG_TAG, "Failed to open drive file: "
                                        + driveContentsResult.getStatus().getStatusMessage());
                            }

                            DriveContents contents = driveContentsResult.getDriveContents();

                            try {
                                String text = StreamUtil.readStream(contents.getInputStream());
                                Log.i(LOG_TAG, "Got text from google drive: " + text);
                                callback.receiveFileContent(text);
                            } catch (IOException ex) {
                                Log.e(LOG_TAG, "Failed to read Google drive file.", ex);
                            }

                            contents.discard(googleApiClient);
                        }
                    });
        }

    }

    public interface GoogleDriveReaderCallback {

        void receiveFileContent(String fileContent);
    }
}
