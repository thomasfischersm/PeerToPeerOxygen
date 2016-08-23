package com.playposse.peertopeeroxygen.android.student;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.data.DataRepository;
import com.playposse.peertopeeroxygen.android.data.facebook.FacebookProfilePhotoCache;
import com.playposse.peertopeeroxygen.android.widgets.RenderQrCodeAsyncTask;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.UserBean;

/**
 * An activity that shows the profile information of the student.
 */
public class StudentProfileActivity extends StudentParentActivity {

    private static final String LOG_CAT = StudentProfileActivity.class.getSimpleName();

    private ImageView profilePhotoImageView;
    private TextView profileNameTextView;
    private ImageView qrCodeImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_student_profile);
        super.onCreate(savedInstanceState);

        profilePhotoImageView = (ImageView) findViewById(R.id.profilePhotoImageView);
        profileNameTextView = (TextView) findViewById(R.id.profileNameTextView);
        qrCodeImageView = (ImageView) findViewById(R.id.qrCodeImageView);

        setTitle(getString(R.string.profile_title));
    }

    @Override
    public void receiveData(final DataRepository dataRepository) {
        // Show data.
        final UserBean userBean = dataRepository.getUserBean();
        String fullName =
                userBean.getFirstName() + " " + userBean.getLastName();
        profileNameTextView.setText(fullName);

        // Show profile photo.
        FacebookProfilePhotoCache photoCache = dataServiceConnection
                .getLocalBinder()
                .getDataRepository()
                .getFacebookProfilePhotoCache();
        photoCache.loadImage(
                this,
                profilePhotoImageView,
                userBean.getFbProfileId(),
                userBean.getProfilePictureUrl());

        // Show QR code.
        new RenderQrCodeAsyncTask(userBean.getId(), qrCodeImageView).execute();
    }
}
