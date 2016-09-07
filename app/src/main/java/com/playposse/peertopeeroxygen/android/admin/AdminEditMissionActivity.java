package com.playposse.peertopeeroxygen.android.admin;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.data.DataRepository;
import com.playposse.peertopeeroxygen.android.data.types.PointType;
import com.playposse.peertopeeroxygen.android.model.ExtraConstants;
import com.playposse.peertopeeroxygen.android.ui.widgets.RequiredMissionListView;
import com.playposse.peertopeeroxygen.android.util.MathUtil;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionTreeBean;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * {@link android.app.Activity} that shows, edits, and creates a specific mission.
 */
public class AdminEditMissionActivity extends AdminParentActivity {

    private static Pattern YOUTUBE_URL_PATTERN = Pattern.compile("https://[^v]+v=([a-zA-Z0-9-]+)");
    private static Pattern MOBILE_YOUTUBE_URL_PATTERN =
            Pattern.compile("https://youtu.be/([a-zA-Z0-9-]+)");

    private Long missionLadderId;
    private Long missionTreeId;
    private Long missionId;
    private MissionBean missionBean;
    private MissionTreeBean missionTreeBean;

    EditText nameEditText;
    EditText minimumStudyCountEditText;
    EditText teachPointEditText;
    EditText practicePointEditText;
    EditText heartPointEditText;
    EditText studentVideoIdEditText;
    EditText buddyVideoIdEditText;
    EditText studentInstructionEditText;
    EditText buddyInstructionEditText;
    private RequiredMissionListView requiredMissionsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_admin_edit_mission);
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        missionLadderId = intent.getLongExtra(ExtraConstants.EXTRA_MISSION_LADDER_ID, -1);
        missionTreeId = intent.getLongExtra(ExtraConstants.EXTRA_MISSION_TREE_ID, -1);
        missionId = intent.getLongExtra(ExtraConstants.EXTRA_MISSION_ID, -1);
        missionBean = null;

        nameEditText = (EditText) findViewById(R.id.missionNameEditText);
        minimumStudyCountEditText = (EditText) findViewById(R.id.minimumStudyCountEditText);
        teachPointEditText = (EditText) findViewById(R.id.teachPointEditText);
        practicePointEditText = (EditText) findViewById(R.id.practicePointEditText);
        heartPointEditText = (EditText) findViewById(R.id.heartPointEditText);
        studentVideoIdEditText = (EditText) findViewById(R.id.studentVideoIdEditText);
        buddyVideoIdEditText = (EditText) findViewById(R.id.buddyVideoIdEditText);
        studentInstructionEditText = (EditText) findViewById(R.id.studentInstructionsEditText);
        buddyInstructionEditText = (EditText) findViewById(R.id.buddyInstructionsEditText);
        requiredMissionsListView =
                (RequiredMissionListView) findViewById(R.id.requiredMissionsListView);

        studentVideoIdEditText.addTextChangedListener(new RemoveYouTubeUrlTextWatcher());
        buddyVideoIdEditText.addTextChangedListener(new RemoveYouTubeUrlTextWatcher());
    }

    @Override
    protected void onPause() {
        super.onPause();

        saveIfNecessary();
    }

    @Override
    public void receiveData(final DataRepository dataRepository) {
        if (missionId == -1) {
            // new mission ladder
            nameEditText.setText("");
            minimumStudyCountEditText.setText("1");
            teachPointEditText.setText("1");
            practicePointEditText.setText("0");
            heartPointEditText.setText("0");
            studentVideoIdEditText.setText("");
            buddyVideoIdEditText.setText("");
            studentInstructionEditText.setText("");
            buddyInstructionEditText.setText("");

            setTitle(String.format(
                    getString(R.string.edit_mission_title),
                    getString(R.string.new_entity)));
        } else {
            // existing mission ladder
            missionBean = dataRepository.getMissionBean(
                    missionLadderId,
                    missionTreeId,
                    missionId);
            missionTreeBean = dataRepository.getMissionTreeBean(
                    missionLadderId,
                    missionTreeId);

            nameEditText.setText(missionBean.getName());
            minimumStudyCountEditText.setText("" + missionBean.getMinimumStudyCount());
            teachPointEditText.setText(
                    "" + DataRepository.getPointByType(missionBean, PointType.teach));
            practicePointEditText.setText(
                    "" + DataRepository.getPointByType(missionBean, PointType.practice));
            heartPointEditText.setText(
                    "" + DataRepository.getPointByType(missionBean, PointType.heart));
            studentVideoIdEditText.setText(missionBean.getStudentYouTubeVideoId());
            buddyVideoIdEditText.setText(missionBean.getBuddyYouTubeVideoId());
            studentInstructionEditText.setText(missionBean.getStudentInstruction());
            buddyInstructionEditText.setText(missionBean.getBuddyInstruction());

            requiredMissionsListView.setAdapter(
                    missionTreeBean.getMissionBeans(),
                    missionBean.getRequiredMissionIds(),
                    missionBean);

            setTitle(String.format(
                    getString(R.string.edit_mission_title),
                    missionBean.getName()));
        }
    }

    private void saveIfNecessary() {
        // Determine if data should be saved.
        boolean shouldSave = false;
        if (missionBean == null) {
            if (nameEditText.getText().length() > 0) {
                shouldSave = true;
                missionBean = new MissionBean();
            }
        } else {
            shouldSave =
                    !nameEditText.getText().toString().equals(missionBean.getName())
                            || (missionBean.getMinimumStudyCount() != MathUtil.tryParseInt(minimumStudyCountEditText.getText().toString(), 1))
                            || hasPointCountChanged(teachPointEditText, PointType.teach)
                            || hasPointCountChanged(practicePointEditText, PointType.practice)
                            || hasPointCountChanged(heartPointEditText, PointType.heart)
                            || !studentVideoIdEditText.getText().toString().equals(missionBean.getStudentYouTubeVideoId())
                            || !buddyVideoIdEditText.getText().toString().equals(missionBean.getBuddyYouTubeVideoId())
                            || !studentInstructionEditText.getText().toString().equals(missionBean.getStudentInstruction())
                            || !buddyInstructionEditText.getText().toString().equals(missionBean.getBuddyInstruction())
                            || requiredMissionsListView.isDirty();
        }

        // Save mission.
        if (shouldSave) {
            missionBean.setName(nameEditText.getText().toString());
            missionBean.setMinimumStudyCount(MathUtil.tryParseInt(minimumStudyCountEditText.getText().toString(), 1));
            setPointOnMissionBean(teachPointEditText, PointType.teach);
            setPointOnMissionBean(practicePointEditText, PointType.practice);
            setPointOnMissionBean(heartPointEditText, PointType.heart);
            missionBean.setStudentYouTubeVideoId(studentVideoIdEditText.getText().toString());
            missionBean.setBuddyYouTubeVideoId(buddyVideoIdEditText.getText().toString());
            missionBean.setStudentInstruction(studentInstructionEditText.getText().toString());
            missionBean.setBuddyInstruction(buddyInstructionEditText.getText().toString());
            missionBean.setRequiredMissionIds(requiredMissionsListView.getRequiredMissionIds());

            dataServiceConnection
                    .getLocalBinder()
                    .save(missionLadderId, missionTreeId, missionBean);
        }
    }

    private void setPointOnMissionBean(EditText editText, PointType pointType) {
        int pointCount = MathUtil.tryParseInt(editText.getText().toString(), 0);
        DataRepository.setPoint(missionBean, pointCount, pointType);
    }

    private boolean hasPointCountChanged(EditText editText, PointType pointType) {
        int originalCount = DataRepository.getPointByType(missionBean, pointType);
        int newCount = MathUtil.tryParseInt(editText.getText().toString(), 0);
        return originalCount != newCount;
    }

    /**
     * A {@link TextWatcher} that removes the URL part from YouTube strings. The data store only
     * stores the id. A user however may copy and past the whole URL. So, this {@link TextWatcher}
     * removes the URL pieces.
     */
    private static class RemoveYouTubeUrlTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            // Nothing to do.
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            // Nothing to do.
        }

        @Override
        public void afterTextChanged(Editable editable) {
            extractVideoId(editable, YOUTUBE_URL_PATTERN);
            extractVideoId(editable, MOBILE_YOUTUBE_URL_PATTERN);
        }

        private static void extractVideoId(Editable editable, Pattern pattern) {
            Matcher matcher = pattern.matcher(editable);
            if (matcher.find()) {
                editable.clear();
                editable.append(matcher.group(1));
            }
        }
    }
}
