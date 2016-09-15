package com.playposse.peertopeeroxygen.android.data;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;

import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.data.facebook.FacebookProfilePhotoCache;

/**
 * An activity that connects to the {@link DataService}.
 */
public abstract class DataServiceParentActivity
        extends AppCompatActivity
        implements DataReceivedCallback {

    protected DataServiceConnection dataServiceConnection;
    protected boolean shouldAutoInit = true;

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = new Intent(this, DataService.class);
        dataServiceConnection = new DataServiceConnection(this, shouldAutoInit);
        bindService(intent, dataServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();

        unbindService(dataServiceConnection);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refreshMenuItem:
                dataServiceConnection.getLocalBinder().reload();
                return true;
            case R.id.debugMenuItem:
                boolean debugFlag = !item.isChecked();
                item.setChecked(debugFlag);
                OxygenSharedPreferences.setDebugFlag(this, debugFlag);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void loadProfilePhoto(
            ImageView studentPhotoImageView,
            String fbProfileId,
            String photoUrlString) {

        if ((dataServiceConnection == null)
                || (dataServiceConnection.getLocalBinder() == null)
                || (dataServiceConnection.getLocalBinder().getDataRepository() == null)) {
            return;
        }

        FacebookProfilePhotoCache photoCache = dataServiceConnection
                .getLocalBinder()
                .getDataRepository()
                .getFacebookProfilePhotoCache();
        photoCache.loadImage(
                this,
                studentPhotoImageView,
                fbProfileId,
                photoUrlString);
    }
}
