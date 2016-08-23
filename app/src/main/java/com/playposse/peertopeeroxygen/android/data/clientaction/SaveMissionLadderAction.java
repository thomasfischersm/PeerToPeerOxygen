package com.playposse.peertopeeroxygen.android.data.clientaction;

import android.util.Log;

import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionLadderBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionTreeBean;

import java.io.IOException;
import java.util.ArrayList;

/**
 * A client action that saves a mission ladder.
 */
public class SaveMissionLadderAction extends ClientAction {

    private static final String LOG_CAT = SaveMissionLadderAction.class.getSimpleName();

    public SaveMissionLadderAction(BinderForActions binder) {
        super(binder);
    }

    public void save(final MissionLadderBean missionLadderBean) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    boolean create = missionLadderBean.getId() == null;
                    MissionLadderBean result =
                            getBinder().getApi()
                                    .saveMissionLadder(getBinder().getSessionId(), missionLadderBean)
                                    .execute();
                    missionLadderBean.setId(result.getId()); // Save id for new entities.

                    // Update local data to avoid reloading data from the server.
                    if (create) {
                        missionLadderBean.setMissionTreeBeans(new ArrayList<MissionTreeBean>());
                        getDataRepository()
                                .getCompleteMissionDataBean()
                                .getMissionLadderBeans()
                                .add(missionLadderBean);
                    }

                    getBinder().makeDataReceivedCallbacks();

                    Log.i(LOG_CAT, "Mission ladder has been saved.");
                } catch (IOException ex) {
                    ex.printStackTrace();
                    getBinder().redirectToLoginActivity();
                }
            }
        }).start();
    }
}
