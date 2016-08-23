package com.playposse.peertopeeroxygen.android.admin;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.data.DataRepository;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * Activity that shows the top navigation for the administration activities.
 */
public class AdminMainActivity extends AdminParentActivity {

    public static final String LOG_TAG = AdminMainActivity.class.getSimpleName();

    private TextView openMissionLaddersTextView;
    private TextView openStudentRosterTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_admin_main);
        super.onCreate(savedInstanceState);

        setTitle(R.string.admin_home_title);

        openMissionLaddersTextView = (TextView) findViewById(R.id.openMissionLaddersLink);
        openMissionLaddersTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(
                        new Intent(AdminMainActivity.this, AdminShowMissionLaddersActivity.class));
            }
        });

        openStudentRosterTextView = (TextView) findViewById(R.id.openStudentRosterLink);
        openStudentRosterTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminMainActivity.this, AdminStudentRosterActivity.class));
            }
        });

        // TODO: Remove for release
        printHashKey();
    }

    @Override
    public void receiveData(DataRepository dataRepository) {
        // Nothing to do. Yet, calling the service ensures that the data is already there for other
        // activities.
    }

    public void printHashKey() {
        try {
            PackageManager pm = getPackageManager();
            PackageInfo info = pm.getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.i(LOG_TAG, "printHashKey() Hash Key: " + hashKey);
            }
        } catch (NoSuchAlgorithmException e) {
            Log.e(LOG_TAG, "printHashKey()", e);
        } catch (Exception e) {
            Log.e(LOG_TAG, "printHashKey()", e);
        }
    }
}
