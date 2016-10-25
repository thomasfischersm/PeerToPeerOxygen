package com.playposse.peertopeeroxygen.android.practicamgmt;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.messaging.FirebaseMessaging;
import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.data.DataService;
import com.playposse.peertopeeroxygen.android.data.clientactions.CheckIntoPracticaClientAction;
import com.playposse.peertopeeroxygen.android.data.practicas.PracticaRepository;
import com.playposse.peertopeeroxygen.android.firebase.OxygenFirebaseMessagingService;
import com.playposse.peertopeeroxygen.android.model.ExtraConstants;
import com.playposse.peertopeeroxygen.android.student.StudentPracticaCheckinActivity;
import com.playposse.peertopeeroxygen.android.util.GeoUtil;
import com.playposse.peertopeeroxygen.android.util.PermissionUtil;
import com.playposse.peertopeeroxygen.android.util.ToastUtil;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.PracticaBean;

import static com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY;

/**
 * Encapsulation of business logic around practicas.
 * <p>
 * <p>The manager is lazy about establishing that a geo fence perimeter was crossed. If the
 * location permission is missing or the last known location isn't available, it'll simply wait
 * for the next activity to be started to try again.
 */
public class PracticaManager {

    private static final String LOG_CAT = PracticaManager.class.getSimpleName();

    public static final int LOCATION_PERMISSION_REQUEST = 1;

    private static final int GEO_FENCE_PERIMETER = 100;

    private static boolean isInit = false;
    private static boolean isConnected = false;
    private static GoogleApiClient googleApiClient;

    private static void init(Context context) {
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
     * <p>
     * <p>Note: As this check isn't critical, it can wait for the next activity to be opened if the
     * services aren't ready to use yet.
     */
    public static void refresh(Activity activity, PracticaRepository practicaRepository) {
        Log.i(LOG_CAT, "PracticaManager.refresh called");
        if (!isInit) {
            init(activity);
            return;
        } else if (!isConnected) {
            return;
        }

        if (practicaRepository.getCurrentPractica() == null) {
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
        if (userLocation == null) {
            Log.i(LOG_CAT, "Last known location isn't available.");
            requestLocation(activity);
            return;
        }

        for (PracticaBean practicaBean : practicaRepository.getActivePracticas()) {
            Location practicaLocation = GeoUtil.toLocation(practicaBean.getGpsLocation());
            float distance = GeoUtil.distanceBetween(userLocation, practicaLocation);
            Log.i(LOG_CAT, "Evaluation practica distance: " + practicaBean.getName() + ": "
                    + distance + "m");
            if (distance <= GEO_FENCE_PERIMETER) {
                startPracticaCheckinActivity(activity, practicaBean);
                return;
            }
        }
    }

    private static void requestLocation(Activity activity) {
        if (!PermissionUtil.checkAndGetPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION,
                LOCATION_PERMISSION_REQUEST)) {
            return;
        }

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setNumUpdates(1);
        locationRequest.setInterval(0);
        locationRequest.setPriority(PRIORITY_HIGH_ACCURACY);
        LocationServices.FusedLocationApi.requestLocationUpdates(
                googleApiClient, locationRequest,
                new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        Log.i(LOG_CAT, "Got location update.");
                        LocationServices.FusedLocationApi.removeLocationUpdates(
                                googleApiClient,
                                this);
                    }
                });
    }

    private static void startPracticaCheckinActivity(Activity activity, PracticaBean practicaBean) {
        Intent intent = new Intent(activity, StudentPracticaCheckinActivity.class);
        intent.putExtra(ExtraConstants.EXTRA_PRACTICA_ID, practicaBean.getId());
        activity.startActivity(intent);
    }

    /**
     * Checks if the current practica has ended.
     */
    private static void checkEndOfPractica(PracticaRepository practicaRepository) {
        // TODO
    }

    public static void checkin(
            PracticaBean practicaBean,
            final DataService.LocalBinder localBinder) {

        String topic = OxygenFirebaseMessagingService.PRACTICA_FIREBASE_TOPIC_PREFIX
                + practicaBean.getId();
        FirebaseMessaging.getInstance().subscribeToTopic(topic);

        final PracticaRepository practicaRepository = localBinder
                .getDataRepository()
                .getPracticaRepository();
        practicaRepository.setCurrentPractica(practicaBean);

        localBinder.checkIntoPractica(
                practicaBean.getId(),
                new CheckIntoPracticaClientAction.Callback() {
                    @Override
                    public void onResult(PracticaBean practicaBean) {
                        // Refresh with the latest data about the practica.

                        practicaRepository.setCurrentPractica(practicaBean);

                        practicaRepository.replacePractica(practicaBean);

                        Context context = localBinder.getApplicationContext();
                        ToastUtil.sendToast(context, R.string.successfully_signed_into_practica);
                    }
                });
    }
}