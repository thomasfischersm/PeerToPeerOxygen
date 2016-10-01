package com.playposse.peertopeeroxygen.android.data.facebook;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.playposse.peertopeeroxygen.android.util.StreamUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * A cache that stores Facebook profile photos and retrieves them from Facebook when they aren't
 * found in the cache.
 * <p/>
 * <p>The files are stored in the device's temp directory. When the app is closed, all the cached
 * files are disposed off. That way profile photos don't get too old.
 * <p/>
 * <p>The file name has the Facebook profile ID encoded.
 */
public class FacebookProfilePhotoCache {

    private static final String LOG_CAT = FacebookProfilePhotoCache.class.getSimpleName();

    private static final String PRIVATE_PATH =
            File.separator + "facebook-cache" + File.separator;
    private static final String FILE_SUFFIX = ".jpg";

    public void onStart(Context context) {
        clearCacheAsync(context);
    }

    public void onStop(Context context) {
        Log.i(LOG_CAT, "Stopping photo cache.");
        clearCacheAsync(context);
    }

    public void loadImage(
            Context context,
            ImageView imageView,
            String fbProfileId,
            String photoUrlString) {

        Log.i(LOG_CAT, "loadImage(" + fbProfileId + ")");
        new LoadProfilePhotoAsyncTask(context, imageView, fbProfileId, photoUrlString).execute();
    }

    private static byte[] loadPhotoFromFacebook(String photoUrlString) throws IOException {
        URL photoUrl = new URL(photoUrlString);
        InputStream inputStream = photoUrl.openConnection().getInputStream();
        return StreamUtil.readByteStream(inputStream);
    }

    private static void savePhotoToDevice(Context context, String fbProfileId, byte[] photoData)
            throws IOException {

        // Create directory if necessary.
        File dir = new File(context.getCacheDir() + PRIVATE_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String fileName = dir + File.separator + fbProfileId + FILE_SUFFIX;
        Log.i(LOG_CAT, "Saving photo to cache: " + fileName);
        StreamUtil.writeByteStream(photoData, fileName);
    }

    private static boolean isPhotoOnDevice(Context context, String fbProfileId) {
        File dir = new File(context.getCacheDir() + PRIVATE_PATH);
        if (!dir.exists()) {
            return false;
        }

        String expectedFileName = fbProfileId + FILE_SUFFIX;
        Log.i(LOG_CAT, "Expecting file: " + expectedFileName);
        Log.i(LOG_CAT, "Photo cache size: " + dir.listFiles().length);
        for (File file : dir.listFiles()) {
            Log.i(LOG_CAT, "Found photo cache file on device: " + file.getName());
            if (file.getName().equals(expectedFileName)) {
                return true;
            }
        }
        return false;
    }

    private static byte[] loadPhotoFromDevice(Context context, String fbProfileId)
            throws IOException {

        String filePath = context.getCacheDir() + PRIVATE_PATH + fbProfileId + FILE_SUFFIX;
        return StreamUtil.readByteStream(filePath);
    }

    private static void clearCacheAsync(final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                clearCache(context);
            }
        }).start();
    }

    private static void clearCache(Context context) {
        File dir = new File(context.getCacheDir() + PRIVATE_PATH);

        if (dir.exists()) {
            Log.i(LOG_CAT, "Clearing files from photo cache: " + dir.listFiles().length);
            for (File file : dir.listFiles()) {
                file.delete();
            }
        }
    }

    private class LoadProfilePhotoAsyncTask extends AsyncTask<Void, Void, Bitmap> {

        private final Context context;
        private final ImageView imageView;
        private final String fbProfileId;
        private final String photoUrlString;

        private LoadProfilePhotoAsyncTask(
                Context context,
                ImageView imageView,
                String fbProfileId,
                String photoUrlString) {

            this.context = context;
            this.imageView = imageView;
            this.fbProfileId = fbProfileId;
            this.photoUrlString = photoUrlString;
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {
            try {
                final byte[] photoData;
                if (isPhotoOnDevice(context, fbProfileId)) {
                    Log.i(LOG_CAT, "Cache hit for " + photoUrlString);
                    photoData = loadPhotoFromDevice(context, fbProfileId);
                } else {
                    photoData = loadPhotoFromFacebook(photoUrlString);
                    savePhotoToDevice(context, fbProfileId, photoData);
                }

                return BitmapFactory.decodeByteArray(photoData, 0, photoData.length);
            } catch (IOException ex) {
                Log.e(LOG_CAT, "Failed to load FB profile photo.", ex);
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap photoBitmap) {
            imageView.setImageBitmap(photoBitmap);
        }
    }
}
