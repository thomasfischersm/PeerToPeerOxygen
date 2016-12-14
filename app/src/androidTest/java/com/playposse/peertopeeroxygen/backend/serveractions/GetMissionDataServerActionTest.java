package com.playposse.peertopeeroxygen.backend.serveractions;

import android.support.test.filters.MediumTest;
import android.support.test.runner.AndroidJUnit4;

import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.CompleteMissionDataBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.DomainBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MasterUserBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionLadderBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MissionTreeBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.UserBean;
import com.playposse.peertopeeroxygen.backend.serveractions.util.AbstractServerActionTest;
import com.playposse.peertopeeroxygen.backend.serveractions.util.ApiTestUtil;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

/**
 * An API test for GetMissionDataServerAction.
 */
@RunWith(AndroidJUnit4.class)
@MediumTest
public class GetMissionDataServerActionTest extends AbstractServerActionTest {

    private static final String MISSION_LADDER_NAME = "Generated ladder 1";
    private static final String MISSION_LADDER_DESCRIPTION = "mindless text";

    private static final String MISSION_TREE_NAME = "Generated tree 1";
    private static final String MISSION_TREE_DESCRIPTION = "brainless text";
    private static final Integer MISSION_TREE_LEVEL = 1;

    private static final String MISSION_NAME = "Generated mission 1";
    private static final String MISSION_STUDENT_INSTRUCTIONS = "student instructions";
    private static final String MISSION_BUDDY_INSTRUCTIONS = "buddy instructions";
    private static final String MISSION_STUDENT_YOUTUBE_VIDEO = "https://youtu.be/nQvRhpDIZrQ";
    private static final String MISSION_BUDDY_YOUTUBE_VIDEO = "https://youtu.be/ySSCHRSm1hA";
    private static final Integer MISSION_MINIMUM_STUDY_COUNT = 3;

    private MissionLadderBean missionLadderBean;
    private MissionTreeBean missionTreeBean;
    private MissionBean missionBean;

    @Before
    public void createMissionData() throws IOException {

        // Create mission ladder.
        missionLadderBean = new MissionLadderBean();
        missionLadderBean.setName(MISSION_LADDER_NAME);
        missionLadderBean.setDescription(MISSION_LADDER_DESCRIPTION);
        missionLadderBean.setDomainId(testDomainBean.getId());
        missionLadderBean =
                api.saveMissionLadder(
                        masterUserBean.getSessionId(),
                        testDomainBean.getId(),
                        missionLadderBean)
                        .execute();

        // Create mission tree.
        missionTreeBean = new MissionTreeBean();
        missionTreeBean.setName(MISSION_TREE_NAME);
        missionTreeBean.setDescription(MISSION_TREE_DESCRIPTION);
        missionTreeBean.setLevel(MISSION_TREE_LEVEL);
        missionTreeBean.setDomainId(testDomainBean.getId());
        missionTreeBean =
                api.saveMissionTree(
                        masterUserBean.getSessionId(),
                        missionLadderBean.getId(),
                        testDomainBean.getId(),
                        missionTreeBean)
                        .execute();

        // Create mission.
        missionBean = new MissionBean();
        missionBean.setName(MISSION_NAME);
        missionBean.setStudentInstruction(MISSION_STUDENT_INSTRUCTIONS);
        missionBean.setBuddyInstruction(MISSION_BUDDY_INSTRUCTIONS);
        missionBean.setDomainId(testDomainBean.getId());
        missionBean.setStudentYouTubeVideoId(MISSION_STUDENT_YOUTUBE_VIDEO);
        missionBean.setBuddyYouTubeVideoId(MISSION_BUDDY_YOUTUBE_VIDEO);
        missionBean.setMinimumStudyCount(MISSION_MINIMUM_STUDY_COUNT);
        missionBean =
                api.saveMission(
                        masterUserBean.getSessionId(),
                        missionLadderBean.getId(),
                        missionTreeBean.getId(),
                        testDomainBean.getId(),
                        missionBean)
                        .execute();

    }

    @Test
    public void getMissionData() throws IOException {
        CompleteMissionDataBean missionDataBean =
                api.getMissionData(masterUserBean.getSessionId(), testDomainBean.getId()).execute();

        assertNotNull(missionDataBean);
        assertNotNull(missionDataBean.getMissionLadderBeans());
        assertEquals(1, missionDataBean.getMissionLadderBeans().size());

        MissionLadderBean returnedLadder = missionDataBean.getMissionLadderBeans().get(0);
        assertNotNull(returnedLadder.getId());
        assertEquals(MISSION_LADDER_NAME, returnedLadder.getName());
        assertEquals(MISSION_LADDER_DESCRIPTION, returnedLadder.getDescription());
        assertEquals(testDomainBean.getId(), returnedLadder.getDomainId());
        assertNotNull(returnedLadder.getMissionTreeBeans());

        MissionTreeBean returnedTree = returnedLadder.getMissionTreeBeans().get(0);
        assertNotNull(returnedTree.getId());
        assertEquals(MISSION_TREE_NAME, returnedTree.getName());
        assertEquals(MISSION_TREE_DESCRIPTION, returnedTree.getDescription());
        assertEquals(testDomainBean.getId(), returnedTree.getDomainId());
        assertEquals(MISSION_TREE_LEVEL, returnedTree.getLevel());
        assertNotNull(returnedTree.getMissionBeans());

        MissionBean returnedMission = returnedTree.getMissionBeans().get(0);
        assertNotNull(returnedMission.getId());
        assertEquals(MISSION_NAME, returnedMission.getName());
        assertEquals(MISSION_STUDENT_INSTRUCTIONS, returnedMission.getStudentInstruction());
        assertEquals(MISSION_BUDDY_INSTRUCTIONS, returnedMission.getBuddyInstruction());
        assertEquals(MISSION_STUDENT_YOUTUBE_VIDEO, returnedMission.getStudentYouTubeVideoId());
        assertEquals(MISSION_BUDDY_YOUTUBE_VIDEO, returnedMission.getBuddyYouTubeVideoId());
        assertEquals(MISSION_MINIMUM_STUDY_COUNT, returnedMission.getMinimumStudyCount());
        assertEquals(testDomainBean.getId(), returnedMission.getDomainId());

        UserBean userBean = missionDataBean.getUserBean();
        assertEquals(ApiTestUtil.TEST_USER_NAME, userBean.getName());
        assertEquals(testDomainBean.getId(), userBean.getDomainId());

        DomainBean domainBean = missionDataBean.getDomainBean();
        assertEquals(testDomainBean.getId(), domainBean.getId());
        assertEquals(testDomainBean.getName(), domainBean.getName());
        assertFalse(domainBean.getPublic());
        assertEquals(ApiTestUtil.GENERATED_DOMAIN_DESCRIPTION, domainBean.getDescription());

        MasterUserBean ownerBean = domainBean.getOwnerBean();
        assertNotNull(ownerBean);
        assertNull(ownerBean.getSessionId());
        assertEquals(ApiTestUtil.TEST_USER_NAME, ownerBean.getName());
        assertEquals(masterUserBean.getId(), ownerBean.getId());
    }
}
