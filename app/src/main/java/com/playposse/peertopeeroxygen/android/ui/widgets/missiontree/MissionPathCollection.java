package com.playposse.peertopeeroxygen.android.ui.widgets.missiontree;

import java.util.ArrayList;
import java.util.List;

/**
 * Container for {@link MissionPath}s that extend from a specific mission. It also provides useful
 * methods for comparing the different {@link MissionPath}s.
 */
public class MissionPathCollection {

    private final MissionWrapper startingWrapper;
    private final List<MissionPath> bossMissionPaths = new ArrayList<>();
    private final List<MissionPath> nonBossMissionPaths = new ArrayList<>();
    private final List<MissionPath> inProgressMissionPaths = new ArrayList<>();

    public MissionPathCollection(MissionWrapper startingWrapper) {
        this.startingWrapper = startingWrapper;

        inProgressMissionPaths.add(new MissionPath(startingWrapper));

        findMissionPaths();
    }

    private void findMissionPaths() {
        while (inProgressMissionPaths.size() > 0) {
            MissionPath missionPath = inProgressMissionPaths.remove(0);
            MissionWrapper lastWrapper = missionPath.getLastMissionWrapper();

            for (MissionWrapper parent : lastWrapper.getParents()) {
                if (!missionPath.contains(parent)) {
                    MissionPath newMissionPath = missionPath.duplicate();
                    newMissionPath.addParent(parent);
                    fileNewMissionPath(parent, newMissionPath);
                }
            }

            for (MissionWrapper children : lastWrapper.getChildren()) {
                if (!missionPath.contains(children)) {
                    MissionPath newMissionPath = missionPath.duplicate();
                    newMissionPath.addChild(children);
                    fileNewMissionPath(children, newMissionPath);
                }
            }
        }
    }

    private void fileNewMissionPath(MissionWrapper wrapper, MissionPath newMissionPath) {
        if (wrapper.isBossMission()) {
            bossMissionPaths.add(newMissionPath);
        } else {
            inProgressMissionPaths.add(newMissionPath);
            if ((wrapper.getParents().size() == 0) || (wrapper.getChildren().size() == 0)) {
                nonBossMissionPaths.add(newMissionPath);
            }
        }
    }

    public boolean getConnectedToBossMission() {
        return startingWrapper.isBossMission() || (bossMissionPaths.size() > 0);
    }

    public boolean getLeadsToBossMission() {
        for (MissionPath missionPath : bossMissionPaths) {
            if (missionPath.getLeadsToBossMission()) {
                return true;
            }
        }
        return false;
    }

    public int getVerticalOrdinalRelativeToBossMission() {
        Integer verticalOrdinal = null;

        if (getLeadsToBossMission()) {
            // If this leads to the boss mission, get the mission path that puts this mission the
            // lowest.
            for (MissionPath missionPath : bossMissionPaths) {
                if (verticalOrdinal == null) {
                    verticalOrdinal = missionPath.getRelativeHeight();
                } else {
                    verticalOrdinal = Math.max(verticalOrdinal, missionPath.getRelativeHeight());
                }
            }
        } else {
            // If this doesn't lead to the boss mission, get the highest.
            for (MissionPath missionPath : bossMissionPaths) {
                if (verticalOrdinal == null) {
                    verticalOrdinal = missionPath.getRelativeHeight();
                } else {
                    verticalOrdinal = Math.min(verticalOrdinal, missionPath.getRelativeHeight());
                }
            }

        }

        return (verticalOrdinal != null) ? verticalOrdinal : 0;
    }

    public int getVerticalOrdinalRelativeToPeakOfOrphanTree() {
        Integer verticalOrdinal = 0;
        for (MissionPath missionPath : nonBossMissionPaths) {
            verticalOrdinal = Math.max(verticalOrdinal, missionPath.getRelativeHeight());
        }

        return verticalOrdinal;
    }
}
