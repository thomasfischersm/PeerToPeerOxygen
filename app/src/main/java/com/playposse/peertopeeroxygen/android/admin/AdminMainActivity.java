package com.playposse.peertopeeroxygen.android.admin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.CompleteMissionDataBean;


/**
 * Activity that shows the top navigation for the administration activities.
 */
public class AdminMainActivity extends AdminParentActivity {

    private TextView openMissionLaddersTextView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_menu, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_admin_main);
        super.onCreate(savedInstanceState);

        setTitle(R.string.admin_home_title);

        openMissionLaddersTextView = (TextView) findViewById(R.id.openMissionLaddersLink);
        openMissionLaddersTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =
                        new Intent(AdminMainActivity.this, AdminShowMissionLaddersActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void receiveData(CompleteMissionDataBean completeMissionDataBean) {
        // Nothing to do. Yet, calling the service ensures that the data is already there for other
        // activities.
    }
}
