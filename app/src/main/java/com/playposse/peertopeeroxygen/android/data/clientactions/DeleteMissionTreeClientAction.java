package com.playposse.peertopeeroxygen.android.data.clientactions;

import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionLadderBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionTreeBean;

import java.io.IOException;

/**
 * A client action that deletes a mission tree.
 */
public class DeleteMissionTreeClientAction extends ApiClientAction {

    private static final String LOG_CAT = DeleteMissionTreeClientAction.class.getSimpleName();

    private final Long missionLadderId;
    private final Long missionTreeId;

    public DeleteMissionTreeClientAction(
            BinderForActions binder,
            Long missionLadderId,
            Long missionTreeId) {

        super(binder, true);

        this.missionLadderId = missionLadderId;
        this.missionTreeId = missionTreeId;
    }

    @Override
    protected void preExecute() {
        MissionLadderBean missionLadderBean =
                getDataRepository().getMissionLadderBean(missionLadderId);
        MissionTreeBean missionTreeBean =
                getDataRepository().getMissionTreeBean(missionLadderId, missionTreeId);
        missionLadderBean.getMissionTreeBeans().remove(missionTreeBean);

        // Fix level of other mission trees.
        int level = 1;
        for (MissionTreeBean otherMissionTreeBean : missionLadderBean.getMissionTreeBeans()) {
            otherMissionTreeBean.setLevel(level++);
        }
    }

    @Override
    public void executeAsync() throws IOException {
        getBinder().getApi()
                .deleteMissionTree(getBinder().getSessionId(), missionLadderId, missionTreeId)
                .execute();
    }
}
