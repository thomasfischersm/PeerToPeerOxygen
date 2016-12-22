package com.playposse.peertopeeroxygen.android.admin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.data.DataRepository;
import com.playposse.peertopeeroxygen.android.data.OxygenSharedPreferences;
import com.playposse.peertopeeroxygen.android.model.ExtraConstants;
import com.playposse.peertopeeroxygen.android.ui.dialogs.ConfirmationDialogBuilder;
import com.playposse.peertopeeroxygen.android.ui.widgets.ListViewNoScroll;
import com.playposse.peertopeeroxygen.android.util.StringUtil;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionLadderBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionTreeBean;

import java.util.List;

/**
 * {@link android.app.Activity} that shows, edits, and creates a specific mission ladder.
 */
public class AdminEditMissionLadderActivity extends AdminParentActivity {

    private static final String LOG_CAT = AdminEditMissionLadderActivity.class.getSimpleName();

    private MissionLadderBean missionLadderBean;
    private Long missionLadderId;

    private EditText nameEditText;
    private EditText descriptionEditText;
    private ListViewNoScroll missionLaddersListView;
    private Button createMissionTreeButton;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_admin_edit_mission_ladder);
        super.onCreate(savedInstanceState);

        missionLadderId =
                getIntent().getLongExtra(ExtraConstants.EXTRA_MISSION_LADDER_ID, -1);
        missionLadderBean = null;

        Log.i(LOG_CAT, "Edit mission ladder called with ladder id: " + missionLadderId);

        nameEditText = (EditText) findViewById(R.id.missionLadderNameEditText);
        descriptionEditText = (EditText) findViewById(R.id.missionLadderDescriptionEditText);
        missionLaddersListView = (ListViewNoScroll) findViewById(R.id.missionTreesListView);
        createMissionTreeButton = (Button) findViewById(R.id.createMissionTreeButton);

        createMissionTreeButton.setOnClickListener(new View.OnClickListener() {
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
    protected void onPause() {
        super.onPause();

        saveIfNecessary();
    }

    private void saveIfNecessary() {
        // Determine if the data should be saved.
        boolean shouldSave = false;
        if (missionLadderBean == null) {
            // Check if any data has been entered in the name field.
            if (nameEditText.getText().length() > 0) {
                missionLadderBean = new MissionLadderBean();
                missionLadderBean.setDomainId(OxygenSharedPreferences.getCurrentDomainId(this));
                shouldSave = true;
            }
        } else {
            // Check if changes have been made.
            shouldSave = !StringUtil.equals(nameEditText, missionLadderBean.getName())
                    || !StringUtil.equals(descriptionEditText, missionLadderBean.getDescription());
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
    public void receiveData(final DataRepository dataRepository) {
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
                    dataRepository.getMissionLadderBean(missionLadderId);
            if (missionLadderBean != null) {
                nameEditText.setText(missionLadderBean.getName());
                descriptionEditText.setText(missionLadderBean.getDescription());

                MissionTreeBeanArrayAdapter adapter = new MissionTreeBeanArrayAdapter(
                        missionLadderBean.getMissionTreeBeans());
                missionLaddersListView.setAdapter(adapter);

                setTitle(String.format(
                        getString(R.string.edit_mission_ladder_title),
                        missionLadderBean.getName()));
            }
        }
    }

    private final class MissionTreeBeanArrayAdapter
            extends ArrayAdapter<MissionTreeBean> {

        private MissionTreeBeanArrayAdapter(List<MissionTreeBean> objects) {
            super(getApplicationContext(), R.layout.list_item_mission_tree, objects);
        }

        @Override
        public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) AdminEditMissionLadderActivity.this
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.list_item_mission_tree, parent, false);
            }

            final MissionTreeBean missionTreeBean = getItem(position);
            final String missionTreeNameLabel = String.format(
                    getString(R.string.mission_tree_label),
                    missionTreeBean.getLevel(),
                    missionTreeBean.getName());
            Button missionTreeNameButton =
                    (Button) convertView.findViewById(R.id.missionTreeNameButton);
            missionTreeNameButton.setText(missionTreeNameLabel);
            missionTreeNameButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    saveIfNecessary();

                    Long missionTreeId = missionTreeBean.getId();
                    Intent intent = new Intent(
                            getApplicationContext(),
                            AdminEditMissionTreeActivity.class);
                    intent.putExtra(ExtraConstants.EXTRA_MISSION_LADDER_ID, missionLadderId);
                    intent.putExtra(ExtraConstants.EXTRA_MISSION_TREE_ID, missionTreeId);
                    startActivity(intent);
                }
            });

            ImageButton missionTreeDeleteButton =
                    (ImageButton) convertView.findViewById(R.id.missionTreeDeleteLink);
            missionTreeDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String deleteMessage = String.format(
                            getString(R.string.confirm_delete_mission_tree_message),
                            missionTreeNameLabel);
                    ConfirmationDialogBuilder.show(
                            AdminEditMissionLadderActivity.this, deleteMessage,
                            new Runnable() {
                                @Override
                                public void run() {
                                    dataServiceConnection.getLocalBinder().deleteMissionTree(
                                            missionLadderBean.getId(),
                                            missionTreeBean.getId());
                                }
                            });
                }
            });

            return convertView;
        }
    }
}
