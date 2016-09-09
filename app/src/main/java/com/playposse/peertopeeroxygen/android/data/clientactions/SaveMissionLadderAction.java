package com.playposse.peertopeeroxygen.android.data.clientactions;

import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.util.ToastUtil;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionLadderBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionTreeBean;

import java.io.IOException;
import java.util.ArrayList;

/**
 * A client action that saves a mission ladder.
 */
public class SaveMissionLadderAction extends ClientAction {

    private static final String LOG_CAT = SaveMissionLadderAction.class.getSimpleName();

    private final MissionLadderBean missionLadderBean;

    public SaveMissionLadderAction(BinderForActions binder, MissionLadderBean missionLadderBean) {
        super(binder, true);

        this.missionLadderBean = missionLadderBean;
    }

    @Override
    protected void executeAsync() throws IOException {
        boolean create = missionLadderBean.getId() == null;
        MissionLadderBean result =
                getBinder().getApi()
                        .saveMissionLadder(getBinder().getSessionId(), missionLadderBean)
                        .execute();
        missionLadderBean.setId(result.getId()); // Save id for new entities.

        // Update local data to avoid reloading data from the server.
        if (create) {
            missionLadderBean.setMissionTreeBeans(new ArrayList<MissionTreeBean>());
            getDataRepository()
                    .getCompleteMissionDataBean()
                    .getMissionLadderBeans()
                    .add(missionLadderBean);
        }

        ToastUtil.sendToast(getContext(), R.string.mission_ladder_save_confirmation_toast);
    }
}
