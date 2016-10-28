package com.playposse.peertopeeroxygen.android.data.clientactions;

import android.util.Log;

import com.playposse.peertopeeroxygen.android.data.DataRepository;
import com.playposse.peertopeeroxygen.android.data.types.PointType;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionCompletionBean;

import java.io.IOException;

/**
 * An action that a buddy uses to notify the server and the student that a mission has been
 * completed.
 */
public class ReportMissionCompleteClientAction extends ApiClientAction {

    private static final String LOG_CAT = ReportMissionCompleteClientAction.class.getSimpleName();

    private final Long studentId;
    private final Long missionId;

    public ReportMissionCompleteClientAction(BinderForActions binder, Long studentId, Long missionId) {
        super(binder, true);

        this.studentId = studentId;
        this.missionId = missionId;
    }

    @Override
    protected void preExecute() {
        // Increment local data to avoid getting fresh data from the server.

        // Update mission completions.
        MissionCompletionBean completionBean = getDataRepository().getMissionCompletion(missionId);
        completionBean.setMentorCount(completionBean.getMentorCount() + 1);

        // Add points.
        DataRepository.addPoints(getDataRepository().getUserBean(), PointType.teach, 1);
    }

    @Override
    protected void executeAsync() throws IOException {
        Log.i(LOG_CAT, "Calling server to reportMissionComplete");
        getBinder().getApi().reportMissionComplete(
                getBinder().getSessionId(),
                studentId,
                missionId).execute();
        Log.i(LOG_CAT, "Succeeded calling server to reportMissionComplete");
    }
}
