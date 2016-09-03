package com.playposse.peertopeeroxygen.android.student;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.data.DataRepository;
import com.playposse.peertopeeroxygen.android.data.types.PointType;
import com.playposse.peertopeeroxygen.android.ui.RenderQrCodeAsyncTask;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.UserBean;

/**
 * An activity that shows the profile information of the student.
 */
public class StudentProfileActivity extends StudentParentActivity {

    private static final String LOG_CAT = StudentProfileActivity.class.getSimpleName();

    private ImageView profilePhotoImageView;
    private TextView profileNameTextView;
    private ImageView qrCodeImageView;
    private TextView teachPointsTextView;
    private TextView practicePointsTextView;
    private TextView heartPointsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_student_profile);
        super.onCreate(savedInstanceState);

        profilePhotoImageView = (ImageView) findViewById(R.id.profilePhotoImageView);
        profileNameTextView = (TextView) findViewById(R.id.profileNameTextView);
        teachPointsTextView = (TextView) findViewById(R.id.teachPointsTextView);
        practicePointsTextView = (TextView) findViewById(R.id.practicePointsTextView);
        heartPointsTextView = (TextView) findViewById(R.id.heartPointsTextView);
        qrCodeImageView = (ImageView) findViewById(R.id.qrCodeImageView);

        setTitle(getString(R.string.profile_title));
    }

    @Override
    public void receiveData(final DataRepository dataRepository) {
        // Show data.
        UserBean userBean = dataRepository.getUserBean();
        String fullName =
                userBean.getFirstName() + " " + userBean.getLastName();
        profileNameTextView.setText(fullName);
        showPoints(userBean, teachPointsTextView, PointType.teach, R.string.teach_points_label);
        showPoints(userBean, practicePointsTextView, PointType.practice, R.string.practice_points_label);
        showPoints(userBean, heartPointsTextView, PointType.heart, R.string.heart_points_label);

        // Show profile photo.
        loadProfilePhoto(
                profilePhotoImageView,
                userBean.getFbProfileId(),
                userBean.getProfilePictureUrl());

        // Show QR code.
        new RenderQrCodeAsyncTask(userBean.getId(), qrCodeImageView).execute();
    }

    private void showPoints(
            UserBean userBean,
            TextView textView,
            PointType pointType,
            int messageId) {

        int points = DataRepository.getPointByType(userBean, pointType);
        String pointsString = String.format(getString(messageId), points);
        textView.setText(pointsString);
    }
}
