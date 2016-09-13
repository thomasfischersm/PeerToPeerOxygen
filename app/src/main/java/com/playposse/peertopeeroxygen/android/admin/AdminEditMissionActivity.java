package com.playposse.peertopeeroxygen.android.admin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.data.DataRepository;
import com.playposse.peertopeeroxygen.android.googledrive.GoogleDriveInitializer;
import com.playposse.peertopeeroxygen.android.googledrive.GoogleDriveReader;
import com.playposse.peertopeeroxygen.android.googledrive.GoogleDriveWriter;
import com.playposse.peertopeeroxygen.android.model.ExtraConstants;
import com.playposse.peertopeeroxygen.android.ui.adapters.EditMissionPagerAdapter;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionTreeBean;

import javax.annotation.Nullable;

/**
 * {@link android.app.Activity} that shows, edits, and creates a specific mission.
 */
public class AdminEditMissionActivity
        extends AdminParentActivity
        implements GoogleDriveActivity, GoogleDriveReader.GoogleDriveReaderCallback {

    private static final String LOG_TAG = AdminEditMissionActivity.class.getSimpleName();

    private static final String SEPARATOR = "------";

    private final MissionFileGenerator missionFileGenerator = new MissionFileGenerator();

    private Long missionLadderId;
    private Long missionTreeId;
    private Long missionId;
    private MissionBean missionBean;
    private MissionTreeBean missionTreeBean;

    private ViewPager editMissionPager;
    private EditMissionPagerAdapter missionPagerAdapter;

    private GoogleApiClient googleApiClient;

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
    protected void onResume() {
        super.onResume();

        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (googleApiClient != null) {
            googleApiClient.disconnect();
        }

        saveIfNecessary();
    }

    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        GoogleDriveInitializer.onActivityResult(requestCode, resultCode, googleApiClient);
        GoogleDriveReader.onActivityResult(requestCode, resultCode, data, googleApiClient, this);
        GoogleDriveWriter.onActivityResult(
                requestCode,
                resultCode,
                data,
                googleApiClient,
                missionFileGenerator);
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

            if (missionBean == null) {
                // There is a special case where this activity could still hang around in the
                // background when the user deletes the mission in the mission tree activity.
                return;
            }

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

    @Override
    public void importFromDrive() {
        Runnable afterAction = new Runnable() {
            @Override
            public void run() {
                GoogleDriveReader.initiate(googleApiClient, AdminEditMissionActivity.this);
            }
        };

        if ((googleApiClient == null) || !googleApiClient.isConnected()) {
            googleApiClient = GoogleDriveInitializer.initialize(
                    this,
                    afterAction);
        } else {
            afterAction.run();
        }
    }

    @Override
    public void exportToDrive() {
        saveIfNecessary();

        Runnable afterAction = new Runnable() {
            @Override
            public void run() {
                GoogleDriveWriter.initiate(
                        googleApiClient,
                        AdminEditMissionActivity.this,
                        missionFileGenerator);
            }
        };

        if ((googleApiClient == null) || !googleApiClient.isConnected()) {
            googleApiClient = GoogleDriveInitializer.initialize(
                    this,
                    afterAction);
        } else {
            afterAction.run();
        }
    }

    @Override
    public void receiveFileContent(String fileContent) {
        String[] strings = fileContent.split(SEPARATOR);

        if (strings.length != 3) {
            Toast.makeText(
                    this,
                    R.string.import_mission_parse_error_toast,
                    Toast.LENGTH_LONG).show();
            return;
        }

        if (missionBean == null) {
            missionBean = new MissionBean();
        }

        missionBean.setName(strings[0]);
        missionBean.setStudentInstruction(strings[1]);
        missionBean.setBuddyInstruction(strings[2]);

        missionPagerAdapter.showMission(missionTreeBean, missionBean);
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

    /**
     * {@link GoogleDriveWriter.FileGenerator} to generate a file with the data of a mission.
     */
    private class MissionFileGenerator implements GoogleDriveWriter.FileGenerator {

        private static final String TEXT_FILE_SUFFIX = ".txt";
        private static final String SEPARATOR = "\n------\n";

        @Override
        public String getFileTitle() {
            return missionBean.getName() + TEXT_FILE_SUFFIX;
        }

        @Override
        public String getFileContent() {
            return missionBean.getName()
                    + SEPARATOR + missionBean.getStudentInstruction()
                    + SEPARATOR + missionBean.getBuddyInstruction();
        }
    }
}
