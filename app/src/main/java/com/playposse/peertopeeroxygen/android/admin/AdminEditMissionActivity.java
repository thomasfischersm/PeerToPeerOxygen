package com.playposse.peertopeeroxygen.android.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.data.DataRepository;
import com.playposse.peertopeeroxygen.android.model.ExtraConstants;
import com.playposse.peertopeeroxygen.android.widgets.RequiredMissionListView;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionTreeBean;

/**
 * {@link android.app.Activity} that shows, edits, and creates a specific mission.
 */
public class AdminEditMissionActivity extends AdminParentActivity {

    private Long missionLadderId;
    private Long missionTreeId;
    private Long missionId;
    private MissionBean missionBean;
    private MissionTreeBean missionTreeBean;

    EditText nameEditText;
    EditText studentInstructionEditText;
    EditText buddyInstructionEditText;
    private RequiredMissionListView requiredMissionsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_admin_edit_mission);
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        missionLadderId = intent.getLongExtra(ExtraConstants.EXTRA_MISSION_LADDER_ID, -1);
        missionTreeId = intent.getLongExtra(ExtraConstants.EXTRA_MISSION_TREE_ID, -1);
        missionId = intent.getLongExtra(ExtraConstants.EXTRA_MISSION_ID, -1);
        missionBean = null;

        nameEditText = (EditText) findViewById(R.id.missionNameEditText);
        studentInstructionEditText = (EditText) findViewById(R.id.studentInstructionsEditText);
        buddyInstructionEditText = (EditText) findViewById(R.id.buddyInstructionsEditText);
        requiredMissionsListView =
                (RequiredMissionListView) findViewById(R.id.requiredMissionsListView);
    }

    @Override
    protected void onPause() {
        super.onPause();

        saveIfNecessary();
    }

    @Override
    public void receiveData(final DataRepository dataRepository) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (missionId == -1) {
                    // new mission ladder
                    nameEditText.setText("");
                    studentInstructionEditText.setText("");
                    buddyInstructionEditText.setText("");

                    setTitle(String.format(
                            getString(R.string.edit_mission_title),
                            getString(R.string.new_entity)));
                } else {
                    // existing mission ladder
                    missionBean = dataRepository.getMissionBean(
                                    missionLadderId,
                                    missionTreeId,
                                    missionId);
                    missionTreeBean = dataRepository.getMissionTreeBean(
                            missionLadderId,
                            missionTreeId);

                    nameEditText.setText(missionBean.getName());
                    studentInstructionEditText.setText(missionBean.getStudentInstruction());
                    buddyInstructionEditText.setText(missionBean.getBuddyInstruction());

                    requiredMissionsListView.setAdapter(
                            missionTreeBean.getMissionBeans(),
                            missionBean.getRequiredMissionIds(),
                            missionBean);

                    setTitle(String.format(
                            getString(R.string.edit_mission_title),
                            missionBean.getName()));
                }
            }
        });
    }

    private void saveIfNecessary() {
        // Determine if data should be saved.
        boolean shouldSave = false;
        if (missionBean == null) {
            if (nameEditText.getText().length() > 0) {
                shouldSave = true;
                missionBean = new MissionBean();
            }
        } else {
            shouldSave =
                    !nameEditText.getText().toString().equals(missionBean.getName())
                    || !studentInstructionEditText.getText().toString().equals(missionBean.getStudentInstruction())
                    || !buddyInstructionEditText.getText().toString().equals(missionBean.getBuddyInstruction())
                    || requiredMissionsListView.isDirty();
        }

        // Save mission.
        if (shouldSave) {
            missionBean.setName(nameEditText.getText().toString());
            missionBean.setStudentInstruction(studentInstructionEditText.getText().toString());
            missionBean.setBuddyInstruction(buddyInstructionEditText.getText().toString());
            missionBean.setRequiredMissionIds(requiredMissionsListView.getRequiredMissionIds());

            dataServiceConnection
                    .getLocalBinder()
                    .save(missionLadderId, missionTreeId, missionBean);
        }
    }
}
