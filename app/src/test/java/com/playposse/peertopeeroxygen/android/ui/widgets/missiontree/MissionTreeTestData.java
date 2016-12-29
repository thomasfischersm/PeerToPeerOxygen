package com.playposse.peertopeeroxygen.android.ui.widgets.missiontree;

import com.playposse.peertopeeroxygen.android.data.DataRepository;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.CompleteMissionDataBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionLadderBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionTreeBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.UserBean;

import java.util.ArrayList;

/**
 * A helper class for tests that creates test data to test classes in the package
 * {@code com.playposse.peertopeeroxygen.android.ui.widgets.missiontree}.
 */
class MissionTreeTestData {

    static MissionBean createTestMission(long id, String name, MissionBean... children) {
        MissionBean missionBean = new MissionBean();
        missionBean.setId(id);
        missionBean.setName(name);

        for (MissionBean child : children) {
            if (missionBean.getRequiredMissionIds() == null) {
                missionBean.setRequiredMissionIds(new ArrayList<Long>());
            }
            missionBean.getRequiredMissionIds().add(child.getId());
        }

        return missionBean;
    }

    static DataRepository createDataRepository(
            boolean isAdmin,
            Long missionLadderId,
            MissionTreeBean missionTreeBean) {

        MissionLadderBean missionLadderBean = new MissionLadderBean();
        missionLadderBean.setId(missionLadderId);
        missionLadderBean.setMissionTreeBeans(new ArrayList<MissionTreeBean>());
        missionLadderBean.getMissionTreeBeans().add(missionTreeBean);

        UserBean userBean = new UserBean();
        userBean.setAdmin(isAdmin);

        CompleteMissionDataBean completeMissionDataBean = new CompleteMissionDataBean();
        completeMissionDataBean.setUserBean(userBean);
        completeMissionDataBean.setMissionLadderBeans(new ArrayList<MissionLadderBean>());
        completeMissionDataBean.getMissionLadderBeans().add(missionLadderBean);

        DataRepository dataRepository = new DataRepository();
        dataRepository.setCompleteMissionDataBean(completeMissionDataBean);
        return dataRepository;
    }
}
