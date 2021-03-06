package com.playposse.peertopeeroxygen.android.data.clientactions;

import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionTreeBean;

import java.io.IOException;

/**
 * A client action that deletes a mission.
 */
public class DeleteMissionClientAction extends ApiClientAction {

    private static final String LOG_CAT = DeleteMissionClientAction.class.getSimpleName();

    private final Long missionLadderId;
    private final Long missionTreeId;
    private final Long missionId;

    public DeleteMissionClientAction(
            BinderForActions binder,
            Long missionLadderId,
            Long missionTreeId,
            Long missionId) {
        super(binder, true);

        this.missionLadderId = missionLadderId;
        this.missionTreeId = missionTreeId;
        this.missionId = missionId;
    }

    public void executeAsync() throws IOException {
        MissionTreeBean missionTreeBean =
                getDataRepository().getMissionTreeBean(missionLadderId, missionTreeId);
        MissionBean missionBean =
                getDataRepository().getMissionBean(missionLadderId, missionTreeId, missionId);
        missionTreeBean.getMissionBeans().remove(missionBean);

        getApi()
                .deleteMission(
                        getSessionId(),
                        missionLadderId,
                        missionTreeId,
                        missionId,
                        getDomainId())
                .execute();
    }
}
