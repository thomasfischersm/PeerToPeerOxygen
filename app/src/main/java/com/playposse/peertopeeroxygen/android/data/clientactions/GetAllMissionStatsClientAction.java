package com.playposse.peertopeeroxygen.android.data.clientactions;

import android.util.Log;

import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionStatsBean;

import java.io.IOException;
import java.util.List;

/**
 * A client action that allows admins to retrieve all mission stats.
 */
public class GetAllMissionStatsClientAction extends ApiClientAction {

    private static final String LOG_CAT = GetAllMissionStatsClientAction.class.getSimpleName();

    private final Callback callback;

    private List<MissionStatsBean> missionStatsBeanList;

    public GetAllMissionStatsClientAction(BinderForActions binder, Callback callback) {
        super(binder, false);

        this.callback = callback;
    }

    @Override
    protected void executeAsync() throws IOException {
        missionStatsBeanList = getApi()
                .getAllMissionStats(getSessionId(), getDomainId())
                .execute()
                .getItems();
        Log.i(LOG_CAT, "Calling API completed");
    }

    @Override
    protected void postExecute() {
        Log.i(LOG_CAT, "About to call callback.");
        callback.onResult(missionStatsBeanList);
    }

    /**
     * A callback that provides all the mission stats.
     */
    public interface Callback {
        void onResult(List<MissionStatsBean> missionStatsBeanList);
    }
}
