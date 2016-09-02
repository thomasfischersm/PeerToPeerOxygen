package com.playposse.peertopeeroxygen.android.firebase.actions;

import android.content.Context;
import android.content.Intent;

import com.google.firebase.messaging.RemoteMessage;
import com.playposse.peertopeeroxygen.android.R;
import com.playposse.peertopeeroxygen.android.data.DataRepository;
import com.playposse.peertopeeroxygen.android.data.types.PointType;
import com.playposse.peertopeeroxygen.android.firebase.FirebaseMessage;
import com.playposse.peertopeeroxygen.android.model.ExtraConstants;
import com.playposse.peertopeeroxygen.android.model.UserBeanParcelable;
import com.playposse.peertopeeroxygen.android.student.StudentMissionTreeActivity;
import com.playposse.peertopeeroxygen.android.util.MathUtil;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.LevelCompletionBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionCompletionBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionTreeBean;

import java.util.ArrayList;
import java.util.Map;

/**
 * A Firebase action that where the student receives notification that a mission has been completed.
 */
public class MissionCompletionAction extends FirebaseAction {

    public MissionCompletionAction(RemoteMessage remoteMessage) {
        super(remoteMessage);
    }

    @Override
    public void execute(RemoteMessage remoteMessage) {
        // Look up data.
        MissionCompletionMessage completionMessage =
                new MissionCompletionMessage(remoteMessage);
        UserBeanParcelable buddyBean = completionMessage.getBuddyBean();
        Long missionId = completionMessage.getMissionId();
        DataRepository dataRepository = getDataRepository();
        Long[] ids = dataRepository.getMissionPath(missionId);
        MissionBean missionBean = dataRepository.getMissionBean(ids[0], ids[1], ids[2]);

        // Update local mission completion count.
        MissionCompletionBean missionCompletion = dataRepository
                .getMissionCompletion(missionId);
        missionCompletion.setStudyCount(missionCompletion.getStudyCount() + 1);
        if (missionCompletion.getStudyCount() >= missionBean.getMinimumStudyCount()) {
            missionCompletion.setStudyComplete(true);
        }

        // Update level completion if necessary.
        MissionTreeBean missionTreeBean =
                getDataRepository().getMissionTreeBeanByMissionId(missionId);
        LevelCompletionBean levelCompletionBean =
                getDataRepository().getLevelCompletionByMissionTreeId(missionTreeBean.getId());
        if (levelCompletionBean == null) {
            if (getDataRepository().getUserBean().getLevelCompletionBeans() == null) {
                getDataRepository().getUserBean().setLevelCompletionBeans(
                        new ArrayList<LevelCompletionBean>());
            }

            levelCompletionBean = new LevelCompletionBean();
            levelCompletionBean.setMissionTreeId(missionTreeBean.getId());
            getDataRepository().getUserBean().getLevelCompletionBeans().add(levelCompletionBean);
        }

        // Update local point counts.
        if (missionBean.getPointCostMap() != null) {
            for (Map.Entry<String, Object> entry : missionBean.getPointCostMap().entrySet()) {
                PointType pointType = PointType.valueOf(entry.getKey());
                int pointCount = 0 - MathUtil.tryParseInt(entry.getValue().toString(), 0);
                DataRepository.addPoints(dataRepository.getUserBean(), pointType, pointCount);
            }
        }

        // Send a toast.
        Context context = getApplicationContext();
        String message = String.format(
                context.getString(R.string.mission_completion_toast),
                missionBean.getName(),
                buddyBean.getFirstName() + " " + buddyBean.getLastName());
        sendToast(message);

        // Re-direct user back to the tree activity.
        Intent intent = ExtraConstants.createIntent(
                context,
                StudentMissionTreeActivity.class,
                ids[0], /* missionLadderId */
                ids[1], /* missionTreeId */
                null); /* missionId */
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * A firebase message that tells the student that the buddy has marked a mission as completed.
     */
    private static final class MissionCompletionMessage extends FirebaseMessage {

        public MissionCompletionMessage(RemoteMessage message) {
            super(message);
        }

        public Long getMissionId() {
            return Long.valueOf(data.get(MISSION_KEY));
        }

        public UserBeanParcelable getBuddyBean() {
            return UserBeanParcelable.fromJson(data.get(BUDDY_BEAN));
        }
    }
}
