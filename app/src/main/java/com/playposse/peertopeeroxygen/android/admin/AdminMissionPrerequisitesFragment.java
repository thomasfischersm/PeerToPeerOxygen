package com.playposse.peertopeeroxygen.android.admin;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.missiondependencies.MissionCycleDetector;
import com.playposse.peertopeeroxygen.android.ui.widgets.RequiredMissionListView;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionTreeBean;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link Fragment} that edits prerequisite missions.
 */
public class AdminMissionPrerequisitesFragment
        extends Fragment
        implements AdminEditMissionActivity.EditMissionFragment {

    private RequiredMissionListView requiredMissionsListView;

    private MissionTreeBean missionTreeBean;
    private MissionBean missionBean;

    public AdminMissionPrerequisitesFragment() {
        // Required empty public constructor
    }

    public static AdminMissionPrerequisitesFragment newInstance() {
        AdminMissionPrerequisitesFragment fragment = new AdminMissionPrerequisitesFragment();
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
                R.layout.fragment_admin_mission_prerequisites,
                container,
                false);

        requiredMissionsListView =
                (RequiredMissionListView) rootView.findViewById(R.id.requiredMissionsListView);

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
        if (requiredMissionsListView == null) {
            return;
        }

        if (missionBean == null) {
            requiredMissionsListView.setAdapter(
                    missionTreeBean.getMissionBeans(),
                    new ArrayList<Long>(),
                    missionBean);
        } else {
            List<MissionBean> availableMissions =
                    MissionCycleDetector.findPossibleChildren(missionBean, missionTreeBean);
            requiredMissionsListView.setAdapter(
                    availableMissions,
                    missionBean.getRequiredMissionIds(),
                    missionBean);
        }
    }

    @Override
    public boolean isDirty(MissionBean missionBean) {
        if (requiredMissionsListView == null) {
            return false;
        } else {
            return requiredMissionsListView.isDirty();
        }
    }

    @Override
    public void save(MissionBean missionBean) {
        if (requiredMissionsListView == null) {
            return;
        }

        missionBean.setRequiredMissionIds(requiredMissionsListView.getRequiredMissionIds());
    }
}
