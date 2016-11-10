package com.playposse.peertopeeroxygen.android.data.clientactions;

import android.util.Log;

import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionLadderBean;

import java.io.IOException;

/**
 * A client action that deletes a mission ladder.
 */
public class DeleteMissionLadderClientAction extends ApiClientAction {

    private static final String LOG_CAT = DeleteMissionLadderClientAction.class.getSimpleName();

    private final Long missionLadderId;

    public DeleteMissionLadderClientAction(BinderForActions binder, Long missionLadderId) {
        super(binder, true);

        this.missionLadderId = missionLadderId;
    }

    @Override
    protected void executeAsync() throws IOException {
        getApi()
                .deleteMissionLadder(getSessionId(), missionLadderId, getDomainId())
                .execute();
        MissionLadderBean missionLadderBean =
                getDataRepository().getMissionLadderBean(missionLadderId);
        getBinder()
                .getDataRepository()
                .getMissionLadderBeans()
                .remove(missionLadderBean);
        Log.i(LOG_CAT, "Completed deleting mission ladder: " + missionLadderId);
    }
}
