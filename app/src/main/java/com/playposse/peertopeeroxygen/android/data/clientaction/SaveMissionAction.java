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

    private final Long missionLadderId;
    private final Long missionTreeId;
    private final MissionBean missionBean;

    public SaveMissionAction(
            BinderForActions binder,
            Long missionLadderId,
            Long missionTreeId,
            MissionBean missionBean) {

        super(binder, true);

        this.missionLadderId = missionLadderId;
        this.missionTreeId = missionTreeId;
        this.missionBean = missionBean;
    }

    @Override
    protected void executeAsync() throws IOException {
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

        Log.i(LOG_CAT, "Mission has been saved.");
    }
}
