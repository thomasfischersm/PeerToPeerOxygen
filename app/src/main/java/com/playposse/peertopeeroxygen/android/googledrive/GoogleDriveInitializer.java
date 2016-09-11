package com.playposse.peertopeeroxygen.android.googledrive;

import android.app.Activity;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;

import javax.annotation.Nullable;

/**
 * A helper class that initializes a connection to Google Drive.
 */
public class GoogleDriveInitializer {

    private static final String LOG_TAG = GoogleDriveInitializer.class.getSimpleName();

    /**
     * Request code for auto Google Play Services error resolution.
     */
    protected static final int REQUEST_CODE_RESOLUTION = 1;

    public static GoogleApiClient initialize(Activity activity, @Nullable Runnable afterAction) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(activity)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .addConnectionCallbacks(new Callback(afterAction))
                .addOnConnectionFailedListener(new FailedListener(activity))
                .build();
        googleApiClient.connect();
        return googleApiClient;
    }

    public static void onActivityResult(
            int requestCode,
            int resultCode,
            GoogleApiClient googleApiClient) {

        if ((requestCode == REQUEST_CODE_RESOLUTION) && (resultCode == Activity.RESULT_OK)) {
            if (!googleApiClient.isConnected()) {
                googleApiClient.connect();
            }
        }
    }

    private static class Callback implements GoogleApiClient.ConnectionCallbacks {

        @Nullable
        private Runnable afterAction;

        public Callback(@Nullable Runnable afterAction) {
            this.afterAction = afterAction;
        }

        @Override
        public void onConnected(@android.support.annotation.Nullable Bundle bundle) {
            Log.i(LOG_TAG, "API client connected.");
            if (afterAction != null) {
                afterAction.run();
                afterAction = null;
            }
        }

        @Override
        public void onConnectionSuspended(int i) {
            Log.i(LOG_TAG, "GoogleApiClient connection suspended");
        }
    }

    private static class FailedListener implements GoogleApiClient.OnConnectionFailedListener {

        private final Activity activity;

        public FailedListener(Activity activity) {
            this.activity = activity;
        }

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult result) {
            // Called whenever the API client fails to connect.
            Log.i(LOG_TAG, "GoogleApiClient connection failed: " + result.toString());
            if (!result.hasResolution()) {
                // show the localized error dialog.
                Log.i(LOG_TAG, "Show resolution dialog for google drive");
                GoogleApiAvailability.getInstance()
                        .getErrorDialog(activity, result.getErrorCode(), 0).show();
                return;
            }
            // The failure has a resolution. Resolve it.
            // Called typically when the app is not yet authorized, and an
            // authorization
            // dialog is displayed to the user.
            try {
                result.startResolutionForResult(activity, REQUEST_CODE_RESOLUTION);
            } catch (IntentSender.SendIntentException e) {
                Log.e(LOG_TAG, "Exception while starting resolution activity", e);
            }
        }
    }
}
