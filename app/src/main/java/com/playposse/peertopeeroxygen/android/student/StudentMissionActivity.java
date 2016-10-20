package com.playposse.peertopeeroxygen.android.student;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.google.android.gms.vision.barcode.Barcode;
import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.data.DataRepository;
import com.playposse.peertopeeroxygen.android.model.ExtraConstants;
import com.playposse.peertopeeroxygen.android.ui.adapters.InstructionPagerAdapter;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionBean;

public class StudentMissionActivity
        extends StudentParentActivity
        implements QrCodeScannerFragment.QrCodeScannerCallback{

    private static final String LOG_CAT = StudentMissionActivity.class.getSimpleName();

    private Long missionLadderId;
    private Long missionTreeId;
    private Long missionId;
    private MissionBean missionBean;

    private ViewPager instructionPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_student_mission);
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        missionLadderId = intent.getLongExtra(ExtraConstants.EXTRA_MISSION_LADDER_ID, -1);
        missionTreeId = intent.getLongExtra(ExtraConstants.EXTRA_MISSION_TREE_ID, -1);
        missionId = intent.getLongExtra(ExtraConstants.EXTRA_MISSION_ID, -1);

        instructionPager = (ViewPager) findViewById(R.id.instructionPager);
        Log.i(LOG_CAT, "onCreate is done");
    }

    @Override
    public void receiveData(DataRepository dataRepository) {
        missionBean = dataRepository
                .getMissionBean(missionLadderId, missionTreeId, missionId);

        final String instruction;
        final String videoId;
        if (dataRepository.getMissionCompletion(missionId).getStudyComplete()
                || dataRepository.getUserBean().getAdmin()) {
            instruction = missionBean.getBuddyInstruction();
            videoId = missionBean.getBuddyYouTubeVideoId();
        } else {
            instruction = missionBean.getStudentInstruction();
            videoId = missionBean.getStudentYouTubeVideoId();
        }

        instructionPager.post(new Runnable() {
            @Override
            public void run() {
                if (instructionPager.getHandler() != null) {// Ensure that the fragments are still attached.
                    instructionPager.setAdapter(new InstructionPagerAdapter(
                            StudentMissionActivity.this.getSupportFragmentManager(),
                            instruction,
                            null, /* invitationFragment */
                            videoId,
                            true, /* enableScanner */
                            StudentMissionActivity.this));
                } else {
                    Log.i(LOG_CAT, "instructionPager.getHandler() is NOT attached during receiveData.");
                }
            }
        });

        setTitle("" + missionBean.getName());
        Log.i(LOG_CAT, "receiveData is done");
    }

    @Override
    public void receivedBarcode(Barcode barcode) {
        dataServiceConnection.getLocalBinder().inviteBuddyToMission(
                new Long(barcode.displayValue),
                missionLadderId,
                missionTreeId,
                missionId);
    }
}
