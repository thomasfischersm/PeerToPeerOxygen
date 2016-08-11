package com.playposse.peertopeeroxygen.android.data;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.student.StudentMainActivity;

/**
 * An activity that connects to the {@link DataService}.
 */
public abstract class DataServiceParentActivity
        extends AppCompatActivity
        implements DataService.DataReceivedCallback {

    protected DataServiceConnection dataServiceConnection;

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = new Intent(this, DataService.class);
        dataServiceConnection = new DataServiceConnection(this);
        bindService(intent, dataServiceConnection, Context.BIND_AUTO_CREATE);
    }


    @Override
    protected void onStop() {
        super.onStop();

        unbindService(dataServiceConnection);
    }
}
