package com.playposse.peertopeeroxygen.backend.serveractions;

import com.google.firebase.iid.FirebaseInstanceId;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.PeerToPeerOxygenApi;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.DomainBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MasterUserBean;
import com.playposse.peertopeeroxygen.backend.serveractions.util.ApiTestUtil;
import com.restfb.types.TestUser;

import org.junit.Test;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;

/**
 * Instrumented test for CreatePrivateDomainServerAction.
 */
public class CreatePrivateDomainServerActionTest {

    @Test
    public void createPrivateDomain() throws IOException {
        // Setup.
        TestUser fbTestUser = ApiTestUtil.createFbTestUser(ApiTestUtil.TEST_USER_NAME);
        MasterUserBean masterUserBean = ApiTestUtil.registerOrLoginUser(
                fbTestUser,
                FirebaseInstanceId.getInstance().getToken(),
                null);
        PeerToPeerOxygenApi api = ApiTestUtil.instantiateApi();

        // Create private domain.
        DomainBean domainBean = api.createPrivateDomain(
                masterUserBean.getSessionId(),
                ApiTestUtil.GENERATED_DOMAIN_NAME,
                ApiTestUtil.GENERATED_DOMAIN_DESCRIPTION)
                .execute();

        assertNotNull(domainBean);
        assertEquals(ApiTestUtil.GENERATED_DOMAIN_NAME, domainBean.getName());
        assertEquals(ApiTestUtil.GENERATED_DOMAIN_DESCRIPTION, domainBean.getDescription());
        assertNotNull(domainBean.getInvitationCode());
        assertNotNull(domainBean.getOwnerBean());
        assertEquals(masterUserBean.getId(), domainBean.getOwnerBean().getId());
        assertFalse(domainBean.getPublic());
    }
}
