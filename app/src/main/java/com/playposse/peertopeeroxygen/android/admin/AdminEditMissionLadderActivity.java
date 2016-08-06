package com.playposse.peertopeeroxygen.android.admin;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import com.playposse.peertopeeroxygen.android.widgets.ListViewNoScroll;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.CompleteMissionDataBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionLadderBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionTreeBean;

import java.util.List;

/**
 * {@link android.app.Activity} that shows, edits, and creates a specific mission ladder.
 */
public class AdminEditMissionLadderActivity
        extends AppCompatActivity
        implements DataService.DataReceivedCallback {

    public static final String LOG_CAT = AdminEditMissionLadderActivity.class.getSimpleName();

    private DataServiceConnection dataServiceConnection;
    private MissionLadderBean missionLadderBean;
    private Long missionLadderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_edit_mission_ladder);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        missionLadderId =
                getIntent().getLongExtra(ExtraConstants.EXTRA_MISSION_LADDER_ID, -1);

        Log.i(LOG_CAT, "Edit mission ladder called with ladder id: " + missionLadderId);

        TextView createMissionTreeLink = (TextView) findViewById(R.id.createMissionTreeLink);
        createMissionTreeLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        getApplicationContext(),
                        AdminEditMissionTreeActivity.class);
                intent.putExtra(ExtraConstants.EXTRA_MISSION_LADDER_ID, missionLadderId);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        missionLadderBean = null;
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
            // TODO: Update ID and local bean.
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

                if (missionLadderId == -1) {
                    // new mission ladder
                    nameEditText.setText("");
                    descriptionEditText.setText("");

                    setTitle(String.format(
                            getString(R.string.edit_mission_ladder_title),
                            getString(R.string.new_entity)));
                } else {
                    // existing mission ladder
                    missionLadderBean =
                            dataServiceConnection.getLocalBinder().getMissionLadderBean(
                                    missionLadderId);
                    nameEditText.setText(missionLadderBean.getName());
                    descriptionEditText.setText(missionLadderBean.getDescription());

                    ListViewNoScroll missionLaddersListView =
                            (ListViewNoScroll) findViewById(R.id.missionTreesListView);
                    MissionTreeBeanArrayAdapter adapter = new MissionTreeBeanArrayAdapter(
                            missionLadderBean.getMissionTreeBeans());
                    missionLaddersListView.setAdapter(adapter);

                    setTitle(String.format(
                            getString(R.string.edit_mission_ladder_title),
                            missionLadderBean.getName()));
                }
            }
        });
    }

    private final class MissionTreeBeanArrayAdapter
            extends ArrayAdapter<MissionTreeBean> {

        public MissionTreeBeanArrayAdapter(List<MissionTreeBean> objects) {
            super(getApplicationContext(), R.layout.list_item_mission_tree, objects);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) AdminEditMissionLadderActivity.this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.list_item_mission_tree, parent, false);

            TextView missionTreeNameLink =
                    (TextView) rowView.findViewById(R.id.missionTreeNameLink);
            missionTreeNameLink.setText(getItem(position).getName());
            missionTreeNameLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    saveIfNecessary();

                    Long missionTreeId = new Long(getItem(position).getId());
                    Intent intent = new Intent(
                            getApplicationContext(),
                            AdminEditMissionTreeActivity.class);
                    intent.putExtra(ExtraConstants.EXTRA_MISSION_LADDER_ID, missionLadderId);
                    intent.putExtra(ExtraConstants.EXTRA_MISSION_TREE_ID, missionTreeId);
                    startActivity(intent);
                }
            });

            TextView missionTreeDeleteLink =
                    (TextView) rowView.findViewById(R.id.missionTreeDeleteLink);
            // TODO: onClick

            return rowView;
        }
    }
}
