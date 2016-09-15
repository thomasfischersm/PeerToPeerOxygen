package com.playposse.peertopeeroxygen.android.admin;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.util.StringUtil;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionTreeBean;

/**
 * A {@link Fragment} that edits the buddy instructions of a mission.
 */
public class AdminMissionBuddyInstructionsFragment
        extends Fragment
        implements AdminEditMissionActivity.EditMissionFragment {

    private EditText buddyInstructionEditText;

    private MissionTreeBean missionTreeBean;
    private MissionBean missionBean;

    public AdminMissionBuddyInstructionsFragment() {
        // Required empty public constructor
    }

    public static AdminMissionBuddyInstructionsFragment newInstance() {
        AdminMissionBuddyInstructionsFragment fragment =
                new AdminMissionBuddyInstructionsFragment();
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
                R.layout.fragment_admin_mission_buddy_instructions,
                container,
                false);

        buddyInstructionEditText =
                (EditText) rootView.findViewById(R.id.buddyInstructionsEditText);

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
        if (buddyInstructionEditText == null) {
            return;
        }

        if (missionBean == null) {
            buddyInstructionEditText.setText("");
        } else {
            buddyInstructionEditText.setText(missionBean.getBuddyInstruction());
        }
    }

    @Override
    public boolean isDirty(MissionBean missionBean) {
        if (buddyInstructionEditText == null) {
            return false;
        } else if (missionBean == null) {
            return false; // Don't save unless a name is specified for the mission.
        } else {
            return !StringUtil.equals(buddyInstructionEditText, missionBean.getBuddyInstruction());
        }
    }

    @Override
    public void save(MissionBean missionBean) {
        if (buddyInstructionEditText == null) {
            return;
        }

        missionBean.setBuddyInstruction(buddyInstructionEditText.getText().toString());
    }
}