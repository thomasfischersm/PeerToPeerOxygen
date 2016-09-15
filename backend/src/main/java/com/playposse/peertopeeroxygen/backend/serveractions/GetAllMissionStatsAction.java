package com.playposse.peertopeeroxygen.backend.serveractions;

import com.playposse.peertopeeroxygen.backend.beans.MissionStatsBean;
import com.playposse.peertopeeroxygen.backend.schema.MissionStats;

import java.util.ArrayList;
import java.util.List;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * A server action to get all {@link MissionStats}.
 */
public class GetAllMissionStatsAction extends ServerAction {

    public static List<MissionStatsBean> getAllMissionStats() {
        List<MissionStats> missionStatsList =
                ofy().load().type(MissionStats.class).list();

        ArrayList<MissionStatsBean> missionStatsBeanList =
                new ArrayList<>(missionStatsList.size());
        for (MissionStats missionStats : missionStatsList) {
            missionStatsBeanList.add(new MissionStatsBean(missionStats));
        }

        return missionStatsBeanList;
    }
}
