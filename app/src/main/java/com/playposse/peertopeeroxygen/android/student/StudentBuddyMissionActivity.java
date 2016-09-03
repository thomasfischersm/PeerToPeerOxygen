package com.playposse.peertopeeroxygen.android.student;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.google.android.gms.vision.barcode.Barcode;
import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.data.DataRepository;
import com.playposse.peertopeeroxygen.android.model.ExtraConstants;
import com.playposse.peertopeeroxygen.android.model.UserBeanParcelable;
import com.playposse.peertopeeroxygen.android.ui.adapters.InstructionPagerAdapter;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionCompletionBean;

/**
 * An activity that shows the mission to a buddy.
 */
public class StudentBuddyMissionActivity
        extends StudentParentActivity
        implements QrCodeScannerFragment.QrCodeScannerCallback {

    private static final String LOG_CAT = StudentBuddyMissionActivity.class.getSimpleName();

    private Long missionLadderId;
    private Long missionTreeId;
    private Long missionId;
    private MissionBean missionBean;
    private UserBeanParcelable studentBean;

    private ViewPager instructionPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_student_buddy_mission);
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        missionLadderId = intent.getLongExtra(ExtraConstants.EXTRA_MISSION_LADDER_ID, -1);
        missionTreeId = intent.getLongExtra(ExtraConstants.EXTRA_MISSION_TREE_ID, -1);
        missionId = intent.getLongExtra(ExtraConstants.EXTRA_MISSION_ID, -1);
        studentBean = intent.getParcelableExtra(ExtraConstants.EXTRA_STUDENT_BEAN);

        instructionPager = (ViewPager) findViewById(R.id.instructionPager);

        String studentName = studentBean.getFirstName() + " " + studentBean.getLastName();
        String invitation =
                String.format(getString(R.string.mission_invitation_message), studentName);
    }

    @Override
    public void receiveData(DataRepository dataRepository) {
        missionBean = dataRepository.getMissionBean(missionLadderId, missionTreeId, missionId);

        // Do necessary things for senior buddy requirement.
        MissionCompletionBean completion = dataRepository.getMissionCompletion(missionId);
        boolean requiresSeniorBuddy =
                !completion.getMentorCheckoutComplete() && !dataRepository.getUserBean().getAdmin();

        // Instantiate invitation fragment.
        Fragment invitationFragment = StudentBuddyMissionInvitationFragment.newInstance(
                missionLadderId,
                missionTreeId,
                missionId,
                studentBean);

        // Initiate the instruction ViewPager.
        if (instructionPager.getHandler() != null) {// Ensure that the fragments are still attached.
            InstructionPagerAdapter instructionPagerAdapter = new InstructionPagerAdapter(
                    getSupportFragmentManager(),
                    missionBean.getBuddyInstruction(),
                    invitationFragment,
                    requiresSeniorBuddy,
                    this);
            instructionPager.setAdapter(instructionPagerAdapter);
        }
    }

    @Override
    public void receivedBarcode(Barcode barcode) {
        dataServiceConnection.getLocalBinder().inviteSeniorBuddyToMission(
                studentBean.getId(),
                new Long(barcode.displayValue),
                missionLadderId,
                missionTreeId,
                missionId);
    }
}
