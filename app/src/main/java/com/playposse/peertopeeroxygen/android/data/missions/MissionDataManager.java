package com.playposse.peertopeeroxygen.android.data.missions;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.playposse.peertopeeroxygen.android.data.DataRepository;
import com.playposse.peertopeeroxygen.android.data.DataService;
import com.playposse.peertopeeroxygen.android.data.OxygenSharedPreferences;
import com.playposse.peertopeeroxygen.android.data.clientactions.MissionDataRetrieverClientAction;
import com.playposse.peertopeeroxygen.android.firebase.OxygenFirebaseMessagingService;
import com.playposse.peertopeeroxygen.android.util.StreamUtil;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.CompleteMissionDataBean;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

/**
 * Manager that loads and saves mission data. The {@link DataRepository} keeps the mission data
 * of the current  domain. This manager switches the right data into the {@link DataRepository} as
 * the user switches domains.
 */
public class MissionDataManager {

    private static final String LOG_CAT = MissionDataManager.class.getSimpleName();

    private static boolean isStale = true;

    /**
     * Pattern for saving mission data by domain into a JSON file on the local device.
     */
    private static final String FILE_PATTERN = "mission-data-%1$s.json";

    public static void switchToDomainAsync(
            final Long domainId,
            final Context context,
            DataService.LocalBinder localBinder) {

        Log.i(LOG_CAT, "switchToDomainAsync called with " + domainId);

        if (domainId == null) {
            return;
        }

        OxygenSharedPreferences.setCurrentDomain(context, domainId);
        OxygenSharedPreferences.addSubscribedDomainId(context, domainId);
        OxygenFirebaseMessagingService.subscribeToDomainTopic(domainId);

        if (getFile(context, domainId).exists()) {
            new LoadLocallyAsyncTask(domainId, context, localBinder).execute();
        } else {
            loadRemotelyAsync(domainId, context, localBinder);
        }
    }

    /**
     * Checks if the mission data is stale. Whenever an activity resumes, this method is called.
     * Firebase messages from the server tell the client that the cache is dirty. If it is dirty,
     * this method will retrieve new mission data from the server.
     */
    public static void checkStale(Context context, DataService.LocalBinder localBinder) {
        Log.i(LOG_CAT, "Checking if the mission cache is stale.");
        if ((localBinder != null) && isStale) {
            Log.i(LOG_CAT, "Initiating mission cache clearing");
            isStale = false;
            Long domainId = OxygenSharedPreferences.getCurrentDomainId(context);
            switchToDomainAsync(domainId, context, localBinder);
        }
    }

    public static void invalidate(Context context) {
        Log.i(LOG_CAT, "The mission cache is marked stale.");
        isStale = true;
        Long domainId = OxygenSharedPreferences.getCurrentDomainId(context);
        getFile(context, domainId).delete();
    }

    private static void loadRemotelyAsync(
            final Long domainId,
            final Context context,
            final DataService.LocalBinder localBinder) {

        Log.i(LOG_CAT, "Loading mission data remotely.");
        localBinder.getAllMissionData(
                domainId,
                new MissionDataRetrieverClientAction.Callback() {
                    @Override
                    public void onComplete(CompleteMissionDataBean completeMissionDataBean) {
                        switchToDomainSync(
                                domainId,
                                completeMissionDataBean,
                                true, context,
                                localBinder);
                    }
                });
    }

    private static void switchToDomainSync(
            Long domainId,
            CompleteMissionDataBean completeMissionDataBean,
            boolean shouldSave,
            Context context,
            DataService.LocalBinder localBinder) {

        // Note: This should be called on a worker thread!

        // Save the remotely fetched data to the device.
        if (shouldSave) {
            saveSync(domainId, completeMissionDataBean, context, localBinder);
        }

        // Update the mission data on the device and cause activities to re-render.
        localBinder.getDataRepository().setCompleteMissionDataBean(completeMissionDataBean);
        localBinder.makeDataReceivedCallbacks();
    }

    public static void saveSync(Context context, DataService.LocalBinder localBinder) {

        Long domainId = OxygenSharedPreferences.getCurrentDomainId(context);
        CompleteMissionDataBean completeMissionDataBean =
                localBinder.getDataRepository().getCompleteMissionDataBean();
        saveSync(domainId, completeMissionDataBean, context, localBinder);
    }

    private static void saveSync(
            Long domainId,
            CompleteMissionDataBean completeMissionDataBean,
            Context context,
            DataService.LocalBinder localBinder) {

        if (!domainId.equals(completeMissionDataBean.getUserBean().getDomainId())) {
            // Something went wrong. Throw away the local cache as a safety measure.
            getFile(context, domainId).delete();
            switchToDomainAsync(domainId, context, localBinder);
            return;
        }

        File file = getFile(context, domainId);
        try {
            StreamUtil.writeTextStream(file, toJson(completeMissionDataBean));
            Log.i(LOG_CAT, "Saved mission data to local cache.");
        } catch (Exception ex) {
            Log.e(LOG_CAT, "Failed to save mission data locally.", ex);

            // Let's delete the file. The data seems to be corrupt.
            file.delete();
        }
    }

    private static CompleteMissionDataBean fromJson(String json) throws IOException {
        Log.i(LOG_CAT, "fromJson: " + json);
        JsonObjectParser jsonParser = new JacksonFactory().createJsonObjectParser();
        return jsonParser.parseAndClose(new StringReader(json), CompleteMissionDataBean.class);
    }

    public static String toJson(CompleteMissionDataBean completeMissionDataBean)
            throws IOException {

        String json = completeMissionDataBean.getFactory().toString(completeMissionDataBean);
        Log.i(LOG_CAT, "toJson: " + json);
        return json;
    }

    private static File getFile(Context context, Long domainId) {
        String fileName = String.format(FILE_PATTERN, domainId);
        return new File(context.getCacheDir(), fileName);
    }


    /**
     * {@link AsyncTask} that loads the mission data from the local file system and falls back
     * to the cloud if it fails.
     */
    private static class LoadLocallyAsyncTask extends AsyncTask<Void, Void, Void> {

        private final DataService.LocalBinder localBinder;
        private final Context context;
        private final Long domainId;

        private LoadLocallyAsyncTask(
                Long domainId,
                Context context,
                DataService.LocalBinder localBinder) {

            this.localBinder = localBinder;
            this.context = context;
            this.domainId = domainId;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            File file = getFile(context, domainId);
            try {
                String json = StreamUtil.readTextStream(file);
                CompleteMissionDataBean completeMissionDataBean = fromJson(json);
                switchToDomainSync(
                        domainId,
                        completeMissionDataBean,
                        true, context,
                        localBinder);
                return null;
            } catch (Exception ex) {
                Log.e(LOG_CAT, "Failed to read complete mission data locally.", ex);

                // Delete the apparently corrupt file.
                file.delete();

                // Try to  fall back to remotely.
                loadRemotelyAsync(domainId, context, localBinder);
            }
            return null;
        }
    }
}
