package com.playposse.peertopeeroxygen.android.student;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.playposse.peertopeeroxygen.android.util.FacebookUtil;
import com.playposse.peertopeeroxygen.android.util.VolleySingleton;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionBean;

/**
 * The first {@link Fragment} in the {@link StudentSeniorBuddyMissionActivity} that tells the senior
 * buddy that he/she has just been invited to supervise the teaching of a mission.
 */
public class StudentSeniorBuddyMissionInvitationFragment extends DataServiceParentFragment {

    private static final String LOG_CAT =
            StudentSeniorBuddyMissionInvitationFragment.class.getSimpleName();

    private Long missionLadderId;
    private Long missionTreeId;
    private Long missionId;
    private UserBeanParcelable studentBean;
    private UserBeanParcelable buddyBean;

    private MissionBean missionBean;

    private TextView invitationTextView;
    private NetworkImageView studentPhotoImageView;
    private NetworkImageView buddyPhotoImageView;
    private Button cancelButton;
    private Button graduateButton;

    public StudentSeniorBuddyMissionInvitationFragment() {
        // Required empty public constructor
    }

    public static StudentSeniorBuddyMissionInvitationFragment newInstance(
            Long missionLadderId,
            Long missionTreeId,
            Long missionId,
            UserBeanParcelable studentBean,
            UserBeanParcelable buddyBean) {

        StudentSeniorBuddyMissionInvitationFragment fragment =
                new StudentSeniorBuddyMissionInvitationFragment();
        Bundle args = new Bundle();
        args.putLong(ExtraConstants.EXTRA_MISSION_LADDER_ID, missionLadderId);
        args.putLong(ExtraConstants.EXTRA_MISSION_TREE_ID, missionTreeId);
        args.putLong(ExtraConstants.EXTRA_MISSION_ID, missionId);
        args.putParcelable(ExtraConstants.EXTRA_STUDENT_BEAN, studentBean);
        args.putParcelable(ExtraConstants.EXTRA_BUDDY_BEAN, buddyBean);
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
            buddyBean = getArguments().getParcelable(ExtraConstants.EXTRA_BUDDY_BEAN);
        }
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        View rootView = inflater.inflate(
                R.layout.fragment_student_senior_buddy_mission_invitation,
                container,
                false);

        invitationTextView = (TextView) rootView.findViewById(R.id.invitationTextView);
        studentPhotoImageView =
                (NetworkImageView) rootView.findViewById(R.id.studentPhotoImageView);
        buddyPhotoImageView = (NetworkImageView) rootView.findViewById(R.id.buddyPhotoImageView);
        cancelButton = (Button) rootView.findViewById(R.id.cancelButton);
        graduateButton = (Button) rootView.findViewById(R.id.graduateButton);

        String studentName = studentBean.getFirstName() + " " + studentBean.getLastName();
        String buddyName = buddyBean.getFirstName() + " " + buddyBean.getLastName();
        String invitation = getString(
                R.string.mission_senior_invitation_message,
                buddyName,
                studentName);
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
                graduateBuddy();
            }
        });

        return rootView;
    }

    @Override
    public void receiveData(DataRepository dataRepository) {
        ImageLoader imageLoader = VolleySingleton.getInstance(getContext()).getImageLoader();
        studentPhotoImageView.setImageUrl(FacebookUtil.getProfilePicture(studentBean), imageLoader);
        buddyPhotoImageView.setImageUrl(FacebookUtil.getProfilePicture(buddyBean), imageLoader);
    }

    private void graduateBuddy() {
        dataServiceConnection
                .getLocalBinder()
                .reportMissionCheckoutComplete(
                        studentBean.getId(),
                        buddyBean.getId(),
                        missionId);

        Intent intent = new Intent(getActivity(), StudentMissionRatingActivity.class);
        intent.putExtra(ExtraConstants.EXTRA_MISSION_ID, missionId);
        intent.putExtra(
                ExtraConstants.EXTRA_USER_MISSION_ROLE,
                UserMissionRoleType.superBuddy.name());
        startActivity(intent);
    }
}
