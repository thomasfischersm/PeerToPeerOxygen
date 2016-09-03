package com.playposse.peertopeeroxygen.android.student;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.data.DataRepository;
import com.playposse.peertopeeroxygen.android.model.ExtraConstants;
import com.playposse.peertopeeroxygen.android.ui.widgets.MissionTreeWidget;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionTreeBean;

/**
 * Activity that shows the student the {@link MissionTreeBean}.
 */
public class StudentMissionTreeActivity extends StudentParentActivity {

    private Long missionLadderId;
    private Long missionTreeId;
    private MissionTreeBean missionTreeBean;

    private MissionTreeWidget missionTreeWidget;
    private ImageButton levelDownImageButton;
    private ImageButton levelUpImageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_student_mission_tree);
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        missionLadderId = intent.getLongExtra(ExtraConstants.EXTRA_MISSION_LADDER_ID, -1);
        missionTreeId = intent.getLongExtra(ExtraConstants.EXTRA_MISSION_TREE_ID, -1);

        missionTreeWidget = (MissionTreeWidget) findViewById(R.id.missionTreeWidget);
        levelDownImageButton = (ImageButton) findViewById(R.id.levelDownImageButton);
        levelUpImageButton = (ImageButton) findViewById(R.id.levelUpImageButton);
    }

    @Override
    public void receiveData(DataRepository dataRepository) {
        missionTreeBean = dataRepository
                .getMissionTreeBean(missionLadderId, missionTreeId);
        missionTreeWidget.setMissionTreeBean(
                missionLadderId,
                missionTreeBean,
                dataRepository);
        String title = String.format(
                getString(R.string.student_mission_tree_activity_title),
                missionTreeBean.getLevel(),
                missionTreeBean.getName());
        setTitle(title);

        initLevelButton(dataRepository, levelDownImageButton, missionTreeBean.getLevel() - 1);
        initLevelButton(dataRepository, levelUpImageButton, missionTreeBean.getLevel() + 1);
    }

    private void initLevelButton(DataRepository dataRepository, ImageButton button, int level) {
        final MissionTreeBean otherMissionTreeBean = dataRepository.getMissionTreeBeanByLevel(
                missionLadderId,
                level);
        button.setVisibility(otherMissionTreeBean != null ? View.VISIBLE : View.GONE);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent intent = ExtraConstants.createIntent(
                        getApplicationContext(),
                        StudentMissionTreeActivity.class,
                        missionLadderId,
                        otherMissionTreeBean.getId(),
                        null);
                startActivity(intent);
            }
        });
    }
}
