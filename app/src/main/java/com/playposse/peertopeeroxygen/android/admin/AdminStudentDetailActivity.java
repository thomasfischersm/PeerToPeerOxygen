package com.playposse.peertopeeroxygen.android.admin;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.data.DataRepository;
import com.playposse.peertopeeroxygen.android.data.DataService;
import com.playposse.peertopeeroxygen.android.data.clientactions.ApiClientAction;
import com.playposse.peertopeeroxygen.android.data.types.PointType;
import com.playposse.peertopeeroxygen.android.model.ExtraConstants;
import com.playposse.peertopeeroxygen.android.model.UserBeanParcelable;
import com.playposse.peertopeeroxygen.android.ui.dialogs.NumberPickerDialogBuilder;
import com.playposse.peertopeeroxygen.android.util.VolleySingleton;

import java.text.DateFormat;
import java.util.Date;

/**
 * An activity that shows an admin the details of a student.
 */
public class AdminStudentDetailActivity extends AdminParentActivity {

    private static final String LOG_CAT = AdminStudentDetailActivity.class.getSimpleName();

    private UserBeanParcelable studentBean;

    private NetworkImageView profilePhotoImageView;
    private TextView nameTextView;
    private TextView signupDateTextView;
    private TextView teachPointsTextView;
    private TextView practicePointsTextView;
    private TextView heartPointsTextView;
    private Button promoteAdminButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_admin_student_detail);
        super.onCreate(savedInstanceState);

        studentBean = getIntent().getParcelableExtra(ExtraConstants.EXTRA_STUDENT_BEAN);

        String studentName = studentBean.getFirstName() + " " + studentBean.getLastName();
        setTitle(studentName);

        // Find View references.
        profilePhotoImageView = (NetworkImageView) findViewById(R.id.profilePhotoImageView);
        nameTextView = (TextView) findViewById(R.id.nameTextView);
        signupDateTextView = (TextView) findViewById(R.id.signupDateTextView);
        teachPointsTextView = (TextView) findViewById(R.id.teachPointsTextView);
        practicePointsTextView = (TextView) findViewById(R.id.practicePointsTextView);
        heartPointsTextView = (TextView) findViewById(R.id.heartPointsTextView);
        promoteAdminButton = (Button) findViewById(R.id.promoteAdminButton);

        // Format date.
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
        String signupDate = dateFormat.format(new Date(studentBean.getCreated()));
        String signupDateLabel = String.format(getString(R.string.sign_up_date_label), signupDate);

        // Show data.
        nameTextView.setText(studentName);
        signupDateTextView.setText(signupDateLabel);
        refreshPoints();
        setLabelonPromoteButton();

        // Show profile photo.
        ImageLoader imageLoader = VolleySingleton.getInstance(this).getImageLoader();
        profilePhotoImageView.setImageUrl(studentBean.getProfilePictureUrl(), imageLoader);

        // Attach actions
        promoteAdminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handlePromoteButtonClicked();
            }
        });
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

    private void setLabelonPromoteButton() {
        if (studentBean.isAdmin()) {
            promoteAdminButton.setText(R.string.demote_from_admin_button);
        } else {
            promoteAdminButton.setText(R.string.promote_to_admin_button);
        }
    }

    @Override
    public void receiveData(DataRepository dataRepository) {

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

    private void handlePromoteButtonClicked() {
        if (dataServiceConnection != null) {
            showLoadingProgress();
            dataServiceConnection.getLocalBinder().promoteToAdmin(
                    studentBean.getId(),
                    !studentBean.isAdmin(),
                    new ApiClientAction.CompletionCallback() {
                        @Override
                        public void onComplete() {
                            studentBean.setAdmin(!studentBean.isAdmin());
                            setLabelonPromoteButton();

                            // Ensure that a rotation change won't unset the data locally.
                            getIntent().putExtra(ExtraConstants.EXTRA_STUDENT_BEAN, studentBean);

                            dismissLoadingProgress();
                        }
                    });
        }
    }
}
