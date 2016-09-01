package com.playposse.peertopeeroxygen.android.missiondependencies;

import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionTreeBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A class that contains the dimensions of a visual representation of the tree of mission.
 */
public class MissionTreeMeasurements {

    private int rowCount = 0;
    private int columnCount = 0;
    private int maxRow = 0;
    private int minRow = 0;
    private List<MissionPlaceHolder> missionPlaceHolders = new ArrayList<>();
    private Map<MissionBean, MissionPlaceHolder> missionLookup = new HashMap<>();
    private MissionTreeBean missionTreeBean;
    private List<List<MissionPlaceHolder>> rows = new ArrayList<>();

    public MissionTreeMeasurements() {
        rows.add(new ArrayList<MissionPlaceHolder>());
    }

    public int getColumnCount() {
        return columnCount;
    }

    public void setColumnCount(int columnCount) {
        this.columnCount = columnCount;
    }

    public List<MissionPlaceHolder> getMissionPlaceHolders() {
        return missionPlaceHolders;
    }

    public void addMissionPlaceHolder(MissionPlaceHolder missionPlaceHolder) {
        int oldMaxRow = maxRow;
        int oldMinRow = minRow;

        missionPlaceHolders.add(missionPlaceHolder);
        maxRow = Math.max(maxRow, missionPlaceHolder.getRow());
        minRow = Math.min(minRow, missionPlaceHolder.getRow());
        rowCount = maxRow - minRow + 1;

        // Update row data structure with necessary new row.
        if ((oldMaxRow - maxRow == 0) && (oldMinRow - minRow == 0)) {
            // Nothing to do.
        } else if ((oldMaxRow - maxRow == -1) && (oldMinRow - minRow == 0)) {
            rows.add(new ArrayList<MissionPlaceHolder>());
        } else if ((oldMaxRow - maxRow == 0) && (oldMinRow - minRow == 1)) {
            rows.add(0, new ArrayList<MissionPlaceHolder>());
        } else {
            throw new RuntimeException("The maxima shouldn't both have increased."
                    + "oldMaxRow: " + oldMaxRow
                    + "maxRow: " + maxRow
                    + "oldMinRow: " + minRow
                    + "minRow: " + minRow);
        }

        // Add to row.
        int translatedRowIndex = missionPlaceHolder.getRow() - minRow;
        List<MissionPlaceHolder> row = rows.get(translatedRowIndex);
        row.add(missionPlaceHolder);
        columnCount = Math.max(columnCount, row.size());
//        if ()

        // Add to lookup.
        missionLookup.put(missionPlaceHolder.getMissionBean(), missionPlaceHolder);
    }

    public int getRowCount() {
        return rowCount;
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }
}
