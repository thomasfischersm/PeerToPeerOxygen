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

        wrappers.add(child);
        relativeHeight--;
    }

    public int getRelativeHeight() {
        return relativeHeight;
    }

    public MissionPath duplicate() {
        return new MissionPath(wrappers, relativeHeight);
    }
}
