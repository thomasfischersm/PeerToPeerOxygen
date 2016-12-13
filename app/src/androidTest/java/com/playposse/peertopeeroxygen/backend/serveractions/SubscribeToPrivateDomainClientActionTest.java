package com.playposse.peertopeeroxygen.backend.serveractions;

import android.support.test.filters.MediumTest;
import android.support.test.runner.AndroidJUnit4;

import com.google.firebase.iid.FirebaseInstanceId;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.PeerToPeerOxygenApi;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MasterUserBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.UserBean;
import com.restfb.types.TestUser;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;

/**
 * Instrumented test for SubscribeToPrivateDomainClientAction.
 */
@RunWith(AndroidJUnit4.class)
@MediumTest
public class SubscribeToPrivateDomainClientActionTest {

    @Test
    public void subscribeToPrivateDomain() throws IOException {
        TestUser fbTestUser = TestUserUtil.createFbTestUser(TestUserUtil.TEST_USER_NAME);
        MasterUserBean masterUserBean = TestUserUtil.registerOrLoginUser(
                fbTestUser,
                FirebaseInstanceId.getInstance().getToken(),
                null);

        PeerToPeerOxygenApi api = TestUserUtil.instantiateApi();
        UserBean userBean = api.subscribeToPrivateDomain(
                masterUserBean.getSessionId(),
                TestUserUtil.TESTING_DOMAIN_INVITATION_CODE)
                .execute();

        assertUserBean(userBean);
    }

    @Test
    public void subscribeToPublicDomain() throws IOException {
        TestUser fbTestUser = TestUserUtil.createFbTestUser(TestUserUtil.TEST_USER_NAME);
        MasterUserBean masterUserBean = TestUserUtil.registerOrLoginUser(
                fbTestUser,
                FirebaseInstanceId.getInstance().getToken(),
                null);

        PeerToPeerOxygenApi api = TestUserUtil.instantiateApi();
        UserBean userBean = api.subscribeToPublicDomain(
                masterUserBean.getSessionId(),
                TestUserUtil.TESTING_DOMAIN_ID)
                .execute();

        assertUserBean(userBean);
    }

    private void assertUserBean(UserBean userBean) {
        assertNotNull(userBean);
        assertNotNull(userBean.getDomainId());
        assertEquals(TestUserUtil.TESTING_DOMAIN_ID, userBean.getDomainId());
        assertFalse(userBean.getAdmin());
    }
}
