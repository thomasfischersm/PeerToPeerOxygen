package com.playposse.peertopeeroxygen.android.data.clientaction;

import android.util.Log;

import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionLadderBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionTreeBean;

import java.io.IOException;
import java.util.ArrayList;

/**
 * A client action that saves the mission tree.
 */
public class SaveMissionTreeAction extends ClientAction {

    private static final String LOG_CAT = SaveMissionTreeAction.class.getSimpleName();

    public SaveMissionTreeAction(BinderForActions binder) {
        super(binder);
    }

    public void save(final Long missionLadderId, final MissionTreeBean missionTreeBean) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    boolean create = missionTreeBean.getId() == null;
                    MissionTreeBean result =
                            getBinder().getApi()
                                    .saveMissionTree(
                                            getBinder().getSessionId(),
                                            missionLadderId,
                                            missionTreeBean)
                                    .execute();
                    missionTreeBean.setId(result.getId()); // Save id for new entities.

                    // Update local data to avoid reloading data from the server.
                    if (create) {
                        MissionLadderBean missionLadderBean =
                                getDataRepository().getMissionLadderBean(missionLadderId);
                        missionTreeBean.setMissionBeans(new ArrayList<MissionBean>());
                        missionLadderBean.getMissionTreeBeans().add(missionTreeBean);
                    }

                    getBinder().makeDataReceivedCallbacks();

                    Log.i(LOG_CAT, "Mission tree has been saved.");
                } catch (IOException ex) {
                    Log.e(LOG_CAT, "Failed to create new mission tree.", ex);
                    getBinder().redirectToLoginActivity();
                }
            }
        }).start();
    }
}
