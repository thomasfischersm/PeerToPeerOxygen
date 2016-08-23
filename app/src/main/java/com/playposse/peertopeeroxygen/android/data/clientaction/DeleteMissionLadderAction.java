package com.playposse.peertopeeroxygen.android.data.clientaction;

import android.util.Log;

import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionLadderBean;

import java.io.IOException;

/**
 * A client action that deletes a mission ladder.
 */
public class DeleteMissionLadderAction extends ClientAction {

    private static final String LOG_CAT = DeleteMissionLadderAction.class.getSimpleName();

    private final Long missionLadderId;

    public DeleteMissionLadderAction(BinderForActions binder, Long missionLadderId) {
        super(binder, true);

        this.missionLadderId = missionLadderId;
    }

    @Override
    protected void executeAsync() throws IOException {
        getBinder().getApi()
                .deleteMissionLadder(getBinder().getSessionId(), missionLadderId)
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
