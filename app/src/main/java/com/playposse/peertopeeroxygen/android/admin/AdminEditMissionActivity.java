package com.playposse.peertopeeroxygen.android.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.data.DataRepository;
import com.playposse.peertopeeroxygen.android.data.types.PointType;
import com.playposse.peertopeeroxygen.android.model.ExtraConstants;
import com.playposse.peertopeeroxygen.android.ui.widgets.RequiredMissionListView;
import com.playposse.peertopeeroxygen.android.util.MathUtil;
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
    EditText minimumStudyCountEditText;
    EditText teachPointEditText;
    EditText practicePointEditText;
    EditText heartPointEditText;
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
        minimumStudyCountEditText = (EditText) findViewById(R.id.minimumStudyCountEditText);
        teachPointEditText = (EditText) findViewById(R.id.teachPointEditText);
        practicePointEditText = (EditText) findViewById(R.id.practicePointEditText);
        heartPointEditText = (EditText) findViewById(R.id.heartPointEditText);
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
        if (missionId == -1) {
            // new mission ladder
            nameEditText.setText("");
            minimumStudyCountEditText.setText("1");
            teachPointEditText.setText("1");
            practicePointEditText.setText("0");
            heartPointEditText.setText("0");
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
            minimumStudyCountEditText.setText("" + missionBean.getMinimumStudyCount());
            teachPointEditText.setText(
                    "" + DataRepository.getPointByType(missionBean, PointType.teach));
            practicePointEditText.setText(
                    "" + DataRepository.getPointByType(missionBean, PointType.practice));
            heartPointEditText.setText(
                    "" + DataRepository.getPointByType(missionBean, PointType.heart));
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
                            || (missionBean.getMinimumStudyCount() != MathUtil.tryParseInt(minimumStudyCountEditText.getText().toString(), 1))
                            || hasPointCountChanged(teachPointEditText, PointType.teach)
                            || hasPointCountChanged(practicePointEditText, PointType.practice)
                            || hasPointCountChanged(heartPointEditText, PointType.heart)
                            || !studentInstructionEditText.getText().toString().equals(missionBean.getStudentInstruction())
                            || !buddyInstructionEditText.getText().toString().equals(missionBean.getBuddyInstruction())
                            || requiredMissionsListView.isDirty();
        }

        // Save mission.
        if (shouldSave) {
            missionBean.setName(nameEditText.getText().toString());
            missionBean.setMinimumStudyCount(MathUtil.tryParseInt(minimumStudyCountEditText.getText().toString(), 1));
            setPointOnMissionBean(teachPointEditText, PointType.teach);
            setPointOnMissionBean(practicePointEditText, PointType.practice);
            setPointOnMissionBean(heartPointEditText, PointType.heart);
            missionBean.setStudentInstruction(studentInstructionEditText.getText().toString());
            missionBean.setBuddyInstruction(buddyInstructionEditText.getText().toString());
            missionBean.setRequiredMissionIds(requiredMissionsListView.getRequiredMissionIds());

            dataServiceConnection
                    .getLocalBinder()
                    .save(missionLadderId, missionTreeId, missionBean);
        }
    }

    private void setPointOnMissionBean(EditText editText, PointType pointType) {
        int pointCount = MathUtil.tryParseInt(editText.getText().toString(), 0);
        DataRepository.setPoint(missionBean, pointCount, pointType);
    }

    private boolean hasPointCountChanged(EditText editText, PointType pointType) {
        int originalCount = DataRepository.getPointByType(missionBean, pointType);
        int newCount = MathUtil.tryParseInt(editText.getText().toString(), 0);
        return originalCount != newCount;
    }
}
