package com.playposse.peertopeeroxygen.android.data.clientactions;

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

    public final void execute() {
        preExecute();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    executeAsync();
                } catch (IOException ex) {
                    Log.e(LOG_CAT, "Failed to execute: " + this.getClass().getName(), ex);
                    binder.redirectToLoginActivity();
                }

                if (notifyDataReceivedCallbacks) {
                    binder.makeDataReceivedCallbacks();
                }
            }
        }).start();

        // TODO: Figure out postExecute.
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
}
