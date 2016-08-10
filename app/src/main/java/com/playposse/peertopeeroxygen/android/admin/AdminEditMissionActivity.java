package com.playposse.peertopeeroxygen.android.admin;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;

import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.data.DataService;
import com.playposse.peertopeeroxygen.android.data.DataServiceConnection;
import com.playposse.peertopeeroxygen.android.widgets.ListViewNoScroll;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.CompleteMissionDataBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionBean;

/**
 * {@link android.app.Activity} that shows, edits, and creates a specific mission.
 */
public class AdminEditMissionActivity
        extends AppCompatActivity
        implements DataService.DataReceivedCallback {

    private DataServiceConnection dataServiceConnection;
    private Long missionLadderId;
    private Long missionTreeId;
    private Long missionId;
    private MissionBean missionBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_edit_mission);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        missionLadderId = intent.getLongExtra(ExtraConstants.EXTRA_MISSION_LADDER_ID, -1);
        missionTreeId = intent.getLongExtra(ExtraConstants.EXTRA_MISSION_TREE_ID, -1);
        missionId = intent.getLongExtra(ExtraConstants.EXTRA_MISSION_ID, -1);
    }

    @Override
    protected void onResume() {
        super.onResume();

        missionBean = null;
        Intent intent = new Intent(this, DataService.class);
        dataServiceConnection = new DataServiceConnection(this);
        bindService(intent, dataServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();

        unbindService(dataServiceConnection);
    }

    @Override
    protected void onPause() {
        super.onPause();

        saveIfNecessary();
    }

    @Override
    public void receiveData(final CompleteMissionDataBean completeMissionDataBean) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                EditText nameEditText = (EditText) findViewById(R.id.missionNameEditText);
                EditText studentInstructionEditText =
                        (EditText) findViewById(R.id.studentInstructionsEditText);
                EditText buddyInstructionEditText =
                        (EditText) findViewById(R.id.buddyInstructionsEditText);

                if (missionId == -1) {
                    // new mission ladder
                    nameEditText.setText("");
                    studentInstructionEditText.setText("");
                    buddyInstructionEditText.setText("");

                    setTitle(String.format(
                            getString(R.string.edit_mission_title),
                            getString(R.string.new_entity)));
                } else {
                    // existing mission ladder
                    missionBean =
                            dataServiceConnection.getLocalBinder().getMissionBean(
                                    missionLadderId,
                                    missionTreeId,
                                    missionId);
                    nameEditText.setText(missionBean.getName());
                    studentInstructionEditText.setText(missionBean.getStudentInstruction());
                    buddyInstructionEditText.setText(missionBean.getBuddyInstruction());

                    setTitle(String.format(
                            getString(R.string.edit_mission_title),
                            missionBean.getName()));
                }
            }
        });
    }

    private void saveIfNecessary() {
        EditText nameEditText = (EditText) findViewById(R.id.missionNameEditText);
        EditText studentInstructionEditText =
                (EditText) findViewById(R.id.studentInstructionsEditText);
        EditText buddyInstructionEditText =
                (EditText) findViewById(R.id.buddyInstructionsEditText);

        // Determine if data should be saved.
        boolean shouldSave = false;
        if (missionBean == null) {
            if (nameEditText.getText().length() > 0) {
                shouldSave = true;
                missionBean = new MissionBean();
            }
        } else {
            shouldSave =
                    !nameEditText.getText().toString().equals(missionBean.getName())
                    || !studentInstructionEditText.getText().toString().equals(missionBean.getStudentInstruction())
                    || !buddyInstructionEditText.getText().toString().equals(missionBean.getBuddyInstruction());
        }

        // Save mission.
        if (shouldSave) {
            missionBean.setName(nameEditText.getText().toString());
            missionBean.setStudentInstruction(studentInstructionEditText.getText().toString());
            missionBean.setBuddyInstruction(buddyInstructionEditText.getText().toString());
            dataServiceConnection
                    .getLocalBinder()
                    .save(missionLadderId, missionTreeId, missionBean);
        }
    }
}
