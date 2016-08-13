package com.playposse.peertopeeroxygen.android.student;

import android.content.Intent;
import android.os.Bundle;

import com.playposse.peertopeeroxygen.android.ExtraConstants;
import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.widgets.MissionTreeWidget;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.CompleteMissionDataBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionTreeBean;

/**
 * Activity that shows the student the {@link MissionTreeBean}.
 */
public class StudentMissionTreeActivity extends StudentParentActivity {

    private Long missionLadderId;
    private Long missionTreeId;
    private MissionTreeBean missionTreeBean;

    private MissionTreeWidget missionTreeWidget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_student_mission_tree);
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        missionLadderId = intent.getLongExtra(ExtraConstants.EXTRA_MISSION_LADDER_ID, -1);
        missionTreeId = intent.getLongExtra(ExtraConstants.EXTRA_MISSION_TREE_ID, -1);

        missionTreeWidget = (MissionTreeWidget) findViewById(R.id.missionTreeWidget);
    }

    @Override
    public void receiveData(CompleteMissionDataBean completeMissionDataBean) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                missionTreeBean = dataServiceConnection
                        .getLocalBinder()
                        .getMissionTreeBean(missionLadderId, missionTreeId);
                missionTreeWidget.setMissionTreeBean(missionLadderId, missionTreeBean);
                setTitle("" + missionTreeBean.getName());
            }
        });
    }
}
