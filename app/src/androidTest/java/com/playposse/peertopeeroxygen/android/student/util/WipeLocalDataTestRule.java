package com.playposse.peertopeeroxygen.android.student.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.test.InstrumentationRegistry;
import android.util.Log;

import com.playposse.peertopeeroxygen.android.data.OxygenSharedPreferences;

import org.junit.rules.ExternalResource;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.io.File;
import java.io.FilenameFilter;

import static com.playposse.peertopeeroxygen.android.data.OxygenSharedPreferences.PREFS_NAME;
import static com.playposse.peertopeeroxygen.android.data.missions.MissionDataManager.FILE_REGEX;

/**
 * A {@link org.junit.rules.TestRule} that wipes out data local to the device.
 */
public class WipeLocalDataTestRule extends ExternalResource {

    private static final String LOG_CAT = WipeLocalDataTestRule.class.getSimpleName();

    @Override
    protected void before() throws Throwable {
        wipeLocalData();
    }

    @Override
    protected void after() {
        wipeLocalData();
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
