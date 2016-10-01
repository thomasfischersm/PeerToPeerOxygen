package com.playposse.peertopeeroxygen.android.data.clientactions;

import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.PeerToPeerOxygenApi;

import java.io.IOException;

/**
 * A client action that allows an admin to add points to a students account.
 */
public class AddPointsByAdminClientAction extends ApiClientAction {

    private final Long sessionId;
    private final Long studentId;
    private final String pointType;
    private final int addedPoints;

    public AddPointsByAdminClientAction(
            BinderForActions binder,
            Long sessionId,
            Long studentId,
            String pointType,
            int addedPoints) {

        super(binder, false);

        this.sessionId = sessionId;
        this.studentId = studentId;
        this.pointType = pointType;
        this.addedPoints = addedPoints;
    }

    @Override
    protected void executeAsync() throws IOException {
        PeerToPeerOxygenApi api = getBinder().getApi();
        api.addPointsByAdmin(sessionId, studentId, pointType, addedPoints).execute();
    }
}
