package com.playposse.peertopeeroxygen.android.googledrive;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.OpenFileActivityBuilder;
import com.playposse.peertopeeroxygen.android.util.StreamUtil;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionBean;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * A helper that writes a file to Google Drive.
 */
public class GoogleDriveWriter {

    private static final String LOG_TAG = GoogleDriveWriter.class.getSimpleName();

    private static final int REQUEST_CODE_CREATOR = 3;
    private static final String TEXT_FILE_SUFFIX = ".txt";
    private static final String SEPARATOR = "\n------\n";

    public static void initiate(
            GoogleApiClient googleApiClient,
            Activity activity,
            String missionName) {

        DriveContentsCallback driveContentsCallback =
                new DriveContentsCallback(activity, googleApiClient, missionName);

        Drive.DriveApi.newDriveContents(googleApiClient)
                .setResultCallback(driveContentsCallback);
    }

    public static void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data,
            final GoogleApiClient googleApiClient,
            final MissionBean missionBean) {

        if ((requestCode == REQUEST_CODE_CREATOR) && (resultCode == Activity.RESULT_OK)) {
            DriveId driveId = (DriveId) data.getParcelableExtra(
                    OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);
            DriveFile file = driveId.asDriveFile();
            file.open(googleApiClient, DriveFile.MODE_WRITE_ONLY, null).setResultCallback(
                    new ResultCallback<DriveApi.DriveContentsResult>() {
                        @Override
                        public void onResult(DriveApi.DriveContentsResult result) {
                            if (!result.getStatus().isSuccess()) {
                                // Handle error
                                return;
                            }

                            String missionStr = encodeMission(missionBean);

                            DriveContents driveContents = result.getDriveContents();
                            ParcelFileDescriptor parcelFileDescriptor =
                                    driveContents.getParcelFileDescriptor();
                            FileOutputStream fileOutputStream =
                                    new FileOutputStream(parcelFileDescriptor.getFileDescriptor());
                            try {
                                StreamUtil.writeStream(fileOutputStream, missionStr);
                            } catch (IOException ex) {
                                Log.e(LOG_TAG, "Failed to write mission to Google drive.", ex);
                                driveContents.discard(googleApiClient);
                            }
                            driveContents.commit(googleApiClient, null).setResultCallback(
                                    new ResultCallback<Status>() {
                                        @Override
                                        public void onResult(Status result) {
                                            Log.i(LOG_TAG, "Succeeded in exporting mission.");
                                        }
                                    });
                        }
                    });
        }
    }

    private static String encodeMission(MissionBean missionBean) {
        return missionBean.getName()
                + SEPARATOR + missionBean.getStudentInstruction()
                + SEPARATOR + missionBean.getBuddyInstruction();
    }

    private static class DriveContentsCallback
            implements ResultCallback<DriveApi.DriveContentsResult> {

        private final GoogleApiClient googleApiClient;
        private final Activity activity;
        private final String missionName;

        public DriveContentsCallback(
                Activity activity,
                GoogleApiClient googleApiClient,
                String missionName) {

            this.activity = activity;
            this.googleApiClient = googleApiClient;
            this.missionName = missionName;
        }

        @Override
        public void onResult(@NonNull DriveApi.DriveContentsResult result) {
            MetadataChangeSet metadataChangeSet = new MetadataChangeSet.Builder()
                    .setMimeType("text/plain")
                    .setTitle(missionName + TEXT_FILE_SUFFIX)
                    .build();
            IntentSender intentSender = Drive.DriveApi
                    .newCreateFileActivityBuilder()
                    .setInitialMetadata(metadataChangeSet)
                    .setInitialDriveContents(result.getDriveContents())
                    .build(googleApiClient);
            try {
                activity.startIntentSenderForResult(
                        intentSender, REQUEST_CODE_CREATOR, null, 0, 0, 0);
            } catch (IntentSender.SendIntentException e) {
                Log.w(LOG_TAG, "Unable to send intent", e);
            }
        }
    }
}
