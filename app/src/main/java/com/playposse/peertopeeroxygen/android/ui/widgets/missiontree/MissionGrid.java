package com.playposse.peertopeeroxygen.android.ui.widgets.missiontree;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A container to lay out missions in a grid.
 */
public class MissionGrid {

    private final String LOG_CAT = MissionGrid.class.getSimpleName();

    private final int maxColumn;

    private List<List<MissionWrapper>> grid = new ArrayList<>();

    public MissionGrid(int maxColumn) {
        this.maxColumn = maxColumn;
    }

    public void add(int row, int column, MissionWrapper wrapper) {
        // Initialize empty rows as needed.
        while (grid.size() <= row) {
            grid.add(new ArrayList<MissionWrapper>());
        }

        // Grow row if necessary.
        List<MissionWrapper> rowItems = grid.get(row);
        while (rowItems.size() <= column) {
            rowItems.add(null);
        }
        if (rowItems.get(column) != null) {
            throw new RuntimeException("Didn't expect an item at " + column + " " + row);
        }
        rowItems.set(column, wrapper);
        wrapper.place(row, column);
    }

    public MissionWrapper get(int row, int column) {
        if (row >= grid.size()) {
            return null;
        }

        List<MissionWrapper> rowItems = grid.get(row);
        if (column >= rowItems.size()) {
            return null;
        }
        return rowItems.get(column);
    }

    public int getMaxRow() {
        return grid.size();
    }

    public int getMaxColumn() {
        return maxColumn;
    }

    public boolean attemptAdd(int row, double column, MissionWrapper wrapper) {
        int roundedColumn = (int) Math.round(column);
        if (roundedColumn >= maxColumn) {
            column = maxColumn - 1;
        }

        while (roundedColumn < maxColumn) {
            if (isFree(row, roundedColumn)) {
                add(row, roundedColumn, wrapper);
                return true;
            }

            if (attemptPushLeft(row, roundedColumn)) {
                add(row, roundedColumn, wrapper);
                return true;
            }
            roundedColumn++;
        }

        return false;
    }

    private boolean isFree(int row, int column) {
        if (row >= grid.size()) {
            return true;
        }

        List<MissionWrapper> rowItems = grid.get(row);
        if (column >= rowItems.size()) {
            return true;
        } else {
            return rowItems.get(column) == null;
        }
    }

    private boolean attemptPushLeft(int row, int column) {
        if (column == 0) {
            return false;
        }

        if (isFree(row, column - 1)) {
            move(row, column, row, column - 1);
            return true;
        } else if (attemptPushLeft(row, column - 1)) {
            move(row, column, row, column - 1);
            return true;
        } else {
            return false;
        }
    }

    private void move(int fromRow, int fromColumn, int toRow, int toColumn) {
        List<MissionWrapper> fromRowItems = grid.get(fromRow);
        MissionWrapper wrapper = fromRowItems.get(fromColumn);
        fromRowItems.set(fromColumn, null);

        List<MissionWrapper> toRowItems = grid.get(toRow);
        toRowItems.set(toColumn, wrapper);
        wrapper.place(toRow, toColumn);
    }

    public void add(OrphanTree orphanTree) {
        int row = 1;
        while (!canAdd(row, orphanTree)) {
            row++;
        }
        add(row, orphanTree);
    }

    private void add(int row, OrphanTree orphanTree) {
        Map<Integer, List<MissionWrapper>> ordinalToWrapperMap =
                orphanTree.getOrdinalToWrapperMap();
        List<Integer> ordinals = orphanTree.getSortedOrdinals();

        int currentRow = row;
        for (Integer ordinal : ordinals) {
            for (MissionWrapper wrapper : ordinalToWrapperMap.get(ordinal)) {
                Integer column = null;
                while (column == null) {
                    column = findFirstFreeColumnFromRight(currentRow);
                    if (column == null) {
                        currentRow++;
                    }
                }
                Log.i(LOG_CAT, "Attempting to place column " + wrapper.getMissionBean().getName()
                        + " of orphan tree at " + currentRow + " " + column);
                boolean addSuccess = attemptAdd(currentRow, column, wrapper);
                if (!addSuccess) {
                    throw new RuntimeException("Failed to add orphan tree at row " + currentRow
                            + " for mission " + wrapper.getMissionBean().getId());
                }
            }
            currentRow++;
        }
    }

    private boolean canAdd(int row, OrphanTree orphanTree) {
        Map<Integer, List<MissionWrapper>> ordinalToWrapperMap =
                orphanTree.getOrdinalToWrapperMap();
        List<Integer> ordinals = orphanTree.getSortedOrdinals();
        for (Integer ordinal : ordinals) {
            int freeCount = getFreeColumnCount(row);
            int wrapperCount = ordinalToWrapperMap.get(ordinal).size();
            if (freeCount < wrapperCount) {
                return false;
            }

            row++;
            if (row >= grid.size()) {
                // We've reached the bottom of every other mission. Stop checking because things
                // can't get any better.
                return true;
            }
        }
        return true;
    }

    private int getFreeColumnCount(int row) {
        if (row >= grid.size()) {
            return maxColumn;
        } else {
            return maxColumn - grid.get(row).size();
        }
    }

    @Nullable
    private Integer findFirstFreeColumnFromRight(int row) {
        if (row >= grid.size()) {
            // We are beyond the bottom of the grid.
            return 0;
        }

        List<MissionWrapper> rowItems = grid.get(row);
        Integer column = null;
        for (int i = maxColumn - 1; i >= 0; i--) {
            if (i >= rowItems.size()) {
                column = i;
                continue;
            } else if (rowItems.get(i) != null) {
                break;
            } else {
                column = i;
            }
        }
        return column;
    }
}
