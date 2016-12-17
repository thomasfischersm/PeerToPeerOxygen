package com.playposse.peertopeeroxygen.backend.serveractions;

import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.DomainBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.PracticaBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.PracticaBeanCollection;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.PracticaUserBean;
import com.playposse.peertopeeroxygen.backend.peerToPeerOxygenApi.model.UserBean;
import com.playposse.peertopeeroxygen.backend.serveractions.util.AbstractServerActionTest;
import com.playposse.peertopeeroxygen.backend.serveractions.util.ApiTestUtil;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

/**
 * An API test for GetPracticaServerAction.
 */
public class GetPracticaServerActionTest extends AbstractServerActionTest {

    private final String PRACTICA_NAME = "Moon practica";
    private final String PRACTICA_GREETING = "Hello there!!!";
    private final String PRACTICA_GPS_LOCATION =
            "1600 Pennsylvania Avenue Northwest, Washington, DC";
    private final String POST_FIX_1 = " +1";
    private final String POST_FIX_2 = " +2";

    private static final long ONE_HOUR_IN_MS = 1 * 60 * 60 * 1_000;

    private enum PracticaDates {
        future,
        past,
    }

    private UserBean userBean;

    @Before
    public void subscribeToDomain() throws IOException {
        userBean = api
                .subscribeToPrivateDomain(
                        masterUserBean.getSessionId(),
                        testDomainBean.getInvitationCode())
                .execute();
    }

    @Test
    public void getPractica_differentDates() throws IOException {
        // Get past practicas with zero.
        List<Long> domainIds = Collections.singletonList(testDomainBean.getId());
        PracticaBeanCollection practicas = api
                .getPractica(domainIds, PracticaDates.past.name(), masterUserBean.getSessionId())
                .execute();
        assertNull(practicas.getItems());

        // Get future practias with zero.
        practicas = api
                .getPractica(domainIds, PracticaDates.future.name(), masterUserBean.getSessionId())
                .execute();
        assertNull(practicas.getItems());

        // Create a future practica.
        long nextDay = System.currentTimeMillis() + 24 * 60 * 60 * 1_000;
        createPractica(nextDay);

        // Get future practica with one result.
        practicas = api
                .getPractica(domainIds, PracticaDates.future.name(), masterUserBean.getSessionId())
                .execute();
        assertEquals(1, practicas.getItems().size());
        assertPracticaBean(practicas.getItems().get(0), nextDay);

        // Ensure past practicas are still zero.
        practicas = api
                .getPractica(domainIds, PracticaDates.past.name(), masterUserBean.getSessionId())
                .execute();
        assertNull(practicas.getItems());

        // Create a past practica.
        long previousDay = System.currentTimeMillis() - 24 * 60 * 60 * 1_000;
        createPractica(previousDay);

        // Ensure that there is still only one future practica.
        practicas = api
                .getPractica(domainIds, PracticaDates.future.name(), masterUserBean.getSessionId())
                .execute();
        assertEquals(1, practicas.getItems().size());
        assertPracticaBean(practicas.getItems().get(0), nextDay);

        // Ensure that the past practica now exists.
        practicas = api
                .getPractica(domainIds, PracticaDates.past.name(), masterUserBean.getSessionId())
                .execute();
        assertEquals(1, practicas.getItems().size());
        assertPracticaBean(practicas.getItems().get(0), previousDay);
    }

    @Test
    public void getPractica_differentDomains() throws IOException {
        // Create second and third domain.
        DomainBean testDomainBean0 = testDomainBean;
        DomainBean testDomainBean1 = ApiTestUtil.createPrivateTestDomain(api, masterUserBean);
        DomainBean testDomainBean2 = ApiTestUtil.createPrivateTestDomain(api, masterUserBean);

        Long domainId0 = testDomainBean0.getId();
        Long domainId1 = testDomainBean1.getId();
        Long domainId2 = testDomainBean2.getId();

        // Create practica in first domain.
        long nextDay = System.currentTimeMillis() + 24 * 60 * 60 * 1_000;
        PracticaBean practica0 = createPractica(POST_FIX_1, nextDay, domainId0);

        // Create practica in second domain.
        PracticaBean practica1 = createPractica(POST_FIX_2, nextDay, domainId1);

        // Query first domain.
        List<Long> domainIds = Collections.singletonList(domainId0);
        PracticaBeanCollection practicas = api
                .getPractica(domainIds, PracticaDates.future.name(), masterUserBean.getSessionId())
                .execute();
        assertEquals(1, practicas.getItems().size());
        assertEquals(practica0.getId(), practicas.getItems().get(0).getId());
        assertPracticaBean(practicas.getItems().get(0), POST_FIX_1, nextDay, domainId0);

        // Query first domain.
        domainIds = Collections.singletonList(domainId1);
        practicas = api
                .getPractica(domainIds, PracticaDates.future.name(), masterUserBean.getSessionId())
                .execute();
        assertEquals(1, practicas.getItems().size());
        assertEquals(practica1.getId(), practicas.getItems().get(0).getId());
        assertPracticaBean(practicas.getItems().get(0), POST_FIX_2, nextDay, domainId1);

        // Query third domain.
        domainIds = Collections.singletonList(domainId2);
        practicas = api
                .getPractica(domainIds, PracticaDates.future.name(), masterUserBean.getSessionId())
                .execute();
        assertNull(practicas.getItems());

        // Query all domains.
        domainIds = Arrays.asList(domainId0, domainId1, domainId2);
        practicas = api
                .getPractica(domainIds, PracticaDates.future.name(), masterUserBean.getSessionId())
                .execute();
        assertNotNull(practicas);
        assertEquals(2, practicas.getItems().size());

        Set<Long> practicaIds = new HashSet<>();
        for (PracticaBean practicaBean : practicas.getItems()) {
            practicaIds.add(practicaBean.getId());
        }
        assertTrue(practicaIds.contains(practica0.getId()));
        assertTrue(practicaIds.contains(practica1.getId()));
    }

    private PracticaBean createPractica(Long startTime) throws IOException {
        return createPractica(POST_FIX_1, startTime, testDomainBean.getId());
    }

    private PracticaBean createPractica(String postfix, Long startTime, Long domainId)
            throws IOException {

        PracticaUserBean hostBean = new PracticaUserBean();
        hostBean.setId(userBean.getId());

        PracticaBean practicaBean = new PracticaBean();
        practicaBean.setName(PRACTICA_NAME + postfix);
        practicaBean.setGreeting(PRACTICA_GREETING + postfix);
        practicaBean.setDomainId(domainId);
        practicaBean.setStart(startTime);
        practicaBean.setEnd(startTime + ONE_HOUR_IN_MS);
        practicaBean.setGpsLocation(PRACTICA_GPS_LOCATION);
        practicaBean.setHostUserBean(hostBean);

        PracticaBean resultBean = api
                .savePractica(masterUserBean.getSessionId(), domainId, practicaBean)
                .execute();

        assertPracticaBean(resultBean, postfix, startTime, domainId);

        return resultBean;
    }

    private void assertPracticaBean(PracticaBean resultBean, Long startTime) {
        assertPracticaBean(resultBean, POST_FIX_1, startTime, testDomainBean.getId());
    }
    private void assertPracticaBean(
            PracticaBean resultBean,
            String postfix,
            Long startTime,
            Long domainId) {

        assertNotNull(resultBean);
        assertNotNull(resultBean.getId());
        assertEquals(PRACTICA_NAME + postfix, resultBean.getName());
        assertEquals(PRACTICA_GREETING + postfix, resultBean.getGreeting());
        assertEquals(PRACTICA_GPS_LOCATION, resultBean.getGpsLocation());
        assertEquals(domainId, resultBean.getDomainId());
        assertEquals(startTime, resultBean.getStart());
        assertEquals(Long.valueOf(startTime + ONE_HOUR_IN_MS), resultBean.getEnd());

        PracticaUserBean resultHostBean = resultBean.getHostUserBean();
        assertNotNull(resultHostBean);
        assertEquals(userBean.getId(), resultHostBean.getId());
        assertEquals(masterUserBean.getName(), resultHostBean.getName());
    }
}
