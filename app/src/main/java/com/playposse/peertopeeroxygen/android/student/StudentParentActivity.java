package com.playposse.peertopeeroxygen.android.student;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.admin.AdminMainActivity;
import com.playposse.peertopeeroxygen.android.data.DataServiceParentActivity;

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
        getMenuInflater().inflate(R.menu.student_menu, menu);
        return true;
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