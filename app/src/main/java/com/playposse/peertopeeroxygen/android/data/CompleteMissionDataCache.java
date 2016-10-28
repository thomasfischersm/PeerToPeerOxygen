package com.playposse.peertopeeroxygen.android.data;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.playposse.peertopeeroxygen.android.data.clientactions.BinderForActions;
import com.playposse.peertopeeroxygen.android.data.clientactions.MissionDataRetrieverClientAction;
import com.playposse.peertopeeroxygen.android.util.StreamUtil;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.CompleteMissionDataBean;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

/**
 * Stores the complete mission data on the device and retrieves it.
 */
public class CompleteMissionDataCache {

    private static final String LOG_CAT = CompleteMissionDataCache.class.getSimpleName();

    private static final String CACHE_PATH = "complete_mission_data.json";

    private static boolean isStale = true;

    private static boolean isLocalCacheAvailable(Context context) {
        File cacheFile = getCacheFile(context);
        Log.i(LOG_CAT, "Local cache file is: " + cacheFile.getAbsolutePath());
        return cacheFile.exists();
    }

    public static void getCompleteMissionDataBean(
            BinderForActions binderForActions,
            Context context) {

        isStale = false;
        if (isLocalCacheAvailable(context)) {
            getLocally(binderForActions, context);
        } else {
            getRemotely(binderForActions, context);
        }
    }

    private static void getLocally(BinderForActions binderForActions, Context context) {
        new LoadLocallyAsyncTask(binderForActions, context).execute();
    }

    private static void getRemotely(BinderForActions binderForActions, Context context) {
        LoadRemotelyCallback callback = new LoadRemotelyCallback(context);
        new MissionDataRetrieverClientAction(binderForActions, callback).execute();
    }

    private static File getCacheFile(Context context) {
        return new File(context.getCacheDir(), CACHE_PATH);
    }

    private static void saveLocally(
            Context context,
            CompleteMissionDataBean completeMissionDataBean) {

        try {
            StreamUtil.writeTextStream(getCacheFile(context), toJson(completeMissionDataBean));
            Log.i(LOG_CAT, "Saved mission data to local cache.");
        } catch (Exception ex) {
            Log.e(LOG_CAT, "Failed to save mission data locally.", ex);

            // Let's delete the file. The data seems to be corrupt.
            deleteCache(context);
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

    private static void deleteCache(Context context) {
        Log.i(LOG_CAT, "Mission cache has been deleted.");
        getCacheFile(context).delete();
    }

    /**
     * Checks if the mission data is stale. Whenever an activity resumes, this method is called.
     * Firebase messages from the server tell the client that the cache is dirty. If it is dirty,
     * this method will retrieve new mission data from the server.
     */
    public static void checkStale(DataService.LocalBinder localBinder) {
        Log.i(LOG_CAT, "Checking if the mission cache is stale.");
        if ((localBinder != null) && isStale) {
            Log.i(LOG_CAT, "Initiating mission cache clearing");
            isStale = false;
            localBinder.reload();
        }
    }

    public static void invalidate() {
        Log.i(LOG_CAT, "The mission cache is marked stale.");
        isStale = true;
    }

    /**
     * Save the cache to the device after the data has been changed, e.g. the student graduated
     * from a mission.
     */
    public static void save(Context context, DataRepository dataRepository) {
        new SaveLocallyAsyncTask(context, dataRepository.getCompleteMissionDataBean()).execute();
    }

    /**
     * {@link AsyncTask} that loads the mission data from the local file system and falls back
     * to the could if it fails.
     */
    private static class LoadLocallyAsyncTask extends AsyncTask<Void, Void, CompleteMissionDataBean> {

        private final BinderForActions binderForActions;
        private final Context context;

        public LoadLocallyAsyncTask(BinderForActions binderForActions, Context context) {
            this.binderForActions = binderForActions;
            this.context = context;
        }

        @Override
        protected CompleteMissionDataBean doInBackground(Void... voids) {
            try {
                String json = StreamUtil.readTextStream(getCacheFile(context));
                return fromJson(json);
            } catch (Exception ex) {
                Log.e(LOG_CAT, "Failed to read complete mission data locally.", ex);

                // Delete the apparently corrupt file.
                deleteCache(context);

                // Try to  fall back to remotely.
                getRemotely(binderForActions, context);
            }
            return null;
        }

        @Override
        protected void onPostExecute(CompleteMissionDataBean completeMissionDataBean) {
            if (completeMissionDataBean != null) {
                binderForActions.getDataRepository()
                        .setCompleteMissionDataBean(completeMissionDataBean);
                binderForActions.makeDataReceivedCallbacks();
                Log.i(LOG_CAT, "Succeeded loading mission data locally.");
            }
        }
    }

    /**
     * {@link AsyncTask} that saves the mission data locally.
     */
    private static class SaveLocallyAsyncTask extends AsyncTask<Void, Void, Void> {

        private final Context context;
        private final CompleteMissionDataBean completeMissionDataBean;

        private SaveLocallyAsyncTask(
                Context context,
                CompleteMissionDataBean completeMissionDataBean) {

            this.context = context;
            this.completeMissionDataBean = completeMissionDataBean;
        }


        @Override
        protected Void doInBackground(Void... voids) {
            saveLocally(context, completeMissionDataBean);
            return null;
        }
    }

    /**
     * Callback that the data retrieval action calls when the cloud has returned the mission data.
     */
    public static class LoadRemotelyCallback
            implements MissionDataRetrieverClientAction.MissionDataRetrieverCallback {

        private final Context context;

        public LoadRemotelyCallback(Context context) {
            this.context = context;
        }

        @Override
        public void onComplete(CompleteMissionDataBean completeMissionDataBean) {
            new SaveLocallyAsyncTask(context, completeMissionDataBean).execute();
        }
    }
}
