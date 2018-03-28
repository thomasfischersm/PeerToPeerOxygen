package com.playposse.peertopeeroxygen.android.student;

import android.os.Bundle;
import android.widget.TextView;

import com.playposse.peertopeeroxygen.android.R;

/**
 * An {@link android.app.Activity} that shows information about the app.
 */
public class StudentAboutActivity extends StudentParentActivity {

    private TextView playServicesAttributionTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_student_about);
        super.onCreate(savedInstanceState);
    }
}
