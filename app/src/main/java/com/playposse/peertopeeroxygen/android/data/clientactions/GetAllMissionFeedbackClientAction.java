package com.playposse.peertopeeroxygen.android.data.clientactions;

import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionFeedbackBean;

import java.io.IOException;
import java.util.List;

/**
 * A client action that allows admins to retrieve all mission feedback.
 */
public class GetAllMissionFeedbackClientAction extends ApiClientAction {

    private final Callback callback;

    private List<MissionFeedbackBean> missionFeedbackBeanList;

    public GetAllMissionFeedbackClientAction(BinderForActions binder, Callback callback) {
        super(binder, false);

        this.callback = callback;
    }

    @Override
    protected void executeAsync() throws IOException {
        missionFeedbackBeanList = getApi()
                .getAllMissionFeedback(getSessionId(), getDomainId())
                .execute()
                .getItems();
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
