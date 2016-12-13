package com.playposse.peertopeeroxygen.backend.serveractions;

import android.support.test.filters.MediumTest;
import android.support.test.runner.AndroidJUnit4;

import com.google.firebase.iid.FirebaseInstanceId;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.PeerToPeerOxygenApi;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MasterUserBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.UserBean;
import com.playposse.peertopeeroxygen.backend.serveractions.util.ApiTestUtil;
import com.playposse.peertopeeroxygen.backend.serveractions.util.VerificationUtil;
import com.restfb.types.TestUser;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;

/**
 * Instrumented test for SubscribeToPrivateDomainServerAction.
 */
@RunWith(AndroidJUnit4.class)
@MediumTest
public class SubscribeToPrivateDomainServerActionTest {

    private PeerToPeerOxygenApi api;

    @Before
    public void setUp() {
        api = ApiTestUtil.instantiateApi();
    }

    @Test
    public void subscribeToPrivateDomain() throws IOException {
        TestUser fbTestUser = ApiTestUtil.createFbTestUser(ApiTestUtil.TEST_USER_NAME);
        MasterUserBean masterUserBean = ApiTestUtil.registerOrLoginUser(
                fbTestUser,
                FirebaseInstanceId.getInstance().getToken(),
                null);
        VerificationUtil.verifySubscribedDomains(api, masterUserBean, Collections.<Long>emptyList());

        UserBean userBean = api.subscribeToPrivateDomain(
                masterUserBean.getSessionId(),
                ApiTestUtil.TESTING_DOMAIN_INVITATION_CODE)
                .execute();
        assertUserBean(userBean);
        List<Long> domainIds = Collections.<Long>singletonList(ApiTestUtil.TESTING_DOMAIN_ID);
        VerificationUtil.verifySubscribedDomains(api, masterUserBean, domainIds);

        // Ensure that duplicate subscription doesn't create duplicates.
        userBean = api.subscribeToPrivateDomain(
                masterUserBean.getSessionId(),
                ApiTestUtil.TESTING_DOMAIN_INVITATION_CODE)
                .execute();
        assertUserBean(userBean);
        VerificationUtil.verifySubscribedDomains(api, masterUserBean, domainIds);
    }

    @Test
    public void subscribeToPublicDomain() throws IOException {
        TestUser fbTestUser = ApiTestUtil.createFbTestUser(ApiTestUtil.TEST_USER_NAME);
        MasterUserBean masterUserBean = ApiTestUtil.registerOrLoginUser(
                fbTestUser,
                FirebaseInstanceId.getInstance().getToken(),
                null);

        UserBean userBean = api.subscribeToPublicDomain(
                masterUserBean.getSessionId(),
                ApiTestUtil.TESTING_DOMAIN_ID)
                .execute();
        assertUserBean(userBean);
        List<Long> domainIds = Collections.<Long>singletonList(ApiTestUtil.TESTING_DOMAIN_ID);
        VerificationUtil.verifySubscribedDomains(api, masterUserBean, domainIds);

        // Ensure that duplicate subscription doesn't create duplicates.
        userBean = api.subscribeToPublicDomain(
                masterUserBean.getSessionId(),
                ApiTestUtil.TESTING_DOMAIN_ID)
                .execute();
        assertUserBean(userBean);
        VerificationUtil.verifySubscribedDomains(api, masterUserBean, domainIds);
    }

    private void assertUserBean(UserBean userBean) {
        assertNotNull(userBean);
        assertNotNull(userBean.getDomainId());
        assertEquals(ApiTestUtil.TESTING_DOMAIN_ID, userBean.getDomainId());
        assertFalse(userBean.getAdmin());
    }
}
