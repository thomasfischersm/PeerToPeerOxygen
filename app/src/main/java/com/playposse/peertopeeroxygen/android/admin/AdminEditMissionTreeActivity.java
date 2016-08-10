package com.playposse.peertopeeroxygen.android.admin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.data.DataService;
import com.playposse.peertopeeroxygen.android.data.DataServiceConnection;
import com.playposse.peertopeeroxygen.android.widgets.ConfirmationDialogBuilder;
import com.playposse.peertopeeroxygen.android.widgets.ListViewNoScroll;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.CompleteMissionDataBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionLadderBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionTreeBean;

import java.util.List;

/**
 * {@link android.app.Activity} that shows, edits, and creates a specific mission tree.
 */
public class AdminEditMissionTreeActivity
        extends AppCompatActivity
        implements DataService.DataReceivedCallback {


    private DataServiceConnection dataServiceConnection;
    private Long missionLadderId;
    private Long missionTreeId;
    private MissionLadderBean missionLadderBean;
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

        TextView createMissionLink = (TextView) findViewById(R.id.createMissionLink);
        createMissionLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        getApplicationContext(),
                        AdminEditMissionActivity.class);
                intent.putExtra(ExtraConstants.EXTRA_MISSION_LADDER_ID, missionLadderId);
                intent.putExtra(ExtraConstants.EXTRA_MISSION_TREE_ID, missionTreeId);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        missionTreeBean = null;
        Intent intent = new Intent(this, DataService.class);
        dataServiceConnection = new DataServiceConnection(this);
        bindService(intent, dataServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();

        saveIfNecessary();
    }

    private void saveIfNecessary() {
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
                missionTreeBean.setLevel(determineNextLevel());
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

                    setTitle(String.format(
                            getString(R.string.edit_mission_tree_title),
                            getString(R.string.new_entity)));
                } else {
                    // existing mission ladder
                    missionTreeBean = dataServiceConnection.getLocalBinder().getMissionTreeBean(
                            missionLadderId,
                            missionTreeId);
                    nameEditText.setText(missionTreeBean.getName());
                    descriptionEditText.setText(missionTreeBean.getDescription());

                    setTitle(String.format(
                            getString(R.string.edit_mission_tree_title),
                            missionTreeBean.getName()));

                    ListViewNoScroll missionsListView =
                            (ListViewNoScroll) findViewById(R.id.missionsListView);
                    MissionBeanArrayAdapter adapter = new MissionBeanArrayAdapter(
                            missionTreeBean.getMissionBeans());
                    missionsListView.setAdapter(adapter);
                }

                missionLadderBean = dataServiceConnection
                        .getLocalBinder()
                        .getMissionLadderBean(missionLadderId);
            }
        });
    }

    /**
     * Checks the current levels an determines what the next level would be.
     */
    private int determineNextLevel() {
        int level = 1;
        for (MissionTreeBean missionTreeBean : missionLadderBean.getMissionTreeBeans()) {
            level = Math.max(level, missionTreeBean.getLevel() + 1);
        }
        return level;
    }

    private final class MissionBeanArrayAdapter
            extends ArrayAdapter<MissionBean> {

        public MissionBeanArrayAdapter(List<MissionBean> objects) {
            super(getApplicationContext(), R.layout.list_item_mission, objects);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) AdminEditMissionTreeActivity.this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.list_item_mission, parent, false);

            final MissionBean missionBean = getItem(position);
            TextView missionNameLink = (TextView) rowView.findViewById(R.id.missionNameLink);
            missionNameLink.setText(missionBean.getName());
            missionNameLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    saveIfNecessary();

                    Long missionId = new Long(missionBean.getId());
                    Intent intent = new Intent(
                            getApplicationContext(),
                            AdminEditMissionActivity.class);
                    intent.putExtra(ExtraConstants.EXTRA_MISSION_LADDER_ID, missionLadderId);
                    intent.putExtra(ExtraConstants.EXTRA_MISSION_TREE_ID, missionTreeId);
                    intent.putExtra(ExtraConstants.EXTRA_MISSION_ID, missionId);
                    startActivity(intent);
                }
            });

            TextView missionDeleteLink =
                    (TextView) rowView.findViewById(R.id.missionDeleteLink);
            missionDeleteLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String deleteMessage = String.format(
                            getString(R.string.confirm_delete_mission_message),
                            missionBean.getName());
                    ConfirmationDialogBuilder.show(AdminEditMissionTreeActivity.this, deleteMessage, new Runnable() {
                        @Override
                        public void run() {
                            dataServiceConnection.getLocalBinder().deleteMission(
                                    missionLadderBean.getId(),
                                    missionTreeBean.getId(),
                                    missionBean.getId());
                        }
                    });
                }
            });

            return rowView;
        }
    }
}
