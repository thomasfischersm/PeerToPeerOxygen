package com.playposse.peertopeeroxygen.backend.serveractions;

import org.junit.AfterClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import java.io.IOException;

/**
 * A test suite for running all the API tests.
 *
 * <p>Careful! This calls the production API!
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        RegisterOrLoginServerActionTest.class,
        CreatePrivateDomainServerActionTest.class,
        SubscribeToPrivateDomainServerActionTest.class,
})
public class ApiTestSuite {

    @AfterClass
    public static void cleanUpTestData() throws IOException {
        ApiTestUtil.cleanTestData(ApiTestUtil.instantiateApi());
    }
}
