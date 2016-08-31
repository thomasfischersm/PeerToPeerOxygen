package com.playposse.peertopeeroxygen.android.data.clientactions;

import java.io.IOException;

/**
 * A client action that invites a buddy to teach a mission.
 */
public class InviteBuddyToMissionAction extends ClientAction {

    private static final String LOG_CAT = InviteBuddyToMissionAction.class.getSimpleName();

    private final Long buddyId;
    private final Long missionLadderId;
    private final Long missionTreeId;
    private final Long missionId;

    public InviteBuddyToMissionAction(
            BinderForActions binder,
            Long buddyId,
            Long missionLadderId,
            Long missionTreeId,
            Long missionId) {

        super(binder, false);

        this.buddyId = buddyId;
        this.missionLadderId = missionLadderId;
        this.missionTreeId = missionTreeId;
        this.missionId = missionId;
    }

    @Override
    protected void executeAsync() throws IOException {
        getBinder().getApi().inviteBuddyToMission(
                getBinder().getSessionId(),
                buddyId,
                missionLadderId,
                missionTreeId,
                missionId).execute();
    }
}
