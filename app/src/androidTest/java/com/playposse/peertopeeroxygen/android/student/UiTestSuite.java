package com.playposse.peertopeeroxygen.android.student;

import android.util.Log;

import com.playposse.peertopeeroxygen.android.student.util.WipeLocalDataTestRule;
import com.playposse.peertopeeroxygen.backend.serveractions.util.ApiTestUtil;
import com.restfb.types.TestUser;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import java.io.IOException;

/**
 * A test suite for all the automated UI tests.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        StudentLoginActivityTest.class,
        StudentCreatePrivateDomainActivityTest.class,
})
public class UiTestSuite {

    private static final String LOG_CAT = UiTestSuite.class.getSimpleName();

    @Rule
    public static WipeLocalDataTestRule wipeLocalDataTestRule = new WipeLocalDataTestRule();

    private static TestUser fbTestUser;

    @BeforeClass
    public static void createTestFbUser() throws IOException {
        ApiTestUtil.cleanTestData(ApiTestUtil.instantiateApi());
        fbTestUser = ApiTestUtil.createFbTestUser(ApiTestUtil.TEST_USER_NAME);
    }

    @AfterClass
    public static void cleanUpTestData() throws IOException {
        Log.i(LOG_CAT, "UiTestSuite.cleanUpTestData started");
        ApiTestUtil.cleanTestData(ApiTestUtil.instantiateApi());
        Log.i(LOG_CAT, "UiTestSuite.cleanUpTestData finished");
    }

    public static TestUser getFbTestUser() {
        return fbTestUser;
    }
}