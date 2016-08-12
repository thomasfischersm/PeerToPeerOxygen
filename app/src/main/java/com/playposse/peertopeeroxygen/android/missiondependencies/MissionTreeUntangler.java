package com.playposse.peertopeeroxygen.android.missiondependencies;

import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionTreeBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class that untangles a list of missions into a tree based on dependencies into a tree.
 * Every pre-requisite has to be at least one level lower.
 */
public class MissionTreeUntangler {

    public static List<List<MissionPlaceHolder>> untangle(MissionTreeBean missionTreeBean) {
        return sortIntoRows(sortIntoList(missionTreeBean));
    }

//    private static MissionTreeMeasurements sortVertically(MissionTreeBean missionTreeBean) {
//        MissionTreeMeasurements measurements = new MissionTreeMeasurements();
//
//        // Add MissionTree itself.
//        MissionPlaceHolder treeHolder = new MissionPlaceHolder(missionTreeBean);
//        treeHolder.setRow(0);
//        treeHolder.setColumn(0);
//        measurements.addMissionPlaceHolder();
//    }

    private static List<MissionPlaceHolder> sortIntoList(MissionTreeBean missionTreeBean) {
        // Add everything into an unsorted list.
        List<MissionPlaceHolder> holders = new ArrayList<>();
        Map<Long, MissionPlaceHolder> map = new HashMap<>();
        holders.add(new MissionPlaceHolder(missionTreeBean));
        for (MissionBean missionBean : missionTreeBean.getMissionBeans()) {
            MissionPlaceHolder holder = new MissionPlaceHolder(missionBean);
            holders.add(holder);
            map.put(missionBean.getId(), holder);
        }

        // Add dependencies.
        for (MissionPlaceHolder holder : holders) {
            final List<Long> childMissionIds;
            if (holder.getMissionTreeBean() != null) {
                childMissionIds = holder.getMissionTreeBean().getRequiredMissionIds();
            } else {
                childMissionIds = holder.getMissionBean().getRequiredMissionIds();
            }

            if (childMissionIds != null) {
                for (Long childMissionId : childMissionIds) {
                    MissionPlaceHolder childHolder = map.get(childMissionId);
                    holder.getChildren().add(childHolder);
                    childHolder.getParents().add(holder);
                }
            }
        }

        // Sort.
        for (int i = 0; i < holders.size(); i++) {
            // Is there an easy check to bail out if there is a cycle?

            MissionPlaceHolder holder = holders.get(i);

            int maxParentId = -1;
            for (MissionPlaceHolder parentHolder : holder.getParents()) {
                maxParentId = Math.max(maxParentId, holders.indexOf(parentHolder));
            }

            if (maxParentId >= i) {
                holders.remove(holder);
                holders.add(maxParentId, holder);
                i--;
            }
        }
        return holders;
    }

    private static List<List<MissionPlaceHolder>> sortIntoRows(List<MissionPlaceHolder> holders) {
        List<List<MissionPlaceHolder>> rows = new ArrayList<>();
        List<MissionPlaceHolder> currentRow = new ArrayList<>();
        rows.add(currentRow);

        for (MissionPlaceHolder holder : holders) {
            boolean needNewRow = false;
            needNewRow |= holder.getMissionTreeBean() != null;
            for (MissionPlaceHolder parentHolder : holder.getParents()) {
                needNewRow |= currentRow.contains(parentHolder);
            }

            if (needNewRow) {
                currentRow = new ArrayList<>();
                rows.add(currentRow);
            }

            currentRow.add(holder);
        }
        return rows;
    }
}
