package com.playposse.peertopeeroxygen.android.practicamgmt;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.playposse.peertopeeroxygen.android.data.practicas.PracticaRepository;
import com.playposse.peertopeeroxygen.android.util.GeoUtil;
import com.playposse.peertopeeroxygen.android.util.PermissionUtil;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.PracticaBean;

/**
 * Encapsulation of business logic around practicas.
 */
public class PracticaManager {

    private static final String LOG_CAT = PracticaManager.class.getSimpleName();

    public static final int LOCATION_PERMISSION_REQUEST = 1;

    private static final int GEO_FENCE_PERIMETER = 100;

    private static boolean isInit = false;
    private static boolean isConnected = false;
    private static GoogleApiClient googleApiClient;

    public static void init(Context context) {
        googleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        isConnected = true;
                    }

                    @Override
                    public void onConnectionSuspended(int cause) {
                        Log.i(LOG_CAT, "Goolge API connection is suspended: " + cause);
                        isConnected = false;
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.e(LOG_CAT, "Failed to connect to Google API."
                                + connectionResult.getErrorMessage());
                    }
                })
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
        isInit = true;
    }

    /**
     * Refreshes the practica state in the app. This method should be called whenever an activity
     * is resumed. It'll check if the student walked into a practica and clear up the information
     * once the practica is over.
     *
     * <p>Note: As this check isn't critical, it can wait for the next activity to be opened if the
     * services aren't ready to use yet.
     */
    public static void refresh(Activity activity, PracticaRepository practicaRepository) {
        if (!isInit) {
            init(activity);
            return;
        } else if (!isConnected) {
            return;
        }

        if (practicaRepository.getCurrentPractica() != null) {
            checkPracticaGeoFence(activity, practicaRepository);
        }

        checkEndOfPractica(practicaRepository);
    }

    /**
     * Checks if the user walked into the geometric radius around a practica.
     */
    private static void checkPracticaGeoFence(
            Activity activity,
            PracticaRepository practicaRepository) {

        if (!PermissionUtil.checkAndGetPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION,
                LOCATION_PERMISSION_REQUEST)) {
            return;
        }
        Location userLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        for (PracticaBean practicaBean : practicaRepository.getActivePracticas()) {
            Location practicaLocation = GeoUtil.toLocation(practicaBean.getGpsLocation());
            float distance = GeoUtil.distanceBetween(userLocation, practicaLocation);
            Log.i(LOG_CAT, "Evaluation practica distance: " + practicaBean.getName() + ": "
                    + distance + "m");
            if (distance <= GEO_FENCE_PERIMETER) {
                startPracticaCheckinActivity(practicaBean);
                return;
            }
        }
    }

    private static void startPracticaCheckinActivity(PracticaBean practicaBean) {
        // TODO
    }

    /**
     * Checks if the current practica has ended.
     */
    private static void checkEndOfPractica(PracticaRepository practicaRepository) {
        // TODO
    }
}
