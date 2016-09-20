package com.playposse.peertopeeroxygen.android.student;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.common.GoogleApiAvailability;
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

        playServicesAttributionTextView =
                (TextView) findViewById(R.id.playServicesAttributionTextView);

        new AttributionLoaderAsyncTask().execute();
    }

    /**
     * An {@link AsyncTask} that loads the copyright notices for the Google Play Services. The text
     * is very long and slows down the loading of the {@link android.app.Activity}.
     */
    private class AttributionLoaderAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            return GoogleApiAvailability
                    .getInstance()
                    .getOpenSourceSoftwareLicenseInfo(StudentAboutActivity.this);
        }

        @Override
        protected void onPostExecute(String playServicesAttribution) {
            playServicesAttributionTextView.setText(playServicesAttribution);
        }
    }
}
