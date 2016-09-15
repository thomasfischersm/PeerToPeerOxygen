package com.playposse.peertopeeroxygen.android.data.clientactions;

import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionFeedbackBean;

import java.io.IOException;
import java.util.List;

/**
 * A client action that allows admins to retrieve all mission feedback.
 */
public class GetAllMissionFeedbackAction extends ClientAction {

    private final Callback callback;

    private List<MissionFeedbackBean> missionFeedbackBeanList;

    public GetAllMissionFeedbackAction(BinderForActions binder, Callback callback) {
        super(binder, false);

        this.callback = callback;
    }

    @Override
    protected void executeAsync() throws IOException {
        Long sessionId = getBinder().getSessionId();
        missionFeedbackBeanList =
                getBinder().getApi().getAllMissionFeedback(sessionId).execute().getItems();
    }

    @Override
    protected void postExecute() {
        callback.onResult(missionFeedbackBeanList);
    }

    /**
     * A callback that provides all the mission feedback.
     */
    public interface Callback {
        void onResult(List<MissionFeedbackBean> missionFeedbackBeanList);
    }
}
