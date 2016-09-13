package com.playposse.peertopeeroxygen.android.admin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.api.GoogleApiClient;
import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.data.CompleteMissionDataCache;
import com.playposse.peertopeeroxygen.android.data.DataRepository;
import com.playposse.peertopeeroxygen.android.googledrive.GoogleDriveBackupWriter;
import com.playposse.peertopeeroxygen.android.googledrive.GoogleDriveInitializer;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.CompleteMissionDataBean;

import java.io.IOException;
import java.util.Calendar;

/**
 * An {@link android.app.Activity} that allows an admin to back up all mission data to Google Drive.
 */
public class AdminBackupActivity extends AdminParentActivity {

    private static final String LOG_CAT = AdminBackupActivity.class.getSimpleName();

    private final BackupFileGenerator fileGenerator = new BackupFileGenerator();

    private Button backupButton;

    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_admin_backup);
        super.onCreate(savedInstanceState);

        backupButton = (Button) findViewById(R.id.backupButton);
        backupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backup();
            }
        });
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
    }

    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        GoogleDriveInitializer.onActivityResult(requestCode, resultCode, googleApiClient);
        GoogleDriveBackupWriter.onActivityResult(
                requestCode,
                resultCode,
                data,
                googleApiClient,
                fileGenerator);
    }

    @Override
    public void receiveData(DataRepository dataRepository) {
        // Nothing to do.
    }

    private void backup() {
        Runnable afterAction = new Runnable() {
            @Override
            public void run() {
                GoogleDriveBackupWriter.initiate(
                        googleApiClient,
                        AdminBackupActivity.this,
                        fileGenerator);
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

    /**
     * {@link GoogleDriveBackupWriter.FileGenerator} that converts all the mission data to JSON.
     */
    private class BackupFileGenerator implements GoogleDriveBackupWriter.FileGenerator {

        @Override
        public String getFileTitle() {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            return String.format(
                    getString(R.string.backup_mission_data_file_name),
                    year,
                    month,
                    day);
        }

        @Override
        public String getFileContent() {
            CompleteMissionDataBean completeMissionDataBean =
                    dataServiceConnection
                            .getLocalBinder()
                            .getDataRepository()
                            .getCompleteMissionDataBean();
            String json = null;
            try {
                json = CompleteMissionDataCache.toJson(completeMissionDataBean);
            } catch (IOException ex) {
                Log.e(LOG_CAT, "Failed to encode mission data in json.", ex);
            }
            return json;
        }
    }
}
