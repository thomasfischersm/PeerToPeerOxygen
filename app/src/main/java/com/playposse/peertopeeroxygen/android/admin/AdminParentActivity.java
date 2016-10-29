package com.playposse.peertopeeroxygen.android.admin;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.data.DataServiceParentActivity;
import com.playposse.peertopeeroxygen.android.data.OxygenSharedPreferences;

/**
 * A base activity that implements common functionality for admin activities.
 */
public abstract class AdminParentActivity extends DataServiceParentActivity {

    private ProgressDialog progressDialog;

    public AdminParentActivity() {
        shouldCheckPractica = false;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_menu, menu);

        boolean debugFlag = OxygenSharedPreferences.getDebugFlag(this);
        MenuItem debugMenuItem = menu.findItem(R.id.debugMenuItem);
        debugMenuItem.setChecked(debugFlag);

        return true;
    }

    protected void showLoadingProgress() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(R.string.progress_dialog_title);
        progressDialog.setMessage(getString(R.string.progress_dialog_message));
        progressDialog.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progressDialog.show();
    }

    protected void dismissLoadingProgress() {
        progressDialog.dismiss();
        progressDialog = null;
    }
}
