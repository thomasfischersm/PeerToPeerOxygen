package com.playposse.peertopeeroxygen.backend.serveractions;

import com.playposse.peertopeeroxygen.backend.beans.MissionFeedbackBean;
import com.playposse.peertopeeroxygen.backend.schema.MissionFeedback;

import java.util.ArrayList;
import java.util.List;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * A server action to get all {@link MissionFeedback}.
 */
public class GetAllMissionFeedbackAction extends ServerAction {

    public static List<MissionFeedbackBean> getAllMissionFeedback() {
        List<MissionFeedback> missionFeedbackList =
                ofy().load().type(MissionFeedback.class).list();

        ArrayList<MissionFeedbackBean> missionFeedbackBeanList =
                new ArrayList<>(missionFeedbackList.size());
        for (MissionFeedback missionFeedback : missionFeedbackList) {
            MissionFeedbackBean missionFeedbackBean = new MissionFeedbackBean(missionFeedback);

            // Clear sensitive data
            missionFeedbackBean.getUserBean().setSessionId(null);

            missionFeedbackBeanList.add(missionFeedbackBean);
        }

        return missionFeedbackBeanList;
    }
}
