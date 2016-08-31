package com.playposse.peertopeeroxygen.android.student;

import android.content.Intent;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.vision.barcode.Barcode;
import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.data.DataRepository;
import com.playposse.peertopeeroxygen.android.model.ExtraConstants;
import com.playposse.peertopeeroxygen.android.model.UserBeanParcelable;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionCompletionBean;

/**
 * An activity that shows the mission to a buddy.
 */
public class StudentBuddyMissionActivity extends StudentParentActivityWithCameraSource {

    private static final String LOG_CAT = StudentBuddyMissionActivity.class.getSimpleName();

    private Long missionLadderId;
    private Long missionTreeId;
    private Long missionId;
    private MissionBean missionBean;
    private UserBeanParcelable studentBean;

    private TextView invitationTextView;
    private ImageView studentPhotoImageView;
    private TextView missionNameTextView;
    private TextView missionBuddyDescriptionTextView;
    private Button cancelButton;
    private Button graduateButton;

    // Views only visible when a senior buddy checkout is required.
    private TextView seniorBuddyRequiredTextView;
    private Button startScanButton;
    private SurfaceView surfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_student_buddy_mission);
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        missionLadderId = intent.getLongExtra(ExtraConstants.EXTRA_MISSION_LADDER_ID, -1);
        missionTreeId = intent.getLongExtra(ExtraConstants.EXTRA_MISSION_TREE_ID, -1);
        missionId = intent.getLongExtra(ExtraConstants.EXTRA_MISSION_ID, -1);
        studentBean = intent.getParcelableExtra(ExtraConstants.EXTRA_STUDENT_BEAN);

        invitationTextView = (TextView) findViewById(R.id.invitationTextView);
        studentPhotoImageView = (ImageView) findViewById(R.id.studentPhotoImageView);
        missionNameTextView = (TextView) findViewById(R.id.missionNameTextView);
        missionBuddyDescriptionTextView =
                (TextView) findViewById(R.id.missionBuddyDescriptionTextView);
        cancelButton = (Button) findViewById(R.id.cancelButton);
        graduateButton = (Button) findViewById(R.id.graduateButton);
        seniorBuddyRequiredTextView = (TextView) findViewById(R.id.seniorBuddyRequiredTextView);
        startScanButton = (Button) findViewById(R.id.startScanButton);
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);

        String studentName = studentBean.getFirstName() + " " + studentBean.getLastName();
        String invitation =
                String.format(getString(R.string.mission_invitation_message), studentName);
        invitationTextView.setText(invitation);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        graduateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataServiceConnection
                        .getLocalBinder()
                        .reportMissionComplete(studentBean.getId(), missionId);
                startActivity(new Intent(getApplicationContext(), StudentMainActivity.class));
            }
        });

        startScanButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        scanForQrCode(surfaceView);
                    }
                }
        );
    }

    @Override
    public void receiveData(DataRepository dataRepository) {
        loadProfilePhoto(
                studentPhotoImageView,
                studentBean.getFbProfileId(),
                studentBean.getProfilePictureUrl());

        missionBean = dataRepository
                .getMissionBean(missionLadderId, missionTreeId, missionId);

        missionNameTextView.setText(missionBean.getName());
        missionBuddyDescriptionTextView.setText(
                missionBean.getBuddyInstruction());

        MissionCompletionBean completion = dataRepository.getMissionCompletion(missionId);
        if (!completion.getMentorCheckoutComplete()) {
            // Update visibility of UI elements.
            graduateButton.setEnabled(false);
            seniorBuddyRequiredTextView.setVisibility(View.VISIBLE);
            startScanButton.setVisibility(View.VISIBLE);
            surfaceView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void receivedBarcode(Barcode barcode) {

    }
}
