package com.playposse.peertopeeroxygen.android.data;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;

import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.admin.AdminMainActivity;
import com.playposse.peertopeeroxygen.android.data.facebook.FacebookProfilePhotoCache;
import com.playposse.peertopeeroxygen.android.data.practicas.PracticaRepository;
import com.playposse.peertopeeroxygen.android.practicamgmt.PracticaManager;
import com.playposse.peertopeeroxygen.android.student.StudentAboutActivity;
import com.playposse.peertopeeroxygen.android.student.StudentMainActivity;

/**
 * An activity that connects to the {@link DataService}.
 */
public abstract class DataServiceParentActivity
        extends AppCompatActivity
        implements DataReceivedCallback, DataServiceConnection.ServiceConnectionListener {

    protected DataServiceConnection dataServiceConnection;
    protected boolean shouldAutoInit = true;
    protected boolean shouldRegisterCallback = true;
    protected boolean shouldCheckPractica = true;

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = new Intent(this, DataService.class);
        dataServiceConnection =
                new DataServiceConnection(this, shouldAutoInit, true, shouldRegisterCallback);
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
            case R.id.studentHomeMenuItem:
                startActivity(new Intent(this, StudentMainActivity.class));
                return true;
            case R.id.adminHomeMenuItem:
                startActivity(new Intent(this, AdminMainActivity.class));
                return true;
            case R.id.refreshMenuItem:
                dataServiceConnection.getLocalBinder().reload();
                return true;
            case R.id.debugMenuItem:
                boolean debugFlag = !item.isChecked();
                item.setChecked(debugFlag);
                OxygenSharedPreferences.setDebugFlag(this, debugFlag);
                return true;
            case R.id.aboutMenuItem:
                startActivity(new Intent(this, StudentAboutActivity.class));
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

    @Override
    public void onServiceConnected() {
        if (shouldCheckPractica) {
            PracticaRepository practicaRepository =
                    dataServiceConnection
                            .getLocalBinder()
                            .getDataRepository()
                            .getPracticaRepository();
            PracticaManager.refresh(this, dataServiceConnection.getLocalBinder());
        }
    }

    /**
     * Provides convenience access to the {@link DataRepository}
     */
    protected DataRepository getDataRepository() {
        if ((dataServiceConnection != null) && (dataServiceConnection.getLocalBinder() != null)) {
            return dataServiceConnection.getLocalBinder().getDataRepository();
        } else {
            return null;
        }
    }
}
