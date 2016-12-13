package com.playposse.peertopeeroxygen.backend.serveractions;

import android.support.test.filters.MediumTest;
import android.support.test.runner.AndroidJUnit4;

import com.google.firebase.iid.FirebaseInstanceId;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.PeerToPeerOxygenApi;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MasterUserBean;
import com.playposse.peertopeeroxygen.backend.serveractions.util.ApiTestUtil;
import com.playposse.peertopeeroxygen.backend.serveractions.util.VerificationUtil;
import com.restfb.types.TestUser;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertNotEquals;

/**
 * An instrumented test that calls the server API.
 */
@RunWith(AndroidJUnit4.class)
@MediumTest
public class RegisterOrLoginServerActionTest {

    private PeerToPeerOxygenApi api;

    @Before
    public void setUp() {
        api = ApiTestUtil.instantiateApi();
    }

    @Test
    public void loginWithoutDomain() throws IOException {
        // Create test user in Facebook.
        TestUser fbTestUser = ApiTestUtil.createFbTestUser(ApiTestUtil.TEST_USER_NAME);

        // Login for the first time.
        MasterUserBean masterUserBean = ApiTestUtil.registerOrLoginUser(
                fbTestUser,
                FirebaseInstanceId.getInstance().getToken(),
                null);
        assertMasterUser(masterUserBean);
        VerificationUtil.verifySubscribedDomains(api, masterUserBean, Collections.<Long>emptyList());
        Long oldSessionId = masterUserBean.getSessionId();

        // Login to existing user.
        masterUserBean = ApiTestUtil.registerOrLoginUser(
                fbTestUser,
                FirebaseInstanceId.getInstance().getToken(),
                null);
        assertMasterUser(masterUserBean);
        VerificationUtil.verifySubscribedDomains(api, masterUserBean, Collections.<Long>emptyList());
        assertNotEquals(oldSessionId, masterUserBean.getSessionId());
    }

    @Test
    public void loginWithDomain() throws IOException {
        // Create test user in Facebook.
        TestUser fbTestUser = ApiTestUtil.createFbTestUser(ApiTestUtil.TEST_USER_NAME);

        // Login for the first time.
        MasterUserBean masterUserBean = ApiTestUtil.registerOrLoginUser(
                fbTestUser,
                FirebaseInstanceId.getInstance().getToken(),
                ApiTestUtil.TESTING_DOMAIN_ID);
        assertMasterUser(masterUserBean);
        VerificationUtil.verifySubscribedDomains(api, masterUserBean, Arrays.asList(ApiTestUtil.TESTING_DOMAIN_ID));
        Long oldSessionId = masterUserBean.getSessionId();

        // Login to existing user.
        masterUserBean = ApiTestUtil.registerOrLoginUser(
                fbTestUser,
                FirebaseInstanceId.getInstance().getToken(),
                ApiTestUtil.TESTING_DOMAIN_ID);
        assertMasterUser(masterUserBean);
        VerificationUtil.verifySubscribedDomains(api, masterUserBean, Arrays.asList(ApiTestUtil.TESTING_DOMAIN_ID));
        assertNotEquals(oldSessionId, masterUserBean.getSessionId());
    }

    private void assertMasterUser(MasterUserBean masterUserBean) {
        assertNotNull(masterUserBean);
        assertEquals(ApiTestUtil.TEST_USER_NAME, masterUserBean.getName());
        assertNotNull(masterUserBean.getSessionId());
    }

}
