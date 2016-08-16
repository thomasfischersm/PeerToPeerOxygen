package com.playposse.peertopeeroxygen.android.student;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.CompleteMissionDataBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.UserBean;

import java.io.IOException;
import java.net.URL;

/**
 * An activity that shows the profile information of the student.
 */
public class StudentProfileActivity extends StudentParentActivity {

    public static final String LOG_CAT = StudentProfileActivity.class.getSimpleName();

    private ImageView profilePhotoImageView;
    private TextView profileNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_student_profile);
        super.onCreate(savedInstanceState);

        profilePhotoImageView = (ImageView) findViewById(R.id.profilePhotoImageView);
        profileNameTextView = (TextView) findViewById(R.id.profileNameTextView);

        setTitle(getString(R.string.profile_title));
    }

    @Override
    public void receiveData(final CompleteMissionDataBean completeMissionDataBean) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final UserBean userBean = completeMissionDataBean.getUserBean();
                    URL photoUrl = new URL(userBean.getProfilePictureUrl());
                    final Bitmap photoBitmap =
                            BitmapFactory.decodeStream(photoUrl.openConnection().getInputStream());

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            profilePhotoImageView.setImageBitmap(photoBitmap);
                            String fullName =
                                    userBean.getFirstName() + " " + userBean.getLastName();
                            profileNameTextView.setText(fullName);
                        }
                    });
                } catch (IOException ex) {
                    Log.e(LOG_CAT, "Failed to download profile photoe from Facebook", ex);
                }
            }
        }).start();
    }
}
