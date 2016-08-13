package com.playposse.peertopeeroxygen.android.student;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.playposse.peertopeeroxygen.android.ExtraConstants;
import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.CompleteMissionDataBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionBean;

public class StudentMissionActivity extends StudentParentActivity {

    private Long missionLadderId;
    private Long missionTreeId;
    private Long missionId;
    private MissionBean missionBean;

    private TextView missionNameTextView;
    private TextView missionInstructionsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_student_mission);
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        missionLadderId = intent.getLongExtra(ExtraConstants.EXTRA_MISSION_LADDER_ID, -1);
        missionTreeId = intent.getLongExtra(ExtraConstants.EXTRA_MISSION_TREE_ID, -1);
        missionId = intent.getLongExtra(ExtraConstants.EXTRA_MISSION_ID, -1);

        missionNameTextView = (TextView) findViewById(R.id.missionNameTextView);
        missionInstructionsTextView = (TextView) findViewById(R.id.missionInstructionsTextView);
    }

    @Override
    public void receiveData(CompleteMissionDataBean completeMissionDataBean) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                missionBean = dataServiceConnection
                        .getLocalBinder()
                        .getMissionBean(missionLadderId, missionTreeId, missionId);

                missionNameTextView.setText(missionBean.getName());
                missionInstructionsTextView.setText(missionBean.getStudentInstruction());
            }
        });

    }
}
