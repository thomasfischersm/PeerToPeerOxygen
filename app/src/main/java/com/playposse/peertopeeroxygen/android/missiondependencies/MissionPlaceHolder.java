package com.playposse.peertopeeroxygen.android.missiondependencies;

import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionTreeBean;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder for a {@link MissionBean} that has meta information to analyze its place in the
 * dependency tree.
 */
public class MissionPlaceHolder {

    private final MissionBean missionBean;
    private final MissionTreeBean missionTreeBean;

    private int row;
    private int column;
    private List<MissionPlaceHolder> parents = new ArrayList<>();
    private List<MissionPlaceHolder> children = new ArrayList<>();

    public MissionPlaceHolder(MissionBean missionBean) {
        this.missionBean = missionBean;
        this.missionTreeBean = null;
    }

    public MissionPlaceHolder(MissionTreeBean missionTreeBean) {
        this.missionBean = null;
        this.missionTreeBean = missionTreeBean;
    }

    public MissionBean getMissionBean() {
        return missionBean;
    }

    public MissionTreeBean getMissionTreeBean() {
        return missionTreeBean;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public List<MissionPlaceHolder> getParents() {
        return parents;
    }

    public void setParents(List<MissionPlaceHolder> parents) {
        this.parents = parents;
    }

    public List<MissionPlaceHolder> getChildren() {
        return children;
    }

    public void setChildren(List<MissionPlaceHolder> children) {
        this.children = children;
    }
}
