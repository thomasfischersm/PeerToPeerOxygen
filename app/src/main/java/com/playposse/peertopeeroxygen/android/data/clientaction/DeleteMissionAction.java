package com.playposse.peertopeeroxygen.android.data.clientaction;

import android.util.Log;

import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionTreeBean;

import java.io.IOException;

/**
 * A client action that deletes a mission.
 */
public class DeleteMissionAction extends ClientAction {

    private static final String LOG_CAT = DeleteMissionAction.class.getSimpleName();

    public DeleteMissionAction(BinderForActions binder) {
        super(binder);
    }

    public void deleteMission(
            final Long missionLadderId,
            final Long missionTreeId,
            final Long missionId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MissionTreeBean missionTreeBean =
                            getDataRepository().getMissionTreeBean(missionLadderId, missionTreeId);
                    MissionBean missionBean =
                            getDataRepository().getMissionBean(missionLadderId, missionTreeId, missionId);
                    missionTreeBean.getMissionBeans().remove(missionBean);

                    getBinder().getApi()
                            .deleteMission(
                                    getBinder().getSessionId(),
                                    missionLadderId,
                                    missionTreeId,
                                    missionId)
                            .execute();

                    getBinder().makeDataReceivedCallbacks();
                } catch (IOException ex) {
                    Log.e(LOG_CAT, "Failed to delete mission.", ex);
                    getBinder().redirectToLoginActivity();
                }
            }
        }).start();
    }
}
