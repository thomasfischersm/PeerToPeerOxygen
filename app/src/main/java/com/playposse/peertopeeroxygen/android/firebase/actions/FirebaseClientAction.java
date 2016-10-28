package com.playposse.peertopeeroxygen.android.firebase.actions;

import android.content.Context;
import android.content.Intent;

import com.google.firebase.messaging.RemoteMessage;
import com.playposse.peertopeeroxygen.android.data.DataRepository;
import com.playposse.peertopeeroxygen.android.data.DataService;
import com.playposse.peertopeeroxygen.android.data.DataServiceConnection;
import com.playposse.peertopeeroxygen.android.util.ToastUtil;

/**
 * A base class for Firebase actions.
 */
public abstract class FirebaseClientAction {

    private final RemoteMessage remoteMessage;

    private Context applicationContext;
    private DataServiceConnection dataServiceConnection;

    public FirebaseClientAction(RemoteMessage remoteMessage) {
        this.remoteMessage = remoteMessage;
    }

    protected abstract void execute(RemoteMessage remoteMessage);

    public void execute(Context applicationContext, DataServiceConnection dataServiceConnection) {
        this.applicationContext = applicationContext;
        this.dataServiceConnection = dataServiceConnection;

        execute(remoteMessage);
    }

    protected Context getApplicationContext() {
        return applicationContext;
    }

    protected void startActivity(Intent intent) {
        getApplicationContext().startActivity(intent);
    }

    protected void sendToast(String message) {
        ToastUtil.sendToast(getApplicationContext(), message);
    }

    protected DataRepository getDataRepository() {
        return dataServiceConnection.getLocalBinder().getDataRepository();
    }

    protected DataService.LocalBinder getLocalBinder() {
        return dataServiceConnection.getLocalBinder();
    }

    protected String getString(int resId) {
        return applicationContext.getString(resId);
    }

    protected void makeDataReceivedCallbacks() {
        dataServiceConnection.getLocalBinder().makeDataReceivedCallbacks();
    }
}
