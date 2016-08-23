package com.playposse.peertopeeroxygen.android.data.clientaction;

import android.util.Log;

import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionLadderBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionTreeBean;

import java.io.IOException;

/**
 * A client action that deletes a mission tree.
 */
public class DeleteMissionTreeAction extends ClientAction {

    private static final String LOG_CAT = DeleteMissionTreeAction.class.getSimpleName();

    public DeleteMissionTreeAction(BinderForActions binder) {
        super(binder);
    }

    public void deleteMissionTree(final Long missionLadderId, final Long missionTreeId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MissionLadderBean missionLadderBean = getBinder().getMissionLadderBean(missionLadderId);
                    MissionTreeBean missionTreeBean = getBinder().getMissionTreeBean(missionLadderId, missionTreeId);
                    missionLadderBean.getMissionTreeBeans().remove(missionTreeBean);
                    getBinder().getApi()
                            .saveMissionLadder(getBinder().getSessionId(), missionLadderBean)
                            .execute();

                    getBinder().makeDataReceivedCallbacks();
                } catch (IOException ex) {
                    Log.e(LOG_CAT, "Failed to delete mission tree.", ex);
                    getBinder().redirectToLoginActivity();
                }
            }
        }).start();
    }
}
