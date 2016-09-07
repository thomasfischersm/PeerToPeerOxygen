package com.playposse.peertopeeroxygen.android.admin;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.data.DataRepository;
import com.playposse.peertopeeroxygen.android.data.types.PointType;
import com.playposse.peertopeeroxygen.android.util.MathUtil;
import com.playposse.peertopeeroxygen.android.util.StringUtil;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionTreeBean;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A {@link Fragment} that makes the basic information of a mission editable.
 */
public class AdminMissionBasicsFragment
        extends Fragment
        implements AdminEditMissionActivity.EditMissionFragment {

    private static Pattern YOUTUBE_URL_PATTERN = Pattern.compile("https://[^v]+v=([a-zA-Z0-9-]+)");
    private static Pattern MOBILE_YOUTUBE_URL_PATTERN =
            Pattern.compile("https://youtu.be/([a-zA-Z0-9-]+)");

    private EditText nameEditText;
    private EditText minimumStudyCountEditText;
    private EditText teachPointEditText;
    private EditText practicePointEditText;
    private EditText heartPointEditText;
    private EditText studentVideoIdEditText;
    private EditText buddyVideoIdEditText;

    private MissionTreeBean missionTreeBean;
    private MissionBean missionBean;

    public AdminMissionBasicsFragment() {
        // Required empty public constructor
    }

    public static AdminMissionBasicsFragment newInstance() {
        AdminMissionBasicsFragment fragment = new AdminMissionBasicsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_admin_mission_basics, container, false);

        nameEditText = (EditText) rootView.findViewById(R.id.missionNameEditText);
        minimumStudyCountEditText = (EditText) rootView.findViewById(R.id.minimumStudyCountEditText);
        teachPointEditText = (EditText) rootView.findViewById(R.id.teachPointEditText);
        practicePointEditText = (EditText) rootView.findViewById(R.id.practicePointEditText);
        heartPointEditText = (EditText) rootView.findViewById(R.id.heartPointEditText);
        studentVideoIdEditText = (EditText) rootView.findViewById(R.id.studentVideoIdEditText);
        buddyVideoIdEditText = (EditText) rootView.findViewById(R.id.buddyVideoIdEditText);

        studentVideoIdEditText.addTextChangedListener(new RemoveYouTubeUrlTextWatcher());
        buddyVideoIdEditText.addTextChangedListener(new RemoveYouTubeUrlTextWatcher());

        refreshMission();

        return rootView;
    }

    public EditText getBuddyVideoIdEditText() {
        return buddyVideoIdEditText;
    }

    public EditText getHeartPointEditText() {
        return heartPointEditText;
    }

    public EditText getMinimumStudyCountEditText() {
        return minimumStudyCountEditText;
    }

    public EditText getNameEditText() {
        return nameEditText;
    }

    public EditText getPracticePointEditText() {
        return practicePointEditText;
    }

    public EditText getStudentVideoIdEditText() {
        return studentVideoIdEditText;
    }

    public EditText getTeachPointEditText() {
        return teachPointEditText;
    }

    @Override
    public void showMission(MissionTreeBean missionTreeBean, MissionBean missionBean) {
        this.missionTreeBean = missionTreeBean;
        this.missionBean = missionBean;

        refreshMission();
    }

    private void refreshMission() {
        if (nameEditText == null) {
            return;
        }

        if (missionBean == null) {
            nameEditText.setText("");
            minimumStudyCountEditText.setText("1");
            teachPointEditText.setText("1");
            practicePointEditText.setText("0");
            heartPointEditText.setText("0");
            studentVideoIdEditText.setText("");
            buddyVideoIdEditText.setText("");
        } else {
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
        }
    }

    @Override
    public boolean isDirty(MissionBean missionBean) {
        if (minimumStudyCountEditText == null) {
            return false;
        } else if (missionBean == null) {
            return nameEditText.getText().length() > 0;
        } else {
            return
                    !nameEditText.getText().toString().equals(missionBean.getName())
                            || (missionBean.getMinimumStudyCount() != MathUtil.tryParseInt(minimumStudyCountEditText.getText().toString(), 1))
                            || hasPointCountChanged(missionBean, teachPointEditText, PointType.teach)
                            || hasPointCountChanged(missionBean, practicePointEditText, PointType.practice)
                            || hasPointCountChanged(missionBean, heartPointEditText, PointType.heart)
                            || !StringUtil.equals(studentVideoIdEditText.getText(), missionBean.getStudentYouTubeVideoId())
                            || !StringUtil.equals(buddyVideoIdEditText.getText(),missionBean.getBuddyYouTubeVideoId());
        }
    }

    @Override
    public void save(MissionBean missionBean) {
        if (minimumStudyCountEditText == null) {
            return;
        }

        int minimumStudyCount =
                MathUtil.tryParseInt(minimumStudyCountEditText.getText().toString(), 1);

        missionBean.setName(nameEditText.getText().toString());
        missionBean.setMinimumStudyCount(minimumStudyCount);
        setPointOnMissionBean(missionBean, teachPointEditText, PointType.teach);
        setPointOnMissionBean(missionBean, practicePointEditText, PointType.practice);
        setPointOnMissionBean(missionBean, heartPointEditText, PointType.heart);
        missionBean.setStudentYouTubeVideoId(StringUtil.getCleanString(studentVideoIdEditText.getText()));
        missionBean.setBuddyYouTubeVideoId(StringUtil.getCleanString(buddyVideoIdEditText.getText()));
    }

    private void setPointOnMissionBean(
            MissionBean missionBean,
            EditText editText,
            PointType pointType) {

        int pointCount = MathUtil.tryParseInt(editText.getText().toString(), 0);
        DataRepository.setPoint(missionBean, pointCount, pointType);
    }

    private boolean hasPointCountChanged(
            MissionBean missionBean,
            EditText editText,
            PointType pointType) {

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
