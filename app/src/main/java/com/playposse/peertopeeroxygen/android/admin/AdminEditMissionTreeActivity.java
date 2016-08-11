package com.playposse.peertopeeroxygen.android.admin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.data.DataService;
import com.playposse.peertopeeroxygen.android.data.DataServiceConnection;
import com.playposse.peertopeeroxygen.android.widgets.ConfirmationDialogBuilder;
import com.playposse.peertopeeroxygen.android.widgets.ListViewNoScroll;
import com.playposse.peertopeeroxygen.android.widgets.RequiredMissionListView;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.CompleteMissionDataBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionLadderBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionTreeBean;

import java.util.List;

/**
 * {@link android.app.Activity} that shows, edits, and creates a specific mission tree.
 */
public class AdminEditMissionTreeActivity extends AdminParentActivity {

    private Long missionLadderId;
    private Long missionTreeId;
    private MissionLadderBean missionLadderBean;
    private MissionTreeBean missionTreeBean;

    private TextView createMissionLink;
    private EditText nameEditText;
    private EditText descriptionEditText;
    private TextView editMissionBossLink;
    private ListViewNoScroll missionsListView;
    private RequiredMissionListView requiredMissionsListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_admin_edit_mission_tree);
        super.onCreate(savedInstanceState);

        missionLadderId = getIntent().getLongExtra(ExtraConstants.EXTRA_MISSION_LADDER_ID, -1);
        missionTreeId = getIntent().getLongExtra(ExtraConstants.EXTRA_MISSION_TREE_ID, -1);
        missionTreeBean = null;

        createMissionLink = (TextView) findViewById(R.id.createMissionLink);
        nameEditText = (EditText) findViewById(R.id.missionTreeNameEditText);
        descriptionEditText = (EditText) findViewById(R.id.missionTreeDescriptionEditText);
        editMissionBossLink = (TextView) findViewById(R.id.editMissionBossLink);
        missionsListView = (ListViewNoScroll) findViewById(R.id.missionsListView);
        requiredMissionsListView =
                (RequiredMissionListView) findViewById(R.id.requiredMissionsListView);

        editMissionBossLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveIfNecessary();

                Intent intent = new Intent(
                        getApplicationContext(),
                        AdminEditMissionBossActivity.class);
                intent.putExtra(ExtraConstants.EXTRA_MISSION_LADDER_ID, missionLadderId);
                intent.putExtra(ExtraConstants.EXTRA_MISSION_TREE_ID, missionTreeId);
                startActivity(intent);
            }
        });

        createMissionLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveIfNecessary();

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
    protected void onPause() {
        super.onPause();

        saveIfNecessary();
    }

    private void saveIfNecessary() {
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
                    || !descriptionEditText.getText().toString().equals(missionTreeBean.getDescription())
                    || requiredMissionsListView.isDirty();
        }

        // Save if necessary.
        if (shouldSave) {
            missionTreeBean.setName(nameEditText.getText().toString());
            missionTreeBean.setDescription(descriptionEditText.getText().toString());
            missionTreeBean.setRequiredMissionIds(requiredMissionsListView.getRequiredMissionIds());
            dataServiceConnection.getLocalBinder().save(missionLadderId, missionTreeBean);
        }
    }

    @Override
    public void receiveData(final CompleteMissionDataBean completeMissionDataBean) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
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

                    MissionBeanArrayAdapter adapter = new MissionBeanArrayAdapter(
                            missionTreeBean.getMissionBeans());
                    missionsListView.setAdapter(adapter);

                    requiredMissionsListView.setAdapter(
                            missionTreeBean.getMissionBeans(),
                            missionTreeBean.getRequiredMissionIds(),
                            null);
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
