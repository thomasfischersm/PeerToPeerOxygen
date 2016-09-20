package com.playposse.peertopeeroxygen.android.student;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.data.OxygenSharedPreferences;
import com.playposse.peertopeeroxygen.android.ui.debug.SelectDebugUserDialogBuilder;
import com.playposse.peertopeeroxygen.android.util.ToastUtil;

import java.io.IOException;

/**
 * A {@link Fragment} that scans QR codes.
 */
public class QrCodeScannerFragment extends Fragment {

    private static final String LOG_CAT = QrCodeScannerFragment.class.getSimpleName();

    private static final int CAMERA_PERMISSION_REQUEST = 1;

    private CameraSource cameraSource;
    private QrCodeScannerCallback qrCodeScannerCallback;
    private SurfaceView surfaceView;
    private boolean hasRequestedCameraPermission = false;

    /**
     * The {@link Fragment#getUserVisibleHint()} has a bug. It doesn't always show the right value.
     * So, let's keep track of it ourselves.
     */
    private boolean userVisibleHint;

    int counter = 0;
    public QrCodeScannerFragment() {
        Log.i(LOG_CAT, "QrCodeScannerFragment instance count: " + (++counter));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView =
                inflater.inflate(R.layout.fragment_qr_code_scanner, container, false);

        surfaceView = (SurfaceView) rootView.findViewById(R.id.surfaceView);
        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof QrCodeScannerCallback) {
            qrCodeScannerCallback = (QrCodeScannerCallback) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement QrCodeScannerCallback");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        qrCodeScannerCallback = null;
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.i(LOG_CAT, "onResume is called");
        onReallyResume();
    }

    /**
     * Activates the {@link Fragment}.
     *
     * <p>The {@link android.support.v4.view.ViewPager} is squirelly where it calls
     * {@link #onResume()} before the {@link Fragment} is actually visible. It tries to cache the
     * next {@link Fragment} before it shows up.
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        userVisibleHint = isVisibleToUser;
//        Log.i(LOG_CAT, "super  : " + getUserVisibleHint() + " " + myState + " " + this);
//        Log.i(LOG_CAT, "Called setUserVisibleHint " + isVisibleToUser + " " + myState);
        onReallyResume();
    }

    private void onReallyResume() {
        Log.i(LOG_CAT, "onReallyResume started with surfaceView: " + surfaceView);
//        Log.i(LOG_CAT, "insanity: " + getUserVisibleHint() + " " + myState + " " + this.toString());
        if (surfaceView != null) {
            surfaceView.post(new Runnable() {
                @Override
                public void run() {
                    if (userVisibleHint) {
                        if (getActivity() != null) {
                            startResumeCamera();
                        }
                    } else {
                        if (cameraSource != null) {
                            cameraSource.stop();
                        }
                    }
                }
            });
        }
    }

    private void startResumeCamera() {
        if (cameraSource == null) {
            // Start camera.
            Log.i(LOG_CAT, "Attempting to start camera");
            if (!OxygenSharedPreferences.getDebugFlag(getActivity())) {
                scanForQrCode(surfaceView);
            } else {
                pickDebugUser();
            }
        } else {
            // Continue paused camera.
            try {
                if (!checkCameraPermission()) {
                    return;
                }
                cameraSource.start(surfaceView.getHolder());
                Log.i(LOG_CAT, "Resuming camera");
            } catch (IOException ex) {
                Log.e(LOG_CAT, "Failed to start camera!", ex);
            }
        }
    }

    private boolean checkCameraPermission() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            Log.e(LOG_CAT, "Mission camera permissions!");
            if (!hasRequestedCameraPermission) {
                ActivityCompat.requestPermissions(
                        getActivity(),
                        new String[]{Manifest.permission.CAMERA},
                        CAMERA_PERMISSION_REQUEST);
                hasRequestedCameraPermission = true;
            }
            return false;
        }
        return true;
    }

    @Override
    public void onPause() {
        super.onPause();

        if (cameraSource != null) {
            cameraSource.stop();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (cameraSource != null) {
            cameraSource.release();
            cameraSource = null;
        }
    }

    protected CameraSource getCameraSource() {
        return cameraSource;
    }

    protected void setCameraSource(CameraSource cameraSource) {
        this.cameraSource = cameraSource;
    }

    protected void stopCameraSource() {
        getActivity().runOnUiThread(new Runnable() {
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

    protected void receivedBarcode(Barcode barcode) {
        Log.i(LOG_CAT, "Found bar code: " + barcode.displayValue);

        stopCameraSource();

        if (qrCodeScannerCallback != null) {
            qrCodeScannerCallback.receivedBarcode(barcode);
        }
    }

    protected void scanForQrCode(final SurfaceView surfaceView) {
        if (getCameraSource() != null) {
            // already running!
            return;
        }

        final int width = surfaceView.getWidth();
        final int height = surfaceView.getHeight();

        if ((width == 0) || (height == 0)) {
            Log.i(LOG_CAT, "Couldn't start camera because the SurfaceView was still 0.");
            return;
        }

        if (!hasCamera()) {
            ToastUtil.sendToast(getContext(), R.string.no_camera_detected_toast);
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {

                BarcodeDetector detector = new BarcodeDetector.Builder(getActivity())
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
                            public Tracker<Barcode> create(Barcode barcode) {
                                return tracker;
                            }
                        };
                MultiProcessor<Barcode> multiProcessor =
                        new MultiProcessor.Builder<>(trackerFactory).build();
                detector.setProcessor(multiProcessor);

                setCameraSource(new CameraSource.Builder(getActivity(), detector)
                        .setFacing(getPreferredCameraFacing())
                        .setRequestedPreviewSize(width, height)
                        .build());
                if (!checkCameraPermission()) {
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

    /**
     * Determines if the preferred camera facing (= back) is available. If not, it fails back to
     * front facing.
     */
    private int getPreferredCameraFacing() {
        PackageManager packageManager = getContext().getPackageManager();
        boolean hasFrontCamera =
                packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT);
        int cameraCount = Camera.getNumberOfCameras();

        if ((cameraCount > 1) || (!hasFrontCamera)) {
            return CameraSource.CAMERA_FACING_BACK;
        } else {
            return CameraSource.CAMERA_FACING_FRONT;
        }
    }

    private boolean hasCamera() {
        PackageManager packageManager = getContext().getPackageManager();
        return packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
                && (Camera.getNumberOfCameras() > 0);
    }

    private void pickDebugUser() {
        SelectDebugUserDialogBuilder.build(
                getActivity(),
                new SelectDebugUserDialogBuilder.DebugUserPickerDialogCallback() {
                    @Override
                    public void onPickedDebugUser(long userId) {
                        Barcode barcode = new Barcode();
                        barcode.displayValue = "" + userId;
                        receivedBarcode(barcode);
                    }
                });
    }

    /**
     * Callback to the activity to report that a QR code has been scanned in.
     */
    public interface QrCodeScannerCallback {

        void receivedBarcode(Barcode barcode);
    }
}
