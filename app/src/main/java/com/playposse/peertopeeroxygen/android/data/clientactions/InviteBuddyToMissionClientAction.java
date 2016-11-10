package com.playposse.peertopeeroxygen.android.data.clientactions;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.util.ToastUtil;

import java.io.IOException;

/**
 * A client action that invites a buddy to teach a mission.
 */
public class InviteBuddyToMissionClientAction extends ApiClientAction {

    private static final String LOG_CAT = InviteBuddyToMissionClientAction.class.getSimpleName();

    private final Long buddyId;
    private final Long missionLadderId;
    private final Long missionTreeId;
    private final Long missionId;

    public InviteBuddyToMissionClientAction(
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
        try {
            getApi()
                    .inviteBuddyToMission(
                            getSessionId(),
                            buddyId,
                            missionLadderId,
                            missionTreeId,
                            missionId,
                            getDomainId())
                    .execute();
        } catch (GoogleJsonResponseException ex) {
            if (ex.getStatusCode() == 403) {
                // A server check determined that the buddy isn't ready to teach. Show the user a
                // toast about this.
                String msg = String.format(
                        getContext().getString(R.string.mentor_not_ready_toast),
                        ex.getDetails().getMessage());
                ToastUtil.sendToast(getContext(), msg);
            } else {
                // The error is something else. Throw it back.
                throw ex;
            }
        }
    }
}
