package com.playposse.peertopeeroxygen.android.student;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.vision.barcode.Barcode;
import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.data.DataRepository;
import com.playposse.peertopeeroxygen.android.data.OxygenSharedPreferences;
import com.playposse.peertopeeroxygen.android.model.ExtraConstants;
import com.playposse.peertopeeroxygen.android.widgets.debug.SelectDebugUserDialogBuilder;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionBean;

public class StudentMissionActivity extends StudentParentActivityWithCameraSource {

    private static final String LOG_CAT = StudentMissionActivity.class.getSimpleName();

    private Long missionLadderId;
    private Long missionTreeId;
    private Long missionId;
    private MissionBean missionBean;

    private TextView missionNameTextView;
    private TextView missionInstructionsTextView;
    private Button startScanButton;
    private SurfaceView surfaceView;

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
        startScanButton = (Button) findViewById(R.id.startScanButton);
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);

        startScanButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!OxygenSharedPreferences.getDebugFlag(getApplicationContext())) {
                            scanForQrCode(surfaceView);
                        } else {
                            pickDebugUser();
                        }
                    }
                }
        );
    }

    @Override
    public void receiveData(DataRepository dataRepository) {
        missionBean = dataRepository
                .getMissionBean(missionLadderId, missionTreeId, missionId);

        missionNameTextView.setText(missionBean.getName());

        if (dataRepository.getMissionCompletion(missionId).getStudyComplete()
                || dataRepository.getUserBean().getAdmin()) {
            missionInstructionsTextView.setText(missionBean.getBuddyInstruction());
        } else {
            missionInstructionsTextView.setText(missionBean.getStudentInstruction());
        }

        setTitle("" + missionBean.getName());
    }

    @Override
    protected void receivedBarcode(Barcode barcode) {
        Log.i(LOG_CAT, "Found bar code: " + barcode.displayValue);
        stopCameraSource();

        dataServiceConnection.getLocalBinder().inviteBuddyToMission(
                new Long(barcode.displayValue),
                missionLadderId,
                missionTreeId,
                missionId);
    }

    private void pickDebugUser() {
        SelectDebugUserDialogBuilder.build(
                this,
                new SelectDebugUserDialogBuilder.DebugUserPickerDialogCallback() {
                    @Override
                    public void onPickedDebugUser(long userId) {
                        Barcode barcode = new Barcode();
                        barcode.displayValue = "" + userId;
                        receivedBarcode(barcode);
                    }
                });
    }
}
