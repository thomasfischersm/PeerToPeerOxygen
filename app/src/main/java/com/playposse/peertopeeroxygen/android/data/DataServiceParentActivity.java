package com.playposse.peertopeeroxygen.android.data;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

/**
 * An activity that connects to the {@link DataService}.
 */
public abstract class DataServiceParentActivity
        extends AppCompatActivity
        implements DataService.DataReceivedCallback {

    protected DataServiceConnection dataServiceConnection;
    protected boolean shouldAutoInit = true;

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = new Intent(this, DataService.class);
        dataServiceConnection = new DataServiceConnection(this, shouldAutoInit);
        bindService(intent, dataServiceConnection, Context.BIND_AUTO_CREATE);
    }


    @Override
    protected void onStop() {
        super.onStop();

        unbindService(dataServiceConnection);
    }
}
