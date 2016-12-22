package com.playposse.peertopeeroxygen.android.admin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ScrollView;

import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.data.DataRepository;
import com.playposse.peertopeeroxygen.android.model.ExtraConstants;
import com.playposse.peertopeeroxygen.android.ui.dialogs.ConfirmationDialogBuilder;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionLadderBean;

import java.util.ArrayList;
import java.util.List;

public class AdminShowMissionLaddersActivity extends AdminParentActivity {

    private Button createMissionLadderButton;
    private ListView missionLaddersListView;
    private ScrollView missionLadderHintScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_admin_show_mission_ladders);
        super.onCreate(savedInstanceState);

        setTitle(R.string.show_mission_ladders_title);

        createMissionLadderButton = (Button) findViewById(R.id.createMissionLadderButton);
        missionLaddersListView = (ListView) findViewById(R.id.missionLaddersListView);
        missionLadderHintScrollView = (ScrollView) findViewById(R.id.missionLadderHintScrollView);

        createMissionLadderButton.setOnClickListener(new View.OnClickListener() {
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
        List<MissionLadderBean> missionLadderBeans = dataRepository.getMissionLadderBeans();
        MissionLadderBeanArrayAdapter adapter = new MissionLadderBeanArrayAdapter(
                new ArrayList<>(missionLadderBeans));
        missionLaddersListView.setAdapter(adapter);
        missionLaddersListView.refreshDrawableState();

        // Show hint if there are no mission ladders.
        if (missionLadderBeans.size() == 0) {
            missionLadderHintScrollView.setVisibility(View.VISIBLE);
            missionLaddersListView.setVisibility(View.GONE);
        } else {
            missionLadderHintScrollView.setVisibility(View.GONE);
            missionLaddersListView.setVisibility(View.VISIBLE);
        }
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
            Button missionLadderNameButton =
                    (Button) convertView.findViewById(R.id.missionLadderNameButton);
            missionLadderNameButton.setText(missionLadderBean.getName());
            missionLadderNameButton.setOnClickListener(new View.OnClickListener() {
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

            ImageButton missionLadderDeleteLink =
                    (ImageButton) convertView.findViewById(R.id.missionLadderDeleteButton);
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
