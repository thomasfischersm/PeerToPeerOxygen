package com.playposse.peertopeeroxygen.android.student;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.data.DataRepository;
import com.playposse.peertopeeroxygen.android.model.ExtraConstants;
import com.playposse.peertopeeroxygen.android.model.UserBeanParcelable;
import com.playposse.peertopeeroxygen.android.ui.adapters.InstructionPagerAdapter;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionBean;

/**
 * An activity that shows the mission to a buddy.
 */
public class StudentSeniorBuddyMissionActivity extends StudentParentActivity {

    private static final String LOG_CAT = StudentSeniorBuddyMissionActivity.class.getSimpleName();

    private Long missionLadderId;
    private Long missionTreeId;
    private Long missionId;
    private MissionBean missionBean;
    private UserBeanParcelable studentBean;
    private UserBeanParcelable buddyBean;

    private ViewPager instructionPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_student_senior_buddy_mission);
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        missionLadderId = intent.getLongExtra(ExtraConstants.EXTRA_MISSION_LADDER_ID, -1);
        missionTreeId = intent.getLongExtra(ExtraConstants.EXTRA_MISSION_TREE_ID, -1);
        missionId = intent.getLongExtra(ExtraConstants.EXTRA_MISSION_ID, -1);
        studentBean = intent.getParcelableExtra(ExtraConstants.EXTRA_STUDENT_BEAN);
        buddyBean = intent.getParcelableExtra(ExtraConstants.EXTRA_BUDDY_BEAN);

        instructionPager = (ViewPager) findViewById(R.id.instructionPager);
    }

    @Override
    public void receiveData(DataRepository dataRepository) {
        missionBean = dataRepository.getMissionBean(missionLadderId, missionTreeId, missionId);

        // Instantiate invitation fragment.
        Fragment invitationFragment = StudentSeniorBuddyMissionInvitationFragment.newInstance(
                missionLadderId,
                missionTreeId,
                missionId,
                studentBean,
                buddyBean);

        // Initiate the instruction ViewPager.
        if (instructionPager.getHandler() != null) {// Ensure that the fragments are still attached.
            InstructionPagerAdapter instructionPagerAdapter = new InstructionPagerAdapter(
                    getSupportFragmentManager(),
                    missionBean.getBuddyInstruction(),
                    invitationFragment,
                    missionBean.getBuddyYouTubeVideoId(),
                    false,
                    this);
            instructionPager.setAdapter(instructionPagerAdapter);
        }
    }
}
