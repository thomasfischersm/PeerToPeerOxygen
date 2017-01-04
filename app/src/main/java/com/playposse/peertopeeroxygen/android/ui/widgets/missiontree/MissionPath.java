package com.playposse.peertopeeroxygen.android.ui.widgets.missiontree;

import java.util.ArrayList;
import java.util.List;

/**
 * A temporary structure to map out the dependencies of missions to analyze how to layout the
 * missions visually.
 */
public class MissionPath {

    private final List<MissionWrapper> wrappers;

    private int relativeHeight = 0;
    private boolean mightLeadToBossMission = true;

    public MissionPath(MissionWrapper startingMission) {
        wrappers = new ArrayList<>();

        wrappers.add(startingMission);
    }

    private MissionPath(List<MissionWrapper> wrappers, int relativeHeight) {
        this.wrappers = wrappers;
        this.relativeHeight = relativeHeight;
    }

    public void addParent(MissionWrapper parent) {
        if (wrappers.contains(parent)) {
            throw new IllegalArgumentException("The mission path cannot return to itself. "
                    + "Origin: " + wrappers.get(0).getMissionBean().getName()
                    + " Destination: " + parent.getMissionBean().getName());
        }

        wrappers.add(parent);
        relativeHeight++;
    }

    public void addChild(MissionWrapper child) {
        if (wrappers.contains(child)) {
            throw new IllegalArgumentException("The mission path cannot return to itself. "
                    + "Origin: " + wrappers.get(0).getMissionBean().getName()
                    + " Destination: " + child.getMissionBean().getName());
        }

        mightLeadToBossMission = false;
        wrappers.add(child);
        relativeHeight--;
    }

    public MissionWrapper getLastMissionWrapper() {
        return wrappers.get(wrappers.size() - 1);
    }

    public boolean contains(MissionWrapper wrapper) {
        return wrappers.contains(wrapper);
    }

    public int getRelativeHeight() {
        return relativeHeight;
    }

    public MissionPath duplicate() {
        return new MissionPath(new ArrayList<>(wrappers), relativeHeight);
    }

    public boolean getLeadsToBossMission() {
        return mightLeadToBossMission && wrappers.get(0).getLeadsToBossMission();
    }
}
