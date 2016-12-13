package com.playposse.peertopeeroxygen.backend.serveractions;

import android.support.test.filters.MediumTest;
import android.support.test.runner.AndroidJUnit4;

import com.google.firebase.iid.FirebaseInstanceId;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MasterUserBean;
import com.playposse.peertopeeroxygen.backend.serveractions.util.ApiTestUtil;
import com.restfb.types.TestUser;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertNotEquals;

/**
 * An instrumented test that calls the server API.
 */
@RunWith(AndroidJUnit4.class)
@MediumTest
public class RegisterOrLoginServerActionTest {

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
        Long oldSessionId = masterUserBean.getSessionId();

        // Login to existing user.
        masterUserBean = ApiTestUtil.registerOrLoginUser(
                fbTestUser,
                FirebaseInstanceId.getInstance().getToken(),
                null);
        assertMasterUser(masterUserBean);
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
        Long oldSessionId = masterUserBean.getSessionId();

        // Login to existing user.
        masterUserBean = ApiTestUtil.registerOrLoginUser(
                fbTestUser,
                FirebaseInstanceId.getInstance().getToken(),
                ApiTestUtil.TESTING_DOMAIN_ID);
        assertMasterUser(masterUserBean);
        assertNotEquals(oldSessionId, masterUserBean.getSessionId());
    }

    private void assertMasterUser(MasterUserBean masterUserBean) {
        assertNotNull(masterUserBean);
        assertEquals(ApiTestUtil.TEST_USER_NAME, masterUserBean.getName());
        assertNotNull(masterUserBean.getSessionId());
    }
}
