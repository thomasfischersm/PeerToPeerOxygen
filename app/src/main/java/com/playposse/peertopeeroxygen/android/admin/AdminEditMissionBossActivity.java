package com.playposse.peertopeeroxygen.android.admin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.CompleteMissionDataBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionBossBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionTreeBean;

import java.util.ArrayList;
import java.util.List;

public class AdminEditMissionBossActivity
        extends AppCompatActivity
        implements DataService.DataReceivedCallback {

    private static final String LOG_CAT = AdminEditMissionBossActivity.class.getSimpleName();

    private DataServiceConnection dataServiceConnection;
    private Long missionLadderId;
    private Long missionTreeId;
    private MissionTreeBean missionTreeBean;
    private MissionBossBean missionBossBean;
    private boolean areChecksDirty = false;

    private EditText descriptionEditText;
    private ListViewNoScroll checksListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_edit_mission_boss);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        missionLadderId = getIntent().getLongExtra(ExtraConstants.EXTRA_MISSION_LADDER_ID, -1);
        missionTreeId = getIntent().getLongExtra(ExtraConstants.EXTRA_MISSION_TREE_ID, -1);

        descriptionEditText = (EditText) findViewById(R.id.missionBossDescriptionEditText);
        checksListView = (ListViewNoScroll) findViewById(R.id.missionBossChecksListView);
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
                missionTreeBean = dataServiceConnection
                        .getLocalBinder()
                        .getMissionTreeBean(missionLadderId, missionTreeId);
                missionBossBean = missionTreeBean.getMissionBossBean();

                if (missionBossBean == null) {
                    missionBossBean = new MissionBossBean();
                    missionBossBean.setChecks(new ArrayList<String>());
                    missionTreeBean.setMissionBossBean(missionBossBean);
                }

                descriptionEditText.setText(missionBossBean.getDescription());
                List<String> checksList = missionBossBean.getChecks();
                if ((checksList.size() == 0)
                        || (checksList.get(checksList.size() - 1).length() > 0)) {
                    checksList.add("");
                }
                MissionBossChecksArrayAdapter adapter =
                        new MissionBossChecksArrayAdapter(checksList);
                checksListView.setAdapter(adapter);

                setTitle(String.format(
                        getString(R.string.edit_mission_boss_title),
                        missionTreeBean.getName()));
            }
        });
    }

    private void saveIfNecessary() {
        String oldDescription = missionBossBean.getDescription();
        String newDescription = descriptionEditText.getText().toString();
        if (!newDescription.equals(oldDescription) || areChecksDirty) {
            missionBossBean.setDescription(newDescription);

            // Remove empty checks
            List<String> checks = missionBossBean.getChecks();
            for (int i = checks.size() - 1; i >= 0; i--) {
                String check = checks.get(i);
                if ((check == null) || (check.length() == 0)) {
                    checks.remove(i);
                }
            }

            dataServiceConnection.getLocalBinder().save(missionLadderId, missionTreeBean);
        }
    }

    private final class MissionBossChecksArrayAdapter
            extends ArrayAdapter<String> {

        public MissionBossChecksArrayAdapter(List<String> checks) {
            super(getApplicationContext(), R.layout.list_item_mission_boss_check, checks);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) AdminEditMissionBossActivity.this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.list_item_mission_boss_check, parent, false);

            final EditText checkEditText = (EditText) rowView.findViewById(R.id.missionBossCheckEditText);
            TextView checkDeleteLink =
                    (TextView) rowView.findViewById(R.id.missionBossCheckDeleteLink);

            final String check = getItem(position);

            checkEditText.setText(check);

            checkEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    Log.i(LOG_CAT, "Registered text change in check.");
                    String newCheck = checkEditText.getText().toString();
                    List<String> checks = missionBossBean.getChecks();

                    boolean isAdapterDirty = false;
                    if (!newCheck.equals(checks.get(position))) {
                        checks.set(position, newCheck);
                        areChecksDirty = true;
                        isAdapterDirty = true;
                    }

                    if ((position == checks.size() - 1) && (newCheck.length() > 0)) {
                        Log.i(LOG_CAT, "Adding empty check.");
                        checks.add("");
                        isAdapterDirty = true;
                    }

                    if (isAdapterDirty) {
                        notifyDataSetChanged();
                    }
                }
            });

            checkDeleteLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String deleteMessage = String.format(
                            getString(R.string.confirm_delete_mission_boss_check_message),
                            check);
                    ConfirmationDialogBuilder.show(AdminEditMissionBossActivity.this, deleteMessage, new Runnable() {
                        @Override
                        public void run() {
                            missionBossBean.getChecks().remove(position);
                            areChecksDirty = true;
                            notifyDataSetChanged();
                        }
                    });
                }
            });

            return rowView;
        }
    }
}
