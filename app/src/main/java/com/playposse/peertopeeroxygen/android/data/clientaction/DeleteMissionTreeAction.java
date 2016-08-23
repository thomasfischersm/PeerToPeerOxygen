package com.playposse.peertopeeroxygen.android.data.clientaction;

import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionLadderBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionTreeBean;

import java.io.IOException;

/**
 * A client action that deletes a mission tree.
 */
public class DeleteMissionTreeAction extends ClientAction {

    private static final String LOG_CAT = DeleteMissionTreeAction.class.getSimpleName();

    private final Long missionLadderId;
    private final Long missionTreeId;

    public DeleteMissionTreeAction(
            BinderForActions binder,
            Long missionLadderId,
            Long missionTreeId) {

        super(binder, true);

        this.missionLadderId = missionLadderId;
        this.missionTreeId = missionTreeId;
    }

    @Override
    public void executeAsync() throws IOException {
        MissionLadderBean missionLadderBean =
                getDataRepository().getMissionLadderBean(missionLadderId);
        MissionTreeBean missionTreeBean =
                getDataRepository().getMissionTreeBean(missionLadderId, missionTreeId);
        missionLadderBean.getMissionTreeBeans().remove(missionTreeBean);
        getBinder().getApi()
                .saveMissionLadder(getBinder().getSessionId(), missionLadderBean)
                .execute();
    }
}
