package com.playposse.peertopeeroxygen.android.student;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.data.DataRepository;
import com.playposse.peertopeeroxygen.android.data.types.UserMissionRoleType;
import com.playposse.peertopeeroxygen.android.model.ExtraConstants;
import com.playposse.peertopeeroxygen.android.ui.widgets.StarRatingView;
import com.playposse.peertopeeroxygen.android.util.StringUtil;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionBean;

/**
 * An activity that lets a user submit a mission rating after the mission has been completed.
 */
public class StudentMissionRatingActivity extends StudentParentActivity {

    private Long missionLadderId;
    private Long missionTreeId;
    private Long missionId;
    private UserMissionRoleType userMissionRole;

    private MissionBean missionBean;

    private TextView headingTextView;
    private StarRatingView ratingView;
    private EditText commentEditText;
    private Button cancelButton;
    private Button continueButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_student_mission_rating);
        super.onCreate(savedInstanceState);

        setTitle(R.string.student_mission_feedback_title);

        missionId = getIntent().getLongExtra(ExtraConstants.EXTRA_MISSION_ID, -1);
        final String userMissionRoleStr =
                getIntent().getStringExtra(ExtraConstants.EXTRA_USER_MISSION_ROLE);
        userMissionRole = UserMissionRoleType.valueOf(userMissionRoleStr);

        headingTextView = (TextView) findViewById(R.id.headingTextView);
        ratingView = (StarRatingView) findViewById(R.id.ratingView);
        commentEditText = (EditText) findViewById(R.id.commentEditText);
        cancelButton = (Button) findViewById(R.id.cancelButton);
        continueButton = (Button) findViewById(R.id.continueButton);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startNextActivity();
            }
        });

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitRating();
            }
        });
    }

    private void submitRating() {
        int rating = ratingView.getRating();
        String comment = StringUtil.getCleanString(commentEditText.getText());

        if (rating == 0) {
            continueButton.setError(getString(R.string.rating_required_error));
            return;
        }

        if (dataServiceConnection != null) {
            dataServiceConnection.getLocalBinder()
                    .submitMissionFeedback(missionId, rating, comment);
        }

        startNextActivity();
    }

    @Override
    public void receiveData(DataRepository dataRepository) {
        Long[] missionPath = dataRepository.getMissionPath(missionId);
        missionLadderId = missionPath[0];
        missionTreeId = missionPath[1];
        missionBean = dataRepository.getMissionBean(missionLadderId, missionTreeId, missionId);

        headingTextView.setText(String.format(
                getString(R.string.student_mission_feedback_heading),
                missionBean.getName()));
    }

    private void startTreeActivity() {
        // Re-direct user back to the tree activity.
        Intent intent = ExtraConstants.createIntent(
                this,
                StudentMissionTreeActivity.class,
                missionLadderId,
                missionTreeId,
                null);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void startNextActivity() {
        if (userMissionRole == UserMissionRoleType.student) {
            startTreeActivity();
        } else {
            startActivity(new Intent(this, StudentMainActivity.class));
        }
    }
}
