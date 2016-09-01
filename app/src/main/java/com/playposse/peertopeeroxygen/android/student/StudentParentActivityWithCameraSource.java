package com.playposse.peertopeeroxygen.android.student;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.SurfaceView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

/**
 * A base activity that manages a {@link CameraSource}. It has to be started and stopped with the
 * activity lifecycle.
 */
public abstract class StudentParentActivityWithCameraSource extends StudentParentActivity {

    private static final String LOG_CAT =
            StudentParentActivityWithCameraSource.class.getSimpleName();

    private CameraSource cameraSource;

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

    protected CameraSource getCameraSource() {
        return cameraSource;
    }

    protected void setCameraSource(CameraSource cameraSource) {
        this.cameraSource = cameraSource;
    }

    protected void stopCameraSource() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (cameraSource != null) {
                    cameraSource.stop();
                    cameraSource.release();
                    setCameraSource(null);
                }
            }
        });
    }

    protected abstract void receivedBarcode(Barcode barcode);

    protected void scanForQrCode(final SurfaceView surfaceView) {
        if (getCameraSource() != null) {
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

                MultiProcessor.Factory<Barcode> trackerFactory =
                        new MultiProcessor.Factory<Barcode>() {
                            @Override
                            public Tracker create(Barcode barcode) {
                                return tracker;
                            }
                        };
                MultiProcessor<Barcode> multiProcessor =
                        new MultiProcessor.Builder<>(trackerFactory).build();
                detector.setProcessor(multiProcessor);

                setCameraSource(new CameraSource.Builder(getApplicationContext(), detector)
                        .setFacing(CameraSource.CAMERA_FACING_BACK)
                        .setRequestedPreviewSize(width, height)
                        .build());
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    Log.e(LOG_CAT, "Mission camera permissions!");
                    return;
                }

                try {
                    getCameraSource().start(surfaceView.getHolder());
                } catch (IOException ex) {
                    Log.e(LOG_CAT, "Failed to start scanning.", ex);
                }

            }
        }).start();
    }
}
