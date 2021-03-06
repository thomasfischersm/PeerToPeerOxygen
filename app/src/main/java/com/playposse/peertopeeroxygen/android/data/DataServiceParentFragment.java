package com.playposse.peertopeeroxygen.android.data;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.widget.ImageView;

import com.playposse.peertopeeroxygen.android.data.missions.MissionDataManager;

/**
 * An abstract {@link Fragment} that connects to the {@link DataService}.
 */
public abstract class DataServiceParentFragment
        extends Fragment
        implements DataReceivedCallback, DataServiceConnection.ServiceConnectionListener {

    protected DataServiceConnection dataServiceConnection;

    @Override
    public void onResume() {
        super.onResume();

        Intent intent = new Intent(getActivity(), DataService.class);
        dataServiceConnection = new DataServiceConnection(this, true);
        getActivity().bindService(intent, dataServiceConnection, Context.BIND_AUTO_CREATE);

        if (dataServiceConnection != null) {
            MissionDataManager.checkStale(getContext(), dataServiceConnection.getLocalBinder());
        }
    }

    @Override
    public void onPause() {
        super.onPause();

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
