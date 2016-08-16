package com.playposse.peertopeeroxygen.android.student;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.facebook.FacebookSdk;
import com.playposse.peertopeeroxygen.android.R;

public class StudentLoginActivity extends AppCompatActivity {

    public static final String LOG_CAT = StudentLoginActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LOG_CAT, "Before Facebook SDK init");
        FacebookSdk.sdkInitialize(getApplicationContext());
        Log.i(LOG_CAT, "After Facebook SDK init");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }
}
