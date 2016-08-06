package com.playposse.peertopeeroxygen.android.admin;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.data.DataService;
import com.playposse.peertopeeroxygen.android.data.DataServiceConnection;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.CompleteMissionDataBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionLadderBean;

import java.util.List;

public class AdminShowMissionLaddersActivity
        extends AppCompatActivity
        implements DataService.DataReceivedCallback {

    private DataServiceConnection dataServiceConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_show_mission_ladders);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle(R.string.show_mission_ladders_title);

        TextView createMissionLadderLink = (TextView) findViewById(R.id.createMissionLadderLink);
        createMissionLadderLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        getApplicationContext(),
                        AdminEditMissionLadderActivity.class);
//                intent.putExtra(Intent.EXTRA_INDEX, new Long(getItem(position).getId()));
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
                ListView missionLaddersListView =
                        (ListView) findViewById(R.id.missionLaddersListView);
                MissionLadderBeanArrayAdapter adapter = new MissionLadderBeanArrayAdapter(
                        completeMissionDataBean.getMissionLadderBeans());
                missionLaddersListView.setAdapter(adapter);
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

            TextView missionLadderNameLink =
                    (TextView) rowView.findViewById(R.id.missionLadderNameLink);
            missionLadderNameLink.setText(getItem(position).getName());
            missionLadderNameLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(
                            getApplicationContext(),
                            AdminEditMissionLadderActivity.class);
                    intent.putExtra(
                            ExtraConstants.EXTRA_MISSION_LADDER_ID,
                            new Long(getItem(position).getId()));
                    startActivity(intent);
                }
            });

            TextView missionLadderDeleteLink =
                    (TextView) rowView.findViewById(R.id.missionLadderDeleteLink);
            // TODO: onClick

            return rowView;
        }
    }
}
