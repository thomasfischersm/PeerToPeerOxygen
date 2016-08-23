package com.playposse.peertopeeroxygen.android.data.clientaction;

import android.util.Log;

import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionCompletionBean;

import java.io.IOException;

/**
 * An action that a buddy uses to notify the server and the student that a mission has been
 * completed.
 */
public class ReportMissionCompleteAction extends ClientAction {

    private static final String LOG_CAT = ReportMissionCompleteAction.class.getSimpleName();

    public ReportMissionCompleteAction(BinderForActions binder) {
        super(binder);
    }

    public void reportMissionComplete(final Long studentId, final Long missionId) {
        // Increment local data to avoid getting fresh data from the server.
        MissionCompletionBean completionBean = getDataRepository().getMissionCompletion(missionId);
        completionBean.setStudyCount(completionBean.getStudyCount() + 1);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    getBinder().getApi().reportMissionComplete(
                            getBinder().getSessionId(),
                            studentId,
                            missionId).execute();
                } catch (IOException ex) {
                    Log.e(LOG_CAT, "Failed to report mission completed.", ex);
                }
            }
        }).start();
    }
}
