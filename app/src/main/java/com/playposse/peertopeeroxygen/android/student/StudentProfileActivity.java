package com.playposse.peertopeeroxygen.android.student;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.data.DataRepository;
import com.playposse.peertopeeroxygen.android.data.facebook.FacebookProfilePhotoCache;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.UserBean;

/**
 * An activity that shows the profile information of the student.
 */
public class StudentProfileActivity extends StudentParentActivity {

    private static final String LOG_CAT = StudentProfileActivity.class.getSimpleName();

    private static final int WHITE = 0xFFFFFFFF;
    private static final int BLACK = 0xFF000000;

    private ImageView profilePhotoImageView;
    private TextView profileNameTextView;
    private ImageView qrCodeImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_student_profile);
        super.onCreate(savedInstanceState);

        profilePhotoImageView = (ImageView) findViewById(R.id.profilePhotoImageView);
        profileNameTextView = (TextView) findViewById(R.id.profileNameTextView);
        qrCodeImageView = (ImageView) findViewById(R.id.qrCodeImageView);

        setTitle(getString(R.string.profile_title));
    }

    @Override
    public void receiveData(final DataRepository dataRepository) {
        final UserBean userBean = dataRepository.getUserBean();
        FacebookProfilePhotoCache photoCache = dataServiceConnection
                .getLocalBinder()
                .getDataRepository()
                .getFacebookProfilePhotoCache();
        photoCache.loadImage(
                this,
                profilePhotoImageView,
                userBean.getFbProfileId(),
                userBean.getProfilePictureUrl());

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final Bitmap qrCodeBitMap = generateQrCode(
                            userBean.getId().toString(),
                            qrCodeImageView.getWidth(),
                            qrCodeImageView.getHeight());

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String fullName =
                                    userBean.getFirstName() + " " + userBean.getLastName();
                            profileNameTextView.setText(fullName);

                            qrCodeImageView.setImageBitmap(qrCodeBitMap);
                            qrCodeImageView.invalidate();
                        }
                    });
                } catch (WriterException ex) {
                    Log.e(LOG_CAT, "Failed to render QR code.", ex);
                }
            }
        }).start();
    }

    private Bitmap generateQrCode(String str, int width, int height) throws WriterException {
        // Generate QR code.
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(str, BarcodeFormat.QR_CODE, width, height);

        // Copy QR code into int array.
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = bitMatrix.get(x, y) ? BLACK : WHITE;
            }
        }

        // Copy int array into Bitmap.
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }
}
