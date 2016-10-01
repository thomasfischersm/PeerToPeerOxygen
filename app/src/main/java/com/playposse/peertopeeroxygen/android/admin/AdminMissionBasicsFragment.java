package com.playposse.peertopeeroxygen.android.admin;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

    private static Pattern YOUTUBE_URL_PATTERN =
            Pattern.compile("https://[^v]+v=([a-zA-Z0-9-_]{11})");
    private static Pattern MOBILE_YOUTUBE_URL_PATTERN =
            Pattern.compile("https://youtu.be/([a-zA-Z0-9-_]{11})");

    private EditText nameEditText;
    private EditText minimumStudyCountEditText;
    private EditText teachPointEditText;
    private EditText practicePointEditText;
    private EditText heartPointEditText;
    private EditText studentVideoIdEditText;
    private EditText buddyVideoIdEditText;
    private Button importButton;
    private Button exportButton;

    private MissionTreeBean missionTreeBean;
    private MissionBean missionBean;

    private GoogleDriveActivity googleDriveActivity;

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
        importButton = (Button) rootView.findViewById(R.id.importButton);
        exportButton = (Button) rootView.findViewById(R.id.exportButton);

        studentVideoIdEditText.addTextChangedListener(new RemoveYouTubeUrlTextWatcher());
        buddyVideoIdEditText.addTextChangedListener(new RemoveYouTubeUrlTextWatcher());
        importButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (googleDriveActivity != null) {
                    googleDriveActivity.importFromDrive();
                }
            }
        });
        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (googleDriveActivity != null) {
                    googleDriveActivity.exportToDrive();
                }
            }
        });

        refreshMission();

        return rootView;
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
        } else if  (missionBean.getId() == null) {
            // Refreshing with data from a Google Drive import.
            nameEditText.setText(missionBean.getName());
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
                    !StringUtil.equals(nameEditText, missionBean.getName())
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof GoogleDriveActivity) {
            googleDriveActivity = (GoogleDriveActivity) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        googleDriveActivity = null;
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
