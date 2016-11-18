package com.playposse.peertopeeroxygen.android.ui;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

/**
 * {@link AsyncTask} that renders a QR code into a {@link Bitmap} and then stores it in the
 * provided {@link ImageView}.
 */
public class RenderQrCodeAsyncTask extends AsyncTask<Void, Void, Bitmap> {

    private static final String LOG_CAT = RenderQrCodeAsyncTask.class.getSimpleName();

    private static final int WHITE = 0xFFFFFFFF;
    private static final int BLACK = 0xFF000000;

    private final Long userId;
    private final ImageView imageView;
    private int width;
    private int height;
    private int retryCount;

    public RenderQrCodeAsyncTask(Long userId, final ImageView imageView) {
        Log.i(LOG_CAT, "Initiate generating QR code.");
        this.userId = userId;
        this.imageView = imageView;
        waitForImageDimensionsToBeSet(imageView);
    }

    private void waitForImageDimensionsToBeSet(final ImageView imageView) {
        imageView.post(new Runnable() {
            @Override
            public void run() {
                retryCount++;
                width = imageView.getWidth();
                height = imageView.getHeight();
                if ((retryCount < 6) && (width == 0)) {
                    waitForImageDimensionsToBeSet(imageView);
                } else {
                    Log.e(LOG_CAT, "Couldn't get the width after 6 retries.");
                }
            }
        });
    }

    @Override
    protected Bitmap doInBackground(Void... voids) {
        while (width == 0) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException ex) {
                Log.e(LOG_CAT, "Failed to wait for width.", ex);
            }
        }

        try {
            return generateQrCode(userId.toString(), width, height);
        } catch (WriterException ex) {
            Log.e(LOG_CAT, "Failed to render QR code.", ex);
            return null;
        }
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
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
        Log.i(LOG_CAT, "Done generating QR code.");
        return bitmap;
    }
}
