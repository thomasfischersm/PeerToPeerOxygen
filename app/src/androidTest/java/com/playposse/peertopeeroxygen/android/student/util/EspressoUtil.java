package com.playposse.peertopeeroxygen.android.student.util;

import android.support.test.espresso.NoMatchingViewException;
import android.view.View;

import org.hamcrest.Matcher;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * A little helper for dealing with Espresso.
 */
public class EspressoUtil {

    public static void waitForViewToBeVisible(Matcher<View> matcher, int timeout) {
        long start = System.currentTimeMillis();

        NoMatchingViewException lastException = null;
        while (start + timeout > System.currentTimeMillis()) {
            try {
                onView(matcher)
                        .check(matches(isDisplayed()));
                return;
            } catch(NoMatchingViewException ex) {
                lastException = ex;
            }

            try {
                Thread.sleep(50);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }

        throw lastException;
    }
}
