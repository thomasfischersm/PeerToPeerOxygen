package com.playposse.peertopeeroxygen.android.student;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.playposse.peertopeeroxygen.android.ExtraConstants;
import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.CompleteMissionDataBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionBean;

import java.io.IOException;

public class StudentMissionActivity extends StudentParentActivity {

    public static final String LOG_CAT = StudentMissionActivity.class.getSimpleName();

    private Long missionLadderId;
    private Long missionTreeId;
    private Long missionId;
    private MissionBean missionBean;

    private TextView missionNameTextView;
    private TextView missionInstructionsTextView;
    private Button startScanButton;
    private SurfaceView surfaceView;

    private CameraSource cameraSource;

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
                        scanForQrCode();
                    }
                }
        );
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (cameraSource != null) {
            try {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    Log.e(LOG_CAT, "Mission camera permissions!");
                    return;
                }
                cameraSource.start();
            } catch (IOException ex) {
                Log.e(LOG_CAT, "Failed to start camera!", ex);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (cameraSource != null) {
            cameraSource.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (cameraSource != null) {
            cameraSource.release();
        }
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
                setTitle("" + missionBean.getName());
            }
        });
    }

    private void receivedBarcode(final Barcode barcode) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i(LOG_CAT, "Found bar code: " + barcode.displayValue);
                cameraSource.stop();
                cameraSource.release();
                cameraSource = null;
            }
        });

        dataServiceConnection.getLocalBinder().inviteBuddyToMission(
                new Long(barcode.displayValue),
                missionLadderId,
                missionTreeId,
                missionId);
    }

    private void scanForQrCode() {
        if (cameraSource != null) {
            // already running!
            return;
        }

        final int width = surfaceView.getWidth();
        final int height = surfaceView.getHeight();

        new Thread(new Runnable() {
            @Override
            public void run() {

                BarcodeDetector detector = new BarcodeDetector.Builder(getApplicationContext())
                        .setBarcodeFormats(Barcode.QR_CODE)
                        .build();

                final Tracker<Barcode> tracker = new Tracker<Barcode>() {
                    @Override
                    public void onNewItem(int i, Barcode barcode) {
                        receivedBarcode(barcode);
                    }
                };

//                FocusingProcessor<Barcode> processor =
//                        new FocusingProcessor<Barcode>(detector, tracker) {
//                            @Override
//                            public int selectFocus(Detector.Detections<Barcode> detections) {
//                                Log.i(LOG_CAT, "selectFocus got called.");
//                                return 0;
//                            }
//                        };
//                detector.setProcessor(processor);

                MultiProcessor.Factory trackerFactory = new MultiProcessor.Factory() {
                    @Override
                    public Tracker create(Object o) {
                        return tracker;
                    }
                };
                MultiProcessor<Barcode> multiProcessor =
                        new MultiProcessor.Builder(trackerFactory).build();
                detector.setProcessor(multiProcessor);

                cameraSource = new CameraSource.Builder(getApplicationContext(), detector)
                        .setFacing(CameraSource.CAMERA_FACING_BACK)
                        .setRequestedPreviewSize(width, height)
                        .build();
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    Log.e(LOG_CAT, "Mission camera permissions!");
                    return;
                }

                try {
                    cameraSource.start(surfaceView.getHolder());
                } catch (IOException ex) {
                    Log.e(LOG_CAT, "Failed to start scanning.", ex);
                }

            }
        }).start();
    }
}
