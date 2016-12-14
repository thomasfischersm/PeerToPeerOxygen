package com.playposse.peertopeeroxygen.backend.serveractions.util;

import com.google.firebase.iid.FirebaseInstanceId;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.PeerToPeerOxygenApi;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.DomainBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MasterUserBean;
import com.restfb.types.TestUser;

import org.junit.Before;

import java.io.IOException;

/**
 * A base class for API tests that creates essential test data.
 */
public class AbstractServerActionTest {

    protected PeerToPeerOxygenApi api;
    protected MasterUserBean masterUserBean;
    protected DomainBean testDomainBean;

    @Before
    public void setUp() throws IOException {
        api = ApiTestUtil.instantiateApi();
        TestUser fbTestUser = ApiTestUtil.createFbTestUser(ApiTestUtil.TEST_USER_NAME);
        masterUserBean = ApiTestUtil.registerOrLoginUser(
                fbTestUser,
                FirebaseInstanceId.getInstance().getToken(),
                null);
        testDomainBean = ApiTestUtil.createPrivateTestDomain(api, masterUserBean);
    }
}
