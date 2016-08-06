package com.playposse.peertopeeroxygen.android.admin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.ListView;

import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.data.DataService;
import com.playposse.peertopeeroxygen.android.data.DataServiceConnection;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.CompleteMissionDataBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionTreeBean;

/**
 * {@link android.app.Activity} that shows, edits, and creates a specific mission tree.
 */
public class AdminEditMissionTreeActivity
        extends AppCompatActivity
        implements DataService.DataReceivedCallback {


    private DataServiceConnection dataServiceConnection;
    private Long missionLadderId;
    private Long missionTreeId;
    private MissionTreeBean missionTreeBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_edit_mission_tree);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        missionLadderId = getIntent().getLongExtra(ExtraConstants.EXTRA_MISSION_LADDER_ID, -1);
        missionTreeId = getIntent().getLongExtra(ExtraConstants.EXTRA_MISSION_TREE_ID, -1);

        missionTreeBean = null;
        Intent intent = new Intent(this, DataService.class);
        dataServiceConnection = new DataServiceConnection(this);
        bindService(intent, dataServiceConnection, Context.BIND_AUTO_CREATE);
    }


    @Override
    protected void onPause() {
        super.onPause();

        // TODO: Use that helper class to save references to views.
        EditText nameEditText = (EditText) findViewById(R.id.missionTreeNameEditText);
        EditText descriptionEditText =
                (EditText) findViewById(R.id.missionTreeDescriptionEditText);

        // Determine if the data should be saved.
        boolean shouldSave = false;
        if (missionTreeBean == null) {
            // Check if any data has been entered in the name field.
            if (nameEditText.getText().length() > 0) {
                missionTreeBean = new MissionTreeBean();
                shouldSave = true;
            }
        } else {
            // Check if changes have been made.
            shouldSave = !nameEditText.getText().toString().equals(missionTreeBean.getName())
                    || !descriptionEditText.getText().toString().equals(missionTreeBean.getDescription());
        }

        // Save if necessary.
        if (shouldSave) {
            missionTreeBean.setName(nameEditText.getText().toString());
            missionTreeBean.setDescription(descriptionEditText.getText().toString());
            dataServiceConnection.getLocalBinder().save(missionLadderId, missionTreeBean);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        unbindService(dataServiceConnection);
    }

    @Override
    public void receiveData(final CompleteMissionDataBean completeMissionDataBean) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                EditText nameEditText = (EditText) findViewById(R.id.missionTreeNameEditText);
                EditText descriptionEditText =
                        (EditText) findViewById(R.id.missionTreeDescriptionEditText);

                if (missionTreeId == -1) {
                    // new mission ladder
                    nameEditText.setText("");
                    descriptionEditText.setText("");
                } else {
                    // existing mission ladder
                    missionTreeBean = dataServiceConnection.getLocalBinder().getMissionTreeBean(
                            missionLadderId,
                            missionTreeId);
                    nameEditText.setText(missionTreeBean.getName());
                    descriptionEditText.setText(missionTreeBean.getDescription());
                }

//                ListView missionLaddersListView =
//                        (ListView) findViewById(R.id.missionTreesListView);
//                MissionTreeBeanArrayAdapter adapter = new MissionTreeBeanArrayAdapter(
//                        missionLadderBean.getMissionTreeBeans());
//                missionLaddersListView.setAdapter(adapter);
            }
        });
    }
}
