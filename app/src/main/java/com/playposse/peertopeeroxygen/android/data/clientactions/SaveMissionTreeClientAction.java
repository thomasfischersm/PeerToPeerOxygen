package com.playposse.peertopeeroxygen.android.data.clientactions;

import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.util.ToastUtil;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionLadderBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionTreeBean;

import java.io.IOException;
import java.util.ArrayList;

/**
 * A client action that saves the mission tree.
 */
public class SaveMissionTreeClientAction extends ApiClientAction {

    private static final String LOG_CAT = SaveMissionTreeClientAction.class.getSimpleName();

    private final Long missionLadderId;
    private final MissionTreeBean missionTreeBean;

    public SaveMissionTreeClientAction(
            BinderForActions binder,
            Long missionLadderId,
            MissionTreeBean missionTreeBean) {

        super(binder, true);

        this.missionLadderId = missionLadderId;
        this.missionTreeBean = missionTreeBean;
    }

    @Override
    protected void executeAsync() throws IOException {
        boolean create = missionTreeBean.getId() == null;
        MissionTreeBean result = getApi()
                .saveMissionTree(
                        getSessionId(),
                        missionLadderId,
                        getDomainId(),
                        missionTreeBean)
                .execute();
        missionTreeBean.setId(result.getId()); // Save id for new entities.

        // Update local data to avoid reloading data from the server.
        if (create) {
            MissionLadderBean missionLadderBean =
                    getDataRepository().getMissionLadderBean(missionLadderId);
            missionTreeBean.setMissionBeans(new ArrayList<MissionBean>());
            missionLadderBean.getMissionTreeBeans().add(missionTreeBean);
        }

        ToastUtil.sendToast(getContext(), R.string.mission_tree_save_confirmation_toast);
    }
}
