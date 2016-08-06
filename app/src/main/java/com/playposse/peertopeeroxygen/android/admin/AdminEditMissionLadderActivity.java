package com.playposse.peertopeeroxygen.android.admin;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.data.DataService;
import com.playposse.peertopeeroxygen.android.data.DataServiceConnection;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.CompleteMissionDataBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionLadderBean;

/**
 * {@link android.app.Activity} that shows, edits, and creates a specific mission ladder.
 */
public class AdminEditMissionLadderActivity
        extends AppCompatActivity
        implements DataService.DataReceivedCallback {

    private DataServiceConnection dataServiceConnection;
    private MissionLadderBean missionLadderBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_edit_mission_ladder);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        missionLadderBean = null;
        Intent intent = new Intent(this, DataService.class);
        dataServiceConnection = new DataServiceConnection(this);
        bindService(intent, dataServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // TODO: Use that helper class to save references to views.
        EditText nameEditText = (EditText) findViewById(R.id.missionLadderNameEditText);
        EditText descriptionEditText =
                (EditText) findViewById(R.id.missionLadderDescriptionEditText);

        // Determine if the data should be saved.
        boolean shouldSave = false;
        if (missionLadderBean == null) {
            // Check if any data has been entered in the name field.
            if (nameEditText.getText().length() > 0) {
                missionLadderBean = new MissionLadderBean();
                shouldSave = true;
            }
        } else {
            // Check if changes have been made.
            shouldSave =  !nameEditText.getText().toString().equals(missionLadderBean.getName())
                    || !descriptionEditText.getText().toString().equals(missionLadderBean.getDescription());
        }

        // Save if necessary.
        if (shouldSave) {
            missionLadderBean.setName(nameEditText.getText().toString());
            missionLadderBean.setDescription(descriptionEditText.getText().toString());
            dataServiceConnection.getLocalBinder().save(missionLadderBean);
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
                EditText nameEditText = (EditText) findViewById(R.id.missionLadderNameEditText);
                EditText descriptionEditText =
                        (EditText) findViewById(R.id.missionLadderDescriptionEditText);
                Long id = getIntent().getLongExtra(Intent.EXTRA_INDEX, -1);

                if (id == -1) {
                    // new mission ladder
                    nameEditText.setText("");
                    descriptionEditText.setText("");
                } else {
                    // existing mission ladder
                    missionLadderBean =
                            dataServiceConnection.getLocalBinder().getMissionLadderBean(id);
                    nameEditText.setText(missionLadderBean.getName());
                    descriptionEditText.setText(missionLadderBean.getDescription());
                }

                // TODO: Show mission trees.

//                ListView missionLaddersListView =
//                        (ListView) findViewById(R.id.missionLaddersListView);
//                ArrayAdapter<MissionLadderBean> adapter = new ArrayAdapter<>(
//                        getApplicationContext(),
//                        R.layout.list_item_mission_ladder,
//                        completeMissionDataBean.getMissionLadderBeans());
//                missionLaddersListView.setAdapter(adapter);
            }
        });
    }
}
