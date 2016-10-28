package com.playposse.peertopeeroxygen.android.data.clientactions;

import com.playposse.peertopeeroxygen.android.data.CompleteMissionDataCache;
import com.playposse.peertopeeroxygen.android.data.DataRepository;
import com.playposse.peertopeeroxygen.android.data.types.PointType;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionCompletionBean;

import java.io.IOException;

/**
 * An action that a senior buddy uses to notify the server and the buddy that a mission has been
 * taught successfully.
 */
public class ReportMissionCheckoutCompleteClientAction extends ApiClientAction {

    private static final String LOG_CAT = ReportMissionCheckoutCompleteClientAction.class.getSimpleName();

    private final Long studentId;
    private final Long buddyId;
    private final Long missionId;

    public ReportMissionCheckoutCompleteClientAction(
            BinderForActions binder,
            Long studentId,
            Long buddyId,
            Long missionId) {

        super(binder, true);

        this.studentId = studentId;
        this.buddyId = buddyId;
        this.missionId = missionId;
    }

    @Override
    protected void preExecute() {
        // Increment local data to avoid getting fresh data from the server.
        MissionCompletionBean completionBean = getDataRepository().getMissionCompletion(missionId);
        completionBean.setMentorCount(completionBean.getMentorCount() + 1);

        DataRepository.addPoints(getDataRepository().getUserBean(), PointType.teach, 1);

        // Save changes to the davice storage.
        CompleteMissionDataCache.save(getContext(), getDataRepository());
    }

    @Override
    protected void executeAsync() throws IOException {
        getBinder().getApi().reportMissionCheckoutComplete(
                getBinder().getSessionId(),
                studentId,
                buddyId,
                missionId).execute();
    }
}
