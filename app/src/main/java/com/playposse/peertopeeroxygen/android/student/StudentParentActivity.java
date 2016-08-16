package com.playposse.peertopeeroxygen.android.student;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.admin.AdminMainActivity;
import com.playposse.peertopeeroxygen.android.data.DataService;
import com.playposse.peertopeeroxygen.android.data.DataServiceParentActivity;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.CompleteMissionDataBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.UserBean;

/**
 * A base activity that implements common functionality for student activities.
 */
public abstract class StudentParentActivity extends DataServiceParentActivity {

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
                && (dataServiceConnection.getLocalBinder().getUserBean() != null)) {
            UserBean userBean = dataServiceConnection.getLocalBinder().getUserBean();
            if (userBean.getAdmin()) {
                getMenuInflater().inflate(R.menu.student_menu, menu);
                return true;
            } else {
                // The user is loaded and not an admin. -> Nothing to do.
                return true;
            }
        } else {
            dataServiceConnection.getLocalBinder().registerDataReceivedCallback(
                    new DataService.DataReceivedCallback() {
                        @Override
                        public void receiveData(CompleteMissionDataBean completeMissionDataBean) {
                            invalidateOptionsMenu();
                            dataServiceConnection
                                    .getLocalBinder()
                                    .unregisterDataReceivedCallback(this);
                        }
                    });
            return true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.adminHomeMenuItem:
                startActivity(new Intent(this, AdminMainActivity.class));
                return true;
            case R.id.refreshMenuItem:
                dataServiceConnection.getLocalBinder().reload();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}