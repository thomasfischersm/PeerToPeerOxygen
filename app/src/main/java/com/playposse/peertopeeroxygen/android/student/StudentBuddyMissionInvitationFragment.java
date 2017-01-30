package com.playposse.peertopeeroxygen.android.student;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.data.DataRepository;
import com.playposse.peertopeeroxygen.android.data.DataServiceParentFragment;
import com.playposse.peertopeeroxygen.android.data.types.UserMissionRoleType;
import com.playposse.peertopeeroxygen.android.model.ExtraConstants;
import com.playposse.peertopeeroxygen.android.model.UserBeanParcelable;
import com.playposse.peertopeeroxygen.android.util.AnalyticsUtil;
import com.playposse.peertopeeroxygen.android.util.FacebookUtil;
import com.playposse.peertopeeroxygen.android.util.VolleySingleton;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionCompletionBean;

import static com.playposse.peertopeeroxygen.android.util.AnalyticsUtil.AnalyticsCategory.studentGraduation;

/**
 * The first {@link Fragment} in the {@link StudentBuddyMissionActivity} that tells the buddy that
 * he/she has just been invited to teach a mission.
 */
public class StudentBuddyMissionInvitationFragment extends DataServiceParentFragment {

    private final static String LOG_CAT =
            StudentBuddyMissionInvitationFragment.class.getSimpleName();

    private Long missionLadderId;
    private Long missionTreeId;
    private Long missionId;
    private UserBeanParcelable studentBean;

    private MissionBean missionBean;

    private TextView invitationTextView;
    private NetworkImageView studentPhotoImageView;
    private Button cancelButton;
    private Button graduateButton;

    // Views only visible when a senior buddy checkout is required.
    private TextView seniorBuddyRequiredTextView;

    public StudentBuddyMissionInvitationFragment() {
        // Required empty public constructor
    }

    public static StudentBuddyMissionInvitationFragment newInstance(
            Long missionLadderId,
            Long missionTreeId,
            Long missionId,
            UserBeanParcelable studentBean) {

        StudentBuddyMissionInvitationFragment fragment = new StudentBuddyMissionInvitationFragment();
        Bundle args = new Bundle();
        args.putLong(ExtraConstants.EXTRA_MISSION_LADDER_ID, missionLadderId);
        args.putLong(ExtraConstants.EXTRA_MISSION_TREE_ID, missionTreeId);
        args.putLong(ExtraConstants.EXTRA_MISSION_ID, missionId);
        args.putParcelable(ExtraConstants.EXTRA_STUDENT_BEAN, studentBean);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            missionLadderId = getArguments().getLong(ExtraConstants.EXTRA_MISSION_LADDER_ID);
            missionTreeId = getArguments().getLong(ExtraConstants.EXTRA_MISSION_TREE_ID);
            missionId = getArguments().getLong(ExtraConstants.EXTRA_MISSION_ID);
            studentBean = getArguments().getParcelable(ExtraConstants.EXTRA_STUDENT_BEAN);
        }
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        View rootView = inflater.inflate(
                R.layout.fragment_student_buddy_mission_invitation,
                container,
                false);

        invitationTextView = (TextView) rootView.findViewById(R.id.invitationTextView);
        studentPhotoImageView = (NetworkImageView) rootView.findViewById(R.id.studentPhotoImageView);
        cancelButton = (Button) rootView.findViewById(R.id.cancelButton);
        graduateButton = (Button) rootView.findViewById(R.id.graduateButton);
        seniorBuddyRequiredTextView = (TextView) rootView.findViewById(R.id.seniorBuddyRequiredTextView);

        String studentName = studentBean.getFirstName() + " " + studentBean.getLastName();
        String invitation = getString(R.string.mission_invitation_message, studentName);
        invitationTextView.setText(invitation);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        graduateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                graduateStudent();
            }
        });

        return rootView;
    }

    @Override
    public void receiveData(DataRepository dataRepository) {
        ImageLoader imageLoader = VolleySingleton.getInstance(getContext()).getImageLoader();
        studentPhotoImageView.setImageUrl(FacebookUtil.getProfilePicture(studentBean), imageLoader);

        missionBean = dataRepository
                .getMissionBean(missionLadderId, missionTreeId, missionId);

        // Do necessary things for senior buddy requirement.
        MissionCompletionBean completion = dataRepository.getMissionCompletion(missionId);
        boolean requiresSeniorBuddy =
                !completion.getMentorCheckoutComplete() && !dataRepository.getUserBean().getAdmin();
        if (requiresSeniorBuddy) {
            // Update visibility of UI elements.
            graduateButton.setEnabled(false);
            seniorBuddyRequiredTextView.setVisibility(View.VISIBLE);
        }
    }

    private void graduateStudent() {
        Log.i(LOG_CAT, "Button to graduate student has been pressed.");
        dataServiceConnection
                .getLocalBinder()
                .reportMissionComplete(studentBean.getId(), missionId);

        Intent intent = new Intent(getActivity(), StudentMissionRatingActivity.class);
        intent.putExtra(ExtraConstants.EXTRA_MISSION_ID, missionId);
        intent.putExtra(ExtraConstants.EXTRA_USER_MISSION_ROLE, UserMissionRoleType.buddy.name());
        startActivity(intent);

        Application app = getActivity().getApplication();
        AnalyticsUtil.reportEvent(app, studentGraduation, "" + missionId);
    }
}
