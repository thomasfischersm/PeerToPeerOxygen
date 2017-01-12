package com.playposse.peertopeeroxygen.android.student;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.playposse.peertopeeroxygen.android.R;

/**
 * An {@link android.app.Activity} that provides static help about how to use the application.
 */
public class StudentHelpActivity extends StudentParentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_student_help);
        super.onCreate(savedInstanceState);

        setTitle(R.string.student_help_title);
    }
}
