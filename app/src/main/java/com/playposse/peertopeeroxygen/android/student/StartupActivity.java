package com.playposse.peertopeeroxygen.android.student;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.globalconfiguration.RedirectRouting;

/**
 * An {@link android.app.Activity} that's called by Android when the app first starts.
 * This {@link android.app.Activity} doesn't show anything to the user. It's sole purpose is to
 * provide an entry point where the app can dynamically figure out which the first activity should
 * be.
 */
public class StartupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_startup);
    }

    @Override
    protected void onStart() {
        super.onStart();

        RedirectRouting.onAppStartup(this);
    }
}
