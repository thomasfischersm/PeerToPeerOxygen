package com.playposse.peertopeeroxygen.android.student;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.data.DataReceivedCallback;
import com.playposse.peertopeeroxygen.android.data.DataRepository;
import com.playposse.peertopeeroxygen.android.data.DataServiceParentActivity;
import com.playposse.peertopeeroxygen.android.data.OxygenSharedPreferences;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.UserBean;

/**
 * A base activity that implements common functionality for student activities.
 */
public abstract class StudentParentActivity extends DataServiceParentActivity {

    private boolean isWaitingToInvalidateOptionsMenu = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if ((dataServiceConnection != null)
                && (dataServiceConnection.getLocalBinder() != null)
                && (dataServiceConnection.getLocalBinder().getDataRepository().getUserBean() != null)) {
            UserBean userBean =
                    dataServiceConnection.getLocalBinder().getDataRepository().getUserBean();
            if (userBean.getAdmin()) {
                getMenuInflater().inflate(R.menu.admin_menu, menu);
                return true;
            } else {
                getMenuInflater().inflate(R.menu.student_menu, menu);
                return true;
            }
        } else {
            if (dataServiceConnection.getLocalBinder() != null) {
                registerDataReceivedCallbackToInvalidateOptionsMenu();
            } else {
                isWaitingToInvalidateOptionsMenu = true;
            }
            return true;
        }
    }

    private void registerDataReceivedCallbackToInvalidateOptionsMenu() {
        dataServiceConnection.getLocalBinder().registerDataReceivedCallback(
                new DataReceivedCallback() {
                    @Override
                    public void receiveData(DataRepository dataRepository) {
                        invalidateOptionsMenu();
                        dataServiceConnection
                                .getLocalBinder()
                                .unregisterDataReceivedCallback(this);
                    }

                    @Override
                    public void runOnUiThread(Runnable runnable) {
                        StudentParentActivity.this.runOnUiThread(runnable);
                    }
                });
    }

    @Override
    public void receiveData(DataRepository dataRepository) {
        if (isWaitingToInvalidateOptionsMenu) {
            invalidateOptionsMenu();
            isWaitingToInvalidateOptionsMenu = false;
        }
    }
}