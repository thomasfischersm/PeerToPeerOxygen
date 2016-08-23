package com.playposse.peertopeeroxygen.android.data.clientaction;

import android.content.Context;

import com.playposse.peertopeeroxygen.android.data.DataRepository;
import com.playposse.peertopeeroxygen.android.data.DataService;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.PeerToPeerOxygenApi;

/**
 * An interface that client actions can use to make calls back to the {@link DataService.LocalBinder}.
 */
public interface BinderForActions {

    PeerToPeerOxygenApi getApi();
    Context getApplicationContext();
    Long getSessionId();
    DataRepository getDataRepository();
    void makeDataReceivedCallbacks();
    void redirectToLoginActivity();
}
