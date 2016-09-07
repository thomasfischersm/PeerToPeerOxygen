package com.playposse.peertopeeroxygen.android.admin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.data.DataRepository;
import com.playposse.peertopeeroxygen.android.model.ExtraConstants;
import com.playposse.peertopeeroxygen.android.ui.adapters.EditMissionPagerAdapter;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionTreeBean;

import javax.annotation.Nullable;

/**
 * {@link android.app.Activity} that shows, edits, and creates a specific mission.
 */
public class AdminEditMissionActivity extends AdminParentActivity {

    private Long missionLadderId;
    private Long missionTreeId;
    private Long missionId;
    private MissionBean missionBean;
    private MissionTreeBean missionTreeBean;

    private ViewPager editMissionPager;
    private EditMissionPagerAdapter missionPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_admin_edit_mission);
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        missionLadderId = intent.getLongExtra(ExtraConstants.EXTRA_MISSION_LADDER_ID, -1);
        missionTreeId = intent.getLongExtra(ExtraConstants.EXTRA_MISSION_TREE_ID, -1);
        missionId = intent.getLongExtra(ExtraConstants.EXTRA_MISSION_ID, -1);
        missionBean = null;

        editMissionPager = (ViewPager) findViewById(R.id.editMissionPager);
        missionPagerAdapter = new EditMissionPagerAdapter(getSupportFragmentManager(), this);
        editMissionPager.setAdapter(missionPagerAdapter);
    }

    @Override
    protected void onPause() {
        super.onPause();

        saveIfNecessary();
    }

    @Override
    public void receiveData(final DataRepository dataRepository) {
        missionTreeBean = dataRepository.getMissionTreeBean(
                missionLadderId,
                missionTreeId);

        if (missionId == -1) {
            // new mission
            missionBean = null;
            setTitle(String.format(
                    getString(R.string.edit_mission_title),
                    getString(R.string.new_entity)));
        } else {
            // existing mission
            missionBean = dataRepository.getMissionBean(
                    missionLadderId,
                    missionTreeId,
                    missionId);


            setTitle(String.format(
                    getString(R.string.edit_mission_title),
                    missionBean.getName()));
        }

        missionPagerAdapter.showMission(missionTreeBean, missionBean);
    }

    private void saveIfNecessary() {
        // Determine if data should be saved.
        boolean shouldSave = missionPagerAdapter.isDirty(missionBean);

        if ((shouldSave) && (missionBean == null)) {
                missionBean = new MissionBean();
        }

        // Save mission.
        if (shouldSave) {
            missionPagerAdapter.save(missionBean);

            dataServiceConnection
                    .getLocalBinder()
                    .save(missionLadderId, missionTreeId, missionBean);
        }
    }

    /**
     * An interface for fragments inside of this {@link Activity} to implement. Each fragment
     * stores part of the mission data.
     */
    public interface EditMissionFragment {
        void showMission(MissionTreeBean missionTreeBean, @Nullable MissionBean missionBean);
        boolean isDirty(MissionBean missionBean);
        void save(MissionBean missionBean);
    }
}
