package com.playposse.peertopeeroxygen.android.admin;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.data.DataService;
import com.playposse.peertopeeroxygen.android.data.DataServiceConnection;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.CompleteMissionDataBean;

public class AdminShowMissionLaddersActivity
        extends AppCompatActivity
        implements DataService.DataReceivedCallback {

    private DataServiceConnection dataServiceConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_show_mission_ladders);
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

            }
        });
    }
}
