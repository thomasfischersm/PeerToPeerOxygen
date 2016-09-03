package com.playposse.peertopeeroxygen.android.admin;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.data.DataRepository;
import com.playposse.peertopeeroxygen.android.data.DataService;
import com.playposse.peertopeeroxygen.android.data.types.PointType;
import com.playposse.peertopeeroxygen.android.model.ExtraConstants;
import com.playposse.peertopeeroxygen.android.model.UserBeanParcelable;
import com.playposse.peertopeeroxygen.android.ui.dialogs.NumberPickerDialogBuilder;

import java.text.DateFormat;
import java.util.Date;

/**
 * An activity that shows an admin the details of a student.
 */
public class AdminStudentDetailActivity extends AdminParentActivity {

    private static final String LOG_CAT = AdminStudentDetailActivity.class.getSimpleName();

    private UserBeanParcelable studentBean;

    private ImageView profilePhotoImageView;
    private TextView nameTextView;
    private TextView signupDateTextView;
    private TextView teachPointsTextView;
    private TextView practicePointsTextView;
    private TextView heartPointsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_admin_student_detail);
        super.onCreate(savedInstanceState);

        studentBean = getIntent().getParcelableExtra(ExtraConstants.EXTRA_STUDENT_BEAN);

        String studentName = studentBean.getFirstName() + " " + studentBean.getLastName();
        setTitle(studentName);

        // Find View references.
        profilePhotoImageView = (ImageView) findViewById(R.id.profilePhotoImageView);
        nameTextView = (TextView) findViewById(R.id.nameTextView);
        signupDateTextView = (TextView) findViewById(R.id.signupDateTextView);
        teachPointsTextView = (TextView) findViewById(R.id.teachPointsTextView);
        practicePointsTextView = (TextView) findViewById(R.id.practicePointsTextView);
        heartPointsTextView = (TextView) findViewById(R.id.heartPointsTextView);

        // Format date.
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
        String signupDate = dateFormat.format(new Date(studentBean.getCreated()));
        String signupDateLabel = String.format(getString(R.string.sign_up_date_label), signupDate);

        // Show data.
        nameTextView.setText(studentName);
        signupDateTextView.setText(signupDateLabel);
        refreshPoints();
    }

    private void refreshPoints() {
        showPointsAndAddHandler(teachPointsTextView, PointType.teach, R.string.teach_points_label);
        showPointsAndAddHandler(practicePointsTextView, PointType.practice, R.string.practice_points_label);
        showPointsAndAddHandler(heartPointsTextView, PointType.heart, R.string.heart_points_label);
    }

    private void showPointsAndAddHandler(
            TextView textView,
            final PointType pointType,
            int messageId) {

        Integer points = studentBean.getPointsMap().get(pointType.name());
        if (points == null) {
            points = 0;
        }
        String pointsString = String.format(getString(messageId), points);
        textView.setText(pointsString);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String dialogTitle = String.format(
                        getString(R.string.add_points_dialog_title),
                        pointType.name());
                NumberPickerDialogBuilder.build(
                        AdminStudentDetailActivity.this,
                        dialogTitle,
                        new NumberPickerDialogBuilder.NumberPickerDialogCallback() {
                            @Override
                            public void onPickedNumer(int addedPoints) {
                                addPoints(pointType, addedPoints);
                            }
                        });
            }
        });
    }

    @Override
    public void receiveData(DataRepository dataRepository) {
        loadProfilePhoto(
                profilePhotoImageView,
                studentBean.getFbProfileId(),
                studentBean.getProfilePictureUrl());
    }

    private void addPoints(PointType pointType, int addedPoints) {
        Log.i(LOG_CAT, "About to add " + addedPoints + " " + pointType.name() + " points.");
        DataService.LocalBinder binder = dataServiceConnection.getLocalBinder();
        binder.addPointsByAdmin(studentBean.getId(), pointType.name(), addedPoints);

        Integer pointCount = studentBean.getPointsMap().get(pointType.name());
        if (pointCount == null) {
            pointCount = 0;
        }
        pointCount += addedPoints;
        studentBean.getPointsMap().put(pointType.name(), pointCount);

        refreshPoints();
    }
}
