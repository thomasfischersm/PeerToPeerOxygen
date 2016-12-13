package com.playposse.peertopeeroxygen.backend.serveractions.util;

import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.PeerToPeerOxygenApi;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.CombinedDomainBeans;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.DomainBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.MasterUserBean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;

/**
 * Helper classes that require making API calls to verify that data was created correctly.
 */
public class VerificationUtil {

    public static void verifySubscribedDomains(
            PeerToPeerOxygenApi api,
            MasterUserBean masterUserBean,
            List<Long> domainIds)
            throws IOException {

        CombinedDomainBeans combinedDomainBeans =
                api.getPublicDomains(masterUserBean.getSessionId()).execute();

        List<DomainBean> subscribedDomainBeans = combinedDomainBeans.getSubscribedBeans();
        if (subscribedDomainBeans == null) {
            subscribedDomainBeans = new ArrayList<>(0);
        }

        assertEquals(domainIds.size(), subscribedDomainBeans.size());

        domainIds = new ArrayList<>(domainIds); // Make a copy to avoid destroying the original.
        for (DomainBean domainBean : subscribedDomainBeans) {
            domainIds.remove(domainBean.getId());
        }
        assertEquals(0, domainIds.size());
    }
}
