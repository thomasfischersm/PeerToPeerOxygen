package com.playposse.peertopeeroxygen.android.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

/**
 * A P@link ListView} that shows required missions and allows the user to update the required
 * missions with a spinner.
 */
public class RequiredMissionListView extends ListViewNoScroll {

    private static final Long BLANK_MISSION_ID = new Long(-2);

    private List<MissionBean> availableMissions;
    private List<MissionBean> requiredMissions;
    @Nullable
    private MissionBean currentMission;
    private MissionBean blankMission;
    private boolean isDirty = false;

    public RequiredMissionListView(Context context) {
        super(context);
    }

    public RequiredMissionListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RequiredMissionListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setAdapter(
            List<MissionBean> availableMissions,
            List<Long> requiredMissionIds,
            @Nullable MissionBean currentMission) {

        this.availableMissions = new ArrayList<>(availableMissions);
        this.requiredMissions = convertIdsToMissionBeans(requiredMissionIds, availableMissions);
        this.currentMission = currentMission;

        blankMission = new MissionBean();
        blankMission.setId(BLANK_MISSION_ID);
        blankMission.setName(getContext().getString(R.string.blank_required_mission));
        this.availableMissions.add(0, blankMission);
        this.requiredMissions.add(blankMission);

        super.setAdapter(new RequiredMissionBeansArrayAdapter());
    }

    @Override
    protected void rebuild() {
        MissionBean lastRequiredMissionBean = requiredMissions.get(requiredMissions.size() - 1);
        if ((requiredMissions.size() + 1 < availableMissions.size())
                && (!lastRequiredMissionBean.getId().equals(BLANK_MISSION_ID))) {
            requiredMissions.add(blankMission);
        }

        super.rebuild();
    }

    private List<MissionBean> convertIdsToMissionBeans(
            List<Long> ids,
            List<MissionBean> possibleMissions) {

        if ((ids == null) || (possibleMissions == null)) {
            return new ArrayList<>();
        }

        // Build lookup.
        Map<Long, MissionBean> missionBeanMap = new HashMap<>();
        for (MissionBean missionBean : possibleMissions) {
            missionBeanMap.put(missionBean.getId(), missionBean);
        }

        // Build result.
        List<MissionBean> missionBeans = new ArrayList<>();
        for (Long id : ids) {
            missionBeans.add(missionBeanMap.get(id));
        }
        return missionBeans;
    }

    @Override
    public boolean isDirty() {
        return isDirty;
    }

    public List<Long> getRequiredMissionIds() {
        List<Long> ids = new ArrayList<>();
        for (MissionBean missionBean : requiredMissions) {
            if (!missionBean.getId().equals(BLANK_MISSION_ID)) {
                ids.add(missionBean.getId());
            }
        }
        return ids;
    }

    /**
     * {@link ArrayAdapter} for the list of required missions.
     */
    private final class RequiredMissionBeansArrayAdapter
            extends ArrayAdapter<MissionBean> {

        public RequiredMissionBeansArrayAdapter() {
            super(
                    RequiredMissionListView.this.getContext(),
                    R.layout.list_item_mission_ladder,
                    requiredMissions);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.list_item_required_mission, parent, false);

            // Find references.
            final MissionBean missionBean = getItem(position);
            Spinner missionNameSpinner = (Spinner) rowView.findViewById(R.id.missionNameSpinner);
            TextView deleteLink = (TextView) rowView.findViewById(R.id.requiredMissionDeleteLink);

            // Populate spinner
            final List<MissionBean> selectableMissions = new ArrayList<>(availableMissions);
            if (missionBean != null) {
                for (MissionBean requiredMission : requiredMissions) {
                    // If the mission is already selected in another spinner, don't show it in this one.
                    if (!requiredMission.getId().equals(missionBean.getId())) {
                        if ((currentMission == null)
                                || !currentMission.getId().equals(missionBean.getId())) {
                            selectableMissions.remove(requiredMission);
                        }
                    }
                }
            }
            missionNameSpinner.setAdapter(new MissionSpinnerArrayAdapter(
                    getContext(),
                    R.layout.list_item_text_view,
                    selectableMissions));
            missionNameSpinner.setSelection(selectableMissions.indexOf(missionBean));

            // Listen to spinner changes.
            missionNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(
                        AdapterView<?> adapterView,
                        View view,
                        int selectedPosition,
                        long id) {

                    if (selectableMissions.indexOf(missionBean) != selectedPosition) {
                        requiredMissions.remove(missionBean);
                        requiredMissions.add(selectableMissions.get(selectedPosition));
                        isDirty = true;
                        notifyDataSetChanged();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    requiredMissions.remove(missionBean);
                    notifyDataSetChanged();
                }
            });

            // Add delete link.
            if (missionBean.getId() == BLANK_MISSION_ID) {
                ((ViewGroup) deleteLink.getParent()).removeView(deleteLink);
            } else {
                deleteLink.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String msg = String.format(
                                getContext().getString(R.string.confirm_delete_required_mission)
                                , missionBean.getName());
                        ConfirmationDialogBuilder.show(getContext(), msg, new Runnable() {
                            @Override
                            public void run() {
                                requiredMissions.remove(missionBean);
                                isDirty = true;
                                notifyDataSetChanged();
                            }
                        });
                    }
                });
            }

            return rowView;
        }
    }

    /**
     * {@link ArrayAdapter} for the spinner to select a mission.
     */
    private final class MissionSpinnerArrayAdapter
            extends ArrayAdapter<MissionBean>
            implements SpinnerAdapter {

        private final List<MissionBean> missionBeans;

        public MissionSpinnerArrayAdapter(
                Context context,
                int resource,
                List<MissionBean> missionBeans) {

            super(context, resource, missionBeans);

            this.missionBeans = missionBeans;

            setDropDownViewResource(R.layout.list_item_text_view); // remove!
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
//            TextView textView = (TextView) convertView.findViewById(R.id.text_view);
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            TextView textView =
                    (TextView) inflater.inflate(R.layout.list_item_text_view, parent, false);
            textView.setText(getItem(position).getName());
            return textView;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getView(position, convertView, parent);
        }
    }
}
