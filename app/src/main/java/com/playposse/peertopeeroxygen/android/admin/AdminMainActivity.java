package com.playposse.peertopeeroxygen.android.admin;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.data.DataRepository;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * Activity that shows the top navigation for the administration activities.
 */
public class AdminMainActivity extends AdminParentActivity {

    public static final String LOG_TAG = AdminMainActivity.class.getSimpleName();

    private TextView openMissionLaddersTextView;
    private TextView openStudentRosterTextView;
    private TextView statsTextView;
    private TextView feedbackTextView;
    private TextView backupTextView;
    private TextView loanerDevicesTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_admin_main);
        super.onCreate(savedInstanceState);

        setTitle(R.string.admin_home_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        openMissionLaddersTextView = (TextView) findViewById(R.id.openMissionLaddersLink);
        openMissionLaddersTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(
                        new Intent(AdminMainActivity.this, AdminShowMissionLaddersActivity.class));
            }
        });

        openStudentRosterTextView = (TextView) findViewById(R.id.openStudentRosterLink);
        openStudentRosterTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminMainActivity.this, AdminStudentRosterActivity.class));
            }
        });

        backupTextView = (TextView) findViewById(R.id.backupLink);
        backupTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminMainActivity.this, AdminBackupActivity.class));
            }
        });

        statsTextView = (TextView) findViewById(R.id.statsLink);
        statsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminMainActivity.this, AdminMissionStatsActivity.class));
            }
        });


        feedbackTextView = (TextView) findViewById(R.id.feedbackLink);
        feedbackTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(
                        new Intent(AdminMainActivity.this, AdminMissionFeedbackActivity.class));
            }
        });

        loanerDevicesTextView = (TextView) findViewById(R.id.loanerDevicesLink);
        loanerDevicesTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminMainActivity.this, AdminLoanerDeviceActivity.class));
            }
        });
    }

    @Override
    public void receiveData(DataRepository dataRepository) {
        // Nothing to do. Yet, calling the service ensures that the data is already there for other
        // activities.
    }
}
