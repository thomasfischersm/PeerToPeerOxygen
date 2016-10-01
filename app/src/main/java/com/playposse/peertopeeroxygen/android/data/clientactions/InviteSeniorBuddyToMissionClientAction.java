package com.playposse.peertopeeroxygen.android.data.clientactions;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.util.ToastUtil;

import java.io.IOException;

/**
 * A client action that invites a senior buddy to supervise the teaching of a mission.
 */
public class InviteSeniorBuddyToMissionClientAction extends ApiClientAction {

    private static final String LOG_CAT = InviteSeniorBuddyToMissionClientAction.class.getSimpleName();

    private final Long studentId;
    private final Long seniorBuddyId;
    private final Long missionLadderId;
    private final Long missionTreeId;
    private final Long missionId;

    public InviteSeniorBuddyToMissionClientAction(
            BinderForActions binder,
            Long studentId,
            Long seniorBuddyId,
            Long missionLadderId,
            Long missionTreeId,
            Long missionId) {

        super(binder, false);

        this.studentId = studentId;
        this.seniorBuddyId = seniorBuddyId;
        this.missionLadderId = missionLadderId;
        this.missionTreeId = missionTreeId;
        this.missionId = missionId;
    }

    @Override
    protected void executeAsync() throws IOException {
        try {
            getBinder().getApi().inviteSeniorBuddyToMission(
                    getBinder().getSessionId(),
                    studentId,
                    seniorBuddyId,
                    missionLadderId,
                    missionTreeId,
                    missionId).execute();
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
