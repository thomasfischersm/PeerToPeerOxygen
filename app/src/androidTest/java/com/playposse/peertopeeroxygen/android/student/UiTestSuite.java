package com.playposse.peertopeeroxygen.android.student;

import com.playposse.peertopeeroxygen.backend.serveractions.util.ApiTestUtil;
import com.restfb.types.TestUser;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import java.io.IOException;

/**
 * A test suite for all the automated UI tests.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        StudentLoginActivityTest.class,
})
public class UiTestSuite {

    private static TestUser fbTestUser;

    @BeforeClass
    public static void setUp() {
        fbTestUser = ApiTestUtil.createFbTestUser(ApiTestUtil.TEST_USER_NAME);

    }

    @AfterClass
    public static void cleanUpTestData() throws IOException {
        ApiTestUtil.cleanTestData(ApiTestUtil.instantiateApi());
    }

    public static TestUser getFbTestUser() {
        return fbTestUser;
    }
}