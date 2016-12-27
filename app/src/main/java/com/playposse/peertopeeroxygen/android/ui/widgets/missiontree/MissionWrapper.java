package com.playposse.peertopeeroxygen.android.ui.widgets.missiontree;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.Spanned;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Toast;

import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.data.DataRepository;
import com.playposse.peertopeeroxygen.android.data.types.PointType;
import com.playposse.peertopeeroxygen.android.missiondependencies.MissionAvailabilityChecker;
import com.playposse.peertopeeroxygen.android.missiondependencies.MissionPlaceHolder;
import com.playposse.peertopeeroxygen.android.model.ExtraConstants;
import com.playposse.peertopeeroxygen.android.student.StudentMissionActivity;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionTreeBean;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A wrapper around the {@link MissionBean} to attach analytical information that's helpful for
 * laying out a mission tree. Most of the getter are recursive calls that cache the result after
 * the first call.
 */
public class MissionWrapper {

    private final MissionBean missionBean;
    private final boolean isBossMission;
    private final Long missionLadderId;
    private final MissionTreeBean missionTreeBean;
    private final DataRepository dataRepository;
    private MissionAvailabilityChecker.MissionAvailability missionAvailability;
    private final MissionPlaceHolder missionPlaceHolder; // TODO: Remove reference to old code.

    private Set<MissionWrapper> parents;
    private Set<MissionWrapper> children;

    private Boolean leadsToBossMission = null;
    private Boolean isConnectedToBossMission = null;
    private Integer verticalOrdinal = null;
    private Boolean isPlaced = null;
    private Integer column = null;
    private Integer row = null;
    private Double averageParentColumn = null;

    public MissionWrapper(
            MissionBean missionBean,
            boolean isBossMission,
            Long missionLadderId,
            MissionTreeBean missionTreeBean,
            DataRepository dataRepository) {

        this.missionBean = missionBean;
        this.isBossMission = isBossMission;
        this.missionLadderId = missionLadderId;
        this.missionTreeBean = missionTreeBean;
        this.dataRepository = dataRepository;

        missionPlaceHolder = new MissionPlaceHolder(missionBean);
        missionAvailability = MissionAvailabilityChecker.determineAvailability(
                missionPlaceHolder,
                missionLadderId,
                missionTreeBean,
                dataRepository);
    }

    public void init(Map<Long, MissionWrapper> missionIdToWrapperMap) {
        // Find children.
        List<Long> childrenIds = missionBean.getRequiredMissionIds();
        children = new HashSet<>();
        if (childrenIds != null) {
            for (Long childId : childrenIds) {
                children.add(missionIdToWrapperMap.get(childId));
            }
        }

        // Find all parents.
        parents = new HashSet<>();
        Long thisId = missionBean.getId();
        for (MissionWrapper otherWrapper : missionIdToWrapperMap.values()) {
            Long otherId = otherWrapper.getMissionBean().getId();
            if (otherId.equals(thisId)) {
                // Skip self.
            }

            List<Long> otherChildIds = otherWrapper.getMissionBean().getRequiredMissionIds();
            if (otherChildIds != null) {
                if (otherChildIds.contains(thisId)) {
                    parents.add(missionIdToWrapperMap.get(otherId));
                }
            }
        }
    }

    public void place(int row, int column) {
        this.isPlaced = true;
        this.column = column;
        this.row = row;
    }

    public MissionBean getMissionBean() {
        return missionBean;
    }

    public boolean isBossMission() {
        return isBossMission;
    }

    public Set<MissionWrapper> getParents() {
        return parents;
    }

    public Set<MissionWrapper> getChildren() {
        return children;
    }

    public boolean getLeadsToBossMission() {
        if (leadsToBossMission == null) {
            leadsToBossMission = false;
            for (MissionWrapper parent : parents) {
                if (parent.getLeadsToBossMission() || isBossMission) {
                    leadsToBossMission = true;
                    break;
                }
            }
        }

        return leadsToBossMission;
    }

    public boolean getConnectedToBossMission() {
        if (isConnectedToBossMission == null) {
            if (getLeadsToBossMission()) {
                isConnectedToBossMission = true;
            } else {
                isConnectedToBossMission = false;
                for (MissionWrapper child : children) {
                    if (child.getConnectedToBossMission()) {
                        isConnectedToBossMission = true;
                        break;
                    }
                }
            }
        }
        return isConnectedToBossMission;
    }

    public Integer getVerticalOrdinal() {
        if (verticalOrdinal == null) {
            if (getLeadsToBossMission()) {
                // Leads to boss mission. So simply recursively walk up to the boss mission.
                verticalOrdinal = 0;
                for (MissionWrapper parent : parents) {
                    verticalOrdinal = Math.max(verticalOrdinal, parent.getVerticalOrdinal() + 1);
                }
            } else if (getConnectedToBossMission()) {
                // The current mission doesn't lead to the boss mission, but a mission below leads
                // to the boss mission. So walk down the tree until it's possible to walk up.
                for (MissionWrapper child : children) {
                    if (verticalOrdinal == null) {
                        verticalOrdinal = child.getVerticalOrdinal() - 1;
                    } else {
                        verticalOrdinal = Math.min(verticalOrdinal, child.getVerticalOrdinal() - 1);
                    }
                }
            } else {
                // We are in an orphan tree. The top mission in the orphan has vertical ordinal 0.
                verticalOrdinal = 0;
                for (MissionWrapper parent : parents) {
                    verticalOrdinal = Math.max(verticalOrdinal, parent.getVerticalOrdinal() + 1);
                }
            }
        }
        return verticalOrdinal;
    }

    public Boolean getPlaced() {
        return isPlaced;
    }

    public Integer getColumn() {
        return column;
    }

    public Integer getRow() {
        return row;
    }

    public double getAverageParentColumn() {
        if (averageParentColumn == null) {
            if (getParents().size() == 0) {
                averageParentColumn = 0.0;
            } else {
                double avg = 0;
                for (MissionWrapper wrapper : getParents()) {
                    avg += wrapper.getColumn();
                }
                averageParentColumn = avg / getParents().size();
            }
        }

        return averageParentColumn;
    }

    public MissionAvailabilityChecker.MissionAvailability getMissionAvailability() {
        return missionAvailability;
    }

    public Long getMissionLadderId() {
        return missionLadderId;
    }

    public MissionTreeBean getMissionTreeBean() {
        return missionTreeBean;
    }

    public DataRepository getDataRepository() {
        return dataRepository;
    }

    public MissionPlaceHolder getMissionPlaceHolder() {
        return missionPlaceHolder;
    }
}
