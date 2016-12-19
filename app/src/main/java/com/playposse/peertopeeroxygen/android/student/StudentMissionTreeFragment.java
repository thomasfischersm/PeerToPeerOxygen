package com.playposse.peertopeeroxygen.android.student;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.data.DataRepository;
import com.playposse.peertopeeroxygen.android.data.DataServiceParentFragment;
import com.playposse.peertopeeroxygen.android.data.types.UserMissionRoleType;
import com.playposse.peertopeeroxygen.android.model.ExtraConstants;
import com.playposse.peertopeeroxygen.android.model.UserBeanParcelable;
import com.playposse.peertopeeroxygen.android.ui.widgets.MissionTreeWidget;
import com.playposse.peertopeeroxygen.android.util.VolleySingleton;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionCompletionBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionTreeBean;

/**
 * A {@link Fragment} that shows a mission tree for the user to visually see the missions and their
 * completions.
 */
public class StudentMissionTreeFragment extends DataServiceParentFragment {

    private final static String LOG_CAT = StudentMissionTreeFragment.class.getSimpleName();

    private Long missionLadderId;
    private Long missionTreeId;

    private MissionTreeWidget missionTreeWidget;

    public StudentMissionTreeFragment() {
        // Required empty public constructor
    }

    public static StudentMissionTreeFragment newInstance(
            Long missionLadderId,
            Long missionTreeId) {

        StudentMissionTreeFragment fragment = new StudentMissionTreeFragment();
        Bundle args = new Bundle();
        args.putLong(ExtraConstants.EXTRA_MISSION_LADDER_ID, missionLadderId);
        args.putLong(ExtraConstants.EXTRA_MISSION_TREE_ID, missionTreeId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            missionLadderId = getArguments().getLong(ExtraConstants.EXTRA_MISSION_LADDER_ID);
            missionTreeId = getArguments().getLong(ExtraConstants.EXTRA_MISSION_TREE_ID);
        }
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        View rootView = inflater.inflate(
                R.layout.fragment_student_mission_tree,
                container,
                false);

        missionTreeWidget = (MissionTreeWidget) rootView.findViewById(R.id.missionTreeWidget);


        return rootView;
    }

    @Override
    public void receiveData(DataRepository dataRepository) {
        MissionTreeBean missionTreeBean = dataRepository
                .getMissionTreeBean(missionLadderId, missionTreeId);

        missionTreeWidget.setMissionTreeBean(
                missionLadderId,
                missionTreeBean,
                dataRepository);
    }
}
