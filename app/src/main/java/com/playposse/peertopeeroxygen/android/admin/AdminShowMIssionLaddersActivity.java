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
import android.widget.ListView;
import android.widget.TextView;

import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.data.DataService;
import com.playposse.peertopeeroxygen.android.data.DataServiceConnection;
import com.playposse.peertopeeroxygen.android.widgets.ConfirmationDialogBuilder;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.CompleteMissionDataBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionLadderBean;

import java.util.ArrayList;
import java.util.List;

public class AdminShowMissionLaddersActivity
        extends AppCompatActivity
        implements DataService.DataReceivedCallback {

    private DataServiceConnection dataServiceConnection;

    private TextView createMissionLadderLink;
    private ListView missionLaddersListView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_menu, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_show_mission_ladders);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle(R.string.show_mission_ladders_title);

        createMissionLadderLink = (TextView) findViewById(R.id.createMissionLadderLink);
        missionLaddersListView = (ListView) findViewById(R.id.missionLaddersListView);

        createMissionLadderLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        getApplicationContext(),
                        AdminEditMissionLadderActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

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
    public void receiveData(final CompleteMissionDataBean completeMissionDataBean) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MissionLadderBeanArrayAdapter adapter = new MissionLadderBeanArrayAdapter(
                        new ArrayList<>(completeMissionDataBean.getMissionLadderBeans()));
                missionLaddersListView.setAdapter(adapter);
                missionLaddersListView.refreshDrawableState();
            }
        });
    }

    private final class MissionLadderBeanArrayAdapter
            extends ArrayAdapter<MissionLadderBean> {

        public MissionLadderBeanArrayAdapter(List<MissionLadderBean> objects) {
            super(getApplicationContext(), R.layout.list_item_mission_ladder, objects);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) AdminShowMissionLaddersActivity.this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.list_item_mission_ladder, parent, false);

            final MissionLadderBean missionLadderBean = getItem(position);
            TextView missionLadderNameLink =
                    (TextView) rowView.findViewById(R.id.missionLadderNameLink);
            missionLadderNameLink.setText(missionLadderBean.getName());
            missionLadderNameLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(
                            getApplicationContext(),
                            AdminEditMissionLadderActivity.class);
                    intent.putExtra(
                            ExtraConstants.EXTRA_MISSION_LADDER_ID,
                            new Long(missionLadderBean.getId()));
                    startActivity(intent);
                }
            });

            TextView missionLadderDeleteLink =
                    (TextView) rowView.findViewById(R.id.missionLadderDeleteLink);
            missionLadderDeleteLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String deleteMessage = String.format(
                            getString(R.string.confirm_delete_mission_ladder_message),
                            missionLadderBean.getName());
                    ConfirmationDialogBuilder.show(
                            AdminShowMissionLaddersActivity.this,
                            deleteMessage,
                            new Runnable() {
                                @Override
                                public void run() {
                                    dataServiceConnection.getLocalBinder()
                                            .deleteMissionLadder(missionLadderBean.getId());
                                }
                            });
                }
            });

            return rowView;
        }
    }
}
