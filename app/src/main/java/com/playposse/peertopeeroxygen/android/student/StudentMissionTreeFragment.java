package com.playposse.peertopeeroxygen.android.student;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.admin.AdminEditMissionTreeActivity;
import com.playposse.peertopeeroxygen.android.data.DataRepository;
import com.playposse.peertopeeroxygen.android.data.DataServiceParentFragment;
import com.playposse.peertopeeroxygen.android.data.types.UserMissionRoleType;
import com.playposse.peertopeeroxygen.android.model.ExtraConstants;
import com.playposse.peertopeeroxygen.android.model.UserBeanParcelable;
import com.playposse.peertopeeroxygen.android.ui.widgets.MissionTreeWidget;
import com.playposse.peertopeeroxygen.android.util.VolleySingleton;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.LevelCompletionBean;
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

    private ImageButton editTreeButton;
    private LinearLayout lockLayout;
    private ImageView lockImageView;
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
        editTreeButton = (ImageButton) rootView.findViewById(R.id.editTreeButton);
        lockLayout = (LinearLayout) rootView.findViewById(R.id.lockLayout);
        lockImageView = (ImageView) rootView.findViewById(R.id.lockImageView);
        missionTreeWidget = (MissionTreeWidget) rootView.findViewById(R.id.missionTreeWidget);

        editTreeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((missionLadderId != null) && (missionTreeId != null)) {
                    Intent intent = ExtraConstants.createIntent(
                            getActivity(),
                            AdminEditMissionTreeActivity.class,
                            missionLadderId,
                            missionTreeId,
                            null);
                    startActivity(intent);
                }
            }
        });

        return rootView;
    }

    @Override
    public void receiveData(DataRepository dataRepository) {
        MissionTreeBean missionTreeBean = dataRepository
                .getMissionTreeBean(missionLadderId, missionTreeId);

        if (missionTreeBean == null) {
            return;
        }

        missionTreeWidget.setMissionTreeBean(
                missionLadderId,
                missionTreeBean,
                dataRepository);

        // Figure out if the level is unlocked.
        int previousLevel = missionTreeBean.getLevel() - 1;
        MissionTreeBean nextMissionTreeBean =
                dataRepository.getMissionTreeBeanByLevel(missionLadderId, previousLevel);
        boolean isUnlocked = false;
        if (dataRepository.getUserBean().getAdmin()) {
            isUnlocked = true;
        } else if (missionTreeBean.getLevel() == 1) {
            isUnlocked = true;
        } else if (nextMissionTreeBean != null) {
            LevelCompletionBean levelCompletionBean =
                    dataRepository.getLevelCompletionByMissionTreeId(nextMissionTreeBean.getId());
            isUnlocked = (levelCompletionBean != null);
        }
        lockLayout.setVisibility(isUnlocked ? View.GONE : View.VISIBLE);

        // Show editTreeButton to admins.
        Boolean isAdmin = dataRepository.getUserBean().getAdmin();
        editTreeButton.setVisibility(isAdmin ? View.VISIBLE : View.GONE);
    }
}
