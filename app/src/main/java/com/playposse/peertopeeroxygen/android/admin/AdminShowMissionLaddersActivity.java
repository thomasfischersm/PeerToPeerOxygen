package com.playposse.peertopeeroxygen.android.admin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.data.DataRepository;
import com.playposse.peertopeeroxygen.android.model.ExtraConstants;
import com.playposse.peertopeeroxygen.android.ui.dialogs.ConfirmationDialogBuilder;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionLadderBean;

import java.util.ArrayList;
import java.util.List;

public class AdminShowMissionLaddersActivity extends AdminParentActivity {

    private TextView createMissionLadderLink;
    private ListView missionLaddersListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_admin_show_mission_ladders);
        super.onCreate(savedInstanceState);

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
    public void receiveData(final DataRepository dataRepository) {
        MissionLadderBeanArrayAdapter adapter = new MissionLadderBeanArrayAdapter(
                new ArrayList<>(dataRepository.getMissionLadderBeans()));
        missionLaddersListView.setAdapter(adapter);
        missionLaddersListView.refreshDrawableState();
    }

    private final class MissionLadderBeanArrayAdapter
            extends ArrayAdapter<MissionLadderBean> {

        private MissionLadderBeanArrayAdapter(List<MissionLadderBean> objects) {
            super(getApplicationContext(), R.layout.list_item_mission_ladder, objects);
        }

        @NonNull
        @Override
        public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) AdminShowMissionLaddersActivity.this
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.list_item_mission_ladder, parent, false);
            }

            final MissionLadderBean missionLadderBean = getItem(position);
            TextView missionLadderNameLink =
                    (TextView) convertView.findViewById(R.id.missionLadderNameLink);
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
                    (TextView) convertView.findViewById(R.id.missionLadderDeleteLink);
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

            return convertView;
        }
    }
}
