package com.playposse.peertopeeroxygen.android.firebase.actions;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

import com.playposse.peertopeeroxygen.android.data.DataRepository;
import com.playposse.peertopeeroxygen.android.data.DataServiceConnection;

/**
 * A base class for Firebase actions.
 */
public abstract class FirebaseAction {

    private final Context applicationContext;
    // TODO: Implement delay until this is populated.
    private final DataServiceConnection dataServiceConnection;

    public FirebaseAction(Context applicationContext, DataServiceConnection dataServiceConnection) {
        this.applicationContext = applicationContext;
        this.dataServiceConnection = dataServiceConnection;
    }

    protected Context getApplicationContext() {
        return applicationContext;
    }

    protected void startActivity(Intent intent) {
        getApplicationContext().startActivity(intent);
    }

    protected void sendToast(final String message) {
        final Context context = getApplicationContext();
        Handler h = new Handler(context.getMainLooper());
        h.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    protected DataRepository getDataRepository() {
        return dataServiceConnection.getLocalBinder().getDataRepository();
    }

    protected String getString(int resId) {
        return applicationContext.getString(resId);
    }

    protected void makeDataReceivedCallbacks() {
        dataServiceConnection.getLocalBinder().makeDataReceivedCallbacks();
    }
}
