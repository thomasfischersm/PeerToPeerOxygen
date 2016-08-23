package com.playposse.peertopeeroxygen.android.data.clientaction;

import android.util.Log;

import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionTreeBean;

import java.io.IOException;

/**
 * A client action that saves a mission.
 */
public class SaveMissionAction extends ClientAction {

    private static final String LOG_CAT = SaveMissionTreeAction.class.getSimpleName();

    public SaveMissionAction(BinderForActions binder) {
        super(binder);
    }

    public void save(
            final Long missionLadderId,
            final Long missionTreeId,
            final MissionBean missionBean) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    boolean create = missionBean.getId() == null;

                    MissionBean result = getBinder().getApi().saveMission(
                            getBinder().getSessionId(),
                            missionLadderId,
                            missionTreeId,
                            missionBean)
                            .execute();
                    missionBean.setId(result.getId());

                    if (create) {
                        MissionTreeBean missionTreeBean =
                                getDataRepository().getMissionTreeBean(missionLadderId, missionTreeId);
                        missionTreeBean.getMissionBeans().add(missionBean);
                    }

                    getBinder().makeDataReceivedCallbacks();

                    Log.i(LOG_CAT, "Mission has been saved.");
                } catch (IOException ex) {
                    Log.e(LOG_CAT, "Failed to save mission bean.", ex);
                    getBinder().redirectToLoginActivity();
                }
            }
        }).start();
    }
}
