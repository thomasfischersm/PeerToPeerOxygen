package com.playposse.peertopeeroxygen.android.data.clientactions;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.playposse.peertopeeroxygen.android.data.DataRepository;

import java.io.IOException;

/**
 * A base class for client actions to implement. It provides useful methods.
 */
public abstract class ClientAction {

    private static final String LOG_CAT = ClientAction.class.getSimpleName();

    private final BinderForActions binder;
    private final boolean notifyDataReceivedCallbacks;

    public ClientAction(BinderForActions binder, boolean notifyDataReceivedCallbacks) {
        this.binder = binder;
        this.notifyDataReceivedCallbacks = notifyDataReceivedCallbacks;
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
    protected void executeAsync() throws IOException {
    }

    /**
     * Child classes can override this to execute code on the UI thread after calling the server.
     */
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
        }
    }
}
