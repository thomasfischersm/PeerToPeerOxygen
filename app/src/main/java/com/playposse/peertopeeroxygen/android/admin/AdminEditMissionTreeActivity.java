package com.playposse.peertopeeroxygen.android.admin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.data.DataRepository;
import com.playposse.peertopeeroxygen.android.model.ExtraConstants;
import com.playposse.peertopeeroxygen.android.ui.adapters.MissionSpinnerArrayAdapter;
import com.playposse.peertopeeroxygen.android.ui.dialogs.ConfirmationDialogBuilder;
import com.playposse.peertopeeroxygen.android.ui.widgets.ListViewNoScroll;
import com.playposse.peertopeeroxygen.android.ui.widgets.RequiredMissionListView;
import com.playposse.peertopeeroxygen.android.util.StringUtil;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionLadderBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionTreeBean;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link android.app.Activity} that shows, edits, and creates a specific mission tree.
 */
public class AdminEditMissionTreeActivity extends AdminParentActivity {

    public static final int DEFAULT_LEVEL = 1;

    private Long missionLadderId;
    private Long missionTreeId;
    private MissionLadderBean missionLadderBean;
    private MissionTreeBean missionTreeBean;
    private MissionBean bossMissionBean;

    private TextView createMissionLink;
    private EditText nameEditText;
    private EditText descriptionEditText;
    private Spinner bossMissionSpinner;
    private ListViewNoScroll missionsListView;
    private RequiredMissionListView requiredMissionsListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_admin_edit_mission_tree);
        super.onCreate(savedInstanceState);

        missionLadderId = getIntent().getLongExtra(ExtraConstants.EXTRA_MISSION_LADDER_ID, -1);
        missionTreeId = getIntent().getLongExtra(ExtraConstants.EXTRA_MISSION_TREE_ID, -1);
        missionTreeBean = null;

        createMissionLink = (TextView) findViewById(R.id.createMissionLink);
        nameEditText = (EditText) findViewById(R.id.missionTreeNameEditText);
        descriptionEditText = (EditText) findViewById(R.id.missionTreeDescriptionEditText);
        bossMissionSpinner = (Spinner) findViewById(R.id.bossMissionSpinner);
        missionsListView = (ListViewNoScroll) findViewById(R.id.missionsListView);
        requiredMissionsListView =
                (RequiredMissionListView) findViewById(R.id.requiredMissionsListView);

        createMissionLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveIfNecessary();

                Intent intent = new Intent(
                        getApplicationContext(),
                        AdminEditMissionActivity.class);
                intent.putExtra(ExtraConstants.EXTRA_MISSION_LADDER_ID, missionLadderId);
                intent.putExtra(ExtraConstants.EXTRA_MISSION_TREE_ID, missionTreeId);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        saveIfNecessary();
    }

    private void saveIfNecessary() {
        // Determine if the data should be saved.
        boolean shouldSave = false;
        if (missionTreeBean == null) {
            // Check if any data has been entered in the name field.
            if (nameEditText.getText().length() > 0) {
                missionTreeBean = new MissionTreeBean();
                missionTreeBean.setLevel(determineNextLevel());
                shouldSave = true;
            }
        } else {
            // Check if changes have been made.
            boolean isBossMissionDirty;
            if ((bossMissionBean == null) && (missionTreeBean.getBossMissionId() == null)) {
                isBossMissionDirty = false;
            } else if (bossMissionBean == null) {
                isBossMissionDirty = true;
            } else {
                isBossMissionDirty = !bossMissionBean.getId().equals(missionTreeBean.getBossMissionId());
            }

            shouldSave = !StringUtil.equals(nameEditText, missionTreeBean.getName())
                    || !StringUtil.equals(descriptionEditText, missionTreeBean.getDescription())
                    || isBossMissionDirty
                    || requiredMissionsListView.isDirty();
        }

        // Save if necessary.
        if (shouldSave) {
            missionTreeBean.setName(nameEditText.getText().toString());
            missionTreeBean.setDescription(descriptionEditText.getText().toString());
            missionTreeBean.setBossMissionId((bossMissionBean != null) ? bossMissionBean.getId() : null);
            missionTreeBean.setRequiredMissionIds(requiredMissionsListView.getRequiredMissionIds());
            dataServiceConnection.getLocalBinder().save(missionLadderId, missionTreeBean);
        }
    }

    @Override
    public void receiveData(final DataRepository dataRepository) {
        if (missionTreeId == -1) {
            // new mission ladder
            nameEditText.setText("");
            descriptionEditText.setText("");

            setTitle(String.format(
                    getString(R.string.edit_mission_tree_title),
                    getString(R.string.new_entity)));
        } else {
            // existing mission ladder
            missionTreeBean = dataRepository.getMissionTreeBean(
                    missionLadderId,
                    missionTreeId);
            nameEditText.setText(missionTreeBean.getName());
            descriptionEditText.setText(missionTreeBean.getDescription());

            setTitle(String.format(
                    getString(R.string.edit_mission_tree_title),
                    missionTreeBean.getName()));

            // Load boss mission spinner.
            final List<MissionBean> possibleBossMissions = getPossibleBossMissions(missionTreeBean);
            bossMissionSpinner.setAdapter(new MissionSpinnerArrayAdapter(
                    this,
                    R.layout.list_item_text_view,
                    possibleBossMissions));
            if (missionTreeBean.getBossMissionId() != null) {
                bossMissionBean = dataRepository.getMissionBean(
                        missionLadderId,
                        missionTreeId,
                        missionTreeBean.getBossMissionId());
                bossMissionSpinner.setSelection(possibleBossMissions.indexOf(bossMissionBean));
            } else {
                bossMissionBean = null;
            }
            bossMissionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(
                        AdapterView<?> adapterView,
                        View view,
                        int selectedPosition,
                        long id) {

                    bossMissionBean = possibleBossMissions.get(selectedPosition);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    bossMissionBean = null;
                }
            });

            // Load child missions.
            MissionBeanArrayAdapter adapter = new MissionBeanArrayAdapter(
                    missionTreeBean.getMissionBeans());
            missionsListView.setAdapter(adapter);

            requiredMissionsListView.setAdapter(
                    missionTreeBean.getMissionBeans(),
                    missionTreeBean.getRequiredMissionIds(),
                    null);
        }

        missionLadderBean = dataRepository.getMissionLadderBean(missionLadderId);
    }

    /**
     * Checks the current levels an determines what the next level would be.
     */
    private int determineNextLevel() {
        int level = DEFAULT_LEVEL;
        for (MissionTreeBean missionTreeBean : missionLadderBean.getMissionTreeBeans()) {
            level = Math.max(level, missionTreeBean.getLevel() + 1);
        }
        return level;
    }

    /**
     * Removes all missions that have a parent. If there is a parent, it shouldn't be the boss
     * mission.
     */
    private List<MissionBean> getPossibleBossMissions(MissionTreeBean missionTreeBean) {
        if (missionTreeBean.getMissionBeans() == null) {
            return new ArrayList<>(0);
        }

        List<MissionBean> missions = new ArrayList<>(missionTreeBean.getMissionBeans());
        for (MissionBean mission : missionTreeBean.getMissionBeans()) {
            List<Long> childMissionIds = mission.getRequiredMissionIds();
            if (childMissionIds != null) {
                for (int i = missions.size() - 1; i >= 0; i--) {
                    MissionBean possibleChildMission = missions.get(i);
                    if (childMissionIds.contains(possibleChildMission.getId())) {
                        missions.remove(possibleChildMission);
                    }
                }
            }
        }
        return missions;
    }

    private final class MissionBeanArrayAdapter
            extends ArrayAdapter<MissionBean> {

        public MissionBeanArrayAdapter(List<MissionBean> objects) {
            super(getApplicationContext(), R.layout.list_item_mission, objects);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) AdminEditMissionTreeActivity.this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.list_item_mission, parent, false);

            final MissionBean missionBean = getItem(position);
            TextView missionNameLink = (TextView) rowView.findViewById(R.id.missionNameLink);
            missionNameLink.setText(missionBean.getName());
            missionNameLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    saveIfNecessary();

                    Long missionId = Long.valueOf(missionBean.getId());
                    Intent intent = new Intent(
                            getApplicationContext(),
                            AdminEditMissionActivity.class);
                    intent.putExtra(ExtraConstants.EXTRA_MISSION_LADDER_ID, missionLadderId);
                    intent.putExtra(ExtraConstants.EXTRA_MISSION_TREE_ID, missionTreeId);
                    intent.putExtra(ExtraConstants.EXTRA_MISSION_ID, missionId);
                    startActivity(intent);
                }
            });

            TextView missionDeleteLink =
                    (TextView) rowView.findViewById(R.id.missionDeleteLink);
            missionDeleteLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String deleteMessage = String.format(
                            getString(R.string.confirm_delete_mission_message),
                            missionBean.getName());
                    ConfirmationDialogBuilder.show(AdminEditMissionTreeActivity.this, deleteMessage, new Runnable() {
                        @Override
                        public void run() {
                            dataServiceConnection.getLocalBinder().deleteMission(
                                    missionLadderBean.getId(),
                                    missionTreeBean.getId(),
                                    missionBean.getId());
                        }
                    });
                }
            });

            return rowView;
        }
    }
}
