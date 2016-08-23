package com.playposse.peertopeeroxygen.android.data.clientaction;

import android.util.Log;

import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionLadderBean;

import java.io.IOException;

/**
 * A client action that deletes a mission ladder.
 */
public class DeleteMissionLadderAction extends ClientAction {

    private static final String LOG_CAT = DeleteMissionLadderAction.class.getSimpleName();

    public DeleteMissionLadderAction(BinderForActions binder) {
        super(binder);
    }

    public void deleteMissionLadder(final Long missionLadderId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    getBinder().getApi()
                            .deleteMissionLadder(getBinder().getSessionId(), missionLadderId)
                            .execute();
                    MissionLadderBean missionLadderBean =
                            getBinder().getMissionLadderBean(missionLadderId);
                    getBinder()
                            .getCompleteMissionDataBean()
                            .getMissionLadderBeans()
                            .remove(missionLadderBean);

                    getBinder().makeDataReceivedCallbacks();
                    Log.i(LOG_CAT, "Completed deleting mission ladder: " + missionLadderId);
                } catch (IOException ex) {
                    Log.e(LOG_CAT, "Failed to delete mission ladder.", ex);
                    getBinder().redirectToLoginActivity();
                }
            }
        }).start();
    }
}
