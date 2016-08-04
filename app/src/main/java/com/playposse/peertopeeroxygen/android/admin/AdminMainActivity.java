package com.playposse.peertopeeroxygen.android.admin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.playposse.peertopeeroxygen.R;

/**
 * Activity that shows the top navigation for the administration activities.
 */
public class AdminMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        TextView openMissionLaddersTextView = (TextView) findViewById(R.id.openMissionLaddersLink);
        openMissionLaddersTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =
                        new Intent(AdminMainActivity.this, AdminShowMissionLaddersActivity.class);
                startActivity(intent);
            }
        });
    }
}
