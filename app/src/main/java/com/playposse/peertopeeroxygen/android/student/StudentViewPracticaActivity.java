package com.playposse.peertopeeroxygen.android.student;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.data.DataRepository;
import com.playposse.peertopeeroxygen.android.model.ExtraConstants;
import com.playposse.peertopeeroxygen.android.util.CreateViewUtil;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.LevelCompletionBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionCompletionBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionLadderBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionTreeBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.PracticaBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.PracticaUserBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.UserBean;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.playposse.peertopeeroxygen.android.util.CreateViewUtil.createTextView;

/**
 * An {@link android.app.Activity} that shows the student the practica, its attendees and possible
 * missions to learn and teach. The data is read from the local practica cache. No server call is
 * needed.
 */
public class StudentViewPracticaActivity extends StudentParentActivity {

    private final String LOG_CAT = StudentViewPracticaActivity.class.getSimpleName();

    private static final String ID_SEPARATOR = ",";
    private static final String BULLET = "&#8226; ";

    private LinearLayout rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_student_view_practica);
        super.onCreate(savedInstanceState);

        rootView = (LinearLayout) findViewById(R.id.rootView);
    }

    @Override
    public void receiveData(DataRepository dataRepository) {
        super.receiveData(dataRepository);

        PracticaBean practicaBean = dataRepository.getPracticaRepository().getCurrentPractica();
        if (practicaBean == null) {
            // Something went wrong. Send the user back to the main student activity.
            Log.w(LOG_CAT, "Can't open practica activity because there is no current practica.");
            startActivity(new Intent(this, StudentMainActivity.class));
            return;
        }

        // Set activity title.
        String title =
                getString(R.string.view_practica_student_activity_title, practicaBean.getName());
        setTitle(title);

        // Create a row for each attendee participant.
        boolean isEmpty = true;
        rootView.removeAllViews();
        if (practicaBean.getAttendeeUserBeans() != null) {
            for (PracticaUserBean attendeeBean : practicaBean.getAttendeeUserBeans()) {
                if (attendeeBean.getId().equals(dataRepository.getUserBean().getId())) {
                    // Skip the current user.
                    continue;
                }

                isEmpty = false;
                LinearLayout rowView =
                        CreateViewUtil.createLinearLayout(this, LinearLayout.HORIZONTAL);
                rootView.addView(rowView);

                rowView.addView(createUserInfoView(attendeeBean));
                rowView.addView(createMissionInfoView(attendeeBean));
            }
        }

        if (isEmpty) {
            // Show empty message.
            rootView.addView(createTextView(this, R.string.empty_practica_message));
        }
    }

    private LinearLayout createUserInfoView(PracticaUserBean attendeeBean) {
        LinearLayout userView = CreateViewUtil.createLinearLayout(this, LinearLayout.VERTICAL);
        userView.addView(CreateViewUtil.createNetworkImageView(
                this,
                attendeeBean.getProfilePictureUrl()));
        userView.addView(createTextView(this, attendeeBean.getFirstName()));
        userView.addView(createTextView(this, attendeeBean.getLastName()));
        return userView;
    }

    private View createMissionInfoView(PracticaUserBean attendeeBean) {
        LinearLayout missionView = CreateViewUtil.createLinearLayout(this, LinearLayout.VERTICAL);

        if (attendeeBean.getAdmin()) {
            addAdminMissionList(missionView);
            return missionView;
        }

        UserBean userBean = getDataRepository().getUserBean();
        PracticaUserBean practicaUserBean = createMinimalPracticaUserBean(userBean);

        if (userBean.getAdmin() && attendeeBean.getAdmin()) {
            // Both people are admins.
            missionView.addView(createTextView(this, R.string.both_admins_heading));
        } else if (userBean.getAdmin()) {
            // The current user is an admin.
            missionView.addView(createTextView(this, R.string.teachable_missions_heading));
            List<MissionBean> teachableMissions =
                    determineLearnableMissionsByAdmin(attendeeBean);
            addMissionList(missionView, teachableMissions);
        } else if (attendeeBean.getAdmin()) {
            // The other user is an admin.
            missionView.addView(createTextView(this, R.string.learnable_missions_heading));
            List<MissionBean> learnableMissions =
                    determineLearnableMissionsByAdmin(practicaUserBean); // TODO: Consider caching this for multiple admins.
            addMissionList(missionView, learnableMissions);
        } else {
            // Both users are students.
            missionView.addView(createTextView(this, R.string.learnable_missions_heading));
            List<MissionBean> learnableMissions =
                    determineTeachableMissions(practicaUserBean, attendeeBean);
            addMissionList(missionView, learnableMissions);

            missionView.addView(createTextView(this, R.string.teachable_missions_heading));
            List<MissionBean> teachableMissions =
                    determineTeachableMissions(attendeeBean, practicaUserBean);
            addMissionList(missionView, teachableMissions);
        }

        return missionView;
    }

    private void addMissionList(LinearLayout linearLayout, List<MissionBean> missionBeans) {
        if (missionBeans.size() == 0) {
            Spanned emptyStr = Html.fromHtml(BULLET + getString(R.string.no_missions_available));
            linearLayout.addView(createTextView(this, emptyStr));
        } else {
            for (final MissionBean missionBean : missionBeans) {
                Spanned missionStr = Html.fromHtml(BULLET + missionBean.getName());
                TextView missionTextView = createTextView(this, missionStr);
                missionTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Long[] missionPath =
                                getDataRepository().getMissionPath(missionBean.getId());
                        Intent intent = ExtraConstants.createIntent(
                                getApplicationContext(),
                                StudentMissionActivity.class,
                                missionPath[0], missionPath[1],
                                missionPath[2]);
                        startActivity(intent);
                    }
                });
                linearLayout.addView(missionTextView);
            }
        }
    }

    private void addAdminMissionList(LinearLayout linearLayout) {
        linearLayout.addView(createTextView(this, R.string.admin_can_teach_everything));
    }

    private List<MissionBean> determineTeachableMissions(
            PracticaUserBean studentBean,
            PracticaUserBean buddyBean) {

        List<MissionBean> result = new ArrayList<>();

        Set<Long> studentCompletedLevelIds = toSet(studentBean.getCompletedLevels());
        Set<Long> studentStudiedMissionIds = toSet(studentBean.getStudiedMissions());

        for (Long buddyMissionId : toSet(buddyBean.getStudiedMissions())) {
            if (studentStudiedMissionIds.contains(buddyMissionId)) {
                // Already learned this mission.
                continue;
            }

            Long[] missionPath = getDataRepository().getMissionPath(buddyMissionId);
            if (missionPath == null) {
                // The mission no longer exists.
                continue;
            }

            if (!isLevelUnlocked(missionPath, studentCompletedLevelIds)) {
                // Hasn't unlocked the previous level yet.
                continue;
            }

            MissionBean missionBean = getDataRepository().getMissionBean(buddyMissionId);
            if (!hasPrerequisitesLearned(missionBean, studentStudiedMissionIds)) {
                // Hasn't learned the prerequisites yet.
                continue;
            }

            result.add(missionBean);
        }

        return result;
    }

    /**
     * Determines which missions the specified student could learn from an admin. The algorithm
     * has to be smart to avoid checking every single possible mission.
     */
    private List<MissionBean> determineLearnableMissionsByAdmin(PracticaUserBean practicaUserBean) {
        List<MissionBean> result = new ArrayList<>();

        Set<Long> studentCompletedLevelIds = toSet(practicaUserBean.getCompletedLevels());
        Set<Long> studentStudiedMissionIds = toSet(practicaUserBean.getStudiedMissions());

        List<MissionLadderBean> missionLadderBeans = getDataRepository().getMissionLadderBeans();
        if (missionLadderBeans != null) {
            ladderLoop: for (MissionLadderBean missionLadderBean : missionLadderBeans) {
                List<MissionTreeBean> missionTreeBeans = missionLadderBean.getMissionTreeBeans();
                if (missionTreeBeans == null) {
                    continue;
                }

                for (MissionTreeBean missionTreeBean : missionTreeBeans) {
                    List<MissionBean> missionBeans = missionTreeBean.getMissionBeans();
                    if (missionBeans == null) {
                        continue;
                    }

                    missionLoop: for (MissionBean missionBean : missionBeans) {
                        if (studentStudiedMissionIds.contains(missionBean.getId())) {
                            // Already learned this mission.
                            continue;
                        }

                        List<Long> requiredMissionIds = missionBean.getRequiredMissionIds();
                        if (requiredMissionIds == null) {
                            result.add(missionBean);
                            continue;
                        }

                        for (Long requiredMissionId : requiredMissionIds) {
                            if (!studentStudiedMissionIds.contains(requiredMissionId)) {
                                continue missionLoop;
                            }
                        }

                        result.add(missionBean);
                    }

                    if (!studentCompletedLevelIds.contains(missionTreeBean.getId())) {
                        // Reached the level that the student is currently working on. We can skip
                        // any higher levels.
                        continue ladderLoop;
                    }
                }
            }
        }

        return result;
    }

    private static Set<Long> toSet(String idString) {
        if ((idString == null) || (idString.trim().length() == 0)) {
            return new HashSet<>(0);
        }

        String[] ids = idString.split(ID_SEPARATOR);
        Set<Long> set = new HashSet<>(ids.length);
        for (String id : ids) {
            set.add(Long.valueOf(id));
        }
        return set;
    }

    private boolean isLevelUnlocked(Long[] missionPath, Set<Long> completedLevelIds) {
        DataRepository dataRepository = getDataRepository();

        MissionTreeBean missionTreeBean =
                dataRepository.getMissionTreeBean(missionPath[0], missionPath[1]);
        int level = missionTreeBean.getLevel();
        if (level == 1) {
            return true;
        }

        MissionTreeBean previousMissionTreeBean =
                dataRepository.getMissionTreeBeanByLevel(missionPath[0], level - 1);
        return completedLevelIds.contains(previousMissionTreeBean.getId());
    }

    private boolean hasPrerequisitesLearned(MissionBean missionBean, Set<Long> learnedMissionId) {
        for (Long requiredMisisonId : missionBean.getRequiredMissionIds()) {
            if (!learnedMissionId.contains(requiredMisisonId)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Creates a minimal {@link PracticaUserBean} from a {@link UserBean}. This makes it a little
     * easier to pass around parameters inside of this class. These minimal instances should never
     * be passed outside of this class!
     */
    private static PracticaUserBean createMinimalPracticaUserBean(UserBean userBean) {
        PracticaUserBean practicaUserBean = new PracticaUserBean();

        // Builds comma delimited string of completed missions.
        StringBuilder missionStringBuilder = new StringBuilder();
        if (userBean.getMissionCompletionBeans() != null) {
            for (MissionCompletionBean missionCompletionBean : userBean.getMissionCompletionBeans()) {
                if (missionCompletionBean.getStudyComplete()) {
                    if (missionStringBuilder.length() > 0) {
                        missionStringBuilder.append(ID_SEPARATOR);
                    }
                    missionStringBuilder.append(missionCompletionBean.getMissionId());
                }
            }
        }
        practicaUserBean.setStudiedMissions(missionStringBuilder.toString());

        // Builds comma delimited string of completed levels.
        StringBuilder levelStringBuilder = new StringBuilder();
        if (userBean.getLevelCompletionBeans() != null) {
            for (LevelCompletionBean levelCompletionBean : userBean.getLevelCompletionBeans()) {
                if (levelStringBuilder.length() > 0) {
                    levelStringBuilder.append(ID_SEPARATOR);
                }
                levelStringBuilder.append(levelCompletionBean.getMissionTreeId());
            }
        }
        practicaUserBean.setCompletedLevels(levelStringBuilder.toString());

        return practicaUserBean;
    }
}
