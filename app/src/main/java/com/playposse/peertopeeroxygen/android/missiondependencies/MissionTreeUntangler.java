package com.playposse.peertopeeroxygen.android.missiondependencies;

import android.util.Log;

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

    private static final String LOG_CAT = MissionTreeUntangler.class.getSimpleName();

    public static List<List<MissionPlaceHolder>> untangle(
            MissionTreeBean missionTreeBean,
            int maxColumnWidth) {

        return compactToMaxWidth(moveChildrenUnderParent(sortIntoRows(sortIntoList(missionTreeBean))), maxColumnWidth);
    }

    private static List<MissionPlaceHolder> sortIntoList(MissionTreeBean missionTreeBean) {
        // Add everything into an unsorted list.
        List<MissionPlaceHolder> holders = new ArrayList<>();
        Map<Long, MissionPlaceHolder> map = new HashMap<>();
        for (MissionBean missionBean : missionTreeBean.getMissionBeans()) {
            MissionPlaceHolder holder = new MissionPlaceHolder(missionBean);
            holders.add(holder);
            map.put(missionBean.getId(), holder);
        }

        // Add dependencies.
        for (MissionPlaceHolder holder : holders) {
            List<Long> childMissionIds = holder.getMissionBean().getRequiredMissionIds();

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

        boolean needNewRow = false;
        for (MissionPlaceHolder holder : holders) {
            for (MissionPlaceHolder parentHolder : holder.getParents()) {
                needNewRow |= currentRow.contains(parentHolder);
            }

            if (needNewRow) {
                currentRow = new ArrayList<>();
                rows.add(currentRow);
                needNewRow = false;
            }

            currentRow.add(holder);
            holder.setColumn(currentRow.size() - 1);
            holder.setRow(rows.size() - 1);
//            needNewRow |= holder.getMissionTreeBean() != null; WHAT DID THIS DO?
        }
        return rows;
    }

    public static int getMaxColumns(List<List<MissionPlaceHolder>> rows) {
        int max = 0;
        for (List<MissionPlaceHolder> row : rows) {
            max = Math.max(max, row.size());
        }
        return max;
    }

    public static void debugDump(List<List<MissionPlaceHolder>> rows) {
        Log.i(LOG_CAT, "Dump untangled mess: ");
        for (List<MissionPlaceHolder> row : rows) {
            Log.i(LOG_CAT, "- New Row");
            for (MissionPlaceHolder holder : row) {
                StringBuilder sb = new StringBuilder();
                sb.append("-- Holder: " + holder.getMissionBean().getName());

                sb.append(" parents: ");
                for (MissionPlaceHolder parent : holder.getParents()) {
                    sb.append(parent.getMissionBean().getName() + ", ");
                }

                sb.append(" children: ");
                for (MissionPlaceHolder child : holder.getChildren()) {
                    sb.append(child.getMissionBean().getName() + ", ");
                }

                Log.i(LOG_CAT, sb.toString());
            }
        }
    }

    private static List<List<MissionPlaceHolder>> moveChildrenUnderParent(
            List<List<MissionPlaceHolder>> rows) {

        for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
            List<MissionPlaceHolder> row = rows.get(rowIndex);
            for (int columnIndex = 0; columnIndex < row.size(); columnIndex++) {
                MissionPlaceHolder holder = row.get(columnIndex);
                int maxParentRowIndex = -1;
                for (MissionPlaceHolder parent : holder.getParents()) {
                    maxParentRowIndex = Math.max(maxParentRowIndex, parent.getRow());
                }
                if (maxParentRowIndex < holder.getRow() - 1) {
                    moveToRowEnd(rows, holder, maxParentRowIndex + 1);
                    columnIndex--;
                }
            }
        }

        return rows;
    }

    /**
     * Tries to make the column width match the width of the screen. If there is a mission that
     * has no children or parent (an optional mission) and it would extend passed the visible screen
     * horizontally, it'll be moved down.
     */
    private static List<List<MissionPlaceHolder>> compactToMaxWidth(
            List<List<MissionPlaceHolder>> rows,
            int maxColumnWidth) {

        for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
            List<MissionPlaceHolder> row = rows.get(rowIndex);
            for (int columnIndex = row.size() - 1; columnIndex >= maxColumnWidth; columnIndex--) {
                if (columnIndex >= maxColumnWidth) {
                    // Find free slot father down
                    MissionPlaceHolder holder = row.get(columnIndex);
                    int targetRow = findRowWithFreeSlot(rows, rowIndex, maxColumnWidth);
                    moveToRowEnd(rows, holder, targetRow);
                }
            }
        }

        return rows;
    }

    private static int findRowWithFreeSlot(
            List<List<MissionPlaceHolder>> rows,
            int rowIndex,
            int maxColumnWidth) {

        if (rows.get(rowIndex).size() < maxColumnWidth) {
            return rowIndex;
        }

        for (++rowIndex; rowIndex < rows.size(); rowIndex++) {
            List<MissionPlaceHolder> row = rows.get(rowIndex);
            if (row.size() < maxColumnWidth) {
                return rowIndex;
            }
        }
        return rowIndex;
    }

    private static void moveToRowEnd(
            List<List<MissionPlaceHolder>> rows,
            MissionPlaceHolder holder,
            int newRowIndex) {

        if (newRowIndex == rows.size()) {
            rows.add(new ArrayList<MissionPlaceHolder>());
        }

        move(rows, holder, newRowIndex, rows.get(newRowIndex).size());
    }

    private static void move(
            List<List<MissionPlaceHolder>> rows,
            MissionPlaceHolder holder,
            int newRowIndex,
            int newColumnIndex) {

        // Remove from old row
        int oldRowIndex = holder.getRow();
        int oldColumnIndex = holder.getColumn();
        List<MissionPlaceHolder> oldRow = rows.get(oldRowIndex);
        oldRow.remove(oldColumnIndex);

        // Move holders on the old column into the vacated space.
        for (int columnIndex = oldColumnIndex; columnIndex < oldRow.size(); columnIndex++) {
            MissionPlaceHolder otherHolder = oldRow.get(columnIndex);
            assert otherHolder.getColumn() - 1 == columnIndex;
            otherHolder.setColumn(otherHolder.getColumn() - 1);
        }

        // If the row is empty, move everyone up.
        if (oldRow.size() == 0) {
            rows.remove(oldRow);
            for (int rowIndex = oldRowIndex; rowIndex < rows.size(); rowIndex++) {
                List<MissionPlaceHolder> row = rows.get(rowIndex);
                for (int columnIndex = 0; columnIndex < row.size(); columnIndex++) {
                    MissionPlaceHolder otherHolder = row.get(columnIndex);
                    assert otherHolder.getRow() - 1 == rowIndex;
                    otherHolder.setRow(otherHolder.getRow() - 1);
                }
            }
        }

        // Move into new row
        if (newRowIndex == rows.size()) {
            rows.add(new ArrayList<MissionPlaceHolder>());
        }
        List<MissionPlaceHolder> newRow = rows.get(newRowIndex);
        newRow.add(newColumnIndex, holder);
        holder.setRow(newRowIndex);
        holder.setColumn(newRow.indexOf(holder));

        // Move holders in the new row to the right.
        for (int columnIndex = newColumnIndex + 1; columnIndex < newRow.size(); columnIndex++) {
            MissionPlaceHolder otherHolder = newRow.get(columnIndex);
            assert  otherHolder.getColumn() + 1 == columnIndex;
            otherHolder.setColumn(otherHolder.getColumn() + 1);
        }
    }

    // TODO: Move missions up if possible to compact
    // TODO: Make the widest column narrower if needed by moving missions down.
    // TODO: Move parentless missions down if possible without increasing the number of rows.
}
