package com.playposse.peertopeeroxygen.android.student;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.test.InstrumentationRegistry;
import android.util.Log;

import com.playposse.peertopeeroxygen.android.data.OxygenSharedPreferences;
import com.playposse.peertopeeroxygen.backend.serveractions.util.ApiTestUtil;
import com.restfb.types.TestUser;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import static com.playposse.peertopeeroxygen.android.data.OxygenSharedPreferences.PREFS_NAME;
import static com.playposse.peertopeeroxygen.android.data.missions.MissionDataManager.FILE_REGEX;

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

    private static TestUser fbTestUser;

    @BeforeClass
    public static void setUp() {
        wipeLocalData();
        fbTestUser = ApiTestUtil.createFbTestUser(ApiTestUtil.TEST_USER_NAME);

    }

    @AfterClass
    public static void cleanUpTestData() throws IOException {
        Log.i(LOG_CAT, "UiTestSuite.cleanUpTestData started");
        ApiTestUtil.cleanTestData(ApiTestUtil.instantiateApi());
        wipeLocalData();
        Log.i(LOG_CAT, "UiTestSuite.cleanUpTestData finished");
    }

    public static TestUser getFbTestUser() {
        return fbTestUser;
    }

    private static void wipeLocalData() {
        wipeSharedPreferences();
        wipeLocalFiles();
    }

    private static void wipeSharedPreferences() {
        Context context = InstrumentationRegistry.getTargetContext();
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().commit();
        Log.i(LOG_CAT, "Session ID after clearing preferences: "
                + OxygenSharedPreferences.getSessionId(context));
        Log.i(LOG_CAT, "Session deck shown flag after clearing preferences: "
                + OxygenSharedPreferences.hasIntroDeckBeenShown(context));
    }

    private static void wipeLocalFiles() {
        Context context = InstrumentationRegistry.getTargetContext();
        File cacheDir = context.getCacheDir();
        File[] files = context.getCacheDir().listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.matches(FILE_REGEX) || name.endsWith("practicaCache.json");
            }
        });
        if (files != null) {
            for (File file : files) {
                Log.i(LOG_CAT, "Deleting file: " + file.getName());
                file.delete();
            }
        }
    }
}