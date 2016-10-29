package com.playposse.peertopeeroxygen.android.data;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.widget.ImageView;

/**
 * An abstract {@link Fragment} that connects to the {@link DataService}.
 */
public abstract class DataServiceParentFragment
        extends Fragment
        implements DataReceivedCallback, DataServiceConnection.ServiceConnectionListener {

    protected DataServiceConnection dataServiceConnection;

    @Override
    public void onStart() {
        super.onStart();

        Intent intent = new Intent(getActivity(), DataService.class);
        dataServiceConnection = new DataServiceConnection(this, true);
        getActivity().bindService(intent, dataServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (dataServiceConnection != null) {
            CompleteMissionDataCache.checkStale(dataServiceConnection.getLocalBinder());
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        getActivity().unbindService(dataServiceConnection);
    }

    @Override
    public void runOnUiThread(Runnable action) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(action);
        }
    }

    @Override
    public void onServiceConnected() {
        // Nothing to do.
    }
}
