package com.playposse.peertopeeroxygen.android.admin;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.util.StringUtil;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionTreeBean;

/**
 * A {@link Fragment} that edits the student instructions of a mission.
 */
public class AdminMissionStudentInstructionsFragment
        extends Fragment
        implements AdminEditMissionActivity.EditMissionFragment {

    private static final String LOG_CAT = AdminMissionStudentInstructionsFragment.class.getSimpleName();

    private EditText studentInstructionEditText;

    private MissionTreeBean missionTreeBean;
    private MissionBean missionBean;

    public AdminMissionStudentInstructionsFragment() {
        // Required empty public constructor
    }

    public static AdminMissionStudentInstructionsFragment newInstance() {
        AdminMissionStudentInstructionsFragment fragment =
                new AdminMissionStudentInstructionsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        View rootView = inflater.inflate(
                R.layout.fragment_admin_mission_student_instructions,
                container,
                false);

        studentInstructionEditText =
                (EditText) rootView.findViewById(R.id.studentInstructionsEditText);

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
        if (studentInstructionEditText == null) {
            return;
        }

        if (missionBean == null) {
            studentInstructionEditText.setText("");
        } else {
            studentInstructionEditText.setText(missionBean.getStudentInstruction());
        }
    }

    @Override
    public boolean isDirty(MissionBean missionBean) {
        if (studentInstructionEditText == null) {
            return false;
        } else if (missionBean == null) {
            return false; // Don't save unless a name is specified for the mission.
        } else {
            return !StringUtil.equals(studentInstructionEditText, missionBean.getStudentInstruction());
        }
    }

    @Override
    public void save(MissionBean missionBean) {
        if (studentInstructionEditText == null) {
            return;
        }

        Log.i(LOG_CAT, "Copies student instructions to the bean: " + missionBean.getStudentInstruction());
        missionBean.setStudentInstruction(studentInstructionEditText.getText().toString());
    }
}
