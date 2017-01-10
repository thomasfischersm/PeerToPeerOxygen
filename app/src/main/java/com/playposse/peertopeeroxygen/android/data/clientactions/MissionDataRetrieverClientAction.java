package com.playposse.peertopeeroxygen.android.data.clientactions;

import android.util.Log;

import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.globalconfiguration.RedirectRouting;
import com.playposse.peertopeeroxygen.android.util.ToastUtil;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.CompleteMissionDataBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionLadderBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionTreeBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.UserBean;

import java.io.IOException;
import java.util.ArrayList;

import javax.annotation.Nullable;

/**
 * A client action that is called on startup to retrieve the mission data from the server.
 */
public class MissionDataRetrieverClientAction extends ApiClientAction {

    private static final String LOG_CAT = MissionDataRetrieverClientAction.class.getSimpleName();

    private final Long domainId;
    @Nullable private final Callback callback;

    public MissionDataRetrieverClientAction(
            Long domainId,
            BinderForActions binder,
            @Nullable Callback callback) {

        super(binder, true);

        this.domainId = domainId;
        this.callback = callback;
    }

    @Override
    protected void executeAsync() throws IOException {
        Long sessionId = getBinder().getSessionId();
        if ((sessionId == null) || (sessionId == -1)) {
            RedirectRouting.onAppError(getContext());
            return;
        }
        CompleteMissionDataBean completeMissionDataBean =
                getBinder().getApi().getMissionData(sessionId, domainId).execute();

        Log.i(LOG_CAT, "BEFORE FIX");
        debugDump(completeMissionDataBean);

        fixNullLists(completeMissionDataBean);

        Log.i(LOG_CAT, "AFTER FIX");
        debugDump(completeMissionDataBean);

        getBinder().getDataRepository()
                .setCompleteMissionDataBean(completeMissionDataBean);
        getBinder().makeDataReceivedCallbacks();

        if (callback != null) {
            callback.onComplete(completeMissionDataBean);
        }

        ToastUtil.sendShortToast(getContext(), R.string.mission_data_refreshed_toast);
        Log.i(LOG_CAT, "The data has been loaded.");
    }


    /**
     * Replaces null lists with an empty list.
     * <p/>
     * <p>Somehow, empty lists turn into null during transport. (JSON probably doesn't
     * differentiate.) To make things easier, all null lists are initialized with an empty list.
     */
    private static void fixNullLists(CompleteMissionDataBean completeMissionDataBean) {
        if (completeMissionDataBean.getMissionLadderBeans() == null) {
            completeMissionDataBean.setMissionLadderBeans(
                    new ArrayList<MissionLadderBean>());
        }

        for (MissionLadderBean missionLadderBean : completeMissionDataBean.getMissionLadderBeans()) {
            if (missionLadderBean.getMissionTreeBeans() == null) {
                missionLadderBean.setMissionTreeBeans(new ArrayList<MissionTreeBean>());
            }

            for (MissionTreeBean missionTreeBean : missionLadderBean.getMissionTreeBeans()) {
                if (missionTreeBean.getMissionBeans() == null) {
                    missionTreeBean.setMissionBeans(new ArrayList<MissionBean>());
                }
            }
        }
    }

    private static void debugDump(CompleteMissionDataBean completeMissionDataBean) {
        Log.i(LOG_CAT, "Dumping user info");
        UserBean userBean = completeMissionDataBean.getUserBean();
        if (userBean == null) {
            Log.i(LOG_CAT, "User bean is null");
        } else {
            Log.i(LOG_CAT, "User is " + userBean.getName());
        }

        Log.i(LOG_CAT, "Dumping complete mission data bean:");
        if (completeMissionDataBean.getMissionLadderBeans() != null) {
            for (MissionLadderBean missionLadderBean : completeMissionDataBean.getMissionLadderBeans()) {
                Log.i(LOG_CAT, "- ladder (id: " + missionLadderBean.getId()
                        + ", name: " + missionLadderBean.getName()
                        + ", description: " + missionLadderBean.getDescription());
                if (missionLadderBean.getMissionTreeBeans() == null) {
                    Log.i(LOG_CAT, "-- mission tree: null");
                } else {
                    if (missionLadderBean.getMissionTreeBeans() != null) {
                        for (MissionTreeBean missionTreeBean : missionLadderBean.getMissionTreeBeans()) {
                            Log.i(LOG_CAT, "-- mission tree (id: " + missionTreeBean.getId()
                                    + ", name: " + missionTreeBean.getName()
                                    + ", description: " + missionTreeBean.getDescription());
                            if (missionTreeBean.getMissionBeans() == null) {
                                Log.i(LOG_CAT, "--- mission: null");
                            } else {
                                if (missionTreeBean.getMissionBeans() != null) {
                                    for (MissionBean missionBean : missionTreeBean.getMissionBeans()) {
                                        Log.i(LOG_CAT, "--- mission: (id: " + missionBean.getId()
                                                + ", name: " + missionBean.getName()
                                                + ", student instruction: " + missionBean.getStudentInstruction()
                                                + ", buddy instruction: " + missionBean.getBuddyInstruction());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Callback that is called when the mission data has been loaded.
     */
    public interface Callback {
        void onComplete(CompleteMissionDataBean completeMissionDataBean);
    }
}
