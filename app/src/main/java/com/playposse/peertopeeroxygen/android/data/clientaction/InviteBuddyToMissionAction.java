package com.playposse.peertopeeroxygen.android.data.clientaction;

import android.util.Log;

import java.io.IOException;

/**
 * A client action that invites a buddy to teach a mission.
 */
public class InviteBuddyToMissionAction extends ClientAction {

    private static final String LOG_CAT = InviteBuddyToMissionAction.class.getSimpleName();

    public InviteBuddyToMissionAction(BinderForActions binder) {
        super(binder);
    }

    public void inviteBuddyToMission(
            final Long buddyId,
            final Long missionLadderId,
            final Long missionTreeId,
            final Long missionId) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    getBinder().getApi().inviteBuddyToMission(
                            getBinder().getSessionId(),
                            buddyId,
                            missionLadderId,
                            missionTreeId,
                            missionId).execute();
                    Log.i(LOG_CAT, "Buddy has been invited via appengine call.");
                } catch (IOException ex) {
                    Log.e(LOG_CAT, "Failed to invite buddy to mission: " + buddyId, ex);
                }
            }
        }).start();
    }
}
