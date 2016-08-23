package com.playposse.peertopeeroxygen.android.data.clientaction;

import android.content.Context;

import com.playposse.peertopeeroxygen.android.data.DataService;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.PeerToPeerOxygenApi;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.CompleteMissionDataBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionCompletionBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionLadderBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionTreeBean;

/**
 * An interface that client actions can use to make calls back to the {@link DataService.LocalBinder}.
 */
public interface BinderForActions {

    PeerToPeerOxygenApi getApi();
    Context getApplicationContext();
    Long getSessionId();
    CompleteMissionDataBean getCompleteMissionDataBean();
    void makeDataReceivedCallbacks();
    void redirectToLoginActivity();

    MissionLadderBean getMissionLadderBean(Long id);
    MissionTreeBean getMissionTreeBean(Long missionLadderId, Long missionTreeId);
    MissionBean getMissionBean(Long missionLadderId, Long missionTreeId, Long missionId);
    MissionCompletionBean getMissionCompletion(Long missionId);
}
