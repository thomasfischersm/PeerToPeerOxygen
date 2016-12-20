package com.playposse.peertopeeroxygen.android.data.clientactions;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import android.util.Log;

import com.playposse.peertopeeroxygen.android.data.DataRepository;
import com.playposse.peertopeeroxygen.android.data.OxygenSharedPreferences;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.PeerToPeerOxygenApi;

import java.io.IOException;

import javax.annotation.Nullable;

/**
 * A base class for client actions to implement. It provides useful methods.
 */
public abstract class ApiClientAction {

    private static final String LOG_CAT = ApiClientAction.class.getSimpleName();

    private final BinderForActions binder;
    private final boolean notifyDataReceivedCallbacks;
    @Nullable private final CompletionCallback completionCallback;

    public ApiClientAction(BinderForActions binder, boolean notifyDataReceivedCallbacks) {

        this(binder, notifyDataReceivedCallbacks, null);
    }
    public ApiClientAction(
            BinderForActions binder,
            boolean notifyDataReceivedCallbacks,
            @Nullable CompletionCallback completionCallback) {

        this.binder = binder;
        this.notifyDataReceivedCallbacks = notifyDataReceivedCallbacks;
        this.completionCallback = completionCallback;
    }

    protected BinderForActions getBinder() {
        return binder;
    }

    protected DataRepository getDataRepository() {
        return binder.getDataRepository();
    }

    protected Context getContext() {
        return binder.getApplicationContext();
    }

    protected PeerToPeerOxygenApi getApi() {
        return getBinder().getApi();
    }

    protected Long getSessionId() {
        return getBinder().getSessionId();
    }

    protected Long getDomainId() {
        Long currentDomainId = OxygenSharedPreferences.getCurrentDomainId(getContext());
        Log.i(LOG_CAT, "Got domain to send to the endpoint: " + currentDomainId);
        return currentDomainId;
    }

    public final void execute() {
        new ClientActionAsyncTask().execute();
    }

    /**
     * Child classes can override this to execute code on the UI thread before calling the server.
     */
    protected void preExecute() {
    }

    /**
     * Child classes can override this to execute methods in a separate thread.
     */
    @WorkerThread
    protected abstract void executeAsync() throws IOException;

    /**
     * Child classes can override this to execute code on the UI thread after calling the server.
     */
    @UiThread
    protected void postExecute() {
    }

    /**
     * An {@link AsyncTask} to deal with the threading.
     */
    private class ClientActionAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            preExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                executeAsync();
            } catch (IOException ex) {
                Log.e(LOG_CAT, "Failed to execute: " + this.getClass().getName(), ex);
                binder.redirectToLoginActivity();
            }

            if (notifyDataReceivedCallbacks) {
                binder.makeDataReceivedCallbacks();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            postExecute();

            if (completionCallback != null) {
                completionCallback.onComplete();
            }
        }
    }

    /**
     * An optional interface that is called when the {@link ApiClientAction} has completed.
     */
    public interface CompletionCallback {

        void onComplete();
    }
}
