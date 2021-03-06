package com.playposse.peertopeeroxygen.android.data.clientactions;

import android.util.Log;

import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.util.ToastUtil;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionTreeBean;

import java.io.IOException;

/**
 * A client action that saves a mission.
 */
public class SaveMissionClientAction extends ApiClientAction {

    private static final String LOG_CAT = SaveMissionTreeClientAction.class.getSimpleName();

    private final Long missionLadderId;
    private final Long missionTreeId;
    private final MissionBean missionBean;

    public SaveMissionClientAction(
            BinderForActions binder,
            Long missionLadderId,
            Long missionTreeId,
            MissionBean missionBean) {

        super(binder, true);

        this.missionLadderId = missionLadderId;
        this.missionTreeId = missionTreeId;
        this.missionBean = missionBean;
    }

    @Override
    protected void executeAsync() throws IOException {
        boolean create = missionBean.getId() == null;

        MissionBean result = getApi().saveMission(
                getSessionId(),
                missionLadderId,
                missionTreeId,
                getDomainId(),
                missionBean)
                .execute();
        missionBean.setId(result.getId());

        if (create) {
            MissionTreeBean missionTreeBean =
                    getDataRepository().getMissionTreeBean(missionLadderId, missionTreeId);
            missionTreeBean.getMissionBeans().add(missionBean);
        }

        ToastUtil.sendToast(getContext(), R.string.mission_save_confirmation_toast);
        Log.i(LOG_CAT, "Mission has been saved.");
    }
}
